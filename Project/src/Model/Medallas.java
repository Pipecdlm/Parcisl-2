package Model;

public class Medallas
{
    // Atributos : Campos de las medallas.
    private int id;
    private String nombre;

    // Metodo Constructor: Metodo invocado al crear el objeto de medallas.
    public Medallas(int id, String nombre)
    {
        this.id = id;
        this.nombre = nombre;
    }

    // Metodos Getter y Setter : Son para acceder a los atributos del objeto, traerlos o modificarlos
    public int getId()
    {
        return this.id;
    }

    public String getNombre()
    {
        return this.nombre;
    }

    public void setNombre(String nombre)
    {
        this.nombre = nombre;
    }
}
