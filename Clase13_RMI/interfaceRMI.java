import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;

public interface interfaceRMI extends Remote {
    public String mayusculas(String name) throws RemoteException;
    public int suma(int a, int b) throws RemoteException;
    public long checksum(int[][] m) throws RemoteException;
}


