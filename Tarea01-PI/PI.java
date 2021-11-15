import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class PI {
    static Object obj = new Object();
    static float pi = 0;

    static class Worker extends Thread {
        Socket conexion;

        Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            // Algoritmo 1
            try {
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                float suma = entrada.readFloat();
                System.out.println("Suma: " + suma);

                synchronized (obj) {
                    pi = suma + pi;
                }

                salida.close();
                entrada.close();
                conexion.close();
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }

    }

    public static void main(String[] args) {
        try {
            if (args.length != 1) {
                System.out.println("Uso:");
                System.out.println("java PI <nodo>");
                System.exit(0);
            }

            int nodo = Integer.valueOf(args[0]);
            if (nodo == 0) {
                // Algoritmo 2
                ServerSocket servidor = new ServerSocket(10000);
                Worker v[] = new Worker[4];

                for (int i = 0; i < 4; ++i) {
                    Socket conexion = servidor.accept();
                    v[i] = new Worker(conexion);
                    v[i].start();
                }

                for (int i = 0; i < 4; ++i) {
                    v[i].join();
                }

                System.out.println("PI: " + pi);
            } else {
                // Algoritmo 3
                Socket conexion = null;

                for (;;) {
                    try {
                        conexion = new Socket("localhost", 10000);
                        break;
                    } catch (Exception e) {
                        Thread.sleep(100);
                    }
                }

                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());

                float suma = 0;

                for (int i = 0; i < 1000000; ++i) {
                    suma += 4.0 / (8 * i + 2 * (nodo - 2) + 3);
                }
                suma = nodo % 2 == 0 ? -suma : suma;

                salida.writeFloat(suma);
                salida.close();
                entrada.close();
                conexion.close();
            }
        } catch (IOException e) {
            System.out.println("Error: " + e.getMessage());
        } catch (InterruptedException e) {
            System.out.println("Error: " + e.getMessage());
        }

    }
}
