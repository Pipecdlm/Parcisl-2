package Controller;

// Seccion de Importaciones
import Model.Entrenador;
import Model.EntrenadorDAO;
import Model.Medallas;
import Model.MedallasDAO;
import Model.Pokemon;
import Model.PokemonDAO;
import Model.TiposPokemon;
import Model.TiposPokemonDAO;
import View.PokedexView;

import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;


public class PokedexController
{
    // Atributos : la View y los DAO que usa el controlador
    private final PokedexView view;
    private final EntrenadorDAO entrenadorDAO = new EntrenadorDAO();
    private final PokemonDAO pokemonDAO = new PokemonDAO();
    private final TiposPokemonDAO tiposDAO = new TiposPokemonDAO();
    private final MedallasDAO medallasDAO = new MedallasDAO();

    // Constructor
    public PokedexController(PokedexView view)
    {
        this.view = view;
    }

    // ================== CICLO PRINCIPAL ==================

    public void iniciar()
    {
        boolean salir = false;
        while (!salir)
        {
            view.mostrarMenu();
            int opcion = view.pedirEntero("Elige una opcion: ");
            try
            {
                switch (opcion)
                {
                    case 1 -> registrarEntrenador();
                    case 2 -> verEntrenadores();
                    case 3 -> registrarPokemon();
                    case 4 -> verPokedex();
                    case 5 -> asignarPokemon();
                    case 6 -> darMedalla();
                    case 7 -> menuEdicion();
                    case 0 -> salir = true;
                    default -> view.mostrarError("Opcion no valida.");
                }
            }
            catch (SQLException e)
            {
                view.mostrarError(traducirError(e));
            }
        }
        view.mostrarMensaje("Hasta luego!");
    }

    private void menuEdicion()
    {
        boolean volver = false;
        while (!volver)
        {
            view.mostrarMenuEdicion();
            int opcion = view.pedirEntero("Elige una opcion: ");
            try
            {
                switch (opcion)
                {
                    case 1 -> editarEntrenador();
                    case 2 -> eliminarEntrenador();
                    case 3 -> editarPokemon();
                    case 4 -> eliminarPokemon();
                    case 5 -> quitarMedalla();
                    case 0 -> volver = true;
                    default -> view.mostrarError("Opcion no valida.");
                }
            }
            catch (SQLException e)
            {
                view.mostrarError(traducirError(e));
            }
        }
    }

    // ================== ENTRENADORES ==================

    private void registrarEntrenador() throws SQLException
    {
        view.mostrarTitulo("REGISTRAR ENTRENADOR");
        String nombre = view.pedirTextoObligatorio("Nombre: ");
        LocalDate fecha = view.pedirFecha("Fecha de nacimiento (aaaa-mm-dd, Enter para omitir): ", null);
        String lugar = view.pedirTextoOpcional("Lugar de nacimiento (Enter para omitir): ", null);

        Entrenador nuevo = entrenadorDAO.insertar(
                new Entrenador(0, nombre, fecha, lugar, new ArrayList<>(), new ArrayList<>()));
        view.mostrarMensaje("Entrenador registrado con id " + nuevo.getId() + ".");
    }

    private void verEntrenadores() throws SQLException
    {
        List<Entrenador> lista = entrenadorDAO.listar();
        if (lista.isEmpty())
        {
            view.mostrarError("No hay entrenadores registrados.");
            return;
        }
        view.mostrarEntrenadores(lista);
    }

    private void editarEntrenador() throws SQLException
    {
        Entrenador e = seleccionarEntrenador();
        if (e == null)
        {
            return;
        }
        view.mostrarTitulo("EDITAR ENTRENADOR (Enter conserva el valor actual)");
        e.setNombre(view.pedirTextoOpcional("Nombre [" + e.getNombre() + "]: ", e.getNombre()));
        e.setFechaNacimiento(view.pedirFecha(
                "Fecha de nacimiento [" + (e.getFechaNacimiento() == null ? "-" : e.getFechaNacimiento()) + "]: ",
                e.getFechaNacimiento()));
        e.setLugarNacimiento(view.pedirTextoOpcional(
                "Lugar de nacimiento [" + (e.getLugarNacimiento() == null ? "-" : e.getLugarNacimiento()) + "]: ",
                e.getLugarNacimiento()));

        entrenadorDAO.actualizar(e);
        view.mostrarMensaje("Entrenador actualizado.");
    }

    private void eliminarEntrenador() throws SQLException
    {
        Entrenador e = seleccionarEntrenador();
        if (e == null)
        {
            return;
        }
        if (view.confirmar("Eliminar a " + e.getNombre() + "? Sus pokemon quedaran sin dueno"))
        {
            entrenadorDAO.eliminar(e.getId());
            view.mostrarMensaje("Entrenador eliminado.");
        }
        else
        {
            view.mostrarMensaje("Operacion cancelada.");
        }
    }

