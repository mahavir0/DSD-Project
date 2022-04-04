package ReplicaManager1.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * @author Mahavir
 *
 */

public class Quebec {
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			System.out.println("Quebec Server ready and waiting ...");
		    QUEImpl queImpl = new QUEImpl();
		    
		    Runnable ReplicaServer = () -> {
				receiveRequestFromReplicaManager(queImpl);
		    };
		    
		    Runnable UDPServer = () -> {
				receiveRequestFromAnotherServer(queImpl);
		    };
		    
		    Thread t1 = new Thread(ReplicaServer);
		    Thread t2 = new Thread(UDPServer);
		    
		    t1.start();
		    t2.start();
		    
		}catch(Exception e) {
			System.err.println("ERROR: " + e);
	        e.printStackTrace(System.out);
		}

	}
	
	public static void receiveRequestFromReplicaManager(QUEImpl queobj) {
		 String result= "";
		 DatagramSocket  ds=null;
		 try {
			 ds = new DatagramSocket(2222);
			 byte[] receive = new byte[10000];
			 System.out.println("UDP Server for Quebec is running on port : 2222");
			 while(true) {
				 DatagramPacket DpReceive = null;
				 DpReceive = new DatagramPacket(receive, receive.length);
				 ds.receive(DpReceive);
				 String received_func_str = new String(DpReceive.getData(),0,DpReceive.getLength());
				 //System.out.println("Request Received : "+received_func_str);
				 //System.out.println(received_func_str);//<----------------------------------------------------------------------------------------------testing
				 String[] temp = received_func_str.split(";");
				 String func = temp[0];
				 //System.out.println(func); //<----------------------------------------------------------------------------------------------testing
				 //String[] received_parameters = temp[1].split(",");
				 List<String> parameters = new ArrayList<String>(Arrays.asList(temp[1].split("=")));
				 //System.out.println(received_parameters); //<----------------------------------------------------------------------------------------------testing
				 //System.out.println(parameters);
				 if(func.equals("addAppointment")) {
					 String[] cap = {parameters.get(3)};
					 result = queobj.addAppointment(parameters.get(1), parameters.get(2), cap);
				 }else if(func.equals("removeAppointment")) {
					 result = queobj.removeAppointment(parameters.get(1),parameters.get(2));
				 }else if(func.equals("listAppointmentAvailability")) {
					 String[] ls = queobj.listAppointmentAvailability(parameters.get(2));
					 result = String.join(",", ls).trim();
				 }else if(func.equals("bookAppointment")) {
					 result = queobj.bookAppointment(parameters.get(0),parameters.get(1),parameters.get(2));
				 }else if(func.equals("getAppointmentSchedule")) {
					 String[] ls = queobj.getAppointmentSchedule(parameters.get(0));
					 result = String.join(",", ls).trim();
				 }else if(func.equals("cancelAppointment")) {
					 result = queobj.cancelAppointment(parameters.get(0), parameters.get(1), parameters.get(2));
				 }else if(func.equals("swapAppointment")) {
					 result = queobj.swapAppointment(parameters.get(0), parameters.get(1), parameters.get(2), parameters.get(4), parameters.get(5));
				 }else if(func.equals("fault")) {
					 result = queobj.handleFault();
				 }
				 //System.out.println(result); //<----------------------------------------------------------------------------------------------testing
				 result+="@";
				 //System.out.println("Result response : "+result);
				 byte[] send_result = result.getBytes();
				 DatagramPacket send_reply = new DatagramPacket(send_result,send_result.length,DpReceive.getAddress(),DpReceive.getPort());
				 ds.send(send_reply); 
				 //System.out.println("Response sent to Replica Manager");
			 }
		 }catch(Exception e) {
			 System.out.println(e);
		 } finally {
			 if(ds!=null)
				 ds.close();
		 }
	}
	
	public static void receiveRequestFromAnotherServer(QUEImpl queobj) {
		 String result= "";
		 DatagramSocket  ds=null;
		 try {
			 ds = new DatagramSocket(4560);
			 byte[] receive = new byte[10000];
			 System.out.println("UDP Internal Server for Quebec is running on port : 4560");
			 while(true) {
				 DatagramPacket DpReceive = null;
				 DpReceive = new DatagramPacket(receive, receive.length);
				 ds.receive(DpReceive);
				 String received_func_str = new String(DpReceive.getData(),0,DpReceive.getLength());
				 //System.out.println(received_func_str);//<----------------------------------------------------------------------------------------------testing
				 String[] temp = received_func_str.split("@");
				 String func = temp[0];
				 //System.out.println(func); //<----------------------------------------------------------------------------------------------testing
				 //String[] received_parameters = temp[1].split(",");
				 List<String> parameters = new ArrayList<String>(Arrays.asList(temp[1].split(",")));
				 //System.out.println(received_parameters); //<----------------------------------------------------------------------------------------------testing
				 //System.out.println(parameters);
				 if(func.equals("list_appointment")) {
					 List<String> ls = new ArrayList<String>();
					 ls = queobj.listAppointmentAvailabilitySupport(parameters.get(0));
					 result = String.join(",", ls).trim();
				 }else if(func.equals("book_appointment")) {
					 result = queobj.bookAppointment(parameters.get(0),parameters.get(1) ,parameters.get(2));
				 }else if(func.equals("get_appointment")) {
					 List<String> data = new ArrayList<String>();
					 String[] datastringarray = new String[256];
					 datastringarray = queobj.getAppointmentSchedule(parameters.get(0));
					 data = Arrays.asList(datastringarray);
					 result = String.join(",", data).trim();
				 }else if(func.equals("cancel_appointment")) {
					 if(parameters.size()==2)
						 result = queobj.cancelAppointment(parameters.get(0), parameters.get(1));
					 if(parameters.size()==3)
						 result = queobj.cancelAppointment(parameters.get(0), parameters.get(1), parameters.get(2));
				 }else if(func.equals("increase_appointment")) {
					 queobj.increaseAppointmentCapacity(parameters.get(0), parameters.get(1));
				 }
				 //System.out.println(result); //<----------------------------------------------------------------------------------------------testing
				 result+="@";
				 byte[] send_result = result.getBytes();
				 DatagramPacket send_reply = new DatagramPacket(send_result,send_result.length,DpReceive.getAddress(),DpReceive.getPort());
				 ds.send(send_reply); 
			 }
		 }catch(Exception e) {
			 System.out.println(e);
		 } finally {
			 if(ds!=null)
				 ds.close();
		 }
	 }
}

