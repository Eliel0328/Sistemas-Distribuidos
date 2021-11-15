import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.Socket;
import java.net.UnknownHostException;

public class Prueba {
    public static void main(String[] args) throws UnknownHostException, IOException {
        Socket conexion = new Socket("sisdis.sytes.net", 20010);

        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        salida.writeDouble(64);
        salida.writeInt(33);
        salida.writeDouble(94);
        salida.writeInt(69);

        double aux = entrada.readDouble();

        System.out.println(aux);

        salida.close();
        entrada.close();
        conexion.close();

    }
}
