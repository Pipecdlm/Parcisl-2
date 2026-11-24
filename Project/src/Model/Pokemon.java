package src.Model;

// Seccion de Importaciones
import java.util.List;

public class Pokemon
{
    // Atributos : Campos de los datos del pokemon.
    private int id;
    private String nombre;
    private List<TiposPokemon> tiposPokemon;
    private int nivel;
    private int vida;
    private boolean capturado;

    // Metodo Constructor : Invocado al crear un objeto de la clase.
    public Pokemon(int id, String nombre, List<TiposPokemon> tiposPokemon, int nivel, int vida, boolean capturado)
    {
        this.id = id;
        this.nombre = nombre;
        this.tiposPokemon = tiposPokemon;
        this.nivel = nivel;
        this.vida = vida;
        this.capturado = capturado;
    }

    // Metodos Getter y Setter : Utilizados para traer atributos o configurarlos.

    public int getId()
    {
        return id;
    }

    public String getNombre()
    {
        return nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }

    public List<TiposPokemon> getTiposPokemon()
    {
        return tiposPokemon;
    }

    public void setTiposPokemon(List<TiposPokemon> tiposPokemon)
    {
        this.tiposPokemon = tiposPokemon;
    }

    public int getNivel()
    {
        return nivel;
    }

    public void setNivel(int nivel)
    {
        this.nivel = nivel;
    }

    public int getVida()
    {
        return vida;
    }

    public void setVida(int vida)
    {
        this.vida = vida;
    }

    public boolean isCapturado()
    {
        return capturado;
    }

    public void setCapturado(boolean capturado)
    {
        this.capturado = capturado;
    }
}
