
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;
import javax.swing.JFileChooser;

/* Clientes con sockets seguros */

public class ClienteSSL {
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    static byte[] lee_archivo(String archivo) throws Exception{
        FileInputStream f = new FileInputStream(archivo);
        byte [] buffer;
        try{
            buffer = new byte[f.available()];
            f.read(buffer);
        }finally{
            f.close();
        }
        return buffer;
    }



    public static void main(String[] args) {
        try {
            SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket conexion = cliente.createSocket("localhost", 50000);
            // Socket conexion = new Socket("localhost", 50000);

            JFileChooser jf = new JFileChooser();
            int r = jf.showOpenDialog(null);
            if (r == JFileChooser.APPROVE_OPTION) {
                File f = jf.getSelectedFile();
                String nombre = f.getName();
                String path = f.getAbsolutePath();
                int tam = (int) f.length();
                System.out.println("Preparandose pare enviar archivo " + path + " de " + tam + " bytes\n\n");

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                
                salida.writeUTF(nombre);
                salida.flush();
                salida.writeInt(tam);
                salida.flush();
                byte[] b = lee_archivo(path);
                salida.write(b);

                
                System.out.println("\nArchivo enviado..");
                Thread.sleep(1000);
                salida.close();
                entrada.close();
            } 

            conexion.close();
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
