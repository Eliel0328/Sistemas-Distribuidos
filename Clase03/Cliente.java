import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;
import java.nio.ByteBuffer;

public class Cliente {
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception {
        while (longitud > 0) {
            int n = f.read(b, posicion, longitud);
            posicion += n;
            longitud -= n;
        }
    }

    public static void main(String[] args) {
        try {
            Socket conexion = new Socket("localhost", 50000);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            salida.writeInt(123);
            salida.writeDouble(125115.11515);
            salida.write("Hola".getBytes());

            byte[] buffer = new byte[4];
            // entrada.read(buffer, 0, 4);
            read(entrada, buffer, 0, 4);
            System.out.println(new String(buffer, "UTF-8"));

            ByteBuffer b = ByteBuffer.allocate(5 * 8);
            for(int i = 0; i < 5; ++i){
                b.putDouble(i);
            }
            
            byte[] a = b.array();
            salida.write(a);

            salida.close();
            entrada.close();
            conexion.close();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
