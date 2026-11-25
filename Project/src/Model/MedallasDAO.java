package Model;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

public class MedallasDAO
{
    // CREATE: guarda una medalla nueva y devuelve el objeto con el id asignado por la BD
    public Medallas insertar(String nombre) throws SQLException
    {
        String sql = "INSERT INTO medalla (nombre) VALUES (?)";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS))
        {
            ps.setString(1, nombre);
            ps.executeUpdate();

            try (ResultSet rs = ps.getGeneratedKeys())
            {
                rs.next();
                return new Medallas(rs.getInt(1), nombre);
            }
        }
    }

    // READ: trae todas las medallas
    public List<Medallas> listar() throws SQLException
    {
        List<Medallas> lista = new ArrayList<>();
        String sql = "SELECT id, nombre FROM medalla ORDER BY id";
        try (Connection c = Database.conectar();
             Statement st = c.createStatement();
             ResultSet rs = st.executeQuery(sql))
        {
            while (rs.next())
            {
                lista.add(new Medallas(rs.getInt("id"), rs.getString("nombre")));
            }
        }
        return lista;
    }

    // READ: busca una medalla por id (devuelve null si no existe)
    public Medallas buscarPorId(int id) throws SQLException
    {
        String sql = "SELECT id, nombre FROM medalla WHERE id = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return new Medallas(rs.getInt("id"), rs.getString("nombre"));
                }
            }
        }
        return null;
    }

    // READ: busca una medalla por nombre (devuelve null si no existe)
    public Medallas buscarPorNombre(String nombre) throws SQLException
    {
        String sql = "SELECT id, nombre FROM medalla WHERE nombre = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, nombre);
            try (ResultSet rs = ps.executeQuery())
            {
                if (rs.next())
                {
                    return new Medallas(rs.getInt("id"), rs.getString("nombre"));
                }
            }
        }
        return null;
    }

    // UPDATE: cambia el nombre de una medalla existente
    public void actualizar(Medallas medalla) throws SQLException
    {
        String sql = "UPDATE medalla SET nombre = ? WHERE id = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setString(1, medalla.getNombre());
            ps.setInt(2, medalla.getId());
            ps.executeUpdate();
        }
    }

    // DELETE: elimina una medalla por id
    public void eliminar(int id) throws SQLException
    {
        String sql = "DELETE FROM medalla WHERE id = ?";
        try (Connection c = Database.conectar();
             PreparedStatement ps = c.prepareStatement(sql))
        {
            ps.setInt(1, id);
            ps.executeUpdate();
        }
    }
}