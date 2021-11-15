
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Servidor {

    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    public static void main(String[] args) throws Exception {
        ServerSocket servidor = new ServerSocket(50000);
        Socket conexion = servidor.accept();

        // DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        byte[] a = new byte[100 * 4];
        read(entrada, a, 0, 100 * 4);

        ByteBuffer b = ByteBuffer.wrap(a);
        int aux = 0;
        for (int i = 0; i < 100; i++){
            int aux1 = b.getInt();
            System.out.println(aux1);
            aux += aux1;
        }
        System.out.println(aux);


        // salida.close();
        entrada.close();
        conexion.close();
        servidor.close();
    }

}