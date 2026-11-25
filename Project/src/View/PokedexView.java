package View;

// Seccion de Importaciones
import Model.Entrenador;
import Model.Medallas;
import Model.Pokemon;
import Model.TiposPokemon;

import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;
import java.util.Scanner;


public class PokedexView
{
    // Atributo : Lector de la consola
    private final Scanner scanner = new Scanner(System.in);

    // ================== MENUS ==================

    public void mostrarMenu()
    {
        System.out.println();
        System.out.println("=========== POKEDEX ===========");
        System.out.println("1. Registrar entrenador");
        System.out.println("2. Ver entrenadores");
        System.out.println("3. Registrar pokemon");
        System.out.println("4. Ver pokedex (todos los pokemon)");
        System.out.println("5. Asignar un pokemon a un entrenador");
        System.out.println("6. Dar una medalla a un entrenador");
        System.out.println("7. Editar / eliminar");
        System.out.println("0. Salir");
        System.out.println("===============================");
    }

    public void mostrarMenuEdicion()
    {
        System.out.println();
        System.out.println("------- EDITAR / ELIMINAR -------");
        System.out.println("1. Editar entrenador");
        System.out.println("2. Eliminar entrenador");
        System.out.println("3. Editar pokemon");
        System.out.println("4. Eliminar pokemon");
        System.out.println("5. Quitar medalla a un entrenador");
        System.out.println("0. Volver");
        System.out.println("---------------------------------");
    }

    // ================== MENSAJES ==================

    public void mostrarTitulo(String titulo)
    {
        System.out.println();
        System.out.println("--- " + titulo + " ---");
    }

    public void mostrarMensaje(String mensaje)
    {
        System.out.println(mensaje);
    }

    public void mostrarError(String mensaje)
    {
        System.out.println("[ERROR] " + mensaje);
    }

    // ================== LECTURA DE DATOS ==================

    // Devuelve el texto escrito, sin espacios en los extremos (puede ser vacio).
    public String pedirTexto(String mensaje)
    {
        System.out.print(mensaje);
        return scanner.nextLine().trim();
    }

    // Repite la pregunta hasta que el usuario escriba algo.
    public String pedirTextoObligatorio(String mensaje)
    {
        while (true)
        {
            String texto = pedirTexto(mensaje);
            if (!texto.isEmpty())
            {
                return texto;
            }
            mostrarError("Este dato es obligatorio.");
        }
    }

    // Si el usuario solo presiona Enter, devuelve el valor actual (sirve para editar u omitir).
    public String pedirTextoOpcional(String mensaje, String actual)
    {
        String texto = pedirTexto(mensaje);
        return texto.isEmpty() ? actual : texto;
    }

    // Repite la pregunta hasta que el usuario escriba un numero entero.
    public int pedirEntero(String mensaje)
    {
        while (true)
        {
            String texto = pedirTexto(mensaje);
            try
            {
                return Integer.parseInt(texto);
            }
            catch (NumberFormatException e)
            {
                mostrarError("Escribe un numero entero.");
            }
        }
    }

    // Entero dentro de un rango. Si porDefecto no es null, Enter conserva ese valor.
    public int pedirEnteroEnRango(String mensaje, int min, int max, Integer porDefecto)
    {
        while (true)
        {
            String texto = pedirTexto(mensaje);
            if (texto.isEmpty() && porDefecto != null)
            {
                return porDefecto;
            }
            try
            {
                int valor = Integer.parseInt(texto);
                if (valor >= min && valor <= max)
                {
                    return valor;
                }
                mostrarError("El valor debe estar entre " + min + " y " + max + ".");
            }
            catch (NumberFormatException e)
            {
                mostrarError("Escribe un numero entero.");
            }
        }
    }

    public int pedirEnteroEnRango(String mensaje, int min, int max)
    {
        return pedirEnteroEnRango(mensaje, min, max, null);
    }

    // Fecha con formato aaaa-mm-dd. Con Enter devuelve porDefecto (puede ser null).
    public LocalDate pedirFecha(String mensaje, LocalDate porDefecto)
    {
        while (true)
        {
            String texto = pedirTexto(mensaje);
            if (texto.isEmpty())
            {
                return porDefecto;
            }
            try
            {
                return LocalDate.parse(texto);
            }
            catch (DateTimeParseException e)
            {
                mostrarError("Formato invalido. Usa aaaa-mm-dd, por ejemplo 2000-05-22.");
            }
        }
    }

