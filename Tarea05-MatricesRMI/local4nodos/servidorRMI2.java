import java.rmi.Naming;

public class servidorRMI2 {
    public static void main(String[] args) throws Exception {
        String url = "rmi://localhost/prueba2";
        claseRMI obj = new claseRMI();
        Naming.rebind(url, obj);
    }
}
