/**
 * 
 */
package ReplicaManager1;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * @author Mahavir
 *
 */
public class RM1 {
	private static int nextSequenceId = 1;
	private static Queue<SequencerMessage> pq = new LinkedList<SequencerMessage>();
	//private static List<SequencerMessage> pq = new ArrayList<SequencerMessage>();
	private static ArrayList<SequencerMessage> message_list = new ArrayList<SequencerMessage>();
	private static final int multicast_Port = 1234;
	private static final String multicast_IPadress = "230.1.1.1";
	private static final int FE_multicast_Port = 4321;
	private static final String FE_multicast_IPadress = "230.1.1.10";
	//private static final String replica_IPaddress = "127.0.1.1";
	private static final int mtl_replica_port = 1111;
	private static final int que_replica_port = 2222;
	private static final int she_replica_port = 3333;
	private static final int RM1_f_port = 1060;
	//private static final String RM1_f_IPAddress = "127.5.5.5";
	
	public RM1() {
		// TODO Auto-generated constructor stub
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		checkWithFE();
		System.out.println("Replica Manager 1 Started");
		Runnable SequencerReceiver = () -> {
			try {
				receiveFromSequencer();
			} catch (UnknownHostException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		};
		Runnable FaultMessageReceiver = () -> {
			receiveFaultMessage();
		};
		Thread t1 = new Thread(SequencerReceiver);
		Thread t2 = new Thread(FaultMessageReceiver);
		t1.start();
		t2.start();
//		while(true) {
//			try {
//				receiveFromSequencer();
//				receiveFaultMessage();
//			} catch (UnknownHostException e) {
//				// TODO Auto-generated catch block
//				e.printStackTrace();
//			}
//		}
	}
	
	public static void receiveFaultMessage() {
		DatagramSocket  ds=null;
		try {
			ds = new DatagramSocket(RM1_f_port,InetAddress.getLocalHost());
			byte[] faultmessage = new byte[1000];
			System.out.println("Fault Handler started .........");
			while(true) {
				DatagramPacket FaultResponse = new DatagramPacket(faultmessage, faultmessage.length);
				ds.receive(FaultResponse);
				String FaultMessageString = new String(FaultResponse.getData(), 0 , FaultResponse.getLength());
				System.out.println("Handling the Fault in RM1 : " + FaultMessageString);
				RemoveFault(FaultMessageString);
			}
		}catch(Exception e) {
			System.out.println("Exception in receiveFaultMessage() : "+e);
		}finally {
			ds.close();
		}
	}
	
	public static void RemoveFault(String message) {
		DatagramSocket dsf = null;
		try {
			dsf = new DatagramSocket();
			byte[] faultmessage = message.getBytes();
			DatagramPacket DpSend = new DatagramPacket(faultmessage,faultmessage.length,InetAddress.getLocalHost(),mtl_replica_port);
			DatagramPacket DpSend1 = new DatagramPacket(faultmessage,faultmessage.length,InetAddress.getLocalHost(),she_replica_port);
			DatagramPacket DpSend2 = new DatagramPacket(faultmessage,faultmessage.length,InetAddress.getLocalHost(),que_replica_port);
			dsf.send(DpSend);
			dsf.send(DpSend1);
			dsf.send(DpSend2);
			System.out.println("Wiping Data and Reperfoming the Operation !!!");
		}catch(Exception e) {
			System.out.println(e);
		}finally {
			dsf.close();
		}
		
		Iterator<SequencerMessage> itr = pq.iterator();
		while(itr.hasNext()) {
			SequencerMessage sm = itr.next();
			DatagramSocket  ds=null;
			try {
				ds = new DatagramSocket();
				int port = decideReplicaPort(sm.getrequestMessage());
				InetAddress ip = InetAddress.getLocalHost();
				byte buf[] = null;
				buf = sm.getrequestMessage().getBytes();
				DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, port);
				ds.send(DpSend);
				System.out.println("Message Sent to Replica : " + sm.getrequestMessage());
				
				Thread.sleep(50);
				
				byte[] received = new byte[1000];
				DatagramPacket DpReceive = new DatagramPacket(received, received.length);
				ds.receive(DpReceive);
				String result1 = new String(DpReceive.getData());
				System.out.println("Message received : "+result1);
				String[] temp = result1.split("@");
				result1 = temp[0];
				System.out.println("Message Received from Replica : " + sm.getrequestMessage());
			}catch(Exception e) {
				System.out.println(e);
			}
		}
		System.out.println("Fault has been recovered !!!........");
	}
	
	
	
