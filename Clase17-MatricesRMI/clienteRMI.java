import java.rmi.Naming;

public class clienteRMI {
    static int N = 6;
    static int[][] separa_matriz(int[][] A, int inicio) {
        int[][] M = new int[N / 2][N];
        for (int i = 0; i < N / 2; i++) {
            for (int j = 0; j < N; j++) {
                M[i][j] = A[i + inicio][j];
            }
        }
    
        return M;
    }

    static void acomoda_matriz(int[][] C, int[][] c, int renglon, int columna) {
        for (int i = 0; i < N / 2; i++) {
            for (int j = 0; j < N / 2; j++) {
                C[i + renglon][j + columna] = c[i][j];
            }
        }
    }

    public static void main(String[] args) throws Exception {
        String url = "rmi://localhost/prueba";
        interfaceRMI r = (interfaceRMI) Naming.lookup(url);

        int[][] A = new int[N][N];
        int[][] B = new int[N][N];
        int[][] C = new int[N][N];

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                A[i][j] = 2 * i - j;
                B[i][j] = i + 2 * j;
            }
        }

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < i; j++) {
                int x = B[i][j];
                B[i][j] = B[j][i];
                B[j][i] = x;
            }
        }

        int[][] A1 = separa_matriz(A, 0);
        int[][] A2 = separa_matriz(A, N / 2);
        int[][] B1 = separa_matriz(B, 0);
        int[][] B2 = separa_matriz(B, N / 2);

        int[][] C1 = r.multiplica_matrices(A1, B1, N);
        int[][] C2 = r.multiplica_matrices(A1, B2, N);
        int[][] C3 = r.multiplica_matrices(A2, B1, N);
        int[][] C4 = r.multiplica_matrices(A2, B2, N);

        acomoda_matriz(C, C1, 0, 0);
        acomoda_matriz(C, C2, 0, N / 2);
        acomoda_matriz(C, C3, N / 2, 0);
        acomoda_matriz(C, C4, N / 2, N / 2);

        long checksum = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }
        
        System.out.println("checksum: " + checksum);
    }
}
