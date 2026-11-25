package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;

public class PokemonDAO
{
    private static final String SELECT_BASE =
            "SELECT id, nombre, nivel, vida, capturado FROM pokemon";

    // CREATE: guarda el pokemon y sus tipos en una sola transaccion.
    // entrenadorId puede ser null (pokemon sin dueño). Devuelve el pokemon con su id real.
    public Pokemon insertar(Pokemon p, Integer entrenadorId) throws SQLException
    {
        String sql = "INSERT INTO pokemon (nombre, entrenador_id, nivel, vida, capturado) VALUES (?, ?, ?, ?, ?)";
        try (Connection c = Database.conectar())
        {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
            {
                ps.setString(1, p.getNombre());
                if (entrenadorId == null)
                {
                    ps.setNull(2, Types.INTEGER);
                }
                else
                {
                    ps.setInt(2, entrenadorId);
                }
                ps.setInt(3, p.getNivel());
                ps.setInt(4, p.getVida());
                ps.setInt(5, p.isCapturado() ? 1 : 0);
                ps.executeUpdate();

                int nuevoId;
                try (ResultSet rs = ps.getGeneratedKeys())
                {
                    rs.next();
                    nuevoId = rs.getInt(1);
                }

                List<TiposPokemon> tipos = p.getTiposPokemon() == null
                        ? new ArrayList<>() : p.getTiposPokemon();
                guardarTipos(c, nuevoId, tipos);

                c.commit();
                return new Pokemon(nuevoId, p.getNombre(), tipos,
                        p.getNivel(), p.getVida(), p.isCapturado());
            }
            catch (SQLException e)
            {
                c.rollback();
                throw e;
            }
        }
    }

    // READ: todos los pokemon
    public List<Pokemon> listar() throws SQLException
    {
        try (Connection c = Database.conectar())
        {
            return consultar(c, SELECT_BASE + " ORDER BY id", null);
        }
    }

    // READ: un pokemon por id (null si no existe)
    public Pokemon buscarPorId(int id) throws SQLException
    {
        try (Connection c = Database.conectar())
        {
            List<Pokemon> r = consultar(c, SELECT_BASE + " WHERE id = ?", id);
            return r.isEmpty() ? null : r.get(0);
        }
    }

    // READ: los pokemon de un entrenador (lo usara EntrenadorDAO para llenar su pokedex)
    public List<Pokemon> listarPorEntrenador(int entrenadorId) throws SQLException
    {
        try (Connection c = Database.conectar())
        {
            return consultar(c, SELECT_BASE + " WHERE entrenador_id = ? ORDER BY id", entrenadorId);
        }
    }

    // UPDATE: modifica los datos del pokemon y reemplaza sus tipos (no cambia el dueño)
    public void actualizar(Pokemon p) throws SQLException
    {
        String sql = "UPDATE pokemon SET nombre = ?, nivel = ?, vida = ?, capturado = ? WHERE id = ?";
        try (Connection c = Database.conectar())
        {
            c.setAutoCommit(false);
            try (PreparedStatement ps = c.prepareStatement(sql))
            {
                ps.setString(1, p.getNombre());
                ps.setInt(2, p.getNivel());
                ps.setInt(3, p.getVida());
                ps.setInt(4, p.isCapturado() ? 1 : 0);
                ps.setInt(5, p.getId());
                ps.executeUpdate();

                try (PreparedStatement borrar = c.prepareStatement(
                        "DELETE FROM pokemon_tipo WHERE pokemon_id = ?"))
                {
                    borrar.setInt(1, p.getId());
                    borrar.executeUpdate();
                }
                if (p.getTiposPokemon() != null)
                {
                    guardarTipos(c, p.getId(), p.getTiposPokemon());
                }

                c.commit();
            }
            catch (SQLException e)
            {
                c.rollback();
                throw e;
            }
        }
    }

    // UPDATE: asigna un dueño al pokemon. Con null lo deja sin dueño.
    public void asignarEntrenador(int pokemonId, Integer entrenadorId) throws SQLException
    {
        String sql = "UPDATE pokemon SET entrenador_id = ? WHERE id = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            if (entrenadorId == null)
            {
                ps.setNull(1, Types.INTEGER);
            }
            else
            {
                ps.setInt(1, entrenadorId);
            }
            ps.setInt(2, pokemonId);
            ps.executeUpdate();
        }
    }

    // DELETE: elimina el pokemon (sus filas en pokemon_tipo se borran solas por ON DELETE CASCADE)
    public void eliminar(int id) throws SQLException
    {
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement("DELETE FROM pokemon WHERE id = ?"))
        {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }

    // ---------- Metodos privados de apoyo ----------

    // Ejecuta una consulta de pokemon y arma los objetos con sus tipos.
    // Primero lee todas las filas y despues carga los tipos, para no tener
    // dos consultas abiertas al mismo tiempo.
    private List<Pokemon> consultar(Connection c, String sql, Integer parametro) throws SQLException
    {
        record Fila(int id, String nombre, int nivel, int vida, boolean capturado) {}

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
                    filas.add(new Fila(rs.getInt("id"), rs.getString("nombre"),
                            rs.getInt("nivel"), rs.getInt("vida"),
                            rs.getInt("capturado") == 1));
                }
            }
        }

        List<Pokemon> lista = new ArrayList<>();
        for (Fila f : filas)
        {
            lista.add(new Pokemon(f.id(), f.nombre(), cargarTipos(c, f.id()),
                    f.nivel(), f.vida(), f.capturado()));
        }
        return lista;
    }

    // Trae los tipos de un pokemon con un JOIN sobre la tabla intermedia
    private List<TiposPokemon> cargarTipos(Connection c, int pokemonId) throws SQLException
    {
        String sql = """
            SELECT t.id, t.nombre
            FROM tipo_pokemon t
            JOIN pokemon_tipo pt ON pt.tipo_id = t.id
            WHERE pt.pokemon_id = ?
            ORDER BY t.id""";
        List<TiposPokemon> tipos = new ArrayList<>();
        try (PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, pokemonId);
            try (ResultSet rs = ps.executeQuery())
            {
                while (rs.next())
                {
                    tipos.add(new TiposPokemon(rs.getInt("id"), rs.getString("nombre")));
                }
            }
        }
        return tipos;
    }

    // Inserta una fila en pokemon_tipo por cada tipo del pokemon
    private void guardarTipos(Connection c, int pokemonId, List<TiposPokemon> tipos) throws SQLException
    {
        String sql = "INSERT INTO pokemon_tipo (pokemon_id, tipo_id) VALUES (?, ?)";
        try (PreparedStatement ps = c.prepareStatement(sql))
        {
            for (TiposPokemon t : tipos)
            {
                ps.setInt(1, pokemonId);
                ps.setInt(2, t.getId());
                ps.addBatch();
            }
            ps.executeBatch();
        }
    }
}