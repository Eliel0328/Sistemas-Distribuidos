import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

import javax.net.ssl.SSLServerSocketFactory;
import javax.net.ssl.SSLSocketFactory;

/* Clientes con sockets seguros */

public class Cliente {
    public static void main(String[] args) {
        try {
            SSLSocketFactory cliente = (SSLSocketFactory) SSLSocketFactory.getDefault();
            Socket conexion = cliente.createSocket("localhost", 50000);

            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());

            salida.writeDouble(123454.541);
            
            salida.close();
            entrada.close();
            conexion.close();
        } catch (UnknownHostException e) {
            System.out.println("UnknownHostException: " + e.getMessage());
        } catch (IOException e) {
            System.out.println("IOException: " + e.getMessage());
        }
    }
}
