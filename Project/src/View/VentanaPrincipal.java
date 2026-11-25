package View;

// Seccion de Importaciones
import Controller.OperacionException;
import Controller.VentanaController;
import Model.Entrenador;
import Model.Medallas;
import Model.Pokemon;
import Model.TiposPokemon;

import javax.swing.DefaultListCellRenderer;
import javax.swing.DefaultListModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.table.DefaultTableModel;
import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Capa View (interfaz grafica con Swing).
 * Solo dibuja la ventana y recoge lo que escribe el usuario.
 * NO conoce los DAO ni la base de datos: todo se lo pide a VentanaController.
 */
public class VentanaPrincipal extends JFrame
{
    // Atributos : el controlador y los componentes de la ventana
    private final VentanaController controller;
    private List<Entrenador> entrenadores = new ArrayList<>();

    private final DefaultListModel<Entrenador> modeloLista = new DefaultListModel<>();
    private final JList<Entrenador> listaEntrenadores = new JList<>(modeloLista);
    private final DefaultTableModel modeloTabla;
    private final JTable tablaPokemons;
    private final JLabel etiquetaMedallas = new JLabel("Medallas: ninguna");

    public VentanaPrincipal(VentanaController controller)
    {
        this.controller = controller;

        setTitle("Gestor de Entrenadores y Pokemon");
        setSize(900, 480);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(new BorderLayout(10, 10));

        // ---------- Panel izquierdo: lista de entrenadores ----------
        // El Model no tiene toString(), asi que la lista muestra el nombre con un renderer
        listaEntrenadores.setCellRenderer(new DefaultListCellRenderer()
        {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index,
                                                          boolean isSelected, boolean cellHasFocus)
            {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
                if (value != null)
                {
                    setText(((Entrenador) value).getNombre());
                }
                return this;
            }
        });

        JButton botonAgregar = new JButton("Agregar entrenador");
        JButton botonEliminar = new JButton("Eliminar entrenador");

        JPanel panelBotones = new JPanel(new GridLayout(2, 1, 5, 5));
        panelBotones.add(botonAgregar);
        panelBotones.add(botonEliminar);

        JPanel panelIzquierdo = new JPanel(new BorderLayout());
        panelIzquierdo.add(new JLabel("Entrenadores"), BorderLayout.NORTH);
        panelIzquierdo.add(new JScrollPane(listaEntrenadores), BorderLayout.CENTER);
        panelIzquierdo.add(panelBotones, BorderLayout.SOUTH);
        panelIzquierdo.setPreferredSize(new Dimension(220, 0));

        // ---------- Panel derecho: tabla de pokemon, medallas y botones ----------
        String[] columnas = {"Id", "Nombre", "Tipo", "Nivel", "Vida"};
        modeloTabla = new DefaultTableModel(columnas, 0)
        {
            @Override
            public boolean isCellEditable(int fila, int columna)
            {
                return false; // la tabla es solo de lectura
            }
        };
        tablaPokemons = new JTable(modeloTabla);

        JButton botonAgregarPokemon = new JButton("Agregar Pokemon");
        JButton botonEliminarPokemon = new JButton("Eliminar Pokemon");
        JButton botonMedalla = new JButton("Dar medalla");

        JPanel panelBotonesPokemon = new JPanel(new GridLayout(1, 3, 5, 5));
        panelBotonesPokemon.add(botonAgregarPokemon);
        panelBotonesPokemon.add(botonEliminarPokemon);
        panelBotonesPokemon.add(botonMedalla);

        JPanel panelInferior = new JPanel(new BorderLayout(5, 5));
        panelInferior.add(etiquetaMedallas, BorderLayout.NORTH);
        panelInferior.add(panelBotonesPokemon, BorderLayout.CENTER);

        JPanel panelDerecho = new JPanel(new BorderLayout());
        panelDerecho.add(new JLabel("Pokemon del entrenador"), BorderLayout.NORTH);
        panelDerecho.add(new JScrollPane(tablaPokemons), BorderLayout.CENTER);
        panelDerecho.add(panelInferior, BorderLayout.SOUTH);

        add(panelIzquierdo, BorderLayout.WEST);
        add(panelDerecho, BorderLayout.CENTER);