class QUEImpl {
	public static HashMap <String, Map<String, List<String>>> app_data = new HashMap<String, Map<String,List<String>>>();
	private static int mtl_port = 7890;
	private static int que_port = 4560;
	private static int she_port = 1230;
	
	public QUEImpl() {
//		List<String> cap = new ArrayList<String>();
//		cap.add("Invalid");
//		Map <String,List<String>> temp = new HashMap<String, List<String>>();
//		temp.put("invalidAppointmentID",cap);
//		app_data.put("Dental", temp);
	}
	
	public String handleFault() {
		app_data.clear();
		return "Fault has been cleared & performing the data consistency";
	}

	public String addAppointment(String appointmentID, String appoinmentType, String[] capacity) {
		// TODO Auto-generated method stub
		synchronized(this) {
			List<String> caplist = new ArrayList<String>(Arrays.asList(capacity));
			List<String> cap = new ArrayList<String>();
			Map <String,List<String>> temp = new HashMap<String, List<String>>();
			String result;
			String status;
			
			if(app_data.containsKey(appoinmentType)) { //check same appointmentType exist or not	
				temp = app_data.get(appoinmentType);
				if(temp.containsKey(appointmentID)) { //check same appointmentID exist or not, if already exist that can't add same appointmentID again
					//System.out.println("It alrready conatins " + appointmentID);
					List<String> cap_data = temp.get(appointmentID);
					//321  System.out.println(cap_data);
					result = "It already contains the appointment.";
					status = "Failed";
				}else { //if appId does not exist then add the appointment for the appointmnetType
					//System.out.println("It does not contain "+ appointmentID);
					cap = caplist; //<---------------------------------------------
					temp.put(appointmentID, cap);
					result =  "Appointmnet added Successfully.";
					status = "Success";
				}
			}else { //if appointmentType does not exist then add everything for the appointment.
				cap = caplist; //<-----------------------------------------------------
				temp.put(appointmentID, cap);
				app_data.put(appoinmentType,temp);
				result = "Appointmnet added Successfully";
				status = "Success";
			}
			//Server Log
			try {
				serverLog("Admin","Add appointment",appointmentID+"-"+appoinmentType+"-"+cap,status,result);
			}catch(IOException e){
				System.out.println(e);
			}
			return result+";"+status;
		}
	}
	
