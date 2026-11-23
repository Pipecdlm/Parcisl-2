package pokemon;

import java.util.ArrayList;

public class Entrenador {
    private int id;
    private String nombre;
    private ArrayList<Pokemon> pokemons;
    private ArrayList<String> medallas;

    public Entrenador(int id, String nombre) {
        this.id = id;
        this.nombre = nombre;
        pokemons = new ArrayList<Pokemon>();
        medallas = new ArrayList<String>();
    }

    public int getId() {
        return id;
    }

    public String getNombre() {
        return nombre;
    }

    public ArrayList<Pokemon> getPokemons() {
        return pokemons;
    }

    public ArrayList<String> getMedallas() {
        return medallas;
    }

    // para que el JList muestre el nombre
    public String toString() {
        return nombre;
    }
}