import java.io.*;
import java.net.*;
import java.util.Scanner;

/**
 * Clase Cliente que implementa un cliente para un sistema de búsqueda de libros.
 * Me permite conectarme a un servidor y realizar búsquedas de libros por diferentes criterios.
 * 
 * En esta implementación:
 * - Me conecto a un servidor local en el puerto 5000
 * - Puedo buscar libros por categoría, autor o título
 * - Muestro un menú interactivo al usuario
 * - Manejo la comunicación con el servidor mediante sockets
 * - Muestro los resultados de las búsquedas de forma formateada
 * - Cuento el número total de libros encontrados
 * 
 * El flujo principal incluye:
 * 1. Establecer conexión con el servidor
 * 2. Mostrar menú de opciones
 * 3. Procesar la selección del usuario
 * 4. Enviar consultas al servidor
 * 5. Recibir y mostrar resultados
 * 6. Permitir múltiples búsquedas hasta que el usuario decida salir
 * 
 * La comunicación con el servidor la realizo a través de:
 * - PrintWriter para enviar datos
 * - BufferedReader para recibir respuestas
 * 
 * Manejo las siguientes excepciones:
 * - ConnectException: cuando no puedo conectar con el servidor
 * - IOException: para errores generales de comunicación
 * 
 * @author Alex de Santiago Vicente
 * @version 1.0
 */
public class Cliente {
    public static void main(String[] args) {
        System.out.println("=== CLIENTE DE BÚSQUEDA DE LIBROS ===");
        System.out.println("Conectando al servidor...");
        
        try (Socket socket = new Socket("localhost", 5000)) {
            PrintWriter salida = new PrintWriter(socket.getOutputStream(), true);
            BufferedReader entrada = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            Scanner scanner = new Scanner(System.in);
            
            System.out.println("Conexión establecida.");
            
            while (true) {
                System.out.println("\n=== MENÚ DE BÚSQUEDA ===");
                System.out.println("1. Buscar por Categoría");
                System.out.println("2. Buscar por Autor");
                System.out.println("3. Buscar por Título");
                System.out.println("4. Salir");
                System.out.print("\nSeleccione una opción (1-4): ");
                
                String opcion = scanner.nextLine();
                
                if (opcion.equals("4")) {
                    System.out.println("Gracias por usar el sistema de búsqueda. ¡Hasta pronto!");
                    break;
                }
                
                String tipoBusqueda;
                String prompt;
                
                switch (opcion) {
                    case "1":
                        tipoBusqueda = "categoria";
                        prompt = "Introduce la categoría (java, python, bases de datos, etc.): ";
                        break;
                    case "2":
                        tipoBusqueda = "autor";
                        prompt = "Introduce el nombre del autor: ";
                        break;
                    case "3":
                        tipoBusqueda = "titulo";
                        prompt = "Introduce el título del libro: ";
                        break;
                    default:
                        System.out.println("Opción no válida. Por favor, intente de nuevo.");
                        continue;
                }
                
                System.out.print("\n" + prompt);
                String termino = scanner.nextLine();
                
                // Enviar tipo de búsqueda y término
                salida.println(tipoBusqueda);
                salida.println(termino);
                
                System.out.println("\nBuscando...");
                System.out.println("------------------------");
                
                // Recibir y mostrar resultados
                String respuesta;
                boolean hayResultados = false;
                int contadorLibros = 0;
                
                while ((respuesta = entrada.readLine()) != null && !respuesta.equals("FIN")) {
                    if (!respuesta.startsWith("No se encontraron")) {
                        if (!hayResultados) {
                            System.out.println("Resultados encontrados:");
                            System.out.println("------------------------");
                        }
                        System.out.println(respuesta);
                        hayResultados = true;
                        if (respuesta.startsWith("Título:")) contadorLibros++;
                    } else {
                        System.out.println(respuesta);
                    }
                }
                
                if (hayResultados) {
                    System.out.println("\nTotal de libros encontrados: " + contadorLibros);
                }
                
                System.out.println("\nPresione ENTER para continuar...");
                scanner.nextLine();
            }
            
        } catch (ConnectException e) {
            System.out.println("Error: No se pudo conectar al servidor. Asegúrate de que el servidor esté en ejecución.");
        } catch (IOException e) {
            System.out.println("Error de comunicación: " + e.getMessage());
        }
    }
}