	public String removeAppointment(String appointmentID, String appoinmentType) {
		// TODO Auto-generated method stub
		String result = "";
		String status = "";
		if(app_data.containsKey(appoinmentType)) {
			Map <String,List<String>> temp = app_data.get(appoinmentType);
			if(temp.containsKey(appointmentID)) {
				List<String> cap_data = temp.get(appointmentID);
				//check the patient are there or not
				if(cap_data.size()>1) { //there are patient reschedule the appointments for them than cancel the appointment;
					String nextAppointment = nextAvailbaleAppointment(appointmentID,appoinmentType);
					
					addAppointment(nextAppointment,appoinmentType,cap_data.toArray(new String[cap_data.size()]));
					temp.remove(appointmentID,cap_data);
					result = "The "+appointmentID+" has been removed from and patient are rescheduled on "+nextAppointment+" for "+appoinmentType;
					status = "Success";
				}else {
					temp.remove(appointmentID,cap_data);
					result = "The "+appointmentID+" has been removed from "+appoinmentType+" appointment.";
					status = "Success";
				}
			}else {
				result = "Error : This Appointment ID does not Exist";
				status = "Failed";
			}
		}else {
			result = "Error : This Appointment Type does not Exist";
			status = "Failed";
		}
		//Server Log
		try {
			serverLog("Admin","Remove appointment",appointmentID+"-"+appoinmentType,status,result);
		}catch(IOException e){
			System.out.println(e);
		}
		return result+";"+status;
	}
	
	public static String nextAvailbaleAppointment(String appointmentID,String appoinmentType) {
		String newDate_before = appointmentID.substring(0,4); //before part
		//System.out.println(newDate_before);
		String appDate = appointmentID.substring(4);  //after part
		String day_str = appDate.substring(0,2); //day
		int day = Integer.parseInt(day_str);
		String month_str = appDate.substring(2,4); //month
		int month = Integer.parseInt(month_str);
		String year_str = appDate.substring(4); //year
		int year = Integer.parseInt(year_str);
		if(day>=28) {
			day=1;
			day_str="01";
			if(month<12){
				month++;
				if(month<10) {
					month_str="0"+Integer.toString(month);
				}else {
					month_str=Integer.toString(month);
				}
			}else {
				month=1;
				month_str="01";
				year++;
				if(year<10) {
					year_str="0"+Integer.toString(year);
				}else {
					year_str=Integer.toString(year);
				}
			}
		}else {
			day++;
			if(day<10) {
				day_str="0"+Integer.toString(day);
			}else {
				day_str=Integer.toString(day);
			}
		}
		String newDate_after = day_str+month_str+year_str; //new date
		String newAppointment = newDate_before+newDate_after; //before + new date part makes new appointment
		Map <String,List<String>> temp = app_data.get(appoinmentType);
		//System.out.println(newAppointment);
		if(temp.containsKey(newAppointment)) { //check appointment is available
			newAppointment = nextAvailbaleAppointment(newAppointment,appoinmentType);
		}
		return newAppointment;
	}
	
