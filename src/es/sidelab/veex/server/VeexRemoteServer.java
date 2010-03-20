package es.sidelab.veex.server;

import java.rmi.Remote;
import java.rmi.RemoteException;

/**
 * 
 * @author patxi
 *
 */
public interface VeexRemoteServer extends Remote {

	VeexRemoteExecutor getExecutor() throws RemoteException;
	
}
