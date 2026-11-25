package pokemon;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

public class Almacenamiento {

    private static final String ARCHIVO_ENTRENADORES = "entrenadores.txt";
    private static final String ARCHIVO_POKEMONS = "pokemons.txt";

    // guarda las dos listas en archivos
    public static void guardar(ArrayList<Entrenador> entrenadores) {
        try {
            PrintWriter escritorE = new PrintWriter(new FileWriter(ARCHIVO_ENTRENADORES));
            PrintWriter escritorP = new PrintWriter(new FileWriter(ARCHIVO_POKEMONS));

            for (Entrenador e : entrenadores) {
                // las medallas van separadas por comas
                String medallas = "";
                for (int i = 0; i < e.getMedallas().size(); i++) {
                    if (i > 0) {
                        medallas = medallas + ",";
                    }
                    medallas = medallas + e.getMedallas().get(i);
                }
                escritorE.println(e.getId() + ";" + e.getNombre() + ";" + medallas);

                for (Pokemon p : e.getPokemons()) {
                    escritorP.println(p.getId() + ";" + p.getNumero() + ";" + p.getNombre() + ";"
                            + p.getTipo() + ";" + p.getNivel() + ";" + e.getId());
                }
            }

            escritorE.close();
            escritorP.close();
        } catch (Exception ex) {
            System.out.println("Error al guardar: " + ex.getMessage());
        }
    }

    // lee los archivos y arma la lista de entrenadores con sus pokemons
    public static ArrayList<Entrenador> cargar() {
        ArrayList<Entrenador> entrenadores = new ArrayList<Entrenador>();

        try {
            // primero los entrenadores
            BufferedReader lectorE = new BufferedReader(new FileReader(ARCHIVO_ENTRENADORES));
            String linea;
            while ((linea = lectorE.readLine()) != null) {
                String[] partes = linea.split(";", -1);
                Entrenador e = new Entrenador(Integer.parseInt(partes[0]), partes[1]);
                if (!partes[2].equals("")) {
                    for (String m : partes[2].split(",")) {
                        e.getMedallas().add(m);
                    }
                }
                entrenadores.add(e);
            }
            lectorE.close();

            // despues los pokemons, buscando a su entrenador por el id
            BufferedReader lectorP = new BufferedReader(new FileReader(ARCHIVO_POKEMONS));
            while ((linea = lectorP.readLine()) != null) {
                String[] partes = linea.split(";");
                int idEntrenador = Integer.parseInt(partes[5]);
                for (Entrenador e : entrenadores) {
                    if (e.getId() == idEntrenador) {
                        Pokemon p = new Pokemon(Integer.parseInt(partes[0]), Integer.parseInt(partes[1]),
                                partes[2], partes[3], Integer.parseInt(partes[4]), e);
                        e.getPokemons().add(p);
                    }
                }
            }
            lectorP.close();
        } catch (Exception ex) {
            // si los archivos no existen todavia, se empieza con la lista vacia
            System.out.println("No se encontraron datos guardados");
        }

        return entrenadores;
    }
}