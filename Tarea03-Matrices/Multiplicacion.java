import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Multiplicacion {
    static int nodo;
    static String ip;
    long checksum = 0;
    static int N = 10;
    static Object obj = new Object();
    static long[][] A = new long[N][N];
    static long[][] B = new long[N][N];
    static long[][] C = new long[N][N];

    static void mostrarMatriz(long[][] M, int col, int fil) {
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < fil; j++) {
                System.out.print(M[i][j] + "\t ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    static long calcularChecksum(long[][] M, int N) {
        long aux = 0;
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                aux += M[i][j];
            }
        }
        return aux;
    }

    static long[][] recibirMatriz(DataInputStream entrada, int fil, int col, int inicioFil, int inicioCol)
            throws Exception {
        long[][] aux = new long[col][fil];

        for (int i = inicioCol; i < col; i++) {
            for (int j = inicioFil; j < fil; j++) {
                aux[i][j] = entrada.readLong();
            }
        }
        return aux;
    }

    static void enviarMatriz(DataOutputStream salida, long[][] M, int fil, int col) throws Exception {
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < fil; j++) {
                salida.writeLong(M[i][j]);
            }
        }

        salida.flush();
    }

    static long[][] dividirMatriz(int inicioCol, int col, int inicioFil, int fil, boolean Matriz) {
        long[][] aux = new long[N / 2][N];

        for (int i = inicioCol; i < col; i++) {
            for (int j = inicioFil; j < fil; j++) {
                if (Matriz) {
                    aux[i - inicioCol][j] = A[i][j];
                } else {
                    aux[i - inicioCol][j] = B[i][j];
                }
            }
        }

        return aux;
    }

    static void agregarFragmento(long[][] M, int inicioFil, int inicioCol, int fil, int col) {
        for (int i = inicioCol; i < col; i++) {
            for (int j = inicioFil; j < fil; j++) {
                C[i][j] = M[i][j];
            }
        }
    }

    static long[][] transponerMatriz(long[][] M, int N) {
        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                long aux = M[i][j];
                M[i][j] = M[j][i];
                M[j][i] = aux;
            }
        }

        return M;
    }

    static class Worker extends Thread {
        Socket conexion;

        public Worker(Socket conexion) {
            this.conexion = conexion;
        }

        public void run() {
            try {
                long[][] auxMA = new long[N / 2][N];
                long[][] auxMB = new long[N / 2][N];
                
                DataInputStream entrada = new DataInputStream(conexion.getInputStream());
                DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());
                int aux = entrada.readInt();
                System.out.println("Conexion de Nodo: " + aux);

                if (aux == 1) {
                    auxMA = dividirMatriz(0, N / 2, 0, N, true);
                    auxMB = dividirMatriz(0, N / 2, 0, N, false);
                } else if (aux == 2) {
                    auxMA = dividirMatriz(0, N / 2, 0, N, true);
                    auxMB = dividirMatriz(N / 2, N, 0, N, false);
                } else if (aux == 3) {
                    auxMA = dividirMatriz(N / 2, N, 0, N, true);
                    auxMB = dividirMatriz(0, N / 2, 0, N, false);
                } else if (aux == 4) {
                    auxMA = dividirMatriz(N / 2, N, 0, N, true);
                    auxMB = dividirMatriz(N / 2, N, 0, N, false);
                }

                enviarMatriz(salida, auxMA, N, N / 2);
                enviarMatriz(salida, auxMB, N, N / 2);

                synchronized (obj) {
                    long[][] matrizAux;
                    if (aux == 1) {
                        matrizAux = recibirMatriz(entrada, N / 2, N / 2, 0, 0);
                        agregarFragmento(matrizAux, 0, 0, N / 2, N / 2);
                    } else if (aux == 2) {
                        matrizAux = recibirMatriz(entrada, N, N / 2, N / 2, 0);
                        agregarFragmento(matrizAux, N / 2, 0, N, N / 2);
                    } else if (aux == 3) {
                        matrizAux = recibirMatriz(entrada, N / 2, N, 0, N / 2);
                        agregarFragmento(matrizAux, 0, N / 2, N / 2, N);
                    } else if (aux == 4) {
                        matrizAux = recibirMatriz(entrada, N, N, N / 2, N / 2);
                        agregarFragmento(matrizAux, N / 2, N / 2, N, N);
                    }
                }

                entrada.close();
                salida.close();
                conexion.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) throws Exception {
        if (args.length != 2) {
            System.err.println("Se debe pasar como parametros el numero del nodo y la IP");
            System.exit(1);
        }

        nodo = Integer.valueOf(args[0]);
        ip = args[1];

        if (nodo == 0) {
            // Inicializar matrices A, B, C
            for (int i = 0; i < N; i++) {
                for (int j = 0; j < N; j++) {
                    A[i][j] = i + 2 * j;
                    B[i][j] = i - 2 * j;
                    C[i][j] = 0;
                }
            }

            B = transponerMatriz(B, N);

            ServerSocket servidor = new ServerSocket(8080);
            Worker[] w = new Worker[4];

            for (int i = 0; i < 4; ++i) {
                Socket conexion = servidor.accept();
                w[i] = new Worker(conexion);
                w[i].start();
            }

            for (int i = 0; i < 4; ++i) {
                w[i].join();
            }

            servidor.close();

            System.out.println("Checksum: " + calcularChecksum(C, N));
            if (N == 10) {
                System.out.println("Matriz A");
                mostrarMatriz(A, N, N);
                System.out.println("Matriz B");
                B = transponerMatriz(B, N);
                mostrarMatriz(B, N, N);
                System.out.println("Matriz C");
                mostrarMatriz(C, N, N);
            }

        } else {
            Socket conexion = new Socket(ip, 8080);
            System.out.println("Conectado a IP: " + ip);

            DataInputStream entrada = new DataInputStream(conexion.getInputStream());
            DataOutputStream salida = new DataOutputStream(conexion.getOutputStream());

            salida.writeInt(nodo);

            // Recibir fragmentos
            long[][] auxMA = recibirMatriz(entrada, N, N / 2, 0, 0);
            long[][] auxMB = recibirMatriz(entrada, N, N / 2, 0, 0);
            long[][] auxMC = new long[N / 2][N / 2];

            // Multiplicar matriz
            for (int i = 0; i < (N / 2); i++) {
                for (int j = 0; j < (N / 2); j++) {
                    long suma = 0;
                    for (int k = 0; k < N; k++) {
                        suma += auxMA[i][k] * auxMB[j][k];
                        auxMC[i][j] = suma;
                    }
                }
            }

            // Regresar fragmento
            enviarMatriz(salida, auxMC, N / 2, N / 2);

            entrada.close();
            salida.close();
            conexion.close();
        }
    }
}
