import java.io.DataInputStream  ;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class exclusionToken {
    static String[] hosts;
    static int[] puertos;
    static int num_nodos = 0;
    static int nodo = 0;

    static void envia_mensaje(String host, int puerto) throws Exception {
        Socket conexion = null;
        for (;;) {
            try {
                conexion = new Socket(host, puerto);
                break;
            } catch (Exception e) {
                // System.out.println("EnviaMensaje-Error:" + e.getMessage());
            }
        }
        System.out.println("Barrera - Conexion al: " + host + ":" + puerto);
        DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
        salida.writeUTF("BARRERA");
        conexion.close();
    }

    static void envia_token(int token, String host, int puerto) {
        Socket conexion = null;
        
        try {
            conexion = new Socket(host, puerto);
            System.out.println("Envia Token a: " + host + ":" + puerto);
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeUTF("TOKEN");
            salida.writeInt(token);
            
            conexion.close();
        } catch (Exception e) {
            System.out.println("Envia token - Error: " + e.getMessage());
        }
    }

    static void exclusion_mutua(){

    }

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                System.out.println("----Worker----");
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                
                String comando = entrada.readUTF();
                System.out.println("Comando: " + comando);
                
                if(!comando.equals("BARRERA")){
                    if(comando.equals("TOKEN")){
                        int token = entrada.readInt();
                        System.out.println("Token: " + token);
        
                        int sig_nodo = (nodo + 1) % num_nodos;
                        envia_token(token, hosts[sig_nodo], puertos[sig_nodo]);
                    }
                }

                conexion.close();
            } catch (Exception e) {
                System.out.println("Worker-Error: " + e.getMessage());
            }
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

        Servidor servidor = new Servidor();
        servidor.start();

        for (int i = 0; i < num_nodos; i++) {
            if (i != nodo)
                envia_mensaje(hosts[i], puertos[i]);
        }

        Thread.sleep(3000);
        //Adquirir el bloqueo
        //Desplegar un letrero que indique que el nodo adaquirio el bloque
        Thread.sleep(3000);
        //Desbloquear
        


        servidor.join();
    }
}
