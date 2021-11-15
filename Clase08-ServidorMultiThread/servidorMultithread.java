import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class servidorMultithread {
    static String[] hosts;
    static int[] puertos;
    static int num_nodos = 0;
    static int nodo = 0;

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            System.out.println("Inició el thread Worker");  
        }
    }

    static class Servidor extends Thread {
        public void run() {
            Socket conexion = null;
            
            try {
                ServerSocket servidor = new ServerSocket(puertos[nodo]);
                for (;;) {
                    conexion = servidor.accept();
                    Worker w = new Worker(conexion);
                    w.start();
                }
            } catch (IOException e) {
                System.out.println(e.getMessage());
            }
        }
    }

    public static void main(String[] args) {
        if (args.length < 2) {
            System.out.println("Ingrese los parametros[ID_nodo, IP-nodos(separados por :)]");
            System.exit(0);
        }

        nodo = Integer.valueOf(args[0]);
        num_nodos = args.length - 1;

        hosts = new String[num_nodos];
        puertos = new int[num_nodos];

        for (int i = 0; i < num_nodos; ++i) {
            hosts[i] = args[i + 1].split(":")[0];
            puertos[i] = Integer.valueOf(args[i + 1].split(":")[1]);
        }

        new Servidor().start();
    }
}
// 
//  0 localhost:50000 localhost:50001 localhost:50002
//  1 localhost:50000 localhost:50001 localhost:50002
//  2 localhost:50000 localhost:50001 localhost:50002
// 