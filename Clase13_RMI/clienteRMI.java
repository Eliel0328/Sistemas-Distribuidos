import java.rmi.Naming;

public class clienteRMI {
    public static void main(String[] args) throws Exception {
        String url = "rmi://localhost/prueba";
        interfaceRMI r = (interfaceRMI) Naming.lookup(url);

        System.out.println(r.mayusculas("hola"));
        System.out.println("Suma: " + r.suma(10, 20));
        int[][] m = { { 1, 2, 3, 4 }, { 5, 6, 7, 8 }, { 9, 10, 11, 12 } };
        System.out.println("checksum: " + r.checksum(m));
    }
}
