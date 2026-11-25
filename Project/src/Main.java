import Controller.PokedexController;
import Controller.VentanaController;
import Model.Database;
import View.PokedexView;
import View.VentanaPrincipal;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class Main
{
    public static void main(String[] args) throws Exception
    {
        // Crea las tablas y los datos iniciales si todavia no existen
        Database.init();

        // Si se ejecuta con el argumento "consola" se usa el menu de texto
        if (args.length > 0 && args[0].equalsIgnoreCase("consola"))
        {
            new PokedexController(new PokedexView()).iniciar();
            return;
        }

        // Por defecto se abre la interfaz grafica
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (Exception e)
        {
            // si falla, Swing usa su estilo por defecto
        }
        SwingUtilities.invokeLater(() -> new VentanaPrincipal(new VentanaController()).setVisible(true));
    }
}

/* Librerias Necesarias
org.xerial:sqlite-jdbc:3.46.1.0
org.slf4j:slf4j-api:2.0.16

 */

/* Librerias Necesarias
org.xerial:sqlite-jdbc:3.46.1.0
org.slf4j:slf4j-api:2.0.16

 */
