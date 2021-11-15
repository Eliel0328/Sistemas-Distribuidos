import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class reloj_log {
    static String[] hosts;
    static int[] puertos;
    static int num_nodos = 0;
    static int nodo = 0;

    static long reloj_logico = 0;
    static Object lock = new Object();

    static void envia_mensaje(long tiempo_logico, String host, int puerto) throws Exception {
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
        salida.writeLong(tiempo_logico);
        conexion.close();
    }

    static class Reloj extends Thread {
        public void run() {
            try {
                for (;;) {
                    synchronized (lock) {
                        System.out.println("Reloj logico: " + reloj_logico);
                        if (nodo == 0)
                            reloj_logico += 4;
                        else if (nodo == 1)
                            reloj_logico += 5;
                        else if (nodo == 2)
                            reloj_logico += 6;
                    }

                    Thread.sleep(10000);

                    // for (int i = 0; i < num_nodos; i++) {
                    // if (i != nodo)
                    // synchronized (lock) {
                    // envia_mensaje(reloj_logico, hosts[i], puertos[i]);
                    // }
                    // }

                }
            } catch (Exception e) {
                System.out.println("Reloj-Error:" + e.getMessage());
            }
        }
    }

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            // System.out.println("Inició el thread Worker");
            try {
                for (;;) {
                    DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                    long tiempo_recibido = entrada.readLong();
                    System.out.println("Tiempo recibido: " + tiempo_recibido);
                    if (tiempo_recibido > 0) {
                        synchronized (lock) {
                            if (tiempo_recibido >= reloj_logico) {
                                reloj_logico = tiempo_recibido + 1;
                                System.out.println("Tiempo logico actualizado(Lamport): " + reloj_logico);
                            }
                        }
                    }
                }
            } catch (Exception e) {
                // System.out.println("Worker-Error:" + e.getMessage());
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
                    // System.out.println("Conexion establecida");
                    Worker w = new Worker(conexion);
                    w.start();
                }
            } catch (Exception e) {
                // System.out.println("Servidor-Error:" + e.getMessage());
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 2) {
            System.out.println("Ingrese los parametros[ID_nodo, IP-nodos(separados por :)]");
            System.exit(0);
        }

        nodo = Integer.parseInt(args[0]);
        num_nodos = args.length - 1;

        hosts = new String[num_nodos];
        puertos = new int[num_nodos];

        for (int i = 0; i < num_nodos; i++) {
            hosts[i] = args[i + 1].split(":")[0];
            puertos[i] = Integer.parseInt(args[i + 1].split(":")[1]);
        }

        Servidor servidor = new Servidor();
        servidor.start();

        for (int i = 0; i < num_nodos; i++) {
            envia_mensaje(-1, hosts[i], puertos[i]);
        }

        Reloj reloj = new Reloj();
        reloj.start();
        servidor.join();
    }

}

// 
//  0 localhost:50000 localhost:50001 localhost:50002
//  1 localhost:50000 localhost:50001 localhost:50002
//  2 localhost:50000 localhost:50001 localhost:50002
// 