	public String[] listAppointmentAvailability(String appoinmentType) {
		// TODO Auto-generated method stub
		List<String> result = new ArrayList<String>();
		List<String> params = new ArrayList<String>();
		params.add(appoinmentType);
		//result.add(appoinmentType);
		result = this.listAppointmentAvailabilitySupport(appoinmentType);
		String montreal_res = requestAnotherServer("list_appointment",params,mtl_port);
		String sherbrooke_res = requestAnotherServer("list_appointment",params,she_port);
		List<String> montreal_list = Stream.of(montreal_res.split(",", -1)).collect(Collectors.toList());
		List<String> she_list = Stream.of(sherbrooke_res.split(",", -1)).collect(Collectors.toList());
		result.addAll(montreal_list);
		result.addAll(she_list);
		//Server Log
		try {
			serverLog("Admin","List appointment availability",appoinmentType,"Success",String.join(", ",result));
		}catch(IOException e){
			System.out.println(e);
		}
		return result.toArray(new String[result.size()]);
	}
	
	public List listAppointmentAvailabilitySupport(String appoinmentType) {
		List<String> result = new ArrayList<String>();
		//result.add(appoinmentType);
		if(app_data.containsKey(appoinmentType)) {
			Map <String,List<String>> temp = app_data.get(appoinmentType);
			for(Map.Entry mapSubElement : temp.entrySet()) {
            	String appID = (String)mapSubElement.getKey();
            	List<String> cap_data = (List<String>)mapSubElement.getValue();
            	String avail_data = appID + " " + cap_data.get(0);
            	result.add(avail_data);
            }
		}
		return result;
	}
	
	public String bookAppointment(String patientID, String appoinmentID, String appointmentType) {
		// TODO Auto-generated method stub
		Map<String,List<String>> temp = new HashMap<String,List<String>>();
		List<String> cap = new ArrayList<String>();
		List<String> params = new ArrayList<String>();
		params.add(patientID);
		params.add(appoinmentID);
		params.add(appointmentType);
		String result = "";
		String status = "";
		boolean flag = true;
		if((appoinmentID.substring(0,3)).equals("QUE")) {
			if(canBookAppointmentToday(patientID,appoinmentID,appointmentType)) {
				if(appoinmentID.substring(0,3).equals(patientID.substring(0, 3))){
					flag = true;
				}else {
					flag = canbookthisweek(patientID,appoinmentID,appointmentType);
				}
				if(flag) {
					if(app_data.containsKey(appointmentType)) { //check if AppointmnetType is exist or not
						temp = app_data.get(appointmentType);
						if(temp.containsKey(appoinmentID)) { //Check is Appointment is available on that day
							cap = temp.get(appoinmentID);
							int capacity= Integer.parseInt(cap.get(0));
							if(capacity!=0) { //check appointments are full or not. 
								capacity--;   //if apoointmnet is available then decrease the capacity.
								cap.set(0, Integer.toString(capacity));
								cap.add(patientID); //book the appointment for the patient
								result =  "Appointment booked Successfully";
								status = "Success";
							}else { 
								result =  "All the Appointments are booked";
								status = "Failed";
							}
						}else {
							result = "Appointmnet is not available on this day";
							status = "Failed";
						}
					}else {
						result = "There are no appointmnets for "+appointmentType;
						status = "Failed";
					}
				}else {
					result = "You have exceed your week limit for booking the appointment";
					status = "Failed";
				}
				
			}else {
				result = "You cannot further book the appointments for the "+appointmentType;
				status = "Failed";
			}
		}else if((appoinmentID.substring(0,3)).equals("MTL")){
			result = requestAnotherServer("book_appointment",params,mtl_port);
			if(result.equals("Appointment booked Successfully"))
				status = "Success";
			else
				status = "Failed";
		}else if((appoinmentID.substring(0,3)).equals("SHE")){
			result = requestAnotherServer("book_appointment",params,she_port);
			if(result.equals("Appointment booked Successfully"))
				status = "Success";
			else
				status = "Failed";
		}
		//Server Log
		try {
			serverLog("Patient "+patientID,"Book an appointment",patientID+" "+appoinmentID+" "+appointmentType,status,result);
		}catch(IOException e){
			System.out.println(e);
		}
		System.out.println(result);
		return result+";"+status;
	}
	
