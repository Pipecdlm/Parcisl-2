package Model;

public class TiposPokemon
{
   // Atributos : Campos de los Tipos del Pokemon
    private int id;
    private String nombre;

    // Metodo Constructor : Invocado al crear un objeto de la clase.
    public TiposPokemon(int id, String nombre)
    {
        this.id = id;
        this.nombre = nombre;
    }

    // Metodos Getter y Setter : Utilizados para traer los atributos y configurarlos

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
}
