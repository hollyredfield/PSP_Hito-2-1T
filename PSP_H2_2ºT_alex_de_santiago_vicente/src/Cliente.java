import java.io.*;
import java.net.*;
import java.util.Scanner;

public class Cliente {
    public static void main(String[] args) {
        System.out.println("=== CLIENTE DE BÚSQUEDA DE LIBROS ===");
        System.out.println("Conectando al servidor...");
        
        try (Socket socket = new Socket("localhost", 5000)) {
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Conexión establecida.");
            
            // Enviar tipo de búsqueda fijo como "categoría" para mantener compatibilidad
            salida.println("categoria"); // Sin tilde para evitar problemas de codificación
            
            System.out.println("\nIntroduce la categoría a buscar (java, python, etc.): ");
            String categoria = scanner.nextLine();
            
            // Enviar el término de búsqueda
            salida.println(categoria);
            System.out.println("Búsqueda enviada: " + categoria);
            
            System.out.println("\nResultados encontrados:");
            System.out.println("------------------------");
            String respuesta;
            boolean hayResultados = false;
            while ((respuesta = entrada.readLine()) != null && !respuesta.equals("FIN")) {
                System.out.println(respuesta);
                hayResultados = true;
            }
            
            if (!hayResultados) {
                System.out.println("No se encontraron libros en esa categoría.");
            }
            
        } catch (ConnectException e) {
            System.out.println("Error: No se pudo conectar al servidor. Asegúrate de que el servidor esté en ejecución.");
        } catch (IOException e) {
            System.out.println("Error de comunicación: " + e.getMessage());
        }
    }
}