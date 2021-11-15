import java.rmi.Naming;

public class servidorRMI {
    public static void main(String[] args) throws Exception {
        String url = "rmi://localhost/prueba";
        claseRMI obj = new claseRMI();
        Naming.rebind(url, obj);
    }
}