	public static void receiveFromSequencer() throws UnknownHostException{
		MulticastSocket ms = null;
		InetAddress ip = InetAddress.getByName(multicast_IPadress);
		try{
			ms = new MulticastSocket(multicast_Port);
			ms.joinGroup(ip);
			byte[] buf = new byte[1000];
			while(true) {
				DatagramPacket request = new DatagramPacket(buf, buf.length);
				System.out.println("Waiting for response from sequencer");
				ms.receive(request);
				System.out.println("Recieved a response from sequencer");
				String response = new String(request.getData(), 0, request.getLength());
				if(response.equals("Connection Message"))
					continue;
				System.out.println("Response in RM1 : "+response);
				
				String[] responseArray = response.split(";");
				SequencerMessage sm = new SequencerMessage(Integer.parseInt(responseArray[0]),responseArray[1] + ";" +responseArray[2] + ";");
				System.out.println("Sequence number got "+responseArray[0]);
				message_list.add(sm);
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
		System.out.println("Sequence number expected " + nextSequenceId);
		Iterator<SequencerMessage> itr = message_list.iterator();
		while(itr.hasNext()) {
			SequencerMessage sm = itr.next();
			if(sm.getsequenceId()==nextSequenceId) {
				pq.add(sm);
				sendToReplica(message_list.get(message_list.indexOf(sm)));
				nextSequenceId++;
			}
		}
	}
	
	public static int decideReplicaPort(String request) {
		String[] temp = request.split(";");
		List<String> params = new ArrayList<String>(Arrays.asList(temp[1].split("="))); 
		System.out.println(params);
		if(params.get(0).substring(0,3).equals("MTL"))
			return mtl_replica_port;
		if(params.get(0).substring(0,3).equals("SHE"))
			return she_replica_port;
		if(params.get(0).substring(0,3).equals("QUE"))
			return que_replica_port;
		if(params.get(0).equals("#")) {
			if(params.get(1).substring(0,3).equals("MTL"))
				return mtl_replica_port;
			if(params.get(1).substring(0,3).equals("SHE"))
				return she_replica_port;
			if(params.get(1).substring(0,3).equals("QUE"))
				return que_replica_port;
		}else {
			if(params.get(0).substring(0,3).equals("MTL"))
				return mtl_replica_port;
			if(params.get(0).substring(0,3).equals("SHE"))
				return she_replica_port;
			if(params.get(0).substring(0,3).equals("QUE"))
				return que_replica_port;
		}
		return mtl_replica_port;
	}
	
	public static void sendToReplica(SequencerMessage sm) {
		// get result from specified server
		System.out.println(sm.getrequestMessage());
		int port = decideReplicaPort(sm.getrequestMessage());
		String result1 = null;
		DatagramSocket  ds=null;
		try {
			ds = new DatagramSocket(); 
			InetAddress ip = InetAddress.getLocalHost();
			byte buf[] = null;
			buf = sm.getrequestMessage().getBytes();
			DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, port);
			ds.send(DpSend);
			System.out.println("Message Sent and waiting for response");
			
			Thread.sleep(1000);
			
			byte[] received = new byte[1000];
			DatagramPacket DpReceive = new DatagramPacket(received, received.length);
			ds.receive(DpReceive);
			result1 = new String(DpReceive.getData());
			System.out.println("Message received : "+result1);
			String[] temp = result1.split("@");
			result1 = temp[0];
		}catch(Exception e) {
			System.out.println(e);
		}finally {
			if(ds!=null)
				ds.close();
		}
		String result = sm.getsequenceId() + ";" + sm.getrequestMessage();
		sendToFrontEnd(result+result1+";");
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
		String final_result = "RM1;" + result + "Nuetral;";
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
