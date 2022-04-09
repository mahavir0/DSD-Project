package ReplicaManager3.Implementation;
import java.io.*;
import java.rmi.RemoteException;
import java.util.*;

import ReplicaManager3.Helper.Appointment;
import ReplicaManager3.Helper.AppointmentList;
import ReplicaManager3.Helper.log_create;
import ReplicaManager3.Helper.forward_request;
import ReplicaManager3.Helper.booking_countcheck;


//import ServerObjectInterfacezApp.DAMSPOA;

public class Montreal_Class {
	static Map<String,Map<String ,Appointment>> appointments = new HashMap<>();
	static Map<String, Appointment> appointmentlists = new HashMap<>();
	static Map<String,List<AppointmentList>> appointment1 = new HashMap<>();

	private int mtlPort = 1111;
	private int shePort = 2222;
	private int quePort = 3333;
	//private String code ="MTL";
	
	public Montreal_Class() {
		super();
		
		//HashMap<String,Map<String ,Appointment>> appointments = new HashMap<>();
		HashMap<String, Appointment> appointmentlists = new HashMap<>();
		HashMap<String, Appointment> appointmentlists1 = new HashMap<>();
		HashMap<String, Appointment> appointmentlists2 = new HashMap<>();
		HashMap<String, Appointment> appointmentlists3 = new HashMap<>();
		HashMap<String,AppointmentList> appointment1 = new HashMap<>();

		appointmentlists1.put("MTLA140222",new Appointment(10));
		appointments.put("DENTAL", appointmentlists1);
		
		appointmentlists2.put("MTLE140222" , new Appointment(10));
		appointments.put("PHYSICIAN", appointmentlists2);
		
		appointmentlists3.put("MTLN140222" , new Appointment(10));
		appointments.put("SURGEON", appointmentlists3);
		
		// TODO Auto-generated constructor stub
			
	}
	
	public String handleFault() {
		appointments.clear();
		appointmentlists.clear();
		appointment1.clear();
		return "Fault has been cleared & performing the data consistency";
	}

