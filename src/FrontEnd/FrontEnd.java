/**
 * 
 */
package FrontEnd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.util.ArrayList;

import org.omg.CORBA.ORB;
import org.omg.CosNaming.NameComponent;
import org.omg.CosNaming.NamingContextExt;
import org.omg.CosNaming.NamingContextExtHelper;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

import DAMSApp.DAMS;
import DAMSApp.DAMSHelper;
import DAMSApp.DAMSPOA;

/**
 * @author Mahavir
 *
 */
class FrontEndImpl extends DAMSPOA {
	// String params = patientID + "=" + appointmentID + "=" + appoinmentType + "="
	// + capacity + "=" + newAppointmentID + "=" + newAppoinmentType;
	// return params;
	private ORB orb;
	public ArrayList<ReplicaResponse> responseList = new ArrayList<ReplicaResponse>();
	private final int sequencerPort = 5555;
	private final String sequencerIPAdress = "127.0.0.131";
	private int RM1_fault = 0;
	private int RM2_fault = 0;
	private int RM3_fault = 0;
	private final String Fault_Multicast_IPAdrress = "230.1.1.30";
	private final int Fault_Multicast_Port = 3496;
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}

	@Override
	public String addAppointment(String adminID, String appointmentID, String appoinmentType, String capacity) {
		// TODO Auto-generated method stub
		String params = adminID + "=" + appointmentID + "=" + appoinmentType + "=" + capacity + "=" + "#" + "=" + "#";
		this.sendtoSequencer("addAppointment", params);

		this.waitingForResponseFromRM();

		return this.getRMResponses("addAppointment", params);
		// return null;
	}

	@Override
	public String removeAppointment(String adminID, String appointmentID, String appoinmentType) {
		// TODO Auto-generated method stub
		// return "remove appointment";
		String params = adminID + "=" + appointmentID + "=" + appoinmentType + "=" + "#" + "=" + "#" + "=" + "#";
		this.sendtoSequencer("removeAppointment", params);

		this.waitingForResponseFromRM();

		return this.getRMResponses("removeAppointment", params);
	}

	@Override
	public String listAppointmentAvailability(String adminID, String appoinmentType) {
		// TODO Auto-generated method stub
		// return "list appointment";
		String params = adminID + "=" + "#" + "=" + appoinmentType + "=" + "#" + "=" + "#" + "=" + "#";
		this.sendtoSequencer("listAppointmentAvailability", params);

		this.waitingForResponseFromRM();

		return this.getRMResponses("listAppointmentAvailability", params);
	}

	@Override
	public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
		// TODO Auto-generated method stub
		// return "book appointment";
		String params = patientID + "=" + appointmentID + "=" + appointmentType + "=" + "#" + "=" + "#" + "=" + "#";
		this.sendtoSequencer("bookAppointment", params);

		this.waitingForResponseFromRM();

		return this.getRMResponses("bookAppointment", params);
	}

	@Override
	public String getAppointmentSchedule(String patientID) {
		// TODO Auto-generated method stub
		// return "get appointment";
		// return null;
		String params = patientID + "=" + "#" + "=" + "#" + "=" + "#" + "=" + "#" + "=" + "#";
		this.sendtoSequencer("getAppointmentSchedule", params);

		this.waitingForResponseFromRM();

		return this.getRMResponses("getAppointmentSchedule", params);
	}

	@Override
	public String cancelAppointment(String patientID, String appointmentID, String appointmentType) {
		// TODO Auto-generated method stub
		// return "cancel appointment";
		String params = patientID + "=" + appointmentID + "=" + appointmentType + "=" + "#" + "=" + "#" + "=" + "#";
		this.sendtoSequencer("cancelAppointment", params);

		this.waitingForResponseFromRM();

		return this.getRMResponses("cancelAppointment", params);
	}

	@Override
	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppoinmentType,
			String newAppointmentID, String newAppoinmentType) {
		// TODO Auto-generated method stub
		// return "swap appointment";
		String params = patientID + "=" + oldAppointmentID + "=" + oldAppoinmentType + "=" + "#" + "="
				+ newAppointmentID + "=" + newAppoinmentType;
		this.sendtoSequencer("swapAppointment", params);

		this.waitingForResponseFromRM();

		return this.getRMResponses("swapAppointment", params);
		// return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		orb.shutdown(false);
	}

	public void addResponse(ReplicaResponse rr) {
		this.responseList.add(rr);
	}

	public ArrayList<ReplicaResponse> getResponses() {
		return this.responseList;
	}

	public void sendtoSequencer(String request, String params) { // ============================== to send messages to
																	// sequencer========================================
																	// //
		String sendrequest = request + ";" + params + ";";
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			InetAddress ip = InetAddress.getLocalHost();
			byte[] sendrequestmessage = sendrequest.getBytes();
			DatagramPacket DpSend = new DatagramPacket(sendrequestmessage, sendrequestmessage.length, ip,
					sequencerPort);
			ds.send(DpSend);
			System.out.println("Sent a response to Sequencer");
		} catch (Exception e) {
			System.out.println("Exception in sendTOSequencer" + e);
		} finally {
			if (ds != null)
				ds.close();
		}
	}

	public void waitingForResponseFromRM() { // ======================= 5 seconds timeout for to get all the responses
												// ====================== //
		try {
			System.out.println("Waiting for the responses of RMs...");
			Thread.sleep(10000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			// e.printStackTrace();
			System.out.println("slept");
		}
	}

	public String getRMResponses(String function, String params) { // ============================== to get the messages
																	// from RMs=========================================
																	// //
		String result = "";
		ArrayList<ReplicaResponse> temp = this.getResponses();
		System.out.println("Size : " + temp.size());
		for (int i = 0; i < temp.size(); i++) {
			System.out.println(
					temp.get(i).getReplicaNo() + " => " + temp.get(i).getResponse() + " => " + temp.get(i).getStatus());
		}
//		fault_invoker++;
//		System.out.println("Fault Invoker Count : " +fault_invoker);
//		if(fault_invoker==5) {
//			notifyRMforFault(RM1_f_port);
//			System.out.println(RM1_f_port + " RM1 notified for the fault ");
//		}
		if (temp.size() == 3) {
			if (temp.get(0).getStatus().equalsIgnoreCase(temp.get(1).getStatus())
					&& temp.get(0).getStatus().equalsIgnoreCase(temp.get(2).getStatus())) {
				result = temp.get(0).getResponse() + "->" + temp.get(0).getStatus();
			} else if (temp.get(0).getStatus().equalsIgnoreCase(temp.get(1).getStatus())) {
				result = temp.get(0).getResponse() + "->" + temp.get(0).getStatus();
				if (temp.get(0).getReplicaNo().equals("RM1") && temp.get(1).getReplicaNo().equals("RM3")) {
					RM2_fault++;
				} else if (temp.get(0).getReplicaNo().equals("RM1") && temp.get(1).getReplicaNo().equals("RM2")) {
					RM3_fault++;
				} else if (temp.get(0).getReplicaNo().equals("RM2") && temp.get(1).getReplicaNo().equals("RM3")) {
					RM1_fault++;
				}
			} else if (temp.get(0).getStatus().equalsIgnoreCase(temp.get(2).getStatus())) {
				result = temp.get(0).getResponse() + "->" + temp.get(0).getStatus();
				if (temp.get(0).getReplicaNo().equals("RM1") && temp.get(2).getReplicaNo().equals("RM3")) {
					RM2_fault++;
				} else if (temp.get(0).getReplicaNo().equals("RM1") && temp.get(2).getReplicaNo().equals("RM2")) {
					RM3_fault++;
				} else if (temp.get(0).getReplicaNo().equals("RM2") && temp.get(2).getReplicaNo().equals("RM3")) {
					RM1_fault++;
				}
			} else if (temp.get(1).getStatus().equalsIgnoreCase(temp.get(2).getStatus())) {
				result = temp.get(1).getResponse() + "->" + temp.get(1).getStatus();
				if (temp.get(1).getReplicaNo().equals("RM1") && temp.get(2).getReplicaNo().equals("RM3")) {
					RM2_fault++;
				} else if (temp.get(1).getReplicaNo().equals("RM1") && temp.get(2).getReplicaNo().equals("RM2")) {
					RM3_fault++;
				} else if (temp.get(1).getReplicaNo().equals("RM2") && temp.get(2).getReplicaNo().equals("RM3")) {
					RM1_fault++;
				}
			}

		} else if (temp.size() == 2) {
			if (temp.get(0).getStatus().equalsIgnoreCase(temp.get(1).getStatus())) {
				result = temp.get(0).getResponse() + "->" + temp.get(0).getStatus();
				if (temp.get(0).getReplicaNo().equals("RM1") && temp.get(1).getReplicaNo().equals("RM3")) {
					RM2_fault++;
				} else if (temp.get(0).getReplicaNo().equals("RM1") && temp.get(1).getReplicaNo().equals("RM2")) {
					RM3_fault++;
				} else if (temp.get(0).getReplicaNo().equals("RM2") && temp.get(1).getReplicaNo().equals("RM3")) {
					RM1_fault++;
				}
			} else {
				result = temp.get(0).getResponse() + "->" + temp.get(0).getStatus();
			}
		} else if (temp.size() == 1) {
			result = temp.get(0).getResponse() + "->" + temp.get(0).getStatus();
			if (temp.get(0).getReplicaNo().equals("RM1")) {
				RM2_fault++;
				RM3_fault++;
			} else if (temp.get(0).getReplicaNo().equals("RM2")) {
				RM1_fault++;
				RM3_fault++;
			} else if (temp.get(0).getReplicaNo().equals("RM3")) {
				RM1_fault++;
				RM2_fault++;
			}
		} else if (temp.size() == 0) {
			result = "Opps! Server is too busy to handle your request!!!";
		} else {
			for (int i = 0; i < temp.size(); i++) {
				System.out.println(i);
				if (temp.get(i).getRequest().equals(function)) {
					result = temp.get(i).getResponse() + "->" + temp.get(i).getStatus();
					// temp.clear();
					// this.responseList = temp;
				}
			}
		}
		System.out.println("==============Fault Counts==============\nRM1 Fault : " + RM1_fault + "\nRM2 Fault : "
				+ RM2_fault + "\nRM3 Fault : " + RM3_fault + "\n=====================================");
		if (RM1_fault >= 3) {
			notifyRMforFault("RM1");
			RM1_fault = 0;
		}
		if (RM2_fault >= 3) {
			notifyRMforFault("RM2");
			RM2_fault = 0;
		}
		if (RM3_fault >= 3) {
			notifyRMforFault("RM3");
			RM3_fault = 0;
		}
		temp.clear();
		this.responseList = temp;
		System.out.println("Final Result in FrontEnd for Client : " + result);
		return result;
	}

	public void connectRMsForFaultRecovery() {
		String f_message = "fault message;Connection Message;";
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			InetAddress ip = InetAddress.getByName(Fault_Multicast_IPAdrress);
			byte[] sendrequestmessage = f_message.getBytes();
			DatagramPacket DpSend = new DatagramPacket(sendrequestmessage, sendrequestmessage.length, ip,
					Fault_Multicast_Port);
			ds.send(DpSend);
			// System.out.println("Sent a Messages to ReplicaManager for handling faulty
			// Replicas");
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (ds != null)
				ds.close();
		}
	}

	public void notifyRMforFault(String RM) { // ============================== Notify the Replica Manager of the faulty
												// replica ==================== //
		String f_message = "fault message;" + RM + ";";
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			InetAddress ip = InetAddress.getByName(Fault_Multicast_IPAdrress);
			byte[] sendrequestmessage = f_message.getBytes();
			DatagramPacket DpSend = new DatagramPacket(sendrequestmessage, sendrequestmessage.length, ip,
					Fault_Multicast_Port);
			ds.send(DpSend);
			System.out.println("Sent a Messages to ReplicaManager for handling faulty Replicas");
		} catch (Exception e) {
			System.out.println(e);
		} finally {
			if (ds != null)
				ds.close();
		}

	}

}

