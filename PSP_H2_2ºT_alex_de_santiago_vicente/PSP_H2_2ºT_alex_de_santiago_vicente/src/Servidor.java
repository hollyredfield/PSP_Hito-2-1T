import java.io.*;
import java.net.*;
import java.util.List;

public class Servidor {
    private final int puerto;
    private final AccesoDatos accesoDatos;
    
    public Servidor(int puerto) {
        this.puerto = puerto;
        this.accesoDatos = new AccesoDatos();
    }
    
    public void iniciar() {
        try (ServerSocket serverSocket = new ServerSocket(puerto)) {
            // Inicio el servidor y muestro mensajes en la consola
            System.out.println("=== SERVIDOR DE LIBROS ===");
            System.out.println("Servidor iniciado en puerto " + puerto);
            System.out.println("Esperando conexiones de clientes...");
            
            // Bucle infinito para aceptar conexiones de clientes
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado desde: " + clienteSocket.getInetAddress());
                // Creo un nuevo hilo para manejar la conexión del cliente
                new Thread(new ManejadorCliente(clienteSocket, accesoDatos)).start();
            }
        } catch (IOException e) {
            // Capturo y muestro cualquier error que ocurra en el servidor
            System.out.println("Error en el servidor: " + e.getMessage());
        }
    }
}

class ManejadorCliente implements Runnable {
    private final Socket clienteSocket;
    private final AccesoDatos accesoDatos;
    
    public ManejadorCliente(Socket socket, AccesoDatos accesoDatos) {
        this.clienteSocket = socket;
        this.accesoDatos = accesoDatos;
    }
    
    @Override
    public void run() {
        try (
            // Creo los streams de entrada y salida para la comunicación con el cliente
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true)
        ) {
            // Leo el tipo de búsqueda y el término de búsqueda enviados por el cliente
            String tipoBusqueda = entrada.readLine();
            String termino = entrada.readLine();
            
            System.out.println("Recibida búsqueda - Tipo: " + tipoBusqueda + ", Término: " + termino);
            
            // Realizo la búsqueda de libros según el tipo de búsqueda
            List<Libro> libros = switch (tipoBusqueda.toLowerCase()) {
                case "categoria", "categoría" -> accesoDatos.buscarPorCategoria(termino);
                case "autor" -> accesoDatos.buscarPorAutor(termino);
                case "titulo", "título" -> accesoDatos.buscarPorTitulo(termino);
                default -> {
                    // Si el tipo de búsqueda no es válido, informo al cliente
                    System.out.println("Tipo de búsqueda no válido: " + tipoBusqueda);
                    salida.println("Tipo de búsqueda no válido");
                    salida.println("FIN");
                    yield null;
                }
            };

            // Envío los resultados de la búsqueda al cliente
            if (libros == null || libros.isEmpty()) {
                System.out.println("No se encontraron libros para: " + termino);
                salida.println("No se encontraron libros para: " + termino);
            } else {
                System.out.println("Enviando " + libros.size() + " libros al cliente");
                for (Libro libro : libros) {
                    salida.println(libro.toString());
                }
            }
            salida.println("FIN");
            
        } catch (Exception e) {
            // Capturo y muestro cualquier error que ocurra en el manejo del cliente
            System.err.println("Error en ManejadorCliente: ");
            e.printStackTrace();
        } finally {
            try {
                // Cierro la conexión con el cliente
                clienteSocket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}