	public boolean canBookAppointmentToday(String patientID, String appoinmentID, String appointmentType) {
		Map<String,List<String>> temp = new HashMap<String,List<String>>();
		List<String> cap = new ArrayList<String>();
		String morning = "";
		String evening = "";
		String afternoon = "";
		String before = appoinmentID.substring(0, 3);
		String after = appoinmentID.substring(4);
		if(appoinmentID.charAt(3)=='M') {
			morning = appoinmentID;
			afternoon = before+"A"+after;
			evening = before+"E"+after;
		}else if(appoinmentID.charAt(3)=='A') {
			morning = before+"M"+after;
			afternoon = appoinmentID;
			evening = before+"E"+after;
		}else if(appoinmentID.charAt(3)=='E') {
			morning = before+"M"+after;
			afternoon = before+"A"+after;
			evening = appoinmentID;
		}
		if(app_data.containsKey(appointmentType)) {
			temp = app_data.get(appointmentType);
			if(temp.containsKey(morning)) {
				cap = temp.get(morning);
				if(cap.contains(patientID)) {
					return false;
				}
			}else if(temp.containsKey(afternoon)) {
				cap = temp.get(afternoon);
				if(cap.contains(patientID)) {
					return false;
				}
			}else if(temp.containsKey(evening)) {
				cap = temp.get(evening);
				if(cap.contains(patientID)) {
					return false;
				}
			}
			
		}
		return true;
	}
	
	public boolean canbookthisweek(String patientID, String appoinmentID, String appointmentType) {
		int count = 0 ;
		Map<String,List<String>> temp = new HashMap<String,List<String>>();
		List<String> weekdays = new ArrayList<String>();
		weekdays.add(appoinmentID);
		String one="";
		String two="";
		if(appoinmentID.substring(3,4).equals("M")) {
			one = "A";
			two = "E";
		}else if(appoinmentID.substring(3,4).equals("A")) {
			one = "M";
			two = "E";
		}else if(appoinmentID.substring(3,4).equals("E")) {
			one = "M";
			two = "A";
		}
		String date = appoinmentID.substring(4);
		String day_one = appoinmentID.substring(0,3) + one + date;
		String day_two = appoinmentID.substring(0,3) + two + date;
		weekdays.add(day_one);
		weekdays.add(day_two);
		String day = date.substring(0,2);
		String month = date.substring(2,4);
		String year = date.substring(4,6);
		Calendar calndr1 = Calendar.getInstance();
		calndr1.set(Integer.parseInt(year)+2000, Integer.parseInt(month)-1,Integer.parseInt(day));
		Date dt = calndr1.getTime();
		int day_of_week = calndr1.get(Calendar.DAY_OF_WEEK);
	    for(int i=1;i<day_of_week;i++) {
	    	Calendar cala = Calendar.getInstance();
	    	cala.setTime(dt);
	    	cala.add(Calendar.DATE, -i);
	    	Date dat = cala.getTime();
	    	DateFormat df = new SimpleDateFormat("ddMMyy");
	    	String week_day = appoinmentID.substring(0,3)+"M" + df.format(dat);
	    	String week_day1 = appoinmentID.substring(0,3)+"A" + df.format(dat);
	    	String week_day2 = appoinmentID.substring(0,3)+"E" + df.format(dat);
	    	weekdays.add(week_day);
	    	weekdays.add(week_day1);
	    	weekdays.add(week_day2);
	    }
	    for(int i=1;i<=7-day_of_week;i++) {
	    	Calendar cala2 = Calendar.getInstance();
	    	cala2.setTime(dt);
	    	cala2.add(Calendar.DATE, i);
	    	Date data = cala2.getTime();
	    	DateFormat df = new SimpleDateFormat("ddMMyy");
	    	String week_day = appoinmentID.substring(0,3)+"M" + df.format(data);
	    	String week_day1 = appoinmentID.substring(0,3)+"A" + df.format(data);
	    	String week_day2 = appoinmentID.substring(0,3)+"E" + df.format(data);
	    	weekdays.add(week_day);
	    	weekdays.add(week_day1);
	    	weekdays.add(week_day2);
	    }
	    List<String> cap = new ArrayList<String>();
	    if(app_data.containsKey(appointmentType)) {
	    	temp = app_data.get(appointmentType);
	    	for(int i=0;i<weekdays.size();i++) {
	    		if(temp.containsKey(weekdays.get(i))) {
	    			cap = temp.get(weekdays.get(i));
	    			if(cap.contains(patientID)) {
	    				count++;
	    			}
	    		}
	    	}
	    }
	    //System.out.println(count);
		if(count<3)
			return true;
		else
			return false;
	}
	
