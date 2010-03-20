package es.sidelab.veex.client;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;

import es.sidelab.veex.server.VeexRemoteExecutor;
import es.sidelab.veex.server.VeexRemoteServer;

public class Client {

	public static void main(String[] args) {
//		test();
		try {
			// null equivale a localhost
			Registry registry = LocateRegistry.getRegistry(null);
			
			VeexRemoteServer veex = (VeexRemoteServer) registry.lookup("veex");
			testServer(veex);
			
//			VeexRemoteServer veex2 = (VeexRemoteServer) registry.lookup("veexUnicast");
//			testServer(veex2);
			
			System.out.println("-----Finished");
		} catch(Exception e) {
			e.printStackTrace();
		}
	}


	private static void testServer(VeexRemoteServer veex) throws IOException {
		VeexRemoteExecutor executor = veex.getExecutor();
		int id = executor.execute(new String[] {"/home/patxi/jdk1.6.0_17/bin/java", "-classpath", "/home/patxi/Documentos/Research/Problems/workspace/VeexRMI/bin/", "AplicacionPrueba"});
		FileWriter writer = new FileWriter("client-id-" + id);
		
		String output = executor.getConsole(id);
		writer.write(output);
		while(output != null) {
			System.out.println("out=" + output);
//				Thread.sleep(10 * 1000);
			output = executor.getConsole(id);
			if(output != null) {
				writer.write(output);
			}
		}
		writer.close();
	}
	

	private static void test() {
		try {
			Process process = Runtime.getRuntime().exec(new String[] {"/home/patxi/jdk1.6.0_17/bin/java", "-classpath", "/home/patxi/Documentos/Research/Problems/workspace/VeexRMI/bin/", "AplicacionPrueba"});
			InputStream is = process.getInputStream();
			BufferedReader reader = new BufferedReader(new InputStreamReader(is));
			String line;
//			Thread.sleep(5 * 1000);
			while((line = reader.readLine())!=null) {
				System.out.println(line);
			}
			System.out.println("----Finished");
		} catch (Exception e) {
			e.printStackTrace();
		}
		
		
	}

}
