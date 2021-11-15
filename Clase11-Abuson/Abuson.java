import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class Abuson {
    static String[] hosts;
    static int[] puertos;
    static int num_nodos = 0;
    static int nodo = 0;

    static int coordinador_actual = 0;

    static void eleccion(int nodo) {
        ArrayList<Integer> nodos = new ArrayList<Integer>();
        for (int i = nodo + 1; i < num_nodos; ++i) {
            String mensaje = envia_mensaje_eleccion(hosts[i], puertos[i]);
            if (mensaje.equals("OK")) {
                nodos.add(i);
            }
        }

        if (nodos.isEmpty()) {
            for (int i = 0; i < num_nodos; ++i) {
                enviar_mensaje_coordinador(hosts[i], puertos[i]);
            }
        }

    }

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

    static String envia_mensaje_eleccion(String host, int puerto) {
        Socket conexion = null;
        try {
            conexion = new Socket(host, puerto);

            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeUTF("ELECCION");
            String aux = entrada.readUTF();
            conexion.close();

            return aux;
        } catch (Exception e) {
            // System.out.println("enviar_mensaje_eleccion-Error:" + e.getMessage());
            System.out.println("Mensaje Eleccion - Sin conexion al: " + host + ":" + puerto);
            return "";
        }
    }

    static void enviar_mensaje_coordinador(String host, int puerto) {
        Socket conexion = null;
        try {
            conexion = new Socket(host, puerto);

            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
            salida.writeUTF("COORDINADOR");
            salida.writeInt(nodo);
            conexion.close();

        } catch (Exception e) {
            // System.out.println("enviar_mensaje_coordinador-Error:" + e.getMessage());
            System.out.println("Mensaje Coordinador - Sin conexion al: " + host + ":" + puerto);
        }
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
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

                String comando = entrada.readUTF();
                System.out.println("Comando: " + comando);

                if (!comando.equals("BARRERA")) {
                    if (comando.equals("ELECCION")) {
                        salida.writeUTF("OK");
                        eleccion(nodo);

                    } else if (comando.equals("COORDINADOR")) {
                        coordinador_actual = entrada.readInt();
                        System.out.println("Coordinador recibido:" + coordinador_actual);
                    }
                }
                
                conexion.close();
            } catch (Exception e) {
                System.out.println("Worker: " + e.getMessage());
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

    public static void main(String[] args) throws Exception {
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
        if (nodo == 7) {
            System.exit(0);
        } else if (nodo == 4) {
            Thread.sleep(3000);
            eleccion(4);
        }

        Thread.sleep(5000);
        servidor.join();
    }
}

// PS1='\W$ '
// java Abuson 0 localhost:50000 localhost:50001 localhost:50002 localhost:50003 localhost:50004 localhost:50005 localhost:50006 localhost:50007
// java Abuson 1 localhost:50000 localhost:50001 localhost:50002 localhost:50003 localhost:50004 localhost:50005 localhost:50006 localhost:50007
// java Abuson 2 localhost:50000 localhost:50001 localhost:50002 localhost:50003 localhost:50004 localhost:50005 localhost:50006 localhost:50007
// java Abuson 3 localhost:50000 localhost:50001 localhost:50002 localhost:50003 localhost:50004 localhost:50005 localhost:50006 localhost:50007
// java Abuson 4 localhost:50000 localhost:50001 localhost:50002 localhost:50003 localhost:50004 localhost:50005 localhost:50006 localhost:50007
// java Abuson 5 localhost:50000 localhost:50001 localhost:50002 localhost:50003 localhost:50004 localhost:50005 localhost:50006 localhost:50007
// java Abuson 6 localhost:50000 localhost:50001 localhost:50002 localhost:50003 localhost:50004 localhost:50005 localhost:50006 localhost:50007
// java Abuson 7 localhost:50000 localhost:50001 localhost:50002 localhost:50003 localhost:50004 localhost:50005 localhost:50006 localhost:50007
