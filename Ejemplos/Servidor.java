package Ejemplos;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

class Servidor{
    //Garantizar recibir el mensaje completo
    static void read(DataInputStream f, byte[] b, int posicion, int longitud) throws Exception
    {
        while(longitud>0){
            int n = f.read(b,posicion,longitud);
            posicion += n;
            longitud -= n;
        }
    }

    public static void main(String[] args) throws Exception
    {
        ServerSocket servidor = new ServerSocket(50000);
		//Deja al servidor esperando a que le llegue conexion.
		Socket conexion = servidor.accept();
        //Al regresar guarda datos en una variable  tipo socket
            //Escribir
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            //Leer
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        int n = entrada.readInt();
        System.out.println(n);

        double x = entrada.readDouble();
        System.out.println(x);

        byte[] buffer = new byte[4];
        read(entrada,buffer,0,4);
        System.out.println(new String(buffer,"UTF-8"));

        salida.write("HOLA".getBytes());

        byte[] a = new byte[5*8];
        read(entrada,a,0,5*8);

        ByteBuffer b = ByteBuffer.wrap(a);

        //Cerramos 
        salida.close();
        entrada.close();
        conexion.close();
    }
}