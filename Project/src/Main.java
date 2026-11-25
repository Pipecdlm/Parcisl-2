import Model.*;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) throws Exception {
        Database.init();
        TiposPokemonDAO tiposDao = new TiposPokemonDAO();
        MedallasDAO medallasDao = new MedallasDAO();
        PokemonDAO pokemonDao = new PokemonDAO();
        EntrenadorDAO dao = new EntrenadorDAO();

        // Datos que ya vienen de Database.init()
        Medallas roca = medallasDao.buscarPorNombre("ROCA");
        Medallas cascada = medallasDao.buscarPorNombre("CASCADA");
        TiposPokemon electrico = tiposDao.buscarPorNombre("ELECTRICO");

        // Entrenador con una medalla
        Entrenador ash = dao.insertar(new Entrenador(0, "Ash", LocalDate.of(2000, 5, 22),
                "Pueblo Paleta", List.of(roca), new ArrayList<>()));
        System.out.println("Entrenador creado con id " + ash.getId());

        // Un pokemon que nace con dueño y otro que se asigna despues
        pokemonDao.insertar(new Pokemon(0, "Pikachu", List.of(electrico), 25, 35, true), ash.getId());
        Pokemon suelto = pokemonDao.insertar(new Pokemon(0, "Pichu", List.of(electrico), 5, 20, false), null);
        pokemonDao.asignarEntrenador(suelto.getId(), ash.getId());

        // Segunda medalla
        dao.agregarMedalla(ash.getId(), cascada.getId());

        // Leer el entrenador completo
        Entrenador leido = dao.buscarPorId(ash.getId());
        System.out.println(leido.getNombre() + " nacio el " + leido.getFechaNacimiento());
        leido.getMedallas().forEach(m -> System.out.println("  Medalla: " + m.getNombre()));
        leido.getPokedex().forEach(p -> System.out.println("  Pokemon: " + p.getNombre()));

        // Eliminar: los pokemon quedan sin dueño
        dao.eliminar(ash.getId());
        System.out.println("Entrenadores: " + dao.listar().size());
        System.out.println("Pokemon que siguen existiendo: " + pokemonDao.listar().size());

        // Limpieza: solo los pokemon de prueba (los tipos y medallas se conservan)
        pokemonDao.listar().forEach(p -> {
            try { pokemonDao.eliminar(p.getId()); } catch (Exception ex) { }
        });
    }
}

/* Librerias Necesarias
org.xerial:sqlite-jdbc:3.46.1.0
org.slf4j:slf4j-api:2.0.16

 */
