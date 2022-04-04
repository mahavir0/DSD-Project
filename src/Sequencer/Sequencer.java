/**
 * 
 */
package Sequencer;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

/**
 * @author Harshal
 *
 */
public class Sequencer {

	private static int sequencerId = 1;
	private static final int multicast_Port = 1234;
	private static final String multicast_IPadress = "230.1.1.1";
	private static final int sequencer_Port = 5555;
	private static final String sequencer_IPAdress = "127.0.0.131";
	/**
	 * 
	 */

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated constructor stub
		checkWithRM();
		DatagramSocket ds = null;
		try 
		{
			ds = new DatagramSocket(sequencer_Port,InetAddress.getLocalHost());
			byte[] responseFromFE = new byte[10000];
			System.out.println("[INFO] Sequencer UDP Server 5555 Started............");
			while (true)
			{
				DatagramPacket response = new DatagramPacket(responseFromFE, responseFromFE.length);
				System.out.println("[INFO] Waiting for a response from FrontEnd");
				ds.receive(response);
				//System.out.println("Received a response from FrontEnd");
				String requestString = new String( response.getData(), 0 , response.getLength());
				System.out.println("[INFO] Received message from FrontEnd => " + requestString);
				sendToRM(requestString);
			}

		} catch (Exception e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (ds != null)
				ds.close();
		}
	}
	
	public static void checkWithRM() {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			byte[] requestMessageToRM = "Connection Message".getBytes();
			InetAddress ip = InetAddress.getByName(multicast_IPadress);
			DatagramPacket request = new DatagramPacket(requestMessageToRM, requestMessageToRM.length);
			request.setAddress(ip);
			request.setPort(multicast_Port);
			ds.send(request);
		}catch(Exception e) {
			System.out.println(e);
		}finally {
			if(ds!=null)
				ds.close();
		}
	}
	
	public static void sendToRM(String resuestString) {
		final String resuestToRM = sequencerId + ";" + resuestString;
		System.out.println("[MESSAGE] Sequencer Message ==> " + resuestToRM);
		
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			byte[] requestMessageToRM = resuestToRM.getBytes();
			InetAddress ip = InetAddress.getByName(multicast_IPadress);
			DatagramPacket request = new DatagramPacket(requestMessageToRM, requestMessageToRM.length);
			request.setAddress(ip);
			request.setPort(multicast_Port);
			ds.send(request);
			System.out.println("[INFO] Sent a message to ReplicaManager");
			sequencerId++;
		}catch(Exception e) {
			System.out.println("[ERROR] Exception :"+ e);
		}finally {
			if(ds!=null)
				ds.close();
		}
	}
}
