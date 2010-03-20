package es.sidelab.veex.server;
import java.rmi.Remote;
import java.rmi.RemoteException;


public interface VeexRemoteExecutor extends Remote {

	int execute(String[] args) throws RemoteException;
	String getConsole(int id) throws RemoteException;
	String getConsole(int id, int startPosition) throws RemoteException;
}