	public String addAppointment( String adminId, String appointmentID, String appointmentType, int capacity) {
		//String adminId = "MTLA1111";
		System.out.println("In the add appointment");
		String region_ = adminId.substring(0,3).toUpperCase();
		String user_prefix = appointmentID.substring(0,3).toUpperCase();
		String user_type = adminId.substring(3,4);
		//check only admin can add details
		String operation = "addAppointment" +adminId+ "|" +appointmentID + "|"+ appointmentType;
		String action1 = "Appointment notAdded" + appointmentID;
		//check appointment id
		
		String result;
		String status;
		
		//String data_check = listAppointmentAvailability(adminId,"");
		
		if(user_prefix.equals("MTL")){
			
			if (appointments.get(appointmentType).containsKey(appointmentID)) {
				if(appointments.get(appointmentType).get(appointmentID).getNumofappointment() > 0) {
					appointments.get(appointmentType).get(appointmentID).setNumofappointment(capacity);
					//return appointments.toString();
					
					try {
						log_create.client_log_create(adminId, operation, user_type);
						log_create.server_log_create(adminId, operation, "Appointment added", "Success", "USER:"+ "MONserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
					}
					catch (IOException e){
						e.printStackTrace();
					}
					result = "Appointment added Successfully";
					status = "Success";

					return result+";"+status;
				}
				
				try {
					log_create.client_log_create(adminId, action1, user_type);
					log_create.server_log_create(adminId, action1, "Appointment Notadded", "Failed", "USER: "+ "MONserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				result = "Appointment not added Successfully";
				status = "Failed";
				return result+";"+status;
			}
			else {
				//appointments.put(appointmentType, );
				Appointment app = new Appointment(capacity);
				Map<String, Appointment> hasma = appointments.get(appointmentType);
				//Map<String,Appointment> hasma  =
				System.out.println(hasma);
				hasma.put(appointmentID, app);
				//return appointments.toString();
				try {
					log_create.client_log_create(adminId, operation, user_type);
					log_create.server_log_create(adminId, operation, "Appointment added", "Success", "USER:"+ "MONserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				result = "Appointment added Successfully";
				status = "Success";

				return result+";"+status;
			}		
		}		
/*		
		else if(user_prefix.equals("SHE")){
			String result =forward_request.send_request(shePort,"addappointment",adminId,appointmentType,appointmentID,capacity); 
			Boolean a = Boolean.parseBoolean(result);
			if(a.equals(true)) {
				try {
					log_create.client_log_create(adminId, operation, user_type);
					log_create.server_log_create(adminId, operation, "Appointment added", "Success", "USER:"+ "SHEserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
				}
				catch (IOException e){
					e.printStackTrace();
				}
			}
			
			else {
			
				try {
					log_create.client_log_create(adminId, action1, user_type);
					log_create.server_log_create(adminId, action1, "Appointment Notadded", "Failed", "USER: "+ "SHEserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				
			}
			return Boolean.parseBoolean(result);
		}
		
		else if (user_prefix.equals("QUE")) {
			String result =forward_request.send_request(quePort,"addappointment",adminId,appointmentType,appointmentID,capacity); 
			Boolean a = Boolean.parseBoolean(result);
			if(a.equals(true)) {
				try {
					log_create.client_log_create(adminId, operation, user_type);
					log_create.server_log_create(adminId, operation, "Appointment added", "Success", "USER:"+ "QUEserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
				}
				catch (IOException e){
					e.printStackTrace();
				}
			}
			
			else {
				
				try {
					log_create.client_log_create(adminId, action1, user_type);
					log_create.server_log_create(adminId, action1, "Appointment Notadded", "Failed", "USER: "+ "QUEserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
				}
				catch (IOException e){
					e.printStackTrace();
				}
			}
			return Boolean.parseBoolean(result);
		}
*/	
		else {
			
			try {
				log_create.client_log_create(adminId, action1, user_type);
				log_create.server_log_create(adminId, action1, "Appointment Notadded", "Failed", "USER: "+ "MTLserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
			}
			catch (IOException e){
				e.printStackTrace();
			}
			result = "Appointment not added Successfully";
			status = "Failed";
			return result+";"+status;
		}

}	

	public String removeAppointment(String adminId, String appointmentID, String appointmentType, int capacity)
			{
		String region_ = adminId.substring(0,3).toUpperCase();
		String user_prefix = appointmentID.substring(0,3).toUpperCase();
		String user_type = adminId.substring(3,4);
		String adminid = adminId;
		String result;
		String status;
		if(user_prefix.equals("MTL")) {
			if(appointments.get(appointmentType).containsKey(appointmentID)) {
				//int a = appointments.get(appointmentType).get(appointmentID).getNumofappointment();
				//appointments.get(appointmentType).get(appointmentID).setNumofappointment(capacity);
				appointments.get(appointmentType).get(appointmentID).setNumofappointment(0);
				String action = "Appointment removed" + appointmentID;
				try {
					log_create.client_log_create(adminId, action, user_type);
					log_create.server_log_create(adminId, action, "removed", "Success", "USER: "+"MONserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointmentRemoved: "+capacity );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				System.out.println(action+""+"Success");
				return action+";"+"Success";
			}
			else {
				String action = "Appointment notremoved" + appointmentID;
				try {
					log_create.client_log_create(adminId, action, user_type);
					log_create.server_log_create(adminId, action, "Appointment notremoved", "Fail", "USER: "+"MONserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointmentRemoved: "+capacity );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				
				return action+";"+"Failed";
			}
		}
		
			String operation = "Appointment removed" + appointmentID;
			String action1 = "Appointment notremoved" + appointmentID;
			try {
				log_create.client_log_create(adminId, action1, user_type);
				log_create.server_log_create(adminId, action1, "Appointment Notadded", "Failed", "USER: "+ "QUEserver"+adminId+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: "+capacity );
			}
			catch (IOException e){
				e.printStackTrace();
			}
		return action1+";"+"Failed";	
		}
	

	
	public String listAppointmentAvailability(String username,String appointmentType) {
		// TODO Auto-generated method stub
		String res;
		String mtl = appointments.toString();
		String que ;
		String she;
		String check = username.substring(0,3);
		
		
		if(check.equals("MTL")) {
			que = forward_request.send_request(quePort,"listAvailability",username,"","",0).toString();
			she = forward_request.send_request(shePort,"listAvailability",username,"","",0).toString();
			res = mtl + que + she ;
			
			String action = "List of Appointments" ;
			try {
				log_create.client_log_create(username, action, "success");
				log_create.server_log_create(username, action, res, "Success", "USER: "+"Allserver"+username);
			}
			catch (IOException e){
				e.printStackTrace();
			}
			return res;
		}
		else {
			String action = "List of Appointments" ;
			try {
				log_create.client_log_create(username, action, "success");
				log_create.server_log_create(username, action, "MonServeraccessed", "Success", "USER: "+"MONserver"+username);
			}
			catch (IOException e){
				e.printStackTrace();
			}
		
			return mtl.toString();
		}
	}

	
	public String bookAppointment(String patientID, String appointmentID, String appointmentType)
			{
	    int count = 0;
		String code = "MTL";
		System.out.println("BACK TO MONT");
		String appointment_prefix = appointmentID.substring(0, Math.min(appointmentType.length(), 3)).toUpperCase();
		System.out.println(appointment_prefix);
		String userPrefix = patientID.substring(0, Math.min(patientID.length(), 3)).toUpperCase();
		String userType = patientID.substring(3, Math.min(patientID.length(), 4)).toUpperCase();
		System.out.println("pid" + patientID + "aid" + appointmentID + "atype" +appointmentType);
		int check_numbers_of_outerside_app ;
		if (appointment_prefix.equals("SHE") ||appointment_prefix.equals("QUE") ) {
			String data = getAppointmentSchedule(patientID);
			System.out.println(data);
			check_numbers_of_outerside_app = booking_countcheck.check_appointment_counts(code,data);
			System.out.println(check_numbers_of_outerside_app);
		}
		else {
			check_numbers_of_outerside_app = 0;
		}
		//System.out.println("count:"+check_numbers_of_outerside_app );
		
		//String date_ = appointmentID.s
		
		//System.out.println(check_numbers_of_outerside_app);
		//count = check_appointment_counts(String a,String code);
		System.out.println("debug");
		if(check_numbers_of_outerside_app <3) {
			System.out.println("1");
		if(code.equals(appointment_prefix)){
			System.out.println("2");
			System.out.println(code.equals(appointment_prefix));
			if(appointments.get(appointmentType).containsKey(appointmentID)){
				//if(appointments.containsKey(patientID)) {
				System.out.println("3");
				if(appointments.get(appointmentType).get(appointmentID).getNumofappointment()>0) {	
					if(appointment1.containsKey(patientID)) {
						System.out.println("4");
						// IF WANT TO BOOK ONLY SINGLE APPOINTMENT FROM OTHER SERVERS userPrefix.equals(appointmenttype)
						if(code.equals(appointment_prefix)) {
							List<AppointmentList> lis = appointment1.get(patientID);
							System.out.println("5");
							if(!lis.stream().filter( o -> o.getAppointmentID().equals(appointmentID)).filter(o -> o.getAppointmentType().equals(appointmentType)).findAny().isPresent()) {	
								System.out.println("6");
								lis.add(new AppointmentList(appointmentID,appointmentType));
								System.out.println("******************************************");
								System.out.println(lis);
								appointment1.replace(patientID,lis);
								int left = appointments.get(appointmentType).get(appointmentID).getNumofappointment() -1 ;
								appointments.get(appointmentType).get(appointmentID).setNumofappointment(left);
								
								String action = "Appointment Booked" + appointmentID;
								try {
									log_create.client_log_create(patientID, action, "true");
									log_create.server_log_create(patientID, action, "TRUE", "Success", "USER: "+"mtlserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType );
								}
								catch (IOException e){
									e.printStackTrace();
								}		
								return action+";"+"Success"; 
								//return true;
							}
							else {
								System.out.println("7");
								String action = "Appointment not Booked" + appointmentID;
								try {
									log_create.client_log_create(patientID, action, "false");
									log_create.server_log_create(patientID, action, "false", "failed", "USER:"+"mtlserver" +patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType );
								}
								catch (IOException e){
									e.printStackTrace();
								}
								return action+";"+"Failed";
								//return false;
							}
						}
					 }
				
				else {
					System.out.println("8");
					List<AppointmentList> items = new ArrayList<>();
					items.add(new AppointmentList(appointmentID,appointmentType));
					appointment1.put(patientID, items);
					int left = appointments.get(appointmentType).get(appointmentID).getNumofappointment() -1 ;
					appointments.get(appointmentType).get(appointmentID).setNumofappointment(left);
					String action = "Appointment Booked" + appointmentID + ";" + "Success" ;
					try {
						log_create.client_log_create(patientID, action, "true");
						log_create.server_log_create(patientID, action, "TRUE", "Success", "USER: "+"mtlserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType );
					}
					catch (IOException e){
						e.printStackTrace();
					}
					//return "Appointment booked " + ";" + "Success";
					return action;
				}
				}
			}
			else {
				System.out.println("9");
				String action = "Appointment not Booked" + appointmentID + ";" + "Failed";
				System.out.println("action onf montreal" + action);
				try {
					log_create.client_log_create(patientID, action, "false");
					log_create.server_log_create(patientID, action, "false", "failed", "USER:"+"mtlserver" +patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				return action;
				//return false;
			}
		}
		else if(appointment_prefix.equals("QUE")) {
				//count = check_appointment_counts()
			System.out.println("10");
				String result = forward_request.send_request(quePort,"bookappointment",patientID,appointmentType,appointmentID,0);
				String[] a_ = result.split(";"); 
				String a  = a_[1];
				System.out.println("Inside montreal checking a: " + a);
				//Boolean a = Boolean.parseBoolean(result);
				String operation = "Appointment Booked" + appointmentID  + ";"+ "Success";
				String action1 = "Appointment notBooked" + appointmentID + ";" +"Failed";
				
				if(a.equals("Success")) {
					try {
						log_create.client_log_create(patientID, operation, "true");
						log_create.server_log_create(patientID, operation, "Appointment added", "Success", "USER:"+ "queserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
					}
					catch (IOException e){
						e.printStackTrace();
					}
					System.out.println("checking for montreal side : " + operation);
					return operation ;
					
				}
				else {
					
					try {
						log_create.client_log_create(patientID, action1, "false");
						log_create.server_log_create(patientID, action1, "Appointment Notadded", "Failed", "USER: "+ "queserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
					}
					catch (IOException e){
						e.printStackTrace();
					}
					System.out.println("Checking for the montreal inside else :" + action1);
					return action1 ;
				}	

			}
		else if(appointment_prefix.equals("SHE")) {
				String result = forward_request.send_request(shePort, "bookappointment", patientID, appointmentType, appointmentID,0);
				//Boolean a = Boolean.parseBoolean(result);
				
				String[] a_ = result.split(";"); 
				String a  = a_[1];
				
				String operation = "Appointment Booked" + appointmentID;
				String action1 = "Appointment notBooked" + appointmentID;
				if(a.equals("Success")) {
					try {
						log_create.client_log_create(patientID, operation, "true");
						log_create.server_log_create(patientID, operation, "Appointment added", "Success", "USER:"+ "SHEserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
					}
					catch (IOException e){
						e.printStackTrace();
					}
					return operation + ";" + a;
				}
				
				else {
					
					try {
						log_create.client_log_create(patientID, action1, "false");
						log_create.server_log_create(patientID, action1, "Appointment Notbooked", "Failed", "USER: "+ "SHEserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
					}
					catch (IOException e){
						e.printStackTrace();
					}
				}
				return action1 + ";" + a;
			}
		System.out.println("11");
		String action1 = "Appointment notBooked" + appointmentID;
		try {
			log_create.client_log_create(patientID, "appointmentbook", "false");
			log_create.server_log_create(patientID, "", "appointmentbook", "Failed", "USER: "+ "QUEServer"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
		}
		catch (IOException e){
			e.printStackTrace();
		}	
		return action1 + ";" + "Failed";
		}
		return "" + ";" + "Failed";
		
	}
	
	
	
	public String getAppointmentSchedule(String patientID) {
		System.out.println("Here1");
		String check = patientID.substring(0,3);
		String res;
		String mtl = "";
		String operation = "getAppointmentSchedule" ;
		if(check.equals("MTL")) {
			if(appointment1.containsKey(patientID)) {
				mtl = appointment1.get(patientID).toString();
				}
			else {
				mtl = "";
			}
			System.out.println("mtl"+mtl);
			String que = forward_request.send_request(quePort,"getappointment",patientID,"","",0).toString();
			System.out.println("que"+que);
			String she = forward_request.send_request(shePort,"getappointment",patientID,"","",0).toString();
			System.out.println(she);
			res = mtl +que +she ;
			System.out.println(res);
//			System.out.println(she);
//			System.out.println(que);
			
			try {
				log_create.client_log_create(patientID,operation, "true");
				log_create.server_log_create(patientID, operation, "getAppointmentSchedule done", "Success", "USER:"+ "allserver"+ res );
			}
			catch (IOException e){
				e.printStackTrace();
			}
			return res;
			}
			
		else if (check.equals("SHE") || check.equals("QUE")){
			if(appointment1.containsKey(patientID)) {
				try {
					log_create.client_log_create(patientID,operation, "true");
					log_create.server_log_create(patientID, operation, "getAppointmentSchedule done", "Success", "USER:"+ "sheorqueserver"+ appointment1.get(patientID).toString() );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				return appointment1.get(patientID).toString();
			}
		}
		return " ";
	}
	
	


	public String cancelAppointment(String patientID, String appointmentID, String appointmentType) {
		// TODO Auto-generated method stub
		
		String code = "MTL";
		String appointment_prefix = appointmentID.substring(0, Math.min(appointmentType.length(), 3));
		
		String userPrefix = patientID.substring(0, Math.min(patientID.length(), 3));
		String userType = patientID.substring(3, Math.min(patientID.length(), 4));
		
		if(appointment_prefix.equals(code)) {
			if(appointment1.containsKey(patientID)) {
				
				List<AppointmentList> app = appointment1.get(patientID);
				for(Iterator<AppointmentList> i = app.iterator(); i.hasNext();) {
					AppointmentList val = i.next(); 
					if(val.getAppointmentID().equals(appointmentID) && val.getAppointmentType().equals(appointmentType)) {
						if((appointments.get(appointmentType).containsKey(appointmentID))) {
							i.remove();
							int left = appointments.get(appointmentType).get(appointmentID).getNumofappointment() +1 ;
							appointments.get(appointmentType).get(appointmentID).setNumofappointment(left);

							}

						String action = "Appointment Cancelled" + appointmentID;
						try {
							log_create.client_log_create(patientID, action, "true");
							log_create.server_log_create(patientID, action, "true", "Success", "USER: "+"MONserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType );
						}
						catch (IOException e){
							e.printStackTrace();
						}

							return action + ";" + "Success" ;
						}
					}
				
				
					String action = "Appointment not Cancelled" + appointmentID;
					try {
						log_create.client_log_create(patientID, action, "false");
						log_create.server_log_create(patientID, action, "false", "failed", "USER: "+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType );
					}
					catch (IOException e){
						e.printStackTrace();
					}
					return action + ";" + "Failed" ;
				}
		}
		else if(appointment_prefix.equals("QUE")) {
			String result = forward_request.send_request(quePort,"cancelappointment",patientID,appointmentType,appointmentID,0);
			//Boolean a = Boolean.parseBoolean(result);
			String[] a_ = result.split(";"); 
			String a  = a_[1];
			
			String operation = "Appointment Cancelled" + appointmentID;
			String action1 = "Appointment not Cancelled" + appointmentID;
			if(a.equals("Success")) {
				try {
					log_create.client_log_create(patientID, operation, "true");
					log_create.server_log_create(patientID, operation, "true", "Success", "USER:"+ "QUEserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				return operation +";"+ a;
			}
			
			else {
				
				try {
					log_create.client_log_create(patientID, action1, "false");
					log_create.server_log_create(patientID, action1, "fail", "Failed", "USER: "+ "QUEserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				return action1 +";"+ a;
			}
		}
	    
		else if(appointment_prefix.equals("SHE")) {
			String result = forward_request.send_request(shePort, "cancelappointment", patientID, appointmentType, appointmentID,0);
			String[] a_ = result.split(";"); 
			String a  = a_[1];
			//Boolean a = Boolean.parseBoolean(result);
			String operation = "Appointment Cancelled" + appointmentID;
			String action1 = "Appointment not Cancelled" + appointmentID;
			if(a.equals("operation")) {
				try {
					log_create.client_log_create(patientID, operation, "true");
					log_create.server_log_create(patientID, operation, "true", "Success", "USER:"+ "SHEserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				return operation + ";" +a;
			}
			
			else {
				
				try {
					log_create.client_log_create(patientID, action1, "false");
					log_create.server_log_create(patientID, action1, "fail", "Failed", "USER: "+ "SHEserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
				}
				catch (IOException e){
					e.printStackTrace();
				}
				return action1 + ";" +a;
			}
			
		}
		
		String action1 = "Appointment not Cancelled" + appointmentID;
		try {
			log_create.client_log_create(patientID, action1, "false");
			log_create.server_log_create(patientID, action1, "fail", "Failed", "USER: "+ "SHEserver"+patientID+"/ AppointmentId: "+ appointmentID+"/ AppointmentType: "+appointmentType+"/ NumofAppointment: " );
		}
		catch (IOException e){
			e.printStackTrace();
		}
		
		return action1 + ";" +"Failed";
	}

	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppointmentType,
			String newAppointmentID, String newAppointmentType) throws RemoteException {
		//boolean newappAvailable = false;
		//boolean oldappAvailable = false;
		
		//boolean checkAlreadyBorrowed = true;
		String patient_id = patientID.substring(0, 3).toUpperCase();
		
		String old_prefix = oldAppointmentID.substring(0,3).toUpperCase();
		
		String new_prefix = newAppointmentID.substring(0,3).toUpperCase();

		if(old_prefix.equals("MTL")) {
			if(appointment1.containsKey(patientID)) {
					String a = cancelAppointment(patientID, oldAppointmentID, oldAppointmentType);
					String[]  a_ = a.split(";");
					if (a_[1].equals("Success")) {
						if(new_prefix.equals("SHE")) {
							//she
							String result = forward_request.send_request(shePort,"bookappointment",patientID,newAppointmentType,newAppointmentID,0);
							String[] a__ = result.split(";");
							if(a__[1].equals("Success")) {
								
								String action = "Appointment Swapped" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType + ";" + "Success";
								try {
									log_create.client_log_create(patientID, action, "true");
									log_create.server_log_create(patientID, action, "true", "Success", "USER: "+"MONservertoSHEserver"+patientID+"/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
								}
								catch (IOException e){
									e.printStackTrace();
								}

								
								return action;
							}
							else {
								String b_ =  bookAppointment(patientID, oldAppointmentID,  oldAppointmentType);
								String action = "Appointment not Swapped" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType + ";" + "Failed" ;
								try {
									log_create.client_log_create(patientID, action, "false");
									log_create.server_log_create(patientID, action, "false", "FAILED", "USER: "+"MONservertoSHEserver"+ patientID + "/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
								}
								catch (IOException e){
									e.printStackTrace();
								}
								
								return action;
							}
							
						}
						else if (new_prefix.equals("QUE")) {
							// que
							String result = forward_request.send_request(quePort,"bookappointment",patientID,newAppointmentType,newAppointmentID,0);
							String[] a__ = result.split(";");
							if(a__[1].equals("Success")) {

								String action = "Appointment Swapped" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType + ";" +"Success";
								try {
									log_create.client_log_create(patientID, action, "true");
									log_create.server_log_create(patientID, action, "true", "Success", "USER: "+"MONservertoQUEserver"+patientID+"/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
								}
								catch (IOException e){
									e.printStackTrace();
								}
								//System.out.println(a_);
								return action;
							}
							else {
								String b_ =  bookAppointment(patientID, oldAppointmentID,  oldAppointmentType);
								//System.out.println(a_);
								String action = "Appointment not Swapped" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType + ";"+ "Failed";
								try {
									log_create.client_log_create(patientID, action, "false");
									log_create.server_log_create(patientID, action, "false", "FAILED", "USER: "+"MONservertoQUEserver"+ patientID + "/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
								}
								catch (IOException e){
									e.printStackTrace();
								}
								return action;
							}
						}
						
						else{
							System.out.println("mtl");
							String result = bookAppointment(patientID,  newAppointmentID, newAppointmentType);
							String[] a__ = result.split(";");
							if(a__[1].equals("Success")) {
								
								String action = "Appointment Swapped" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType+ ";"+ "Success";
								try {
									log_create.client_log_create(patientID, action, "true");
									log_create.server_log_create(patientID, action, "true", "Success", "USER: "+"MONservertoMONserver"+patientID+"/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
								}
								catch (IOException e){
									e.printStackTrace();
								}
								
								return action;
							}
							else {
								String b_ =  bookAppointment(patientID, oldAppointmentID,  oldAppointmentType);
								String action = "Appointment not Swapped" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType+";" + "Failed";
								try {
									log_create.client_log_create(patientID, action, "false");
									log_create.server_log_create(patientID, action, "false", "FAILED", "USER: "+"MONservertoMONserver"+ patientID + "/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
								}
								catch (IOException e){
									e.printStackTrace();
								}
								return action;
							}
						}
					}
					
				
			}
			
		}
		else if (old_prefix.equals("SHE")){
			//int port , String operation , String userid , String oldAppointmentID , String oldAppointmentType, String newAppointmentID , String newAppointmentType
			String result_ = forward_request.send_request_(shePort, "swapappointment", patientID, oldAppointmentID ,oldAppointmentType ,newAppointmentID, newAppointmentType);
			//Boolean a = Boolean.parseBoolean(result_);
			String[] a__ = result_.split(";");
			if(a__[1].equals("Success")) {
			String action = "Sending request to she server for swapappointment" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType+";"+"Success";
			try {
				log_create.client_log_create(patientID, action, "true");
				log_create.server_log_create(patientID, action, "true", "Success", "USER: "+"MONservertoSHEserver"+patientID+"/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
			}
			catch (IOException e){
				e.printStackTrace();
			}
			return action;
			}
			return "data"+";" + "Failed";
			
		}
		
		else if (old_prefix.equals("QUE")) {
			String result_ = forward_request.send_request_(quePort, "swapappointment", patientID, oldAppointmentID ,oldAppointmentType ,newAppointmentID, newAppointmentType);
			//Boolean a = Boolean.parseBoolean(result_);
			String[] a__ = result_.split(";");
			if(a__[1].equals("Success")) {
			String action = "Sending request to que server for swapappointment" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType+";"+"Success";
			try {
				log_create.client_log_create(patientID, action, "true");
				log_create.server_log_create(patientID, action, "true", "Success", "USER: "+"MONservertoQUEserver"+patientID+"/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
			}
			catch (IOException e){
				e.printStackTrace();
			}
			return action;
			}
			return "data" + ";"+"Failed";
		}
		
		else {
			
			String action = "Appointment not Swapped" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType +";"+ "Failed";
			try {
				log_create.client_log_create(patientID, action, "false");
				log_create.server_log_create(patientID, action, "false", "FAILED", "USER: "+"mtlserver"+ patientID + "/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
			}
			catch (IOException e){
				e.printStackTrace();
			}
			return action;
		}
		
		String action = "Appointment not Swapped" + oldAppointmentID +oldAppointmentType  +"to" + newAppointmentID + oldAppointmentType +";"+"Failed" ;
		try {
			log_create.client_log_create(patientID, action, "false");
			log_create.server_log_create(patientID, action, "false", "FAILED", "USER: "+"mtlserver"+ patientID + "/ oldAppointmentId: "+ oldAppointmentID+"/ oldAppointmentType: "+oldAppointmentType +"/ NewAppointmentId: "+ newAppointmentID+"/ newAppointmentType: "+newAppointmentType);
		}
		catch (IOException e){
			e.printStackTrace();
		}
		return action;
	}

}
