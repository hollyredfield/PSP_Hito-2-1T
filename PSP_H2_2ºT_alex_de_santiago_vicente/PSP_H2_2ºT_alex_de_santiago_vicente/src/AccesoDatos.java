import java.io.*;
import java.util.*;

public class AccesoDatos {
    private List<Libro> libros;
    private static final String ARCHIVO = "libros.txt"; // Cambiado a ruta simple
    
    public AccesoDatos() {
        libros = new ArrayList<>();
        cargarLibros();
    }
    
    private void cargarLibros() {
        try {
            // Asegurarse de que el archivo existe en la ubicación correcta
            File archivo = new File(ARCHIVO);
            System.out.println("Intentando cargar libros desde: " + archivo.getAbsolutePath());
            
            // Si el archivo no existe, creamos los libros de ejemplo
            if (!archivo.exists()) {
                System.out.println("Archivo no encontrado. Creando archivo con datos de ejemplo...");
                // Crear el directorio si no existe
                archivo.getParentFile().mkdirs();
                libros.add(new Libro("Programación en Java", "Juan García", "java"));
                libros.add(new Libro("Python para Principiantes", "Ana López", "python"));
                libros.add(new Libro("Java Avanzado", "María Rodríguez", "java"));
                libros.add(new Libro("Python y Data Science", "Pedro Sánchez", "python"));
                libros.add(new Libro("Desarrollo Web con Java", "Laura Martínez", "java"));
                guardarLibros();
                System.out.println("Libros de ejemplo creados y guardados.");
            } else {
                System.out.println("Archivo encontrado, leyendo contenido...");
                try (BufferedReader br = new BufferedReader(new FileReader(archivo))) {
                    String linea;
                    while ((linea = br.readLine()) != null) {
                        System.out.println("Leyendo línea: " + linea);
                        String[] datos = linea.split(",");
                        if (datos.length == 3) {
                            libros.add(new Libro(datos[0], datos[1], datos[2]));
                        }
                    }
                }
            }
            System.out.println("Total de libros cargados: " + libros.size());
            // Imprimir todos los libros para verificar
            libros.forEach(libro -> System.out.println("Libro cargado: " + libro));
            
        } catch (Exception e) {
            System.err.println("Error crítico al cargar libros: " + e.getMessage());
            e.printStackTrace();
            // Asegurar que tengamos al menos algunos datos aunque falle el archivo
            if (libros.isEmpty()) {
                libros.add(new Libro("Error al cargar", "Sistema", "error"));
            }
        }
    }
    
    private void guardarLibros() {
        try (PrintWriter pw = new PrintWriter(new FileWriter(ARCHIVO))) {
            for (Libro libro : libros) {
                pw.println(libro.getTitulo() + "," + libro.getAutor() + "," + libro.getCategoria());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    
    /**
     * Busca libros por categoría
     */
    public List<Libro> buscarPorCategoria(String categoria) {
        if (categoria == null || categoria.trim().isEmpty()) {
            throw new IllegalArgumentException("La categoría no puede estar vacía");
        }
        return libros.stream()
            .filter(l -> l.getCategoria().toLowerCase().contains(categoria.toLowerCase()))
            .toList();
    }

    /**
     * Busca libros por autor
     */
    public List<Libro> buscarPorAutor(String autor) {
        if (autor == null || autor.trim().isEmpty()) {
            throw new IllegalArgumentException("El autor no puede estar vacío");
        }
        return libros.stream()
            .filter(l -> l.getAutor().toLowerCase().contains(autor.toLowerCase()))
            .toList();
    }

    /**
     * Busca libros por título
     */
    public List<Libro> buscarPorTitulo(String titulo) {
        if (titulo == null || titulo.trim().isEmpty()) {
            throw new IllegalArgumentException("El título no puede estar vacío");
        }
        return libros.stream()
            .filter(l -> l.getTitulo().toLowerCase().contains(titulo.toLowerCase()))
            .toList();
    }
}