/**
 * Cliente
 */
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Cliente {
    public static void main(String[] args) throws Exception {
        //Conectarse a desarrollosistemas.sytes.net
        Socket conexion = new Socket("localhost", 50000);
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());
        
        salida.writeInt(123);
        salida.writeDouble(123456789.0123);
        salida.write("HOLA".getBytes());

        byte[] buffer = new byte[4];
        read(entrada, buffer, 0, 4);
        
        System.out.println(new String(buffer, "UTF-8"));

        //Nueva clase
        ByteBuffer b = ByteBuffer.allocate(5*8);
        b.putDouble(1.1);
        b.putDouble(1.2);
        b.putDouble(1.3);
        b.putDouble(1.4);
        b.putDouble(1.5);
        byte[] a = b.array();

        salida.write(a);
        
        salida.close();
        entrada.close();
        conexion.close();   
    }
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while(longitud > 0){
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }
}
