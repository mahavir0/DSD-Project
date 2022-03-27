/**
 * 
 */
package ReplicaManager1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.PriorityQueue;
import ReplicaManager1.SequencerMessageComparator;

/**
 * @author Mahavir
 *
 */
public class RM1 {
	private static int nextSequenceId = 1;
	private static PriorityQueue<SequencerMessage> pq = new PriorityQueue(25);
	private static ArrayList<SequencerMessage> message_list = new ArrayList<SequencerMessage>();
	private static final int multicast_Port = 1234;
	private static final String multicast_IPadress = "230.0.0.0";
	private static final int FE_multicast_Port = 4321;
	private static final String FE_multicast_IPadress = "230.1.1.10";
	/**
	 * 
	 */
	public RM1() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Replica Manager 1 Started");
		Runnable task = () -> {
			receiveFromSequencer();
		};
		Thread t = new Thread(task);
		t.start();
	}
	
	private static void receiveFromSequencer() {
		System.out.println("Here");
		MulticastSocket ms = null;
		try {
			ms = new MulticastSocket(multicast_Port);
			ms.joinGroup(InetAddress.getByName(multicast_IPadress));
			byte[] buf = new byte[1000];
			System.out.println("in try");
			while(true) {
				DatagramPacket request = new DatagramPacket(buf, buf.length);
				ms.receive(request);
				String response = new String(request.getData(), 0, request.getLength());
				System.out.println("Response in RM1 : "+response);
				String[] responseArray = response.split(";");
				SequencerMessage sm = new SequencerMessage(Integer.parseInt(responseArray[0]),responseArray[1] + ";" +responseArray[2] + ";");
				System.out.println("Sequence number got "+responseArray[0]);
				pq.add(sm);
				Thread.sleep(1000);
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
		System.out.println("in getmessage");
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
		System.out.println("in sendtoreplica");
		String result = sm.sequenceId + ";" + sm.requestMessage;
		sendToFrontEnd(result);
	}
	
	public static void sendToFrontEnd(String result) {
		String final_result = "RM1;" + result + "Added;" + "Success;";
		System.out.println("in send to fe");
		System.out.println(final_result);
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			byte[] responseMessageToFE = final_result.getBytes();
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

}
