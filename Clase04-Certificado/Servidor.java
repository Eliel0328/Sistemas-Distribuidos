
import java.io.DataInputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.net.ssl.SSLServerSocketFactory;

public class Servidor {
    public static void main(String[] args) throws Exception {

        SSLServerSocketFactory socket_factory = (SSLServerSocketFactory) SSLServerSocketFactory.getDefault();
        ServerSocket servidor =  socket_factory.createServerSocket(50000);
        Socket conexion = servidor.accept();

        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        double x = entrada.readDouble();
        System.out.println("Entrada: " + x);

        entrada.close();
        conexion.close();
        servidor.close();
    }

}