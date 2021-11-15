import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.Scanner;

public class Chat {
    static void envia_mensaje_multicast(byte[] buffer, String ip, int puerto) throws IOException {
        DatagramSocket socket = new DatagramSocket();
        socket.send(new DatagramPacket(buffer, buffer.length, InetAddress.getByName(ip), puerto));
        socket.close();
    }

    static byte[] recibe_mensaje_multicast(MulticastSocket socket, int longitud_mensaje) throws IOException {
        byte[] buffer = new byte[longitud_mensaje];
        DatagramPacket paquete = new DatagramPacket(buffer, buffer.length);
        socket.receive(paquete);
        return paquete.getData();
    }

    static class Worker extends Thread {
        public void run() {
            // En un ciclo infinito se recibirán los mensajes enviados al
            // grupo 230.0.0.0 a través del puerto 10000 y se desplegarán en la pantalla.
            int longitud_mensaje = 1000;
            for (;;) {
                try {
                    InetAddress ip_grupo = InetAddress.getByName("230.0.0.0");
                    MulticastSocket socket = new MulticastSocket(10000);
                    socket.joinGroup(ip_grupo);
                    byte[] b = recibe_mensaje_multicast(socket, longitud_mensaje);
                    String mensaje = new String(b, "IBM850");
                    System.out.println(mensaje);
                } catch (IOException e) {
                    System.out.println("Error: " + e.getMessage());
                }
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 1) {
            System.err.println("Se debe pasar como parametro el nombre de usuario");
            System.exit(1);
        }
        new Worker().start();

        String nombre = args[0];
        // En un ciclo infinito se leerá cada mensaje del teclado y se enviará al
        // grupo 230.0.0.0 a través del puerto 10000.
        InetAddress ip_grupo = InetAddress.getByName("230.0.0.0");
        MulticastSocket socket = new MulticastSocket(10000);
        socket.joinGroup(ip_grupo);

        System.out.println("Ingrese mensaje a enviar:");
        Scanner myObj = new Scanner(System.in, "IBM850");
        for (;;) {

            String mensaje_enviar = myObj.nextLine();
            String mensaje = nombre + ": " + mensaje_enviar;
            envia_mensaje_multicast(mensaje.getBytes(), "230.0.0.0", 10000);
        }
    }

}
