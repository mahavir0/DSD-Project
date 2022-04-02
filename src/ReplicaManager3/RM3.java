/**
 * 
 */
package ReplicaManager3;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import ReplicaManager1.SequencerMessage;

/**
 * @author Mahavir
 *
 */
public class RM3 {
	private static int nextSequenceId = 1;
	private static Queue<SequencerMessage> pq = new LinkedList<SequencerMessage>();
	//private static List<SequencerMessage> pq = new ArrayList<SequencerMessage>();
	private static ArrayList<SequencerMessage> message_list = new ArrayList<SequencerMessage>();
	private static final int multicast_Port = 1234;
	private static final String multicast_IPadress = "230.1.1.1";
	private static final int FE_multicast_Port = 4321;
	private static final String FE_multicast_IPadress = "230.1.1.10";
	/**
	 * 
	 */
	public RM3() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		checkWithFE();
		System.out.println("Replica Manager 3 Started");
		while(true) {
			receiveFromSequencer();
		}
	}

	public static void receiveFromSequencer() {
		//System.out.println("Here");
		MulticastSocket ms = null;
		try {
			ms = new MulticastSocket(multicast_Port);
			InetAddress ip = InetAddress.getByName(multicast_IPadress);
			ms.joinGroup(ip);
			byte[] buf = new byte[1000];
			//System.out.println("in try");
			while(true) {
				DatagramPacket request = new DatagramPacket(buf, buf.length);
				System.out.println("Waiting for response from sequencer");
				ms.receive(request);
				System.out.println("Recieved a response from sequencer");
				String response = new String(request.getData(), 0, request.getLength());
				if(response.equals("Connection Message"))
					continue;
				System.out.println("Response in RM3 : "+response);
				
				String[] responseArray = response.split(";");
				SequencerMessage sm = new SequencerMessage(Integer.parseInt(responseArray[0]),responseArray[1] + ";" +responseArray[2] + ";");
				System.out.println("Sequence number got "+responseArray[0]);
				pq.add(sm);
				//Thread.sleep(1000);
				getMessage();
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e.getMessage());
		} finally {
			System.out.println("in finaaly");
			if (ms != null)
				ms.close();
		}
	}
	
	public static void getMessage() {
		//System.out.println("in getmessage");
		System.out.println("Sequence number expected " + nextSequenceId);
//		Iterator<SequencerMessage> itr = pq.iterator();
//		while(itr.hasNext()) {
//			SequencerMessage sm = itr.next();
//			nextSequenceId++;
//			sendToReplica(sm);
//			if(sm.getsequenceId()==nextSequenceId) {
//				nextSequenceId++;
//				sendToReplica(sm);
//			}
//		}
		sendToReplica(pq.poll());
		nextSequenceId++;
	}
	
	public static void sendToReplica(SequencerMessage sm) {
		// get result from specificed server
		//System.out.println("in sendtoreplica");
		String result = sm.getsequenceId() + ";" + sm.getrequestMessage();
		sendToFrontEnd(result);
	}
	
	public static void checkWithFE() {
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			byte[] responseMessageToFE = "Connection Message".getBytes();
			InetAddress ip = InetAddress.getByName(FE_multicast_IPadress);
			DatagramPacket response = new DatagramPacket(responseMessageToFE, responseMessageToFE.length, ip, FE_multicast_Port);
			ds.send(response);
		}catch(Exception e) {
			System.out.println("Exception :"+ e);
		}finally {
			if(ds!=null)
				ds.close();
		}
	}
	
	public static void sendToFrontEnd(String result) {
		String final_result = "RM3;" + result + "Added;" + "Success;";
		//System.out.println("in send to fe");
		System.out.println(final_result);
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			byte[] responseMessageToFE = final_result.getBytes();
			InetAddress ip = InetAddress.getByName(FE_multicast_IPadress);
			DatagramPacket response = new DatagramPacket(responseMessageToFE, responseMessageToFE.length, ip, FE_multicast_Port);
			ds.send(response);
			System.out.println("Message sent to FrontEnd");
		}catch(Exception e) {
			System.out.println("Exception :"+ e);
		}finally {
			if(ds!=null)
				ds.close();
		}
	}
}
