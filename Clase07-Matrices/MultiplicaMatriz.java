class MultiplicaMatriz {
    static int N = 9;
    static int[][] A = new int[N][N];
    static int[][] B = new int[N][N];
    static int[][] C = new int[N][N];

    static void mostrarMatriz(int[][] M, int col, int fil) {
        for (int i = 0; i < col; i++) {
            for (int j = 0; j < fil; j++) {
                System.out.print(M[i][j] + "\t ");
            }
            System.out.println("");
        }
        System.out.println("");
    }


    public static void main(String[] args) {
        long t1 = System.currentTimeMillis();

        // inicializa las matrices A y B

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++) {
                A[i][j] = i + 4 * j;
                B[i][j] = i - 4 * j;
                C[i][j] = 0;
            }

        // multiplica la matriz A y la matriz B, el resultado queda en la matriz C

        for (int i = 0; i < N; i++)
            for (int j = 0; j < N; j++)
                for (int k = 0; k < N; k++)
                    C[i][j] += A[i][k] * B[k][j];

        mostrarMatriz(A, N, N);
        mostrarMatriz(B, N, N);
        mostrarMatriz(C, N, N);

        long checksum = 0;

        for (int i = 0; i < N; i++) {
            for (int j = 0; j < N; j++) {
                checksum += C[i][j];
            }
        }

        System.out.println("checksum: " + checksum);

        long t2 = System.currentTimeMillis();
        System.out.println("Tiempo: " + (t2 - t1) + "ms");
    }
}
