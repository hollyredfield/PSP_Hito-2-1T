import java.awt.*;
import java.io.*;
import java.net.*;
import javax.swing.*;

public class ClienteGUI extends JFrame {
    private JTextField busquedaField;
    private JComboBox<String> tipoBusquedaCombo;
    private JTextArea resultadosArea;
    private JButton buscarButton;

    public ClienteGUI() {
        setTitle("Biblioteca Virtual - Búsqueda de Libros");
        setSize(400, 300);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);

        // Panel principal
        JPanel panel = new JPanel();
        panel.setLayout(new BorderLayout(10, 10));
        panel.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        // Panel superior para búsqueda
        JPanel busquedaPanel = new JPanel();
        busquedaPanel.setLayout(new FlowLayout());
        
        tipoBusquedaCombo = new JComboBox<>(new String[]{"Categoría", "Autor", "Título"});
        busquedaField = new JTextField(20);
        buscarButton = new JButton("Buscar");
        busquedaPanel.add(tipoBusquedaCombo);
        busquedaPanel.add(busquedaField);
        busquedaPanel.add(buscarButton);

        // Área de resultados
        resultadosArea = new JTextArea();
        resultadosArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultadosArea);

        panel.add(busquedaPanel, BorderLayout.NORTH);
        panel.add(scrollPane, BorderLayout.CENTER);

        add(panel);

        // Acción del botón
        buscarButton.addActionListener(e -> buscarLibros());
    }

    private void buscarLibros() {
        if (busquedaField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, 
                "Por favor, introduce un término de búsqueda", 
                "Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        resultadosArea.setText("Buscando...\n");
        
        new Thread(() -> {
            try (Socket socket = new Socket("localhost", 5000)) {
                PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
                BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));

                // Enviar tipo de búsqueda y término
                salida.println(tipoBusquedaCombo.getSelectedItem().toString());
                salida.println(busquedaField.getText());

                StringBuilder resultados = new StringBuilder();
                String respuesta;
                while ((respuesta = entrada.readLine()) != null && !respuesta.equals("FIN")) {
                    resultados.append(respuesta).append("\n");
                }

                SwingUtilities.invokeLater(() -> {
                    if (resultados.length() > 0) {
                        resultadosArea.setText(resultados.toString());
                    } else {
                        resultadosArea.setText("No se encontraron libros en esa categoría.");
                    }
                });

            } catch (Exception e) {
                SwingUtilities.invokeLater(() -> {
                    resultadosArea.setText("Error: " + e.getMessage());
                });
            }
        }).start();
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            new ClienteGUI().setVisible(true);
        });
    }
}