    // ================== POKEMON ==================

    private void registrarPokemon() throws SQLException
    {
        view.mostrarTitulo("REGISTRAR POKEMON");
        String nombre = view.pedirTextoObligatorio("Nombre: ");
        int nivel = view.pedirEnteroEnRango("Nivel (1-100): ", 1, 100);
        int vida = view.pedirEnteroEnRango("Vida (1-999): ", 1, 999);
        boolean capturado = view.confirmar("Ya fue capturado?");

        List<TiposPokemon> tipos = seleccionarTipos();
        if (tipos == null)
        {
            return;
        }

        Integer entrenadorId = null;
        if (view.confirmar("Asignarlo a un entrenador?"))
        {
            Entrenador dueno = seleccionarEntrenador();
            if (dueno != null)
            {
                entrenadorId = dueno.getId();
            }
        }

        Pokemon creado = pokemonDAO.insertar(new Pokemon(0, nombre, tipos, nivel, vida, capturado), entrenadorId);
        view.mostrarMensaje("Pokemon registrado con id " + creado.getId() + ".");
    }

    private void verPokedex() throws SQLException
    {
        List<Pokemon> lista = pokemonDAO.listar();
        if (lista.isEmpty())
        {
            view.mostrarError("No hay pokemon registrados.");
            return;
        }
        view.mostrarPokemones(lista);
    }

    private void asignarPokemon() throws SQLException
    {
        view.mostrarTitulo("ASIGNAR POKEMON A UN ENTRENADOR");
        Pokemon p = seleccionarPokemon();
        if (p == null)
        {
            return;
        }
        Entrenador e = seleccionarEntrenador();
        if (e == null)
        {
            return;
        }
        pokemonDAO.asignarEntrenador(p.getId(), e.getId());
        view.mostrarMensaje(p.getNombre() + " ahora pertenece a " + e.getNombre() + ".");
    }

    private void editarPokemon() throws SQLException
    {
        Pokemon p = seleccionarPokemon();
        if (p == null)
        {
            return;
        }
        view.mostrarTitulo("EDITAR POKEMON (Enter conserva el valor actual)");
        p.setNombre(view.pedirTextoOpcional("Nombre [" + p.getNombre() + "]: ", p.getNombre()));
        p.setNivel(view.pedirEnteroEnRango("Nivel [" + p.getNivel() + "]: ", 1, 100, p.getNivel()));
        p.setVida(view.pedirEnteroEnRango("Vida [" + p.getVida() + "]: ", 1, 999, p.getVida()));
        p.setCapturado(view.pedirSiNo(
                "Capturado? [" + (p.isCapturado() ? "s" : "n") + "] (s/n): ", p.isCapturado()));

        if (view.confirmar("Cambiar sus tipos?"))
        {
            List<TiposPokemon> tipos = seleccionarTipos();
            if (tipos != null)
            {
                p.setTiposPokemon(tipos);
            }
        }

        pokemonDAO.actualizar(p);
        view.mostrarMensaje("Pokemon actualizado.");
    }

    private void eliminarPokemon() throws SQLException
    {
        Pokemon p = seleccionarPokemon();
        if (p == null)
        {
            return;
        }
        if (view.confirmar("Eliminar a " + p.getNombre() + "?"))
        {
            pokemonDAO.eliminar(p.getId());
            view.mostrarMensaje("Pokemon eliminado.");
        }
        else
        {
            view.mostrarMensaje("Operacion cancelada.");
        }
    }

    // ================== MEDALLAS ==================

    private void darMedalla() throws SQLException
    {
        view.mostrarTitulo("DAR MEDALLA A UN ENTRENADOR");
        Entrenador e = seleccionarEntrenador();
        if (e == null)
        {
            return;
        }
        Medallas m = seleccionarMedalla();
        if (m == null)
        {
            return;
        }
        for (Medallas tiene : e.getMedallas())
        {
            if (tiene.getId() == m.getId())
            {
                view.mostrarError(e.getNombre() + " ya tiene la medalla " + m.getNombre() + ".");
                return;
            }
        }
        entrenadorDAO.agregarMedalla(e.getId(), m.getId());
        view.mostrarMensaje(e.getNombre() + " recibio la medalla " + m.getNombre() + ".");
    }

