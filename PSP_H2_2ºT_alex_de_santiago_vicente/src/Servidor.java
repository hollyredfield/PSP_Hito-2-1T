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
            System.out.println("=== SERVIDOR DE LIBROS ===");
            System.out.println("Servidor iniciado en puerto " + puerto);
            System.out.println("Esperando conexiones de clientes...");
            
            while (true) {
                Socket clienteSocket = serverSocket.accept();
                System.out.println("Nuevo cliente conectado desde: " + clienteSocket.getInetAddress());
                new Thread(new ManejadorCliente(clienteSocket, accesoDatos)).start();
            }
        } catch (IOException e) {
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
            BufferedReader entrada = new BufferedReader(new InputStreamReader(clienteSocket.getInputStream()));
            PrintWriter salida = new PrintWriter(clienteSocket.getOutputStream(), true)
        ) {
            String tipoBusqueda = entrada.readLine();
            String termino = entrada.readLine();
            
            System.out.println("Recibida búsqueda - Tipo: " + tipoBusqueda + ", Término: " + termino);
            
            List<Libro> libros = switch (tipoBusqueda.toLowerCase()) {
                case "categoria", "categoría" -> accesoDatos.buscarPorCategoria(termino);
                case "autor" -> accesoDatos.buscarPorAutor(termino);
                case "titulo", "título" -> accesoDatos.buscarPorTitulo(termino);
                default -> {
                    System.out.println("Tipo de búsqueda no válido: " + tipoBusqueda);
                    salida.println("Tipo de búsqueda no válido");
                    salida.println("FIN");
                    yield null;
                }
            };

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
            System.err.println("Error en ManejadorCliente: ");
            e.printStackTrace();
        } finally {
            try {
                clienteSocket.close();
            } catch (IOException e) {
                System.err.println("Error al cerrar conexión: " + e.getMessage());
            }
        }
    }
}