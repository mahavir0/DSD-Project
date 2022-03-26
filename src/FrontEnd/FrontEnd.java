/**
 * 
 */
package FrontEnd;

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

	private ORB orb;
	
	public void setORB(ORB orb_val) {
		orb = orb_val;
	}
	
	@Override
	public String addAppointment(String appointmentID, String appoinmentType, String capacity) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String removeAppointment(String appointmentID, String appoinmentType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String listAppointmentAvailability(String appoinmentType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String bookAppointment(String patientID, String appoinmentID, String appointmentType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getAppointmentSchedule(String patientID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String cancelAppointment(String patientID, String appoinmentID) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppoinmentType,
			String newAppointmentID, String newAppoinmentType) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void shutdown() {
		// TODO Auto-generated method stub
		orb.shutdown(false);
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
		    FrontEndImpl forntendobj = new FrontEndImpl();
		    forntendobj.setORB(orb);
		    
		    // get object reference from the servant
		    org.omg.CORBA.Object ref = rootpoa.servant_to_reference(forntendobj);
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
					receiveClientRequest(forntendobj);
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
	
	public static void receiveClientRequest(FrontEndImpl forntendobj) {
		
	}

}