    private void quitarMedalla() throws SQLException
    {
        view.mostrarTitulo("QUITAR MEDALLA A UN ENTRENADOR");
        Entrenador e = seleccionarEntrenador();
        if (e == null)
        {
            return;
        }
        if (e.getMedallas().isEmpty())
        {
            view.mostrarError(e.getNombre() + " no tiene medallas.");
            return;
        }
        view.mostrarMedallas(e.getMedallas());
        int id = view.pedirEntero("Id de la medalla a quitar (0 para cancelar): ");
        if (id == 0)
        {
            return;
        }
        for (Medallas m : e.getMedallas())
        {
            if (m.getId() == id)
            {
                entrenadorDAO.quitarMedalla(e.getId(), id);
                view.mostrarMensaje("Medalla " + m.getNombre() + " retirada.");
                return;
            }
        }
        view.mostrarError("Ese entrenador no tiene una medalla con ese id.");
    }

    // ================== SELECCION DE REGISTROS ==================
    // Cada metodo muestra la lista, pide un id y devuelve el objeto elegido.
    // Devuelve null si no hay registros, si el usuario cancela (0) o si el id no existe.

    private Entrenador seleccionarEntrenador() throws SQLException
    {
        List<Entrenador> lista = entrenadorDAO.listar();
        if (lista.isEmpty())
        {
            view.mostrarError("No hay entrenadores registrados.");
            return null;
        }
        view.mostrarResumenEntrenadores(lista);
        int id = view.pedirEntero("Id del entrenador (0 para cancelar): ");
        if (id == 0)
        {
            return null;
        }
        for (Entrenador e : lista)
        {
            if (e.getId() == id)
            {
                return e;
            }
        }
        view.mostrarError("No existe un entrenador con ese id.");
        return null;
    }

    private Pokemon seleccionarPokemon() throws SQLException
    {
        List<Pokemon> lista = pokemonDAO.listar();
        if (lista.isEmpty())
        {
            view.mostrarError("No hay pokemon registrados.");
            return null;
        }
        view.mostrarResumenPokemones(lista);
        int id = view.pedirEntero("Id del pokemon (0 para cancelar): ");
        if (id == 0)
        {
            return null;
        }
        for (Pokemon p : lista)
        {
            if (p.getId() == id)
            {
                return p;
            }
        }
        view.mostrarError("No existe un pokemon con ese id.");
        return null;
    }

    private Medallas seleccionarMedalla() throws SQLException
    {
        List<Medallas> lista = medallasDAO.listar();
        if (lista.isEmpty())
        {
            view.mostrarError("No hay medallas registradas.");
            return null;
        }
        view.mostrarMedallas(lista);
        int id = view.pedirEntero("Id de la medalla (0 para cancelar): ");
        if (id == 0)
        {
            return null;
        }
        for (Medallas m : lista)
        {
            if (m.getId() == id)
            {
                return m;
            }
        }
        view.mostrarError("No existe una medalla con ese id.");
        return null;
    }

    // Pide un tipo principal (obligatorio) y uno secundario (opcional).
    private List<TiposPokemon> seleccionarTipos() throws SQLException
    {
        List<TiposPokemon> todos = tiposDAO.listar();
        if (todos.isEmpty())
        {
            view.mostrarError("No hay tipos registrados.");
            return null;
        }
        view.mostrarTipos(todos);

        List<TiposPokemon> elegidos = new ArrayList<>();
        TiposPokemon primero = pedirTipo(todos, "Tipo principal (id): ", false);
        elegidos.add(primero);

        TiposPokemon segundo = pedirTipo(todos, "Tipo secundario (id, 0 si no tiene): ", true);
        if (segundo != null)
        {
            if (segundo.getId() == primero.getId())
            {
                view.mostrarMensaje("Es el mismo tipo; se guardara solo uno.");
            }
            else
            {
                elegidos.add(segundo);
            }
        }
        return elegidos;
    }

    // Repite hasta que el usuario escriba un id de la lista (o 0 si se permite "ninguno").
    private TiposPokemon pedirTipo(List<TiposPokemon> todos, String mensaje, boolean permiteNinguno)
    {
        while (true)
        {
            int id = view.pedirEntero(mensaje);
            if (id == 0 && permiteNinguno)
            {
                return null;
            }
            for (TiposPokemon t : todos)
            {
                if (t.getId() == id)
                {
                    return t;
                }
            }
            view.mostrarError("Ese id no esta en la lista.");
        }
    }

    // ================== MANEJO DE ERRORES ==================

    // Convierte los errores tecnicos de SQLite en mensajes entendibles
    private String traducirError(SQLException e)
    {
        String msg = e.getMessage() == null ? "" : e.getMessage().toUpperCase();
        if (msg.contains("UNIQUE"))
        {
            return "Ya existe un registro con ese nombre.";
        }
        if (msg.contains("FOREIGN KEY"))
        {
            return "No se puede completar la accion: el registro esta relacionado con otros datos.";
        }
        return "Error de base de datos: " + e.getMessage();
    }
}