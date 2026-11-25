package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class TiposPokemonDAO
{
    // CREATE: guarda un tipo nuevo y devuelve el objeto con el id que le asignó la BD
    public TiposPokemon insertar(String nombre) throws SQLException
    {
        String sql = "INSERT INTO tipo_pokemon (nombre) VALUES (?)";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, nombre);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys())
            {
                rs.next();
                return new TiposPokemon(rs.getInt(1), nombre);
            }
        }
    }

    // READ: trae todos los tipos
    public List<TiposPokemon> listar() throws SQLException
    {
        List<TiposPokemon> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM tipo_pokemon ORDER BY id";
        try (Connection c = Database.conectar();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next())
            {
                lista.add(new TiposPokemon(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    // READ: busca un tipo por su id (devuelve null si no existe)
    public TiposPokemon buscarPorId(int id) throws SQLException
    {
        String sql = "SELECT id, nombre FROM tipo_pokemon WHERE id = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return new TiposPokemon(rs.getInt("id"), rs.getString("nombre"));
                }
            }
        }
        return null;
    }

    // READ: busca un tipo por su nombre (devuelve null si no existe)
    public TiposPokemon buscarPorNombre(String nombre) throws SQLException
    {
        String sql = "SELECT id, nombre FROM tipo_pokemon WHERE nombre = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return new TiposPokemon(rs.getInt("id"), rs.getString("nombre"));
                }
            }
        }
        return null;
    }

    // UPDATE: cambia el nombre de un tipo existente
    public void actualizar(TiposPokemon tipo) throws SQLException
    {
        String sql = "UPDATE tipo_pokemon SET nombre = ? WHERE id = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, tipo.getNombre());
            ps.setInt(2, tipo.getId());
            ps.executeUpdate();
        }
    }

    // DELETE: elimina un tipo por su id
    public void eliminar(int id) throws SQLException
    {
        String sql = "DELETE FROM tipo_pokemon WHERE id = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}