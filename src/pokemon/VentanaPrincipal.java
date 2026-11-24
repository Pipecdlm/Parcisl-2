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
    private JLabel etiquetaMedallas;

    public VentanaPrincipal() {
        entrenadores = Almacenamiento.cargar();

        setTitle("Gestor de Entrenadores y Pokemon");
        setSize(800, 450);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));
        modeloLista = new DefaultListModel<Entrenador>();
        listaEntrenadores = new JList<Entrenador>(modeloLista);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(new JLabel("Entrenadores"), BorderLayout.NORTH);
        panelIzquierdo.add(new JScrollPane(listaEntrenadores), BorderLayout.CENTER);
        panelIzquierdo.setPreferredSize(new Dimension(220, 0));
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
        String[] columnas = {"Numero", "Nombre", "Tipo", "Nivel"};
        modeloTabla = new DefaultTableModel(columnas, 0);
        tablaPokemons = new JTable(modeloTabla);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(new JLabel("Pokemon del entrenador"), BorderLayout.NORTH);
        panelDerecho.add(new JScrollPane(tablaPokemons), BorderLayout.CENTER);
        JButton botonAgregarPokemon = new JButton("Agregar Pokemon");
        JButton botonEliminarPokemon = new JButton("Eliminar Pokemon");
        JButton botonMedalla = new JButton("Dar medalla");

        etiquetaMedallas = new JLabel("Medallas: ninguna");

        JPanel panelBotonesPokemon = new JPanel(new GridLayout(1, 3, 5, 5));
        panelBotonesPokemon.add(botonAgregarPokemon);
        panelBotonesPokemon.add(botonEliminarPokemon);
        panelBotonesPokemon.add(botonMedalla);

        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.add(etiquetaMedallas, BorderLayout.NORTH);
        panelInferior.add(panelBotonesPokemon, BorderLayout.CENTER);
        panelDerecho.add(panelInferior, BorderLayout.SOUTH);

        botonAgregarPokemon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                agregarPokemon();
            }
        });

        botonEliminarPokemon.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                eliminarPokemon();
            }
        });

        botonMedalla.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                darMedalla();
            }
        });

        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);
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
            etiquetaMedallas.setText("Medallas: ninguna");
            return;
        }
        for (Pokemon p : seleccionado.getPokemons()) {
            Object[] fila = {p.getNumero(), p.getNombre(), p.getTipo(), p.getNivel()};
            modeloTabla.addRow(fila);
        }
        if (seleccionado.getMedallas().size() == 0) {
            etiquetaMedallas.setText("Medallas: ninguna");
        } else {
            String texto = "Medallas: ";
            for (int i = 0; i < seleccionado.getMedallas().size(); i++) {
                if (i > 0) {
                    texto = texto + ", ";
                }
                texto = texto + seleccionado.getMedallas().get(i);
            }
            etiquetaMedallas.setText(texto);
        }
    }
    private void agregarEntrenador() {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del entrenador:");

        if (nombre == null) {
            return;
        }

        if (nombre.trim().equals("")) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio", "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
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
    private void agregarPokemon() {
        Entrenador seleccionado = listaEntrenadores.getSelectedValue();

        if (seleccionado == null) {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador primero");
            return;
        }
        if (seleccionado.getPokemons().size() >= 6) {
            JOptionPane.showMessageDialog(this, "Un entrenador solo puede tener 6 pokemon",
                    "Regla", JOptionPane.ERROR_MESSAGE);
            return;
        }


        JTextField campoNumero = new JTextField();
        JTextField campoNombre = new JTextField();
        String[] tipos = {"Normal", "Fuego", "Agua", "Planta", "Electrico", "Hielo", "Lucha",
                "Veneno", "Tierra", "Volador", "Psiquico", "Bicho", "Roca", "Fantasma",
                "Dragon", "Siniestro", "Acero", "Hada"};
        JComboBox<String> campoTipo = new JComboBox<String>(tipos);
        JTextField campoNivel = new JTextField();

        Object[] formulario = {
                "Numero (1 a 1025):", campoNumero,
                "Nombre:", campoNombre,
                "Tipo:", campoTipo,
                "Nivel (1 a 100):", campoNivel
        };

        int respuesta = JOptionPane.showConfirmDialog(this, formulario,
                "Nuevo Pokemon", JOptionPane.OK_CANCEL_OPTION);

        if (respuesta != JOptionPane.OK_OPTION) {
            return;
        }
        int numero;
        int nivel;
        try {
            numero = Integer.parseInt(campoNumero.getText().trim());
            nivel = Integer.parseInt(campoNivel.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "El numero y el nivel deben ser enteros",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (numero < 1 || numero > 1025) {
            JOptionPane.showMessageDialog(this, "El numero debe estar entre 1 y 1025",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (nivel < 1 || nivel > 100) {
            JOptionPane.showMessageDialog(this, "El nivel debe estar entre 1 y 100",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        if (campoNombre.getText().trim().equals("")) {
            JOptionPane.showMessageDialog(this, "El nombre es obligatorio",
                    "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }


        int nuevoId = 1;
        for (Entrenador e : entrenadores) {
            for (Pokemon p : e.getPokemons()) {
                if (p.getId() >= nuevoId) {
                    nuevoId = p.getId() + 1;
                }
            }
        }

        Pokemon nuevo = new Pokemon(nuevoId, numero, campoNombre.getText().trim(),
                (String) campoTipo.getSelectedItem(), nivel, seleccionado);
        seleccionado.getPokemons().add(nuevo);

        Almacenamiento.guardar(entrenadores);
        mostrarPokemons();}
        // elimina el pokemon seleccionado en la tabla
        private void eliminarPokemon() {
            Entrenador entrenador = listaEntrenadores.getSelectedValue();
            int fila = tablaPokemons.getSelectedRow();

            if (entrenador == null || fila == -1) {
                JOptionPane.showMessageDialog(this, "Selecciona un entrenador y un pokemon de la tabla");
                return;
            }

            // la fila de la tabla coincide con la posicion en la lista
            Pokemon pokemon = entrenador.getPokemons().get(fila);

            int respuesta = JOptionPane.showConfirmDialog(this,
                    "Eliminar a " + pokemon.getNombre() + "?",
                    "Confirmar", JOptionPane.YES_NO_OPTION);

            if (respuesta == JOptionPane.YES_OPTION) {
                entrenador.getPokemons().remove(pokemon);
                Almacenamiento.guardar(entrenadores);
                mostrarPokemons();
            }
        }

        // le da una medalla al entrenador seleccionado
        private void darMedalla() {
            Entrenador entrenador = listaEntrenadores.getSelectedValue();

            if (entrenador == null) {
                JOptionPane.showMessageDialog(this, "Selecciona un entrenador primero");
                return;
            }

            String[] medallas = {"Roca", "Cascada", "Trueno", "Arcoiris", "Alma", "Pantano", "Volcan", "Tierra"};
            String elegida = (String) JOptionPane.showInputDialog(this, "Elige la medalla:",
                    "Dar medalla", JOptionPane.QUESTION_MESSAGE, null, medallas, medallas[0]);

            // si cancela, no hacemos nada
            if (elegida == null) {
                return;
            }

            // regla: no se puede repetir una medalla
            if (entrenador.getMedallas().contains(elegida)) {
                JOptionPane.showMessageDialog(this, entrenador.getNombre() + " ya tiene la medalla " + elegida,
                        "Regla", JOptionPane.ERROR_MESSAGE);
                return;
            }

            entrenador.getMedallas().add(elegida);
            Almacenamiento.guardar(entrenadores);
            mostrarPokemons();
        }
    }