	public String[] getAppointmentSchedule(String patientID) {
		// TODO Auto-generated method stub
		List<String> params = new ArrayList<>();
		params.add(patientID);
		List<String> result = new ArrayList<String>();
		for (Map.Entry mapElement : app_data.entrySet()) {
            String appType = (String)mapElement.getKey();
            Map <String,List<String>> temp = (Map<String,List<String>>)mapElement.getValue();
            //System.out.println(appType);
            //System.out.println(temp);
            //result.add(appType);
            for(Map.Entry mapSubElement : temp.entrySet()) {
            	String appID = (String)mapSubElement.getKey();
            	List<String> cap_data = (List<String>)mapSubElement.getValue();
            	//System.out.println(appID);
            	//System.out.println(cap_data);
            	if(cap_data.contains(patientID)) {
            		if(!(result.contains("Quebec Hospital : "))) {
            			result.add("Quebec Hospital : ");
            		}
            		if(!(result.contains(appType))) {
            			result.add(appType);
            		}
            		result.add(appID);
            	}	
            }
        }
		//Another Server Result
		if((patientID.substring(0,3)).equals("QUE")) {
			String montreal_res = requestAnotherServer("get_appointment",params,mtl_port);
			String sherbrooke_res = requestAnotherServer("get_appointment",params,she_port);
			//List<String> que_list = new ArrayList<String>(Arrays.asList(quebec_res.split(",")));
			//List<String> she_list = new ArrayList<String>(Arrays.asList(sherbrooke_res.split(",")));
			List<String> mtl_list = Stream.of(montreal_res.split(",", -1)).collect(Collectors.toList());
			List<String> she_list = Stream.of(sherbrooke_res.split(",", -1)).collect(Collectors.toList());
			result.addAll(mtl_list);
			result.addAll(she_list);
		}
		//Server Log
		try {
			serverLog("Patient "+patientID,"Get appointments schedule",patientID,"Success",String.join("-", result));
		}catch(IOException e){
			System.out.println(e);
		}
		return result.toArray(new String[result.size()]);
	}
	
	public String cancelAppointment(String patientID, String appoinmentID) {
		// TODO Auto-generated method stub
		String result = "";
		String status = "";
		List<String> params = new ArrayList<String>();
		params.add(patientID);
		params.add(appoinmentID);
		if((appoinmentID.substring(0,3)).equals("QUE")) {
			int flag=0;
			for (Map.Entry mapElement : app_data.entrySet()) {
	            String appType = (String)mapElement.getKey();
	            Map <String,List<String>> temp = (Map<String,List<String>>)mapElement.getValue();
	            //System.out.println(appType);
	            //System.out.println(temp);
	            for(Map.Entry mapSubElement : temp.entrySet()) { //check AppointMentID is there or not
	            	String appID = (String)mapSubElement.getKey();
	            	if(appID.equals(appoinmentID)) {  //if AppointMentID is there check the right patient is trying to cancel the app...
	            		List<String> cap_data = (List<String>)mapSubElement.getValue();
	            		if(cap_data.contains(patientID)) { //if yes then 
	            			cap_data.remove(patientID); //remove the appointment
	            			int capacity = Integer.parseInt(cap_data.get(0));
	            			capacity++;
	            			cap_data.set(0, Integer.toString(capacity));
	            			result = "the Appointment has been cancelled for the patient "+patientID;
	            			status = "Success";
	            		}else {
	            			result = "the Appointment is not booked by "+patientID+" so he/she cannot cancel it ";
	            			status = "Failed";
	            		}
	            		flag=1;
	            		break;
	            	}else {
	            		result = "the Appointment "+appoinmentID+" does not exist ";
	        			status = "Failed";
	            	}
	        	}
	            if(flag==1)
	            	break;
			}
		}else if((appoinmentID.substring(0,3)).equals("MTL")) {
			result = requestAnotherServer("cancel_appointment",params,mtl_port);
			if(result.equals("the Appointment has been cancelled for the patient "+patientID))
				status = "Success";
			else
				status = "Failed";
		}else if((appoinmentID.substring(0,3)).equals("SHE")) {
			result = requestAnotherServer("cancel_appointment",params,she_port);
			if(result.equals("the Appointment has been cancelled for the patient "+patientID))
				status = "Success";
			else
				status = "Failed";
		}
		//Server Log
		try {
			serverLog("Patient "+patientID,"Cancel an appointment",patientID+" "+appoinmentID,status,result);
		}catch(IOException e){
			System.out.println(e);
		}
		return result+";"+status;
	}
	
