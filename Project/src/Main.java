import java.sql.*;

public class Main {
    public static void main(String[] args) throws Exception {
        try (Connection c = DriverManager.getConnection("jdbc:sqlite:prueba.db")) {
            System.out.println("Conectado: " + c.getMetaData().getDatabaseProductVersion());
        }
    }
}

/* Librerias Necesarias
org.xerial:sqlite-jdbc:3.46.1.0
org.slf4j:slf4j-api:2.0.16

 */
