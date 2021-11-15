package Ejemplos;

import java.io.DataOutputStream;
import java.io.DataInputStream;
import java.nio.ByteBuffer;
import java.net.Socket;
import java.io.IOException;

class Cliente2 {
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
        Socket conexion = new Socket("localhost",50000);
        //Escribir
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        //Leer
        DataInputStream entrada = new DataInputStream(conexion.getInputStream());

        long time1 = System.currentTimeMillis();
        
        /*
        //Mandar datos al servidor 1 a 1
        for (int i=0;i<10000 ;i++ ) {
			salida.writeDouble(i+1);
		}*/
    
        ByteBuffer b = ByteBuffer.allocate(10000*8);
        
        for (int i=0;i<10000;i++ ) {
			b.putDouble(i+1);
		}

        byte[] a = b.array();
        salida.write(a);

        long time2 = System.currentTimeMillis();
        long res = time2 - time1;

       //System.out.println("Tiempo de envio 1 a 1: "+res+"ms");
       System.out.println("Tiempo de envio con ByteBuffer: "+res+"ms");

        //Cerrar
        salida.close();
        entrada.close();
        conexion.close();
    }
}