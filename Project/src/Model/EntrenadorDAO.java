package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class EntrenadorDAO
{
    private final PokemonDAO pokemonDAO = new PokemonDAO();

    // CREATE: guarda el entrenador y sus medallas en una sola transaccion.
    // La pokedex NO se guarda aqui: los pokemon se asignan con PokemonDAO
    // (insertar(p, entrenadorId) o asignarEntrenador).
    public Entrenador insertar(Entrenador e) throws SQLException
    {
        String sql = "INSERT INTO entrenador (nombre, fecha_nacimiento, lugar_nacimiento) VALUES (?, ?, ?)";
        try (Connection c = Database.conectar())
        {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
            {
                ps.setString(1, e.getNombre());
                ps.setString(2, e.getFechaNacimiento() == null ? null : e.getFechaNacimiento().toString());
                ps.setString(3, e.getLugarNacimiento());
                ps.executeUpdate();

                int nuevoId;
                try (ResultSet rs = ps.getGeneratedKeys())
                {
                    rs.next();
                    nuevoId = rs.getInt(1);
                }

                List<Medallas> medallas = e.getMedallas() == null
                        ? new ArrayList<>() : e.getMedallas();
                guardarMedallas(c, nuevoId, medallas);

                c.commit();
                return new Entrenador(nuevoId, e.getNombre(), e.getFechaNacimiento(),
                        e.getLugarNacimiento(), medallas, new ArrayList<>());
            }
            catch (SQLException ex)
            {
                c.rollback();
                throw ex;
            }
        }
    }

    // READ: todos los entrenadores, con sus medallas y su pokedex
    public List<Entrenador> listar() throws SQLException
    {
        try (Connection c = Database.conectar())
        {
            return consultar(c, "SELECT id, nombre, fecha_nacimiento, lugar_nacimiento FROM entrenador ORDER BY id", null);
        }
    }

    // READ: un entrenador por id (null si no existe)
    public Entrenador buscarPorId(int id) throws SQLException
    {
        try (Connection c = Database.conectar())
        {
            List<Entrenador> r = consultar(c,
                    "SELECT id, nombre, fecha_nacimiento, lugar_nacimiento FROM entrenador WHERE id = ?", id);
            return r.isEmpty() ? null : r.get(0);
        }
    }

    // UPDATE: modifica los datos del entrenador y reemplaza sus medallas
    public void actualizar(Entrenador e) throws SQLException
    {
        String sql = "UPDATE entrenador SET nombre = ?, fecha_nacimiento = ?, lugar_nacimiento = ? WHERE id = ?";
        try (Connection c = Database.conectar())
        {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql))
            {
                ps.setString(1, e.getNombre());
                ps.setString(2, e.getFechaNacimiento() == null ? null : e.getFechaNacimiento().toString());
                ps.setString(3, e.getLugarNacimiento());
                ps.setInt(4, e.getId());
                ps.executeUpdate();

                try (PreparedStatement borrar = c.prepareStatement(
                        "DELETE FROM entrenador_medalla WHERE entrenador_id = ?"))
                {
                    borrar.setInt(1, e.getId());
                    borrar.executeUpdate();
                }
                if (e.getMedallas() != null)
                {
                    guardarMedallas(c, e.getId(), e.getMedallas());
                }

                c.commit();
            }
            catch (SQLException ex)
            {
                c.rollback();
                throw ex;
            }
        }
    }

    // UPDATE: le da una medalla a un entrenador (si ya la tenia, no hace nada)
    public void agregarMedalla(int entrenadorId, int medallaId) throws SQLException
    {
        String sql = "INSERT OR IGNORE INTO entrenador_medalla (entrenador_id, medalla_id) VALUES (?, ?)";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, entrenadorId);
            ps.setInt(2, medallaId);
            ps.executeUpdate();
        }
    }

    // UPDATE: le quita una medalla a un entrenador
    public void quitarMedalla(int entrenadorId, int medallaId) throws SQLException
    {
        String sql = "DELETE FROM entrenador_medalla WHERE entrenador_id = ? AND medalla_id = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, entrenadorId);
            ps.setInt(2, medallaId);
            ps.executeUpdate();
        }
    }

    // DELETE: elimina al entrenador. Sus pokemon NO se borran: quedan sin dueño.
    // Sus filas en entrenador_medalla se borran solas por ON DELETE CASCADE.
    public void eliminar(int id) throws SQLException
    {
        try (Connection c = Database.conectar())
        {
            c.setAutoCommit(false);
            try
            {
                try (PreparedStatement liberar = c.prepareStatement(
                        "UPDATE pokemon SET entrenador_id = NULL WHERE entrenador_id = ?"))
                {
                    liberar.setInt(1, id);
                    liberar.executeUpdate();
                }
                try (PreparedStatement borrar = c.prepareStatement(
                        "DELETE FROM entrenador WHERE id = ?"))
                {
                    borrar.setInt(1, id);
                    borrar.executeUpdate();
                }
                c.commit();
            }
            catch (SQLException ex)
            {
                c.rollback();
                throw ex;
            }
        }
    }

    // ---------- Metodos privados de apoyo ----------

    // Ejecuta una consulta de entrenadores y arma los objetos completos.
    // Primero lee las filas y luego carga medallas y pokedex de cada uno.
    private List<Entrenador> consultar(Connection c, String sql, Integer parametro) throws SQLException
    {
        record Fila(int id, String nombre, LocalDate fecha, String lugar) {}

        List<Fila> filas = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql))
        {
            if (parametro != null)
            {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    String fechaTexto = rs.getString("fecha_nacimiento");
                    LocalDate fecha = fechaTexto == null ? null : LocalDate.parse(fechaTexto);
                    filas.add(new Fila(rs.getInt("id"), rs.getString("nombre"),
                            fecha, rs.getString("lugar_nacimiento")));
                }
            }
        }

        List<Entrenador> lista = new ArrayList<>();
        for (Fila f : filas)
        {
            lista.add(new Entrenador(f.id(), f.nombre(), f.fecha(), f.lugar(),
                    cargarMedallas(c, f.id()),
                    pokemonDAO.listarPorEntrenador(f.id())));
        }
        return lista;
    }

    // Trae las medallas de un entrenador con un JOIN sobre la tabla intermedia
    private List<Medallas> cargarMedallas(Connection c, int entrenadorId) throws SQLException
    {
        String sql = """
            SELECT m.id, m.nombre
            FROM medalla m
            JOIN entrenador_medalla em ON em.medalla_id = m.id
            WHERE em.entrenador_id = ?
            ORDER BY m.id""";
        List<Medallas> medallas = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, entrenadorId);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    medallas.add(new Medallas(rs.getInt("id"), rs.getString("nombre")));
                }
            }
        }
        return medallas;
    }

    // Inserta una fila en entrenador_medalla por cada medalla
    private void guardarMedallas(Connection c, int entrenadorId, List<Medallas> medallas) throws SQLException
    {
        String sql = "INSERT OR IGNORE INTO entrenador_medalla (entrenador_id, medalla_id) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql))
        {
            for (Medallas m : medallas)
            {
                ps.setInt(1, entrenadorId);
                ps.setInt(2, m.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}