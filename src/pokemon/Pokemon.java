package pokemon;

public class Pokemon {
    private int id;
    private int numero;
    private String nombre;
    private String tipo;
    private int nivel;
    private Entrenador entrenador;

    public Pokemon(int id, int numero, String nombre, String tipo, int nivel, Entrenador entrenador) {
        this.id = id;
        this.numero = numero;
        this.nombre = nombre;
        this.tipo = tipo;
        this.nivel = nivel;
        this.entrenador = entrenador;
    }

    public int getId() {
        return id;
    }

    public int getNumero() {
        return numero;
    }

    public String getNombre() {
        return nombre;
    }

    public String getTipo() {
        return tipo;
    }

    public int getNivel() {
        return nivel;
    }

    public Entrenador getEntrenador() {
        return entrenador;
    }
}