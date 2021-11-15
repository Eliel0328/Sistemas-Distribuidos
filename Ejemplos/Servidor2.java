package Ejemplos;

import java.net.ServerSocket;
import java.net.Socket;
import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;

class Servidor2{
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

        long time1 = System.currentTimeMillis();

        /*
        //Recibe 1 a 1
        for(int i=0; i<10000; i++)
           entrada.readDouble();
        */

        byte[] a = new byte[10000*8];
        read(entrada,a,0,10000*8);
        ByteBuffer b = ByteBuffer.wrap(a);
        
        long time2 = System.currentTimeMillis();
        long res = time2 - time1;

       // System.out.println("Tiempo de recibo 1 a 1: "+res+"ms");
       System.out.println("Tiempo de recibo con ByteBuffer: "+res+"ms");
        
        //Cerramos 
        salida.close();
        entrada.close();
        conexion.close();
    }
}