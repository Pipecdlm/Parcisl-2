package Model;

// Seccion de Importaciones
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public class Database
{
    // Ruta del archivo: se crea solo en la raíz del proyecto la primera vez
    private static final String URL = "jdbc:sqlite:pokedex.db";

    // Abre una conexión. Cada DAO la llamará cuando necesite la BD.
    public static Connection conectar() throws SQLException
    {
        Connection conn = DriverManager.getConnection(URL);
        try (Statement st = conn.createStatement())
        {
            // SQLite trae las llaves foráneas desactivadas; hay que activarlas en cada conexión
            st.execute("PRAGMA foreign_keys = ON");
        }
        return conn;
    }

    // Crea las tablas si todavía no existen. Se llama una vez al iniciar el programa.
    public static void init() throws SQLException
    {
        try (Connection conn = conectar(); Statement st = conn.createStatement())
        {
            st.execute("""
                CREATE TABLE IF NOT EXISTS tipo_pokemon (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS medalla (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL UNIQUE
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS entrenador (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    fecha_nacimiento TEXT,
                    lugar_nacimiento TEXT
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS pokemon (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    nombre TEXT NOT NULL,
                    entrenador_id INTEGER REFERENCES entrenador(id),
                    nivel INTEGER NOT NULL DEFAULT 1,
                    vida INTEGER NOT NULL,
                    capturado INTEGER NOT NULL DEFAULT 0
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS pokemon_tipo (
                    pokemon_id INTEGER NOT NULL REFERENCES pokemon(id) ON DELETE CASCADE,
                    tipo_id INTEGER NOT NULL REFERENCES tipo_pokemon(id),
                    PRIMARY KEY (pokemon_id, tipo_id)
                )""");

            st.execute("""
                CREATE TABLE IF NOT EXISTS entrenador_medalla (
                    entrenador_id INTEGER NOT NULL REFERENCES entrenador(id) ON DELETE CASCADE,
                    medalla_id INTEGER NOT NULL REFERENCES medalla(id),
                    PRIMARY KEY (entrenador_id, medalla_id)
                )""");

            // Datos iniciales de los tipos (si ya existen, se ignoran)
            st.execute("""
                INSERT OR IGNORE INTO tipo_pokemon (nombre) VALUES
                ('NORMAL'), ('PELEA'), ('VOLAR'), ('VENENO'), ('TIERRA'), ('PIEDRA'), 
                ('INSECTO'), ('FANTASMA'), ('ACERO'), ('FUEGO'), ('AGUA'), ('HIERBA'),
                ('ELECTRICO'), ('PSIQUICO'), ('HIELO'), ('DRAGON'), ('SINIESTRO'),
                ('HADA'), ('DESCONOCIDO')""");

            // Datos iniciales de las medallas
            st.execute("""
                INSERT OR IGNORE INTO medalla (nombre) VALUES 
                ('ROCA'), ('CASCADA'), ('TRUENO'), ('ARCOIRIS'), ('ALMA'), ('PANTANO'),
                ('VOLCAN'), ('TIERRA')
                """);
        }
    }
}