
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocketFactory;

public class ServidorSSL {
    static class Worker extends Thread {
        Socket conexion;
        String ruta;

        Worker(Socket conexion, String ruta) {
            this.conexion = conexion;
            this.ruta = ruta;
        }

        public void run() {
            try {
                System.out.println("----Nueva conexion");

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                String nombre = entrada.readUTF();
                int tam = entrada.readInt();
                System.out.println("Comienza descarga del archivo " + nombre + " de " + tam + " bytes\n\n");

                byte[] b = new byte[tam];
                read(entrada, b, 0, tam);
                escribe_archivo(ruta + nombre, b);
                System.out.println("Archivo recibido..");

                salida.close();
                entrada.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    static void escribe_archivo(String archivo, byte[] buffer) throws Exception {
        FileOutputStream f = new FileOutputStream(archivo);
        try {
            f.write(buffer);
        } finally {
            f.close();
        }
    }

    public static void main(String[] args) throws Exception {
        // System.setProperty("javax.net.ssl.keyStore", "keystore_servidor.jks");
        // System.setProperty("javax.net.ssl.keyStore.Password", "1234567");

        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory)
        SSLServerSocketFactory.getDefault();
        ServerSocket servidor = socket_factory.createServerSocket(50000);
        // Socket conexion = servidor.accept();

        // ServerSocket servidor = new ServerSocket(50000);

        File f = new File("");
        String ruta = f.getAbsolutePath() + "/archivos/";
        File f2 = new File(ruta);
        f2.mkdirs();
        f2.setWritable(true);

        for (;;) {
            System.out.println("Servidor esperando por archivos..\n");

            Socket conexion = servidor.accept();
            Worker w = new Worker(conexion, ruta);
            w.start();
        }
    }

}