import Controller.PokedexController;
import Model.Database;
import View.PokedexView;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        // Crea las tablas y los datos iniciales si todavia no existen
        Database.init();

        // Arranca el programa: View y Controller
        new PokedexController(new PokedexView()).iniciar();
    }
}

/* Librerias Necesarias
org.xerial:sqlite-jdbc:3.46.1.0
org.slf4j:slf4j-api:2.0.16

 */
