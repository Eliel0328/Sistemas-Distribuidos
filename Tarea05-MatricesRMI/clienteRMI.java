import java.rmi.Naming;

public class clienteRMI {
    static int N = 9;

    static double[][] separa_matriz(double[][] A, int inicio) {
        double[][] M = new double[N / 3][N];
        for (int i = 0; i < N / 3; i++) {
            for (int j = 0; j < N; j++) {
                M[i][j] = A[i + inicio][j];
            }
        }

        return M;
    }

    static void mostrarMatriz(double[][] M, int col, int fil) {
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < fil; j++) {
                System.out.print(M[i][j] + "\t ");
            }
            System.out.println("");
        }
        System.out.println("");
    }

    static void acomoda_matriz(double[][] C, double[][] c, int renglon, int columna) {
        for (int i = 0; i < N / 3; i++) {
            for (int j = 0; j < N / 3; j++) {
                C[i + renglon][j + columna] = c[i][j];
            }
        }
    }

    public static void main(String[] args) throws Exception {
        // String url = "rmi://localhost/prueba";
        // interfaceRMI r = (interfaceRMI) Naming.lookup(url);

        String url1 = "rmi://10.0.0.6/prueba";
        interfaceRMI r1 = (interfaceRMI) Naming.lookup(url1);

        String url2 = "rmi://10.0.0.7/prueba";
        interfaceRMI r2 = (interfaceRMI) Naming.lookup(url2);

        String url3 = "rmi://10.0.0.9/prueba";
        interfaceRMI r3 = (interfaceRMI) Naming.lookup(url3);

        double[][] A = new double[N][N];
        double[][] B = new double[N][N];
        double[][] C = new double[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 4 * j;
                B[i][j] = i - 4 * j;
            }
        }

        if (N == 9) {
            System.out.println("Matriz A:");
            mostrarMatriz(A, N, N);
            System.out.println("Matriz B:");
            mostrarMatriz(B, N, N);
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                double x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }

        double[][] A1 = separa_matriz(A, 0);
        double[][] A2 = separa_matriz(A, N / 3);
        double[][] A3 = separa_matriz(A, 2 * (N / 3));

        double[][] B1 = separa_matriz(B, 0);
        double[][] B2 = separa_matriz(B, N / 3);
        double[][] B3 = separa_matriz(B, 2 * (N / 3));

        double[][] C1 = r1.multiplica_matrices(A1, B1, N);
        double[][] C2 = r1.multiplica_matrices(A1, B2, N);
        double[][] C3 = r1.multiplica_matrices(A1, B3, N);

        double[][] C4 = r2.multiplica_matrices(A2, B1, N);
        double[][] C5 = r2.multiplica_matrices(A2, B2, N);
        double[][] C6 = r2.multiplica_matrices(A2, B3, N);

        double[][] C7 = r3.multiplica_matrices(A3, B1, N);
        double[][] C8 = r3.multiplica_matrices(A3, B2, N);
        double[][] C9 = r3.multiplica_matrices(A3, B3, N);

        acomoda_matriz(C, C1, 0, 0);
        acomoda_matriz(C, C2, 0, N / 3);
        acomoda_matriz(C, C3, 0, 2 * (N / 3));

        acomoda_matriz(C, C4, N / 3, 0);
        acomoda_matriz(C, C5, N / 3, N / 3);
        acomoda_matriz(C, C6, N / 3, 2 * (N / 3));

        acomoda_matriz(C, C7, 2 * (N / 3), 0);
        acomoda_matriz(C, C8, 2 * (N / 3), N / 3);
        acomoda_matriz(C, C9, 2 * (N / 3), 2 * (N / 3));

        if (N == 9) {
            System.out.println("Matriz C:");
            mostrarMatriz(C, N, N);
        }
        
        double checksum = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }

        System.out.println("checksum: " + checksum);
    }
}
