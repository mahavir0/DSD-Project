/**
 * 
 */
package ReplicaManager2;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.NetworkInterface;
import java.net.UnknownHostException;
import java.rmi.NotBoundException;
import java.rmi.RemoteException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

import ReplicaManager2.SequencerMessage;
import ReplicaManager2.Server.AppointmentManagementInterface.*;
import ReplicaManager2.Server.ServerInstance.*;
import ReplicaManager2.Server.*;

/**
 * @author Harshal
 *
 */
public class RM2 {
	private static int nextSequenceId = 1;
	private static Queue<SequencerMessage> pq = new LinkedList<SequencerMessage>();
	//private static List<SequencerMessage> pq = new ArrayList<SequencerMessage>();
	private static ArrayList<SequencerMessage> message_list = new ArrayList<SequencerMessage>();
	private static final int multicast_Port = 1234;
	private static final String multicast_IPadress = "230.1.1.1";
	private static final int FE_multicast_Port = 4321;
	private static final String FE_multicast_IPadress = "230.1.1.10";
	//private static final int mtl_replica_port = 1111;
	//private static final int que_replica_port = 2222;
	//private static final int she_replica_port = 3333;
	private static final String Fault_Multicast_IPAdrress = "230.1.1.30";
	private static final int Fault_Multicast_Port = 3496;
	public static final int Server_Montreal = 2964;
    public static final int Server_Sherbrooke = 2965;
    public static final int Server_Quebec = 2966;
    public static final String AppointmentManagement_RegisterName = "Appointment_Management";
    public static int lastSequenceID = 1;
	/**
	 * 
	 */
	public RM2() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		checkWithFE();
		System.out.println("Replica Manager 2 Started");
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
	}
	
	public static void receiveFaultMessage() {
		MulticastSocket ms = null;
		try {
			InetAddress ip = InetAddress.getByName(Fault_Multicast_IPAdrress);
			ms = new MulticastSocket(Fault_Multicast_Port);
			//ms.setNetworkInterface(NetworkInterface.getByName("en0"));
			ms.joinGroup(ip);
			byte[] faultmessage = new byte[10000];
			System.out.println("[INFO] Fault Handler started .........");
			while(true) {
				DatagramPacket FaultResponse = new DatagramPacket(faultmessage, faultmessage.length);
				ms.receive(FaultResponse);
				String FaultMessageString = new String(FaultResponse.getData(), 0 , FaultResponse.getLength());
				String[] temp = FaultMessageString.split(";");
				if(temp[1].equals("Connection Message"))
					continue;
				if(temp[1].equals("RM2")) {
					System.out.println("Handling the Fault in RM2 : " + FaultMessageString);
					//SequencerMessage sm = new SequencerMessage(0, FaultMessageString);
					requestServerForFaultTolerance();
					//requestToServers(sm);
					RemoveFault(FaultMessageString);
				}
			}
		}catch(Exception e) {
			System.out.println("[ERROR] Exception in receiveFaultMessage() : "+e);
		}finally {
			ms.close();
		}
	}
	
	public static void requestServerForFaultTolerance() {
		try {
		Registry registry = LocateRegistry.getRegistry(Server_Montreal);
        AppointmentManagementInterface obj = (AppointmentManagementInterface ) registry.lookup(AppointmentManagement_RegisterName);
        System.out.println(obj.startBackupReplica()); 

        Registry registry1 = LocateRegistry.getRegistry(Server_Sherbrooke);
        AppointmentManagementInterface obj1 = (AppointmentManagementInterface ) registry1.lookup(AppointmentManagement_RegisterName);
        System.out.println(obj1.startBackupReplica());
        
        Registry registry2 = LocateRegistry.getRegistry(Server_Quebec);
        AppointmentManagementInterface obj2 = (AppointmentManagementInterface ) registry2.lookup(AppointmentManagement_RegisterName);
        System.out.println(obj2.startBackupReplica());
		}catch(Exception e) {
			System.out.println("[ERROR] Exception in RequestServerForFaultTolerance"+e);
		}
	}
	
	public static void receiveFromSequencer() throws UnknownHostException {
		//System.out.println("Here");
		MulticastSocket ms = null;
		InetAddress ip = InetAddress.getByName(multicast_IPadress);
		try {
			ms = new MulticastSocket(multicast_Port);	
			//ms.setNetworkInterface(NetworkInterface.getByName("en0"));
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
				System.out.println("Response in RM2 : "+response);
				String[] responseArray = response.split(";");
				SequencerMessage sm = new SequencerMessage(Integer.parseInt(responseArray[0]),responseArray[1] + ";" +responseArray[2] + ";");
				System.out.println("Sequence number got "+responseArray[0]);
				message_list.add(sm);
				//Thread.sleep(1000);
				
//				   DatagramPacket request = new DatagramPacket(buf, buf.length);
//	                ms.receive(request);
//
//	                String data = new String(request.getData(), 0, request.getLength());
//	                String[] parts = data.split(";");
//	                System.out.println("RM2 recieved message. Detail:" + data);
//	                if (parts[2].equalsIgnoreCase("00")) {
//	                    Message message = message_obj_create(data);
//	                    Message message_To_RMs = message_obj_create(data);
//	                    send_multicast_toRM(message_To_RMs);
//	                    if (message.sequenceId - lastSequenceID > 1) {
//	                        Message initial_message = new Message(0, "Null", Integer.toString(lastSequenceID), Integer.toString(message.sequenceId), "RM2", "Null", "Null", "Null", 0);
//	                        System.out.println("RM2 send request to update its message list. from:" + lastSequenceID + "To:" + message.sequenceId);
//	                        // Request all RMs to send back list of messages
//	                        send_multicast_toRM(initial_message);
//	                    }
//	                }
				getMessage();
			}
	                }
	                catch (Exception e) {
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
//	 private static Message message_obj_create(SequencerMessage sm) {
//	        
//		 
//		 	String[] temp = sm.getrequestMessage().split(";");
//		 	System.out.println("temp : "+temp);
//		 	String[] parts = temp[1].split("=");
//		 	System.out.println("parts : "+parts);
//		 	int sequenceId = sm.getsequenceId();
//		 	System.out.println("sequenceId : "+sequenceId);
//		 	String FrontIpAddress = FE_multicast_IPadress;      
//		 	System.out.println("FrontIpAddress : "+FrontIpAddress);
//		 	String Function = temp[0];
//		 	System.out.println("Function : "+Function);
//		 	String UserID = parts[0];
//		 	System.out.println("UserID : "+UserID);
//		 	String newAppointmentID = parts[4];
//		 	System.out.println("newAppointmentID : "+newAppointmentID);
//		 	String newAppointmentType = parts[5];
//		 	System.out.println("newAppointmentType : "+newAppointmentType);
//		 	String oldAppointmentID = parts[1];
//		 	System.out.println("oldAppointmentID : "+oldAppointmentID);
//		 	String oldAppointmentType = parts[2];
//		 	System.out.println("oldAppointmentType : "+oldAppointmentType);
//		 	int Capacity = 0;
//		 	if(parts[3].equals("#"))
//		 		Capacity = 0;
//		 	else	
//		 		Capacity = Integer.parseInt(parts[3]);
//		 	System.out.println("Capacity : "+Capacity);
//		 	Message message = new Message(sequenceId, FrontIpAddress, Function, UserID, newAppointmentID, newAppointmentType, oldAppointmentID, oldAppointmentType, Capacity);
//	        return message;
//	    }
	 public static void sendToReplica(SequencerMessage sm){
		
		//Message m = message_obj_create(sm);
		System.out.println(sm.getrequestMessage());
		String result1 = "";
		try {
			result1 += requestToServers(sm);
		} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
//		int port = 3456;
//		String result1 = null;
//		DatagramSocket  ds=null;
//		
//        try {
//            ds = new DatagramSocket();
//            byte buf[] = null;
//            buf = sm.getrequestMessage().getBytes();
//            InetAddress ip = InetAddress.getLocalHost();
//            DatagramPacket request = new DatagramPacket(buf, buf.length, ip, port);
//            ds.send(request);
//            System.out.println("Request send to Central Servers");
//            System.out.println("Message multicasted from RM2 to other RMs . Detail:" + sm);
//            
//             Thread.sleep(1000);
//			
//			byte[] received = new byte[1000];
//			DatagramPacket DpReceive = new DatagramPacket(received, received.length);
//			ds.receive(DpReceive);
//			System.out.println("Request Recive from central Server");
//			result1 = new String(DpReceive.getData());
//			System.out.println("Message received : "+result1);
//			String[] temp = result1.split("@");
//			result1 = temp[0];
//		}catch(Exception e) {
//			System.out.println(e);
//		}finally {
//			if(ds!=null)
//				ds.close();
//		}
		String result = sm.getsequenceId() + ";" + sm.getrequestMessage();
		sendToFrontEnd(result+result1+";");
	}
	
	 public static void RemoveFault(String message) {
			try {
				SequencerMessage sm = new SequencerMessage(0, "testHashMap;MTLA0000=#=#=#=#=#;");
				SequencerMessage sm1 = new SequencerMessage(0, "testHashMap;QUEA0000=#=#=#=#=#;");
				SequencerMessage sm2 = new SequencerMessage(0, "testHashMap;SHEA0000=#=#=#=#=#;");
				//System.out.println("[INFO] Wiping Data and Reperfoming the Operation !!!");
				requestToServers(sm);
				requestToServers(sm1);
				requestToServers(sm2);
			}catch(Exception e) {
				System.out.println(e);
			}finally {
				//System.out.println("[INFO] Data has been wiped out !");
			}
			
			Iterator<SequencerMessage> itr = pq.iterator();
			while(itr.hasNext()) {
				SequencerMessage sm = itr.next();
				try {
					requestToServers(sm);
				}catch(Exception e) {
					System.out.println(e);
				}
			}
			System.out.println("[INFO] Fault has been recovered !!!........");
		}
	 
	 
	private static int serverPort(String input) {
		
		  String branch = input.substring(0, 3);
		  System.out.println("branch : " +branch);
        int portNumber = -1;

        if (branch.equalsIgnoreCase("que"))
            portNumber = Server_Quebec;
        else if (branch.equalsIgnoreCase("mtl"))
            portNumber = Server_Montreal;
        else if (branch.equalsIgnoreCase("she"))
            portNumber = Server_Sherbrooke;

        return portNumber;
    }
	
	
	
	//Request to servers 
	 private static String requestToServers(SequencerMessage sm) throws Exception {
	        
		 	String[] temp = sm.getrequestMessage().split(";");
		 	String[] parts = temp[1].split("=");
		 	String Function = temp[0];
		 	String UserID = parts[0];
		 	String newAppointmentID = parts[4];
		 	String newAppointmentType = parts[5];
		 	String oldAppointmentID = parts[1];
		 	String oldAppointmentType = parts[2];
		 	int Capacity = 0;
		 	if(parts[3].equals("#"))
		 		Capacity = 0;
		 	else	
		 		Capacity = Integer.parseInt(parts[3]);
		 	System.out.println("Capacity : "+Capacity); 
		    int portNumber = serverPort(UserID.substring(0, 3));
		 	System.out.println("UserId : "+UserID);
		 	
		 	Registry registry = LocateRegistry.getRegistry(portNumber);
	        AppointmentManagementInterface obj = (AppointmentManagementInterface ) registry.lookup(AppointmentManagement_RegisterName);

	        if (UserID.substring(3, 4).equalsIgnoreCase("A")) {
	            if (Function.equalsIgnoreCase("addAppointment")) {
	                String response = obj.AddAppointment(oldAppointmentID, oldAppointmentType, Capacity);
	                System.out.println(response);
	                return response;
	            } else if (Function.equalsIgnoreCase("removeAppointment")) {
	                String response = obj.RemoveAppointment(oldAppointmentID, oldAppointmentType);
	                System.out.println(response);
	                return response;
	            } else if (Function.equalsIgnoreCase("listAppointmentAvailability")) {
	                String response = obj.ListAppointmentAvailability(oldAppointmentType);
	                System.out.println(response);
	                return response;
	            }
	        } else if (UserID.substring(3, 4).equalsIgnoreCase("P")) {
	            if (Function.equalsIgnoreCase("bookAppointment")) {
	                String response = obj.BookAppointment(UserID, oldAppointmentID, oldAppointmentType);
	                System.out.println(response);
	                return response;
	            } else if (Function.equalsIgnoreCase("getAppointmentSchedule")) {
	                String response = obj.getAppointmentSchedule(UserID);
	                System.out.println(response);
	                return response;
	            } else if (Function.equalsIgnoreCase("cancelAppointment")) {
	                String response = obj.CancelAppointment(UserID, oldAppointmentID, oldAppointmentType);
	                System.out.println(response);
	                return response;
	            } else if (Function.equalsIgnoreCase("swapAppointment")) {
	                String response = obj.SwapAppointment(UserID, newAppointmentID, newAppointmentType, oldAppointmentID, oldAppointmentType);
	                System.out.println(response);
	                return response;
	            }
	        } else if (Function.equalsIgnoreCase("fault message")) {
            	//obj.testHashMap();
            	String response = obj.startBackupReplica();
            	System.out.println(response);
            	System.out.println("[INFO] Replica has been changed Completely..");
            	System.out.println(response);
            }
	        return "Null response from server" + UserID.substring(0, 3);
	    }

		// get result from specificed server
		//System.out.println("in sendtoreplica");
		
//		serversFlag = false;
//		
//        Registry montreal_registry = LocateRegistry.getRegistry(Server_Montreal);
//        AppointmentManagementInterface montreal_obj = (AppointmentManagementInterface) montreal_registry.lookup(AppointmentManagement_RegisterName);
//        montreal_obj.shutDown();
////        Montreal.main(new String[0]);
//        System.out.println("RM2 shutdown Montreal Server");
//        
//
//        Registry quebec_registry = LocateRegistry.getRegistry(Server_Quebec);
//        AppointmentManagementInterface quebec_obj = (AppointmentManagementInterface) quebec_registry.lookup(AppointmentManagement_RegisterName);
//        quebec_obj.shutDown();
////        Quebec.main(new String[0]);
//        System.out.println("RM2 shutdown Quebec Server");
//
//        Registry sherbrook_registry = LocateRegistry.getRegistry(Server_Sherbrooke);
//        AppointmentManagementInterface sherbrook_obj = (AppointmentManagementInterface) sherbrook_registry.lookup(AppointmentManagement_RegisterName);
//        sherbrook_obj.shutDown();
////        Sherbrooke.main(new String[0]);
//        System.out.println("RM2 shutdown Sherbrooke Server");
//
//        //This is going to start all the servers for this implementation
//        Server.main(new String[0]);
//
//        //wait untill are servers are up
//        Thread.sleep(5000);
//

		
	//	String result = sm.getsequenceId() + ";" + sm.getrequestMessage();
		//sendToFrontEnd(result);

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
		String final_result = "RM2;" + result + "Nuetral;";
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
