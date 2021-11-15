import java.rmi.Naming;

public class servidorRMI3 {
    public static void main(String[] args) throws Exception {
        String url = "rmi://localhost/prueba3";
        claseRMI obj = new claseRMI();
        Naming.rebind(url, obj);
    }
}
