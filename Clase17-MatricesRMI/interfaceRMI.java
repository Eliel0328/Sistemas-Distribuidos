import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface interfaceRMI extends Remote {
    public int[][] multiplica_matrices(int[][] A, int[][] B, int N) throws RemoteException;
}


