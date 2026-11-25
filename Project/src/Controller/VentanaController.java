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

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Capa Controller para la interfaz grafica.
 * La ventana (View) nunca toca los DAO: le pide todo a esta clase.
 * Aqui viven las reglas del negocio (maximo 6 pokemon, medallas sin repetir, rangos validos)
 * y los errores de la BD se convierten en OperacionException con un mensaje entendible.
 */
public class VentanaController
{
    // Regla del negocio: un entrenador solo puede tener 6 pokemon
    public static final int MAX_POKEMON = 6;

    // Atributos : los DAO que usa el controlador
    private final EntrenadorDAO entrenadorDAO = new EntrenadorDAO();
    private final PokemonDAO pokemonDAO = new PokemonDAO();
    private final TiposPokemonDAO tiposDAO = new TiposPokemonDAO();
    private final MedallasDAO medallasDAO = new MedallasDAO();

    // ================== CONSULTAS ==================

    public List<Entrenador> listarEntrenadores()
    {
        try
        {
            return entrenadorDAO.listar();
        }
        catch (SQLException e)
        {
            throw error(e);
        }
    }

    public List<TiposPokemon> listarTipos()
    {
        try
        {
            return tiposDAO.listar();
        }
        catch (SQLException e)
        {
            throw error(e);
        }
    }

    public List<Medallas> listarMedallas()
    {
        try
        {
            return medallasDAO.listar();
        }
        catch (SQLException e)
        {
            throw error(e);
        }
    }

    // ================== ENTRENADORES ==================

    public void registrarEntrenador(String nombre)
    {
        if (nombre == null || nombre.trim().isEmpty())
        {
            throw new OperacionException("El nombre es obligatorio.");
        }
        try
        {
            entrenadorDAO.insertar(new Entrenador(0, nombre.trim(), null, null,
                    new ArrayList<>(), new ArrayList<>()));
        }
        catch (SQLException e)
        {
            throw error(e);
        }
    }

    // Elimina al entrenador junto con todos sus pokemon
    public void eliminarEntrenador(Entrenador entrenador)
    {
        if (entrenador == null)
        {
            throw new OperacionException("Selecciona un entrenador primero.");
        }
        try
        {
            for (Pokemon p : pokemonDAO.listarPorEntrenador(entrenador.getId()))
            {
                pokemonDAO.eliminar(p.getId());
            }
            entrenadorDAO.eliminar(entrenador.getId());
        }
        catch (SQLException e)
        {
            throw error(e);
        }
    }

    // ================== POKEMON ==================

    public void registrarPokemon(Entrenador dueno, String nombre, int nivel, int vida,
                                 List<TiposPokemon> tipos)
    {
        if (dueno == null)
        {
            throw new OperacionException("Selecciona un entrenador primero.");
        }
        if (nombre == null || nombre.trim().isEmpty())
        {
            throw new OperacionException("El nombre es obligatorio.");
        }
        if (nivel < 1 || nivel > 100)
        {
            throw new OperacionException("El nivel debe estar entre 1 y 100.");
        }
        if (vida < 1 || vida > 999)
        {
            throw new OperacionException("La vida debe estar entre 1 y 999.");
        }
        if (tipos == null || tipos.isEmpty() || tipos.size() > 2)
        {
            throw new OperacionException("Un pokemon debe tener 1 o 2 tipos.");
        }

        try
        {
            // Se consulta la BD (y no la lista de la ventana) para no depender de datos viejos
            if (pokemonDAO.listarPorEntrenador(dueno.getId()).size() >= MAX_POKEMON)
            {
                throw new OperacionException("Un entrenador solo puede tener " + MAX_POKEMON + " pokemon.");
            }
            // Un pokemon que pertenece a un entrenador cuenta como capturado
            pokemonDAO.insertar(new Pokemon(0, nombre.trim(), tipos, nivel, vida, true), dueno.getId());
        }
        catch (SQLException e)
        {
            throw error(e);
        }
    }

    public void eliminarPokemon(Pokemon pokemon)
    {
        if (pokemon == null)
        {
            throw new OperacionException("Selecciona un pokemon de la tabla.");
        }
        try
        {
            pokemonDAO.eliminar(pokemon.getId());
        }
        catch (SQLException e)
        {
            throw error(e);
        }
    }

    // ================== MEDALLAS ==================

    public void darMedalla(Entrenador entrenador, Medallas medalla)
    {
        if (entrenador == null)
        {
            throw new OperacionException("Selecciona un entrenador primero.");
        }
        if (medalla == null)
        {
            throw new OperacionException("Elige una medalla.");
        }
        try
        {
            // Regla: no se puede repetir una medalla
            Entrenador actual = entrenadorDAO.buscarPorId(entrenador.getId());
            if (actual != null)
            {
                for (Medallas m : actual.getMedallas())
                {
                    if (m.getId() == medalla.getId())
                    {
                        throw new OperacionException(entrenador.getNombre()
                                + " ya tiene la medalla " + medalla.getNombre() + ".");
                    }
                }
            }
            entrenadorDAO.agregarMedalla(entrenador.getId(), medalla.getId());
        }
        catch (SQLException e)
        {
            throw error(e);
        }
    }

    // ================== MANEJO DE ERRORES ==================

    // Convierte los errores tecnicos de SQLite en mensajes entendibles
    private OperacionException error(SQLException e)
    {
        String msg = e.getMessage() == null ? "" : e.getMessage().toUpperCase();
        if (msg.contains("UNIQUE"))
        {
            return new OperacionException("Ya existe un registro con ese nombre.", e);
        }
        if (msg.contains("FOREIGN KEY"))
        {
            return new OperacionException("La accion no se puede completar: el registro esta relacionado con otros datos.", e);
        }
        return new OperacionException("Error de base de datos: " + e.getMessage(), e);
    }
}