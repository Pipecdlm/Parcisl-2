package Controller;

/**
 * Error "amigable" que el Controller le entrega a la View.
 * Su mensaje ya esta listo para mostrarse al usuario (por ejemplo en un JOptionPane).
 */
public class OperacionException extends RuntimeException
{
    public OperacionException(String mensaje)
    {
        super(mensaje);
    }

    public OperacionException(String mensaje, Throwable causa)
    {
        super(mensaje, causa);
    }
}