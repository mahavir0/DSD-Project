/**
 * 
 */
package FrontEnd;

import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

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

	
	//String params = patientID + "=" + appointmentID + "=" + appoinmentType + "=" + capacity + "=" + newAppointmentID + "=" + newAppoinmentType;
	//return params;
	
	private ORB orb;
	private ArrayList<ReplicaResponse> responseList = new ArrayList<ReplicaResponse>();
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	
	@Override
	public String addAppointment(String appointmentID, String appoinmentType, String capacity) {
		// TODO Auto-generated method stub
		String params = null + "=" + appointmentID + "=" + appoinmentType + "=" + capacity + "=" + null + "=" + null; 
		this.sendtoSequencer("addAppointment", params);
		return params;
		//return null;
	}

	@Override
	public String removeAppointment(String appointmentID, String appoinmentType) {
		// TODO Auto-generated method stub
		//return "remove appointment";
		String params = null + "=" + appointmentID + "=" + appoinmentType + "=" + null + "=" + null + "=" + null;
		this.sendtoSequencer("removeAppointment", params);
		return params;
	}

	@Override
	public String listAppointmentAvailability(String appoinmentType) {
		// TODO Auto-generated method stub
		//return "list appointment";
		String params = null + "=" + null + "=" + appoinmentType + "=" + null + "=" + null + "=" + null;
		this.sendtoSequencer("listAppointmentAvailability", params);
		return params;
	}

	@Override
	public String bookAppointment(String patientID, String appointmentID, String appointmentType) {
		// TODO Auto-generated method stub
		//return "book appointment";
		String params = patientID + "=" + appointmentID + "=" + appointmentType + "=" + null + "=" + null + "=" + null;
		this.sendtoSequencer("bookAppointment", params);
		return params;
	}

	@Override
	public String getAppointmentSchedule(String patientID) {
		// TODO Auto-generated method stub
		//return "get appointment";
		//return null;
		String params = patientID + "=" + null + "=" + null + "=" + null + "=" + null + "=" + null;
		this.sendtoSequencer("getAppointmentSchedule", params);
		return params;
	}

	@Override
	public String cancelAppointment(String patientID, String appointmentID) {
		// TODO Auto-generated method stub
		//return "cancel appointment";
		String params = patientID + "=" + appointmentID + "=" + null + "=" + null + "=" + null + "=" + null;
		this.sendtoSequencer("cancelAppointment", params);
		return params;
	}

	@Override
	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppoinmentType,
			String newAppointmentID, String newAppoinmentType) {
		// TODO Auto-generated method stub
		//return "swap appointment";
		String params = patientID + "=" + oldAppointmentID + "=" + oldAppoinmentType + "=" + null + "=" + newAppointmentID + "=" + newAppoinmentType;
		this.sendtoSequencer("swapAppointment", params);
		return params;
		//return null;
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
	
	public void sendtoSequencer(String request,String params) {  // ============================== to send messages to sequencer======================================== //
		String sendrequest = request + ";" + params + ";";
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket();
			InetAddress ip = InetAddress.getLocalHost();
			byte[] sendrequestmessage = sendrequest.getBytes();
			DatagramPacket DpSend = new DatagramPacket(sendrequestmessage, sendrequestmessage.length, ip, 5555);
			ds.send(DpSend);
		}catch(Exception e) {
			System.out.println("Exception "+ e);
		}finally {
			if(ds!=null)
				ds.close();
		}
	}
	
	public void waitingForResponseFromRM(){ // ======================= 5 seconds timeout for to get all the responses ====================== //
		try {
            System.out.println("Waiting for the responses of RMs...");
            Thread.sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public String getRMResponses() { // ============================== to get the messages from RMs========================================= //
		return null;
	}
	
	public void notifyRMforFault() { // ============================== Notify the Replica Manager of the faulty replica ==================== //
		
	}
	
	
}

public class FrontEnd {

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
		    
		    // get object reference from the servant
		    org.omg.CORBA.Object ref = rootpoa.servant_to_reference(frontendobj);
		    DAMS href = DAMSHelper.narrow(ref);
		    
		    org.omg.CORBA.Object objRef = orb.resolve_initial_references("NameService");
		    NamingContextExt ncRef = NamingContextExtHelper.narrow(objRef);
		    
		    // bind the Object Reference in Naming
		    String name = "frontend";
		    NameComponent path[] = ncRef.to_name( name );
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
		    
		    while(true) {
		    	orb.run();
		    }
		}catch(Exception e) {
			System.err.println("ERROR: " + e);
	        e.printStackTrace(System.out);
		}

	}
	
	public static void receiveClientRequest(FrontEndImpl frontendobj) {
		while(true) {
			
		}
	}

}
