package src.Model;

public class Pokemon
{
    // Atributos : Campos de los datos del pokemon.
    private int id;
    private String nombre;
    private Entrenador entrenador;
    private TiposPokemon[] tiposPokemon;
    private int nivel;
    private int vida;
    private boolean capturado;

    // Metodo Constructor : Invocado al crear un objeto de la clase.
    public Pokemon(int id, String nombre, Entrenador entrenador, TiposPokemon[] tiposPokemon, int nivel, int vida, boolean capturado)
    {
        this.id = id;
        this.nombre = nombre;
        this.entrenador = entrenador;
        this.tiposPokemon = tiposPokemon;
        this.nivel = nivel;
        this.vida = vida;
        this.capturado = capturado;
    }
}