public class FrontEnd {

	private static final int FE_multicast_Port = 4321;
	private static final String FE_multicast_IPadress = "230.1.1.10";

	/**
	 * 
	 */
	public FrontEnd() {
		// TODO Auto-generated constructor stub
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			ORB orb = ORB.init(args, null);
			POA rootpoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
			rootpoa.the_POAManager().activate();

			// create servant and register it with the ORB
			FrontEndImpl frontendobj = new FrontEndImpl();
			frontendobj.setORB(orb);
			frontendobj.connectRMsForFaultRecovery();
			// get object reference from the servant
			org.omg.CORBA.Object ref = rootpoa.servant_to_reference(frontendobj);
			DAMS href = DAMSHelper.narrow(ref);

			org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
			NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);

			// bind the Object Reference in Naming
			String name = "frontend";
			NameComponent path[] = ncRef.to_name(name);
			ncRef.rebind(path, href);

			System.out.println("FrontEnd Server ready and waiting ...");

			Runnable UDPServer = new Runnable() {
				@Override
				public void run() {
					// TODO Auto-generated method stub
					receiveClientRequest(frontendobj);
				}
			};
			Thread t = new Thread(UDPServer);
			t.start();

			for (;;) {
				orb.run();
			}
		} catch (Exception e) {
			System.err.println("ERROR: " + e);
			e.printStackTrace(System.out);
		}

	}

	public static void receiveClientRequest(FrontEndImpl frontendobj) {
		MulticastSocket ms = null;
		try {
			ms = new MulticastSocket(FE_multicast_Port);
			ms.joinGroup(InetAddress.getByName(FE_multicast_IPadress));
			byte[] buf = new byte[10000];
			// System.out.println("in FE receive");
			while (true) {
				DatagramPacket RMresponse = new DatagramPacket(buf, buf.length);
				// System.out.println("Waiting for a response from ReplicaManager");
				ms.receive(RMresponse);
				// System.out.println("Received a response from ReplicaManager");
				String response = new String(RMresponse.getData(), 0, RMresponse.getLength());
				if (response.equals("Connection Message"))
					continue;
				// System.out.println("Response in FrontEnd : "+response);

				String[] result = response.split(";");
				ReplicaResponse rr = new ReplicaResponse();
				rr.setReplicaNo(result[0]);
				rr.setSeqId(Integer.parseInt(result[1]));
				rr.setRequest(result[2]);
				rr.setParams(result[3]);
				rr.setResponse(result[4]);
				rr.setStatus(result[5]);
				frontendobj.addResponse(rr);
				System.out.println("[INFO] "+result[0]+" Response Received.");
				// String[] responseArray = response.split(";");
				// SequencerMessage sm = new
				// SequencerMessage(Integer.parseInt(responseArray[0]),responseArray[1] + ";"
				// +responseArray[2] + ";");
			}
		} catch (Exception e) {
			System.out.println("Exception : " + e);
		} finally {
			System.out.println("MulticastSocket is currently unavailable!");
			if (ms != null)
				ms.close();
		}
	}

}
