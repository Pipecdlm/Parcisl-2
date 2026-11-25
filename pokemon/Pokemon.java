package pokemon;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.ArrayList;

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

    public static class VentanaPrincipal extends JFrame {

        private ArrayList<Entrenador> entrenadores;

        private DefaultListModel<Entrenador> modeloLista;
        private JList<Entrenador> listaEntrenadores;
        private DefaultTableModel modeloTabla;
        private JTable tablaPokemons;

        public VentanaPrincipal() {
            // cargamos los datos guardados
            entrenadores = Almacenamiento.cargar();

            setTitle("Gestor de Entrenadores y Pokemon");
            setSize(800, 450);
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLocationRelativeTo(null);
            setLayout(new BorderLayout(10, 10));

            // panel de la izquierda: lista de entrenadores
            modeloLista = new DefaultListModel<Entrenador>();
            listaEntrenadores = new JList<Entrenador>(modeloLista);

            JPanel panelIzquierdo = new JPanel(new BorderLayout());
            panelIzquierdo.add(new JLabel("Entrenadores"), BorderLayout.NORTH);
            panelIzquierdo.add(new JScrollPane(listaEntrenadores), BorderLayout.CENTER);
            panelIzquierdo.setPreferredSize(new Dimension(220, 0));

            // panel de la derecha: tabla de pokemons
            String[] columnas = {"Numero", "Nombre", "Tipo", "Nivel"};
            modeloTabla = new DefaultTableModel(columnas, 0);
            tablaPokemons = new JTable(modeloTabla);

            JPanel panelDerecho = new JPanel(new BorderLayout());
            panelDerecho.add(new JLabel("Pokemon del entrenador"), BorderLayout.NORTH);
            panelDerecho.add(new JScrollPane(tablaPokemons), BorderLayout.CENTER);

            add(panelIzquierdo, BorderLayout.WEST);
            add(panelDerecho, BorderLayout.CENTER);

            // cuando se selecciona un entrenador, se muestran sus pokemons
            listaEntrenadores.addListSelectionListener(new ListSelectionListener() {
                public void valueChanged(ListSelectionEvent e) {
                    mostrarPokemons();
                }
            });

            actualizarLista();
        }

        // llena la lista de la izquierda con los entrenadores
        private void actualizarLista() {
            modeloLista.clear();
            for (Entrenador e : entrenadores) {
                modeloLista.addElement(e);
            }
        }

        // llena la tabla con los pokemons del entrenador seleccionado
        private void mostrarPokemons() {
            modeloTabla.setRowCount(0);
            Entrenador seleccionado = listaEntrenadores.getSelectedValue();
            if (seleccionado == null) {
                return;
            }
            for (Pokemon p : seleccionado.getPokemons()) {
                Object[] fila = {p.getNumero(), p.getNombre(), p.getTipo(), p.getNivel()};
                modeloTabla.addRow(fila);
            }
        }
    }

    public static class Main {
        public static void main(String[] args) {
            VentanaPrincipal ventana = new VentanaPrincipal();
            ventana.setVisible(true);
        }
    }
}