    // Pregunta de si/no. Si porDefecto no es null, Enter conserva ese valor.
    public boolean pedirSiNo(String mensaje, Boolean porDefecto)
    {
        while (true)
        {
            String texto = pedirTexto(mensaje).toLowerCase();
            if (texto.isEmpty() && porDefecto != null)
            {
                return porDefecto;
            }
            if (texto.equals("s") || texto.equals("si"))
            {
                return true;
            }
            if (texto.equals("n") || texto.equals("no"))
            {
                return false;
            }
            mostrarError("Responde s (si) o n (no).");
        }
    }

    public boolean confirmar(String mensaje)
    {
        return pedirSiNo(mensaje + " (s/n): ", null);
    }

    // ================== MOSTRAR LISTAS ==================

    public void mostrarTipos(List<TiposPokemon> tipos)
    {
        System.out.println("Tipos disponibles:");
        for (TiposPokemon t : tipos)
        {
            System.out.println("  " + t.getId() + ". " + t.getNombre());
        }
    }

    public void mostrarMedallas(List<Medallas> medallas)
    {
        System.out.println("Medallas:");
        for (Medallas m : medallas)
        {
            System.out.println("  " + m.getId() + ". " + m.getNombre());
        }
    }

    public void mostrarResumenEntrenadores(List<Entrenador> entrenadores)
    {
        System.out.println("Entrenadores:");
        for (Entrenador e : entrenadores)
        {
            System.out.println("  " + e.getId() + ". " + e.getNombre());
        }
    }

    public void mostrarResumenPokemones(List<Pokemon> pokemones)
    {
        System.out.println("Pokemon:");
        for (Pokemon p : pokemones)
        {
            System.out.println("  " + formatoPokemon(p));
        }
    }

    public void mostrarPokemones(List<Pokemon> pokemones)
    {
        mostrarTitulo("POKEDEX");
        int capturados = 0;
        for (Pokemon p : pokemones)
        {
            System.out.println(formatoPokemon(p));
            if (p.isCapturado())
            {
                capturados++;
            }
        }
        System.out.println("Capturados: " + capturados + " de " + pokemones.size());
    }

    public void mostrarEntrenadores(List<Entrenador> entrenadores)
    {
        mostrarTitulo("ENTRENADORES");
        for (Entrenador e : entrenadores)
        {
            mostrarEntrenador(e);
        }
    }

    public void mostrarEntrenador(Entrenador e)
    {
        System.out.println();
        System.out.println("Entrenador #" + e.getId() + ": " + e.getNombre());
        System.out.println("  Nacimiento: "
                + (e.getFechaNacimiento() == null ? "-" : e.getFechaNacimiento())
                + " | Lugar: "
                + (e.getLugarNacimiento() == null ? "-" : e.getLugarNacimiento()));

        List<Medallas> medallas = e.getMedallas();
        if (medallas == null || medallas.isEmpty())
        {
            System.out.println("  Medallas: ninguna");
        }
        else
        {
            StringBuilder sb = new StringBuilder();
            for (Medallas m : medallas)
            {
                if (sb.length() > 0)
                {
                    sb.append(", ");
                }
                sb.append(m.getNombre());
            }
            System.out.println("  Medallas (" + medallas.size() + "): " + sb);
        }

        List<Pokemon> pokedex = e.getPokedex();
        if (pokedex == null || pokedex.isEmpty())
        {
            System.out.println("  Pokemon: ninguno");
        }
        else
        {
            System.out.println("  Pokemon (" + pokedex.size() + "):");
            for (Pokemon p : pokedex)
            {
                System.out.println("    - " + formatoPokemon(p));
            }
        }
    }

    // ================== APOYO ==================

    // Una sola linea con los datos de un pokemon
    private String formatoPokemon(Pokemon p)
    {
        StringBuilder tipos = new StringBuilder();
        if (p.getTiposPokemon() != null)
        {
            for (TiposPokemon t : p.getTiposPokemon())
            {
                if (tipos.length() > 0)
                {
                    tipos.append("/");
                }
                tipos.append(t.getNombre());
            }
        }
        return "#" + p.getId() + " " + p.getNombre()
                + " [" + tipos + "]"
                + " Nv." + p.getNivel()
                + " Vida:" + p.getVida()
                + (p.isCapturado() ? " (capturado)" : " (no capturado)");
    }
}