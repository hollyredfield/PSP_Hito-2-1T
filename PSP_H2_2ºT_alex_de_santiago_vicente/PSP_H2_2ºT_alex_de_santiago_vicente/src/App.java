/**
 * En esta clase principal, creo un servidor para la aplicación.
 */
public class App {
    /**
     * En el método main, que es el punto de entrada de la aplicación:
     * - Creo una nueva instancia del servidor en el puerto 5000
     * - Inicio el servidor para que comience a escuchar conexiones
     */
    public static void main(String[] args) {
        Servidor servidor = new Servidor(5000); // Creo un nuevo servidor en el puerto 5000
        servidor.iniciar(); // Inicio el servidor para que empiece a funcionar
    }
}
