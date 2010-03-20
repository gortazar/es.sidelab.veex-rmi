package es.sidelab.veex.server;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.RandomAccessFile;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.rmi.server.UnicastRemoteObject;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * Hay que ejecutarlo con:
 * 
 * "-Djava.rmi.server.codebase=file:${workspace_loc}/${project_name}/bin/"
 * 
 * @author patxi
 *
 */
public class VeexServer extends UnicastRemoteObject implements VeexRemoteServer {

	public static void main(String[] args) {
		try {
			VeexServer server = new VeexServer();
//			VeexRemoteServer veex = (VeexRemoteServer) UnicastRemoteObject.exportObject(server, 10001);
			Registry registry = LocateRegistry.getRegistry();
			registry.rebind("veex", server);
			
			System.out.println("Server ready");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}

	class ProcessInfo {
		public ProcessInfo(Process process, Lock fileOutputLock) {
			this.process =  process;
			this.lock = fileOutputLock;
		}
		Process process;
		Lock lock;
	}
	
	private int nextId;
	private Map<Integer, ProcessInfo> register = new HashMap<Integer, ProcessInfo>();
	private ExecutorService service;
	private Lock lock = new ReentrantLock();
	
	
	public VeexServer() throws RemoteException {
		service = Executors.newFixedThreadPool(5);
	}
	
	@Override
	public VeexRemoteExecutor getExecutor() throws RemoteException {
		return new VeexExecutor(this);
	}
	
	public int registerAndSave(final Process process) {
		
		lock.lock();
		final int id = nextId++;
		final Lock fileOutputLock = new ReentrantLock();
		register.put(id, new ProcessInfo(process, fileOutputLock));
		lock.unlock();
		
		service.execute(new Runnable() {
			
			@Override
			public void run() {
				InputStream is = process.getInputStream();
				BufferedInputStream bis = new BufferedInputStream(is);
				try {
					
					int result;
					do {
						byte[] buf = new byte[1024];
						result = bis.read(buf);
						if(result != -1) {
							fileOutputLock.lock();
							FileOutputStream os = new FileOutputStream("output-id-" + id);
							os.write(buf);
							os.close();
							fileOutputLock.unlock();
						}
					} while(result != -1);
					
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		});
		
		return id;
	}
	
	public byte[] readBytes(int id, int offset) throws IOException {
		// It may be the case that the file has not yet been created
		try {
			RandomAccessFile raf = new RandomAccessFile("output-id-" + id, "r");
			raf.seek(offset);
			byte[] bytes = new byte[1024];
			int bytesRead = raf.read(bytes);
			if(bytesRead == -1) {
				return null;
			}
			return bytes;
		} catch (FileNotFoundException e) {
			// Ok. Let's return 0 bytes
			return new byte[0];
		}
	}

}
