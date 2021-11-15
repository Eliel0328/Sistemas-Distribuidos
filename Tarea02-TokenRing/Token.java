import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocket;
import javax.net.ssl.SSLSocketFactory;

public class Token {
    static DataInputStream entrada;
    static DataOutputStream salida;
    static boolean inicio = true;
    static String ip;
    static int nodo;
    static long token;

    static class Worker extends Thread {
        public void run() {
            // Algoritmo 1
            try {
                System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
                System.setProperty("javax.net.ssl.keyStorePassword", "1234567");

                System.out.println("IP: " + ip);
                System.out.println("Nodo: " + nodo);

                SSLServerSocketFactory serverFactory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
                ServerSocket servidor = serverFactory.createServerSocket(20000 + nodo);
                SSLSocket conexion = (SSLSocket) servidor.accept();

                // ServerSocket servidor = new ServerSocket(20000 + nodo);
                // Socket conexion = servidor.accept();

                entrada = new DataInputStream(conexion.getInputStream());

            } catch (Exception e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        System.setProperty("javax.net.ssl.trustStore", "keystore_cliente.jks");
        System.setProperty("javax.net.ssl.trustStorePassword", "123456");

        if (args.length != 2) {
            System.err.println(
                    "Se debe pasar como parametros el numero del nodo y la IP del siguiente nodo en el anillo");
            System.exit(1);
        }

        nodo = Integer.valueOf(args[0]);
        ip = args[1];

        // Algoritmo 2
        Worker w = new Worker();
        w.start();

        Socket conexion = null;

        for (;;) {
            try {
                SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
                conexion = (SSLSocket) cliente.createSocket(ip, 20000 + (nodo + 1) % 4);
                // conexion = new Socket(ip, 20000 + (nodo + 1) % 4);
                System.out.println("IP: " + ip);
                break;
            } catch (Exception e) {
                Thread.sleep(500);          
            }
        }

        salida = new DataOutputStream(conexion.getOutputStream());
        w.join();

        for (;;) {
            if (nodo == 0) {
                if (inicio) {
                    inicio = false;
                    token = 1;
                } else {
                    token = entrada.readLong();
                    ++token;
                    System.out.println("Nodo: " + nodo);
                    System.out.println("Token: " + token);
                }
            } else {
                token = entrada.readLong();
                ++token;
                System.out.println("Nodo: " + nodo);
                System.out.println("Token: " + token);
            }

            if (nodo == 0 && token >= 1000) {
                break;
            }
            salida.writeLong(token);
        }

    }

}
