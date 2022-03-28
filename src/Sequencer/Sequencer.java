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
	private static final String sequencer_IPAdress = "192.168.0.131";
	/**
	 * 
	 */
	public Sequencer() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated constructor stub
		DatagramSocket ds = null;
		try 
		{
			ds = new DatagramSocket(sequencer_Port,InetAddress.getByName(sequencer_IPAdress));
			byte[] responseFromFE = new byte[1000];
			System.out.println("Sequencer UDP Server 5555 Started............");
			while (true)
			{
				DatagramPacket response = new DatagramPacket(responseFromFE, responseFromFE.length);
				ds.receive(response);
				String requestString = new String( response.getData(), 0 , response.getLength());
				System.out.println(requestString);
				sendToRM(requestString);
			}

		} catch (Exception e) {
			System.out.println("IO: " + e.getMessage());
		} finally {
			if (ds != null)
				ds.close();
		}
	}
	
	public static void sendToRM(String resuestString) {
		final String resuestToRM = sequencerId + ";" + resuestString;
		System.out.println(resuestToRM);
		
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			byte[] requestMessageToRM = resuestToRM.getBytes();
			InetAddress ip = InetAddress.getByName(multicast_IPadress);
			DatagramPacket request = new DatagramPacket(requestMessageToRM, requestMessageToRM.length);
			request.setAddress(ip);
			request.setPort(multicast_Port);
			ds.send(request);
			System.out.println("Sent to RM");
			sequencerId++;
		}catch(Exception e) {
			System.out.println("Exception :"+ e);
		}finally {
			if(ds!=null)
				ds.close();
		}
	}
}