        // ---------- Eventos ----------
        botonAgregar.addActionListener(e -> agregarEntrenador());
        botonEliminar.addActionListener(e -> eliminarEntrenador());
        botonAgregarPokemon.addActionListener(e -> agregarPokemon());
        botonEliminarPokemon.addActionListener(e -> eliminarPokemon());
        botonMedalla.addActionListener(e -> darMedalla());

        // Al seleccionar un entrenador se muestran sus pokemon
        listaEntrenadores.addListSelectionListener(e -> mostrarPokemons());

        recargar(null);
    }

    // ================== REFRESCAR LA PANTALLA ==================

    // Vuelve a leer los entrenadores desde la BD (a traves del controlador).
    // Si se pasa un id, ese entrenador queda seleccionado de nuevo.
    private void recargar(Integer idSeleccionado)
    {
        try
        {
            entrenadores = controller.listarEntrenadores();
        }
        catch (OperacionException ex)
        {
            mostrarError(ex.getMessage());
            entrenadores = new ArrayList<>();
        }

        modeloLista.clear();
        Entrenador aSeleccionar = null;
        for (Entrenador e : entrenadores)
        {
            modeloLista.addElement(e);
            if (idSeleccionado != null && e.getId() == idSeleccionado)
            {
                aSeleccionar = e;
            }
        }
        if (aSeleccionar != null)
        {
            listaEntrenadores.setSelectedValue(aSeleccionar, true);
        }
        mostrarPokemons();
    }

    // Llena la tabla y la etiqueta de medallas con el entrenador seleccionado
    private void mostrarPokemons()
    {
        modeloTabla.setRowCount(0);
        Entrenador seleccionado = listaEntrenadores.getSelectedValue();
        if (seleccionado == null)
        {
            etiquetaMedallas.setText("Medallas: ninguna");
            return;
        }

        for (Pokemon p : seleccionado.getPokedex())
        {
            Object[] fila = {p.getId(), p.getNombre(), tiposComoTexto(p), p.getNivel(), p.getVida()};
            modeloTabla.addRow(fila);
        }

        if (seleccionado.getMedallas().isEmpty())
        {
            etiquetaMedallas.setText("Medallas: ninguna");
        }
        else
        {
            StringBuilder texto = new StringBuilder("Medallas: ");
            for (int i = 0; i < seleccionado.getMedallas().size(); i++)
            {
                if (i > 0)
                {
                    texto.append(", ");
                }
                texto.append(seleccionado.getMedallas().get(i).getNombre());
            }
            etiquetaMedallas.setText(texto.toString());
        }
    }

    // ================== ACCIONES DE LOS BOTONES ==================

    private void agregarEntrenador()
    {
        String nombre = JOptionPane.showInputDialog(this, "Nombre del entrenador:");
        if (nombre == null)
        {
            return; // cancelo
        }
        try
        {
            controller.registrarEntrenador(nombre);
            recargar(idSeleccionado());
        }
        catch (OperacionException ex)
        {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarEntrenador()
    {
        Entrenador seleccionado = listaEntrenadores.getSelectedValue();
        if (seleccionado == null)
        {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador primero");
            return;
        }

        int respuesta = JOptionPane.showConfirmDialog(this,
                "Eliminar a " + seleccionado.getNombre() + " y todos sus pokemon?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION)
        {
            return;
        }
        try
        {
            controller.eliminarEntrenador(seleccionado);
            recargar(null);
        }
        catch (OperacionException ex)
        {
            mostrarError(ex.getMessage());
        }
    }

    private void agregarPokemon()
    {
        Entrenador seleccionado = listaEntrenadores.getSelectedValue();
        if (seleccionado == null)
        {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador primero");
            return;
        }

        try
        {
            // Los tipos salen de la BD, asi la lista siempre coincide con la del Model
            List<TiposPokemon> tipos = controller.listarTipos();
            String[] nombresTipos = new String[tipos.size()];
            String[] nombresTipos2 = new String[tipos.size() + 1];
            nombresTipos2[0] = "(ninguno)";
            for (int i = 0; i < tipos.size(); i++)
            {
                nombresTipos[i] = tipos.get(i).getNombre();
                nombresTipos2[i + 1] = tipos.get(i).getNombre();
            }

            JTextField campoNombre = new JTextField();
            JComboBox<String> campoTipo1 = new JComboBox<>(nombresTipos);
            JComboBox<String> campoTipo2 = new JComboBox<>(nombresTipos2);
            JTextField campoNivel = new JTextField();
            JTextField campoVida = new JTextField();

            Object[] formulario = {
                    "Nombre:", campoNombre,
                    "Tipo principal:", campoTipo1,
                    "Tipo secundario:", campoTipo2,
                    "Nivel (1 a 100):", campoNivel,
                    "Vida (1 a 999):", campoVida
            };

            int respuesta = JOptionPane.showConfirmDialog(this, formulario,
                    "Nuevo Pokemon", JOptionPane.OK_CANCEL_OPTION);
            if (respuesta != JOptionPane.OK_OPTION)
            {
                return;
            }

            int nivel;
            int vida;
            try
            {
                nivel = Integer.parseInt(campoNivel.getText().trim());
                vida = Integer.parseInt(campoVida.getText().trim());
            }
            catch (NumberFormatException ex)
            {
                mostrarError("El nivel y la vida deben ser numeros enteros");
                return;
            }

            // Se arma la lista de tipos (el secundario es opcional y no puede repetirse)
            List<TiposPokemon> elegidos = new ArrayList<>();
            elegidos.add(tipos.get(campoTipo1.getSelectedIndex()));
            int indice2 = campoTipo2.getSelectedIndex();
            if (indice2 > 0 && indice2 - 1 != campoTipo1.getSelectedIndex())
            {
                elegidos.add(tipos.get(indice2 - 1));
            }

            controller.registrarPokemon(seleccionado, campoNombre.getText(), nivel, vida, elegidos);
            recargar(seleccionado.getId());
        }
        catch (OperacionException ex)
        {
            mostrarError(ex.getMessage());
        }
    }

    private void eliminarPokemon()
    {
        Entrenador entrenador = listaEntrenadores.getSelectedValue();
        int fila = tablaPokemons.getSelectedRow();
        if (entrenador == null || fila == -1)
        {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador y un pokemon de la tabla");
            return;
        }

        // La fila de la tabla coincide con la posicion en la lista del entrenador
        Pokemon pokemon = entrenador.getPokedex().get(fila);

        int respuesta = JOptionPane.showConfirmDialog(this,
                "Eliminar a " + pokemon.getNombre() + "?",
                "Confirmar", JOptionPane.YES_NO_OPTION);
        if (respuesta != JOptionPane.YES_OPTION)
        {
            return;
        }
        try
        {
            controller.eliminarPokemon(pokemon);
            recargar(entrenador.getId());
        }
        catch (OperacionException ex)
        {
            mostrarError(ex.getMessage());
        }
    }

    private void darMedalla()
    {
        Entrenador entrenador = listaEntrenadores.getSelectedValue();
        if (entrenador == null)
        {
            JOptionPane.showMessageDialog(this, "Selecciona un entrenador primero");
            return;
        }

        try
        {
            // Las medallas tambien salen de la BD
            List<Medallas> medallas = controller.listarMedallas();
            if (medallas.isEmpty())
            {
                JOptionPane.showMessageDialog(this, "No hay medallas registradas");
                return;
            }
            String[] nombres = new String[medallas.size()];
            for (int i = 0; i < medallas.size(); i++)
            {
                nombres[i] = medallas.get(i).getNombre();
            }

            Object elegida = JOptionPane.showInputDialog(this, "Elige la medalla:",
                    "Dar medalla", JOptionPane.QUESTION_MESSAGE, null, nombres, nombres[0]);
            if (elegida == null)
            {
                return; // cancelo
            }

            Medallas medalla = medallas.get(Arrays.asList(nombres).indexOf(elegida));
            controller.darMedalla(entrenador, medalla);
            recargar(entrenador.getId());
        }
        catch (OperacionException ex)
        {
            mostrarError(ex.getMessage());
        }
    }

    // ================== APOYO ==================

    // Id del entrenador seleccionado (o null si no hay ninguno)
    private Integer idSeleccionado()
    {
        Entrenador e = listaEntrenadores.getSelectedValue();
        return e == null ? null : e.getId();
    }

    // Une los tipos de un pokemon: "FUEGO/VOLAR"
    private String tiposComoTexto(Pokemon p)
    {
        StringBuilder sb = new StringBuilder();
        for (TiposPokemon t : p.getTiposPokemon())
        {
            if (sb.length() > 0)
            {
                sb.append("/");
            }
            sb.append(t.getNombre());
        }
        return sb.toString();
    }

    private void mostrarError(String mensaje)
    {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
    }
}