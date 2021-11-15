import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.Socket;
import java.nio.ByteBuffer;

public class Cliente {
    public static void main(String[] args) throws Exception {
        Socket conexion = new Socket("localhost", 50000);
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        ByteBuffer b = ByteBuffer.allocate(100 * 4);
        for(int i = 0; i < 200; ++i){
            if(i % 2 != 0){
                b.putInt(i);
            }
        }
        
        byte[] a = b.array();
        salida.write(a);

        salida.close();
        entrada.close();
        conexion.close();
    }
}
