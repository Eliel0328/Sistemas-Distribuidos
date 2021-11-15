import java.rmi.Remote;
import java.rmi.RemoteException;
// import java.rmi.server.UnicastRemoteObject;

public interface interfaceRMI extends Remote {
    public double[][] multiplica_matrices(double[][] A, double[][] B, int N) throws RemoteException;
}


