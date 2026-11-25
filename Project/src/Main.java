import Model.Database;

public class Main {
    public static void main(String[] args) throws Exception {
        Database.init();
        System.out.println("Base de datos lista");
    }
}

/* Librerias Necesarias
org.xerial:sqlite-jdbc:3.46.1.0
org.slf4j:slf4j-api:2.0.16

 */