	public String swapAppointment(String patientID, String oldAppointmentID, String oldAppoinmentType, String newAppointmentID, String newAppoinmentType) {
		// TODO Auto-generated method stub
		String result = "";
		String status = "";
		String cancelStatus = this.cancelAppointment(patientID, oldAppointmentID,oldAppoinmentType);
		String bookStatus = this.bookAppointment(patientID, newAppointmentID, newAppoinmentType);
		boolean bookOperation = bookStatus.equals("Appointment booked Successfully");
		boolean cancelOperation = cancelStatus.equals("the Appointment has been cancelled for the patient "+patientID);
		if(bookOperation && cancelOperation) { // both operation are successful
			this.increaseAppointmentCapacity(oldAppointmentID, oldAppoinmentType);
			result = "the Appointment has been swapped for the patient "+patientID+" from old appointment id "+oldAppointmentID+" to "+newAppointmentID;
			status = "Success";
		}else if(bookOperation && !(cancelOperation)) { // booking has done but cancel operation is not done
			this.cancelAppointment(patientID, newAppointmentID);
			result = cancelStatus + " that's why swap operation cannot be performed";
			status = "Failed";
		}else if(cancelOperation && !(bookOperation)) { // cancel operation has done but booking is unsuccessful
			this.increaseAppointmentCapacity(oldAppointmentID, oldAppoinmentType);
			this.bookAppointment(patientID, oldAppointmentID, oldAppoinmentType);
			result = bookStatus + " that's why swap operation cannot be performed";
			status = "Failed";
		}else { // both operation failed
			result = "This operation cannot be perfomed by the patient "+patientID+" because he/she neither booked the appointment nor canceled it";
			status = "Failed";
		}
		try {
			serverLog("Patient "+patientID,"Swap an appointment",patientID+" "+oldAppointmentID+" "+oldAppoinmentType+" "+newAppointmentID+" "+newAppoinmentType,status,result);
		}catch(IOException e){
			System.out.println(e);
		}
		return result+";"+status;
	}
	
	public void increaseAppointmentCapacity(String appoinmentID, String appointmentType) {
		List<String> params = new ArrayList<String>();
		params.add(appoinmentID);
		params.add(appointmentType);
		if((appoinmentID.substring(0,3)).equals("QUE")) {
			if(app_data.containsKey(appointmentType)) {
				Map <String,List<String>> temp = app_data.get(appointmentType);
				if(temp.containsKey(appoinmentID)) {
					List<String> cap_data = temp.get(appoinmentID);
					int capacity = Integer.parseInt(cap_data.get(0));
        			capacity++;
        			cap_data.set(0, Integer.toString(capacity));
				}
			}
		}else if((appoinmentID.substring(0,3)).equals("MTL")) {
			requestAnotherServer("increase_appointment",params,mtl_port);
		}else if((appoinmentID.substring(0,3)).equals("SHE")) {
			requestAnotherServer("increase_appointment",params,she_port);
		}
	}
	
