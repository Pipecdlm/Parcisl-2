package pokemon;

import javax.swing.*;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

public class VentanaPrincipal extends JFrame {

    private ArrayList<Entrenador> entrenadores;

    private DefaultListModel<Entrenador> modeloLista;
    private JList<Entrenador> listaEntrenadores;
    private DefaultTableModel modeloTabla;
    private JTable tablaPokemons;

    public VentanaPrincipal() {
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

        // botones
        JButton botonAgregar = new JButton("Agregar entrenador");
        JButton botonEliminar = new JButton("Eliminar entrenador");

        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBotones.add(botonAgregar);
        panelBotones.add(botonEliminar);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);

        botonAgregar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarEntrenador();
            }
        });

        botonEliminar.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarEntrenador();
            }
        });

        // panel de la derecha: tabla de pokemons
        String[] columnas = {"Numero", "Nombre", "Tipo", "Nivel"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaPokemons = new JTable(modeloTabla);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(new JLabel("Pokemon del entrenador"), BorderLayout.NORTH);
        panelDerecho.add(new JScrollPane(tablaPokemons), BorderLayout.CENTER);

        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);

        // al seleccionar un entrenador se muestran sus pokemons
        listaEntrenadores.addListSelectionListener(new ListSelectionListener() {
            public void valueChanged(ListSelectionEvent e) {
                mostrarPokemons();
            }
        });

        actualizarLista();
    }

    private void actualizarLista() {
        modeloLista.clear();
        for (Entrenador e : entrenadores) {
            modeloLista.addElement(e);
        }
    }

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

    // pide el nombre y crea un entrenador nuevo
    private void agregarEntrenador() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del entrenador:");

        if (nombre == null) {
            return;
        }

        if (nombre.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // el id nuevo es el mayor que exista + 1
        int nuevoId = 1;
        for (Entrenador e : entrenadores) {
            if (e.getId() >= nuevoId) {
                nuevoId = e.getId() + 1;
            }
        }

        entrenadores.add(new Entrenador(nuevoId, nombre.trim()));
        Almacenamiento.guardar(entrenadores);
        actualizarLista();
    }

    // elimina el entrenador seleccionado (con sus pokemons)
    private void eliminarEntrenador() {
        Entrenador seleccionado = listaEntrenadores.getSelectedValue();

        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador primero");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "Eliminar a " + seleccionado.getNombre() + " y todos sus pokemon?",
                "Confirmar", JOptionPane.YES_NO_OPTION);

        if (respuesta == JOptionPane.YES_OPTION) {
            entrenadores.remove(seleccionado);
            Almacenamiento.guardar(entrenadores);
            actualizarLista();
            mostrarPokemons();
        }
    }
}