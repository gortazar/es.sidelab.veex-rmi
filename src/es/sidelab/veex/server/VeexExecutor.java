package es.sidelab.veex.server;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;

public class VeexExecutor extends UnicastRemoteObject implements VeexRemoteExecutor, Serializable {

	private transient VeexServer server;
	private int offset;

	public VeexExecutor(VeexServer veexServer) throws RemoteException {
		this.server = veexServer;
		this.offset = -1;
	}

	@Override
	public int execute(String[] args) throws RemoteException {
		try {
			Process process = Runtime.getRuntime().exec(args);
			
			int id = server.registerAndSave(process); 

			offset = 0;
			
			return id;
		} catch (IOException e) {
			throw new RemoteException("Couldn't execute: " + args, e);
		}
	}

	@Override
	public String getConsole(int id) throws RemoteException {
		try {
			byte[] bytes = server.readBytes(id, offset);
			
			if(bytes == null) {
				// Hemos terminado
				return null;
			}

			offset += bytes.length;
			
			return new String(bytes);
			
		} catch(Exception e) {
			throw new RemoteException("Error reading is", e);
		}
	}

	@Override
	public String getConsole(int id, int startPosition) throws RemoteException {
		return null;
	}

}