	public String cancelAppointment(String patientID, String appoinmentID, String appointmentType) {
		String result= "";
		String status = "";
		List<String> params = new ArrayList<String>();
		params.add(patientID);
		params.add(appoinmentID);
		params.add(appointmentType);
		if((appoinmentID.substring(0,3)).equals("QUE")) {
			int flag=0;
			if(app_data.containsKey(appointmentType)) {
				Map <String,List<String>> temp = app_data.get(appointmentType);
				if(temp.containsKey(appoinmentID)) {
					List<String> cap_data = temp.get(appoinmentID);
					//check the patient are there or not
					if(cap_data.contains(patientID)) { //there are patient reschedule the appointments for them than cancel the appointment;
						cap_data.remove(patientID);
						//int capacity = Integer.parseInt(cap_data.get(0));
            			//capacity++;
            			//cap_data.set(0, Integer.toString(capacity));
						result = "the Appointment has been cancelled for the patient "+patientID;
            			status = "Success";
					}else {
						result = "the Appointment is not booked by "+patientID+" so he/she cannot cancel it ";
            			status = "Failed";
					}
				}else {
					result = "Error : This Appointment ID does not Exist";
					status = "Failed";
				}
			}else {
				result = "Error : This Appointment Type does not Exist";
				status = "Failed";
			}
		}else if((appoinmentID.substring(0,3)).equals("MTL")) {
			result = requestAnotherServer("cancel_appointment",params,mtl_port);
			if(result.equals("the Appointment has been cancelled for the patient "+patientID))
				status = "Success";
			else
				status = "Failed";
		}else if((appoinmentID.substring(0,3)).equals("SHE")) {
			result = requestAnotherServer("cancel_appointment",params,she_port);
			if(result.equals("the Appointment has been cancelled for the patient "+patientID))
				status = "Success";
			else
				status = "Failed";
		}
		//Server Log
		try {
			serverLog("Patient "+patientID,"Cancel an appointment",patientID+" "+appoinmentID+" "+appointmentType,status,result);
		}catch(IOException e){
			System.out.println(e);
		}
		return result+";"+status;
	}
	
	
	public void serverLog(String user,String requestType, String params, String status,String result) throws IOException {
		final String log_dir = System.getProperty("user.dir"); //get the directory 
		String log_file = log_dir;
		//System.out.println(log_dir);
		log_file = log_dir+"\\src\\ReplicaManager1\\Server_Logs\\quebec_logs.txt"; //log file path
		File file=new File(log_file); 
		file.createNewFile(); //create a log file if not exist..
		FileWriter fw = new FileWriter(log_file,true); 
		PrintWriter pw = new PrintWriter(fw);
		Date logDate = new Date(); 
		String fmt_str = "yyyy-MM-dd hh:mm:ss a"; //specific date format to add in log file
		DateFormat df = new SimpleDateFormat(fmt_str);
		String log_date= df.format(logDate);
		pw.println(log_date+" "+user+" | "+requestType+" | "+ params +" | "+status+" | "+ result); //add the lof into logfile
		System.out.println("[INFO][LOG] " +log_date+" "+user+" | "+requestType+" | "+ params +" | "+status+" | "+ result);
		pw.close(); //close and save the resources.
	}
	
	public String requestAnotherServer(String function, List params,int port) {
		String result = "";
		DatagramSocket ds = null;
		try {
			ds = new DatagramSocket(); 
			InetAddress ip = InetAddress.getLocalHost(); 
			byte buf[] = null;
			List<String> param_list = params;
			String sending_params = String.join(",",param_list);
			String sending_str = function+"@"+sending_params;
			//System.out.println(sending_str); //<----------------------------------------------------------------------------------------------testing
			buf=sending_str.getBytes();
			DatagramPacket DpSend = new DatagramPacket(buf, buf.length, ip, port);
			ds.send(DpSend);
			/**
			 * 
			 */
			byte[] received = new byte[10000];
			DatagramPacket DpReceive = new DatagramPacket(received, received.length);
			ds.receive(DpReceive);
			result = new String(DpReceive.getData());
			String[] temp = result.split("@");
			result = temp[0];
			//System.out.println(result); //<----------------------------------------------------------------------------------------------testing
		}catch(Exception e) {
			System.out.println(e);
		} finally {
			if(ds!= null) {
				ds.close();
			}	
		}
		return result;
	}
}
