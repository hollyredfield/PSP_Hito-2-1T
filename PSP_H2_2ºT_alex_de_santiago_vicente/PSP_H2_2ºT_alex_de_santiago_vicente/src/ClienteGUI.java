// Importo las librerías necesarias para la interfaz gráfica y la comunicación en red
import java.awt.*;
import java.io.*;
import java.net.*;
import java.util.*;
import javax.swing.*;
import javax.swing.border.*;

// Defino la clase ClienteGUI que extiende de JFrame para crear la interfaz gráfica
// Declaro la clase ClienteGUI que extiende de JFrame para crear una interfaz gráfica de usuario
public class ClienteGUI extends JFrame {
    // Declaro los componentes de la interfaz gráfica
    private JTextField busquedaField;
    private JComboBox<String> tipoBusquedaCombo;
    private JTextArea resultadosArea;
    private JButton buscarButton;
    private JLabel statusLabel;
    private Color primaryColor = new Color(51, 153, 255);
    private Color backgroundColor = new Color(240, 240, 240);

    // Declaro nuevas variables para las sugerencias
    private JList<String> sugerenciasList;
    private DefaultListModel<String> sugerenciasModel;
    private JPanel resultadosPanel;
    private Map<String, String[]> sugerencias;

    // Constructor de la clase ClienteGUI
    public ClienteGUI() {
        // Inicializo el label de estado
        statusLabel = new JLabel("Listo");
        // Configuro la ventana principal
        setTitle("Biblioteca Virtual Pro");
        setSize(800, 600);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        getContentPane().setBackground(backgroundColor);

        // Inicializo las sugerencias
        initializeSugerencias();

        // Inicializo los modelos y componentes
        sugerenciasModel = new DefaultListModel<>();
        sugerenciasList = new JList<>(sugerenciasModel);
        resultadosPanel = new JPanel();
        resultadosArea = new JTextArea(); // Añadir esta línea

        // Modifico el panel principal para dar más espacio a los resultados
        JPanel mainPanel = new JPanel(new BorderLayout(15, 15));
        mainPanel.setBorder(BorderFactory.createEmptyBorder(20, 20, 20, 20));
        mainPanel.setBackground(backgroundColor);

        // Panel superior que contiene título y búsqueda
        JPanel topPanel = new JPanel(new BorderLayout(10, 10));
        topPanel.setBackground(backgroundColor);

        // Panel superior con título
        JPanel headerPanel = new JPanel(new BorderLayout());
        headerPanel.setBackground(backgroundColor);
        JLabel titleLabel = new JLabel("Sistema de Búsqueda de Libros");
        titleLabel.setFont(new Font("Segoe UI", Font.BOLD, 24));
        titleLabel.setForeground(primaryColor);
        headerPanel.add(titleLabel, BorderLayout.CENTER);
        topPanel.add(headerPanel, BorderLayout.NORTH);

        // Modificar el panel de búsqueda
        JPanel searchPanel = new JPanel(new GridBagLayout());
        searchPanel.setBackground(backgroundColor);
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.insets = new Insets(5, 5, 5, 5);

        // Combobox con tooltips
        tipoBusquedaCombo = new JComboBox<>(new String[]{"Categoría", "Autor", "Título"});
        tipoBusquedaCombo.setPreferredSize(new Dimension(150, 35));
        tipoBusquedaCombo.setToolTipText("Seleccione el tipo de búsqueda que desea realizar");
        
        // Campo de búsqueda con placeholder
        busquedaField = new JTextField(20);
        busquedaField.setPreferredSize(new Dimension(250, 35));

        // Panel de sugerencias
        sugerenciasModel = new DefaultListModel<>();
        sugerenciasList = new JList<>(sugerenciasModel);
        sugerenciasList.setVisibleRowCount(4);
        JScrollPane sugerenciasScroll = new JScrollPane(sugerenciasList);
        sugerenciasScroll.setPreferredSize(new Dimension(250, 80));

        // Botón de búsqueda
        buscarButton = new JButton("Buscar");
        buscarButton.setPreferredSize(new Dimension(100, 35));
        buscarButton.setBackground(primaryColor);
        buscarButton.setForeground(Color.WHITE);
        buscarButton.setBorder(new EmptyBorder(5, 15, 5, 15));
        buscarButton.setFocusPainted(false);

        // Añadir componentes con GridBagLayout
        gbc.gridx = 0; gbc.gridy = 0;
        searchPanel.add(new JLabel("Buscar por:"), gbc);
        
        gbc.gridx = 1;
        searchPanel.add(tipoBusquedaCombo, gbc);
        
        gbc.gridx = 2;
        searchPanel.add(busquedaField, gbc);
        
        gbc.gridx = 3;
        searchPanel.add(buscarButton, gbc);

        gbc.gridx = 2; gbc.gridy = 1;
        searchPanel.add(sugerenciasScroll, gbc);

        topPanel.add(searchPanel, BorderLayout.CENTER);

        // Panel de resultados con scroll
        resultadosPanel = new JPanel();
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));
        JScrollPane scrollPane = new JScrollPane(resultadosPanel);
        scrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_AS_NEEDED);
        scrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_NEVER);
        scrollPane.setBorder(BorderFactory.createLineBorder(primaryColor, 1));
        scrollPane.getVerticalScrollBar().setUnitIncrement(16);

        // Panel inferior para el status
        JPanel bottomPanel = new JPanel(new BorderLayout());
        bottomPanel.setBackground(backgroundColor);
        bottomPanel.add(statusLabel, BorderLayout.WEST);

        // Añadir todos los paneles al panel principal
        mainPanel.add(topPanel, BorderLayout.NORTH);
        mainPanel.add(scrollPane, BorderLayout.CENTER);
        mainPanel.add(bottomPanel, BorderLayout.SOUTH);

        // Dar más espacio a los resultados
        scrollPane.setPreferredSize(new Dimension(750, 400));

        add(mainPanel);

        // Configurar eventos
        tipoBusquedaCombo.addActionListener(e -> setPlaceholder());
        setPlaceholder(); // Ahora es seguro llamar a esto
        
        busquedaField.getDocument().addDocumentListener(new javax.swing.event.DocumentListener() {
            public void insertUpdate(javax.swing.event.DocumentEvent e) { actualizarSugerencias(); }
            public void removeUpdate(javax.swing.event.DocumentEvent e) { actualizarSugerencias(); }
            public void changedUpdate(javax.swing.event.DocumentEvent e) { actualizarSugerencias(); }
        });

        sugerenciasList.addListSelectionListener(e -> {
            if (!e.getValueIsAdjusting()) {
                String selected = sugerenciasList.getSelectedValue();
                if (selected != null) {
                    busquedaField.setText(selected);
                }
            }
        });

        // Configurar acciones
        buscarButton.addActionListener(e -> buscarLibros());
        busquedaField.addActionListener(e -> buscarLibros());

        // Hacer la ventana adaptable
        setMinimumSize(new Dimension(600, 400));
    }

    private void initializeSugerencias() {
        sugerencias = new HashMap<>();
        sugerencias.put("Categoría", new String[]{
            "java", "python", "javascript", "bases de datos", "programación",
            "patrones de diseño", "algoritmos", "arquitectura", "testing"
        });
        sugerencias.put("Autor", new String[]{
            "Robert C. Martin", "Joshua Bloch", "Martin Fowler",
            "Eric Evans", "Kent Beck"
        });
        sugerencias.put("Título", new String[]{
            "Clean Code", "Effective Java", "Design Patterns",
            "Python Crash Course", "JavaScript: The Good Parts"
        });
    }

    private void setPlaceholder() {
        String tipo = (String) tipoBusquedaCombo.getSelectedItem();
        switch (tipo) {
            case "Categoría" -> busquedaField.setToolTipText("Ej: java, python, bases de datos...");
            case "Autor" -> busquedaField.setToolTipText("Ej: Robert C. Martin, Joshua Bloch...");
            case "Título" -> busquedaField.setToolTipText("Ej: Clean Code, Effective Java...");
        }
        actualizarSugerencias();
    }

    private void actualizarSugerencias() {
        String tipo = (String) tipoBusquedaCombo.getSelectedItem();
        String texto = busquedaField.getText().toLowerCase();
        sugerenciasModel.clear();
        
        String[] sugerenciasTipo = sugerencias.get(tipo);
        if (sugerenciasTipo != null) {
            for (String sugerencia : sugerenciasTipo) {
                if (texto.isEmpty() || sugerencia.toLowerCase().contains(texto)) {
                    sugerenciasModel.addElement(sugerencia);
                }
            }
        }
    }

    private void mostrarResultados(String resultados) {
        resultadosPanel.removeAll();
        resultadosPanel.setLayout(new BoxLayout(resultadosPanel, BoxLayout.Y_AXIS));

        String[] lineas = resultados.trim().split("\n");
        int numLibros = resultados.trim().isEmpty() ? 0 : lineas.length / 3;

        // Contador de resultados
        JPanel headerPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
        headerPanel.setBackground(new Color(240, 240, 255));
        headerPanel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));
        headerPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 40));
        JLabel contadorLabel = new JLabel(String.format("Se encontraron %d libros:", numLibros));
        contadorLabel.setFont(new Font("Segoe UI", Font.BOLD, 14));
        headerPanel.add(contadorLabel);
        resultadosPanel.add(headerPanel);

        if (numLibros == 0) {
            JPanel noResultadosPanel = new JPanel(new FlowLayout(FlowLayout.LEFT));
            noResultadosPanel.setBackground(Color.WHITE);
            JLabel noResultados = new JLabel("No se encontraron resultados para la búsqueda");
            noResultados.setFont(new Font("Segoe UI", Font.ITALIC, 14));
            noResultadosPanel.add(noResultados);
            resultadosPanel.add(noResultadosPanel);
        } else {
            for (int i = 0; i < lineas.length; i += 3) {
                if (i + 2 < lineas.length) {
                    JPanel libroPanel = new JPanel();
                    libroPanel.setLayout(new BoxLayout(libroPanel, BoxLayout.Y_AXIS));
                    libroPanel.setBackground(Color.WHITE);
                    libroPanel.setBorder(BorderFactory.createCompoundBorder(
                        BorderFactory.createMatteBorder(0, 0, 1, 0, Color.LIGHT_GRAY),
                        BorderFactory.createEmptyBorder(10, 10, 10, 10)
                    ));
                    libroPanel.setMaximumSize(new Dimension(Integer.MAX_VALUE, 100));
                    libroPanel.setAlignmentX(Component.LEFT_ALIGNMENT);

                    // Añadir la información del libro con etiquetas HTML para mejor formato
                    String[] etiquetas = {"Título: ", "Autor: ", "Categoría: "};
                    for (int j = 0; j < 3; j++) {
                        String texto = lineas[i + j].replace(etiquetas[j], "");
                        JLabel label = new JLabel("<html><b>" + etiquetas[j] + "</b>" + texto + "</html>");
                        label.setFont(new Font("Segoe UI", Font.PLAIN, 14));
                        libroPanel.add(label);
                        if (j < 2) libroPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                    }
                    resultadosPanel.add(libroPanel);
                    resultadosPanel.add(Box.createRigidArea(new Dimension(0, 5)));
                }
            }
        }

        // Asegurar que se actualice la vista
        SwingUtilities.invokeLater(() -> {
            resultadosPanel.revalidate();
            resultadosPanel.repaint();
            // Scroll al inicio
            resultadosPanel.scrollRectToVisible(new Rectangle(0, 0, 1, 1));
        });
    }

    private void buscarLibros() {
        if (busquedaField.getText().trim().isEmpty()) {
            mostrarError("Por favor, introduce un término de búsqueda");
            return;
        }

        statusLabel.setText("Buscando...");
        resultadosArea.setText("Realizando búsqueda...\n");
        buscarButton.setEnabled(false);

        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 5000)) {
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                salida.println(tipoBusquedaCombo.getSelectedItem().toString());
                salida.println(busquedaField.getText());

                StringBuilder resultados = new StringBuilder();
                String respuesta;
                while ((respuesta = entrada.readLine()) != null && !respuesta.equals("FIN")) {
                    resultados.append(respuesta).append("\n");
                }

                SwingUtilities.invokeLater(() -> {
                    if (resultados.length() > 0) {
                        String resultadosStr = resultados.toString();
                        mostrarResultados(resultadosStr);
                        int numLibros = (resultadosStr.split("\n").length) / 3;
                        statusLabel.setText(String.format("Búsqueda completada - %d libros encontrados", numLibros));
                    } else {
                        mostrarResultados("");
                        statusLabel.setText("No se encontraron resultados");
                    }
                    buscarButton.setEnabled(true);
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    mostrarError("Error de conexión: " + e.getMessage());
                    buscarButton.setEnabled(true);
                });
            }
        }).start();
    }

    private void mostrarError(String mensaje) {
        JOptionPane.showMessageDialog(this, mensaje, "Error", JOptionPane.ERROR_MESSAGE);
        statusLabel.setText("Error: " + mensaje);
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        SwingUtilities.invokeLater(() -> {
            new ClienteGUI().setVisible(true);
        });
    }
}