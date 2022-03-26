/**
 * 
 */
package Client;

import DAMSApp.*;
import org.omg.CosNaming.*;
import org.omg.CosNaming.NamingContextPackage.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.rmi.registry.LocateRegistry;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import org.omg.CORBA.*;
/**
 * @author Mahavir
 *
 */
public class Client {

	/**
	 * @param args
	 */
	static DAMS damsobj;
	static ORB orb;
	static org.omg.CORBA.Object objRef;
	static NamingContextExt ncRef;
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		try {
			orb = ORB.init(args, null);
			objRef = orb.resolve_initial_references("NameService");
			ncRef = NamingContextExtHelper.narrow(objRef);
			System.out.println("Welcome to DAMS System");
			while(true) {
				Scanner sc = new Scanner(System.in);
				System.out.println("Press 1 to access the system : ");
				System.out.println("Press 0 exit : ");
				String choice = sc.next();
				if(choice.equals("1")) {
					System.out.println("Enter your id : ");
					String id = sc.next();
					id=id.toUpperCase();
					if(checkID(id)) {
						if(id.charAt(3)=='A') {
							if(checkAdmin(id)) {
								adminTask(id);
							}
						}else if(id.charAt(3)=='P'){
							System.out.println("Welcome Patient "+id);
							patientTask(id);
						}
					}
				}else if(choice.equals("0")) {
					System.out.println("Thank You!");
					break;
				}else {
					System.out.println("Please Enter right choice");
				}
			}
			
		}catch(Exception e) {
			System.out.println("ERROR : " + e) ;
	        e.printStackTrace(System.out);
		}
	}
	
	public static void patientTask(String id) throws IOException {
		assignServer(id);
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("Press 1 to Book an Appointment \nPress 2 to get Appointment Schedule \nPress 3 to cancel Appointment \nPress 4 to swap Appointment\nPress 0 to exit\nEnter your choice : ");
			String ch = sc.next();
			String patientID = id;
			String appointmentID,appointmentType,newAppointmentID,newAppointmentType;
			if(ch.equals("1")) {
				System.out.println("Enter the Appointment ID : ");
				appointmentID = sc.next();
				System.out.println("Enter the Appointment Type : ");
				appointmentType = sc.next();
				String result = damsobj.bookAppointment(patientID, appointmentID, appointmentType);
				System.out.println(result);
				clientLogger(patientID,"Book Appointment",patientID+" "+appointmentID+" "+appointmentType,result);
			}else if(ch.equals("2")) {
				List<String> result = new ArrayList<String>();
				result = Arrays.asList(damsobj.getAppointmentSchedule(patientID));
				System.out.println(result);
				clientLogger(patientID,"Get Appointment Schedule",patientID,String.join(",", result));
			}else if(ch.equals("3")) {
				System.out.println("Enter the Appointment ID : ");
				appointmentID = sc.next();
				String result = damsobj.cancelAppointment(patientID, appointmentID);
				System.out.println(result);
				clientLogger(patientID,"Cancel Appointment",patientID+" "+appointmentID,result);
			}else if(ch.equals("4")){
				System.out.println("Enter the Old Appointment ID : ");
				appointmentID = sc.next();
				System.out.println("Enter the Old Appointment Type : ");
				appointmentType = sc.next();
				System.out.println("Enter the New Appointment ID : ");
				newAppointmentID = sc.next();
				System.out.println("Enter the New Appointment Type : ");
				newAppointmentType = sc.next();
				//newAppointmentType = appointmentType;
				String result = damsobj.swapAppointment(patientID, appointmentID, appointmentType, newAppointmentID, newAppointmentType);
				System.out.println(result);
				clientLogger(patientID,"Swap Appointment",patientID+" "+appointmentID+" "+appointmentType+" "+newAppointmentID+" "+newAppointmentType,result);
			}else if(ch.equals("0")) {
				break;
			}else {
				System.out.println("Please Enter the right choice");
			}
		}
		
	}
	
	public static void adminTask(String id) throws IOException {
		assignServer(id);
		Scanner sc = new Scanner(System.in);
		while(true) {
			System.out.println("Press 1 to Add Appointments \nPress 2 to Remove Appointments \nPress 3 to get Appointments Availibily list \nPress 4 to Book an Appointment \nPress 5 to get Appointment Schedule \nPress 6 to cancel Appointment \nPress 7 to swap Appointment\nPress 0 to exit\nEnter your choice : ");
			String ch = sc.next();
			String appointmentID, appointmentType, patientID, newAppointmentID, newAppointmentType;
			List<String> capacity = new ArrayList<String>();
			if(ch.equals("1")) {
				System.out.println("Enter the Appointment ID : ");
				appointmentID = sc.next();
				System.out.println("Enter the Appointment Type : ");
				appointmentType = sc.next();
				System.out.println("Enter the Capacity : ");
				String cap = sc.next();
				capacity.add(cap);
				//String[] capstringlist = new String[1];
				//capstringlist[0]=cap;
				String result = damsobj.addAppointment(appointmentID, appointmentType, cap);
				System.out.println(result);
				clientLogger(id,"Add Appointments",appointmentID+" "+appointmentType+" "+cap,result);
			}else if(ch.equals("2")) {
				System.out.println("Enter the Appointment ID : ");
				appointmentID = sc.next();
				System.out.println("Enter the Appointment Type : ");
				appointmentType = sc.next();
				String result = damsobj.removeAppointment(appointmentID, appointmentType);
				System.out.println(result);
				clientLogger(id,"Remove Appointments",appointmentID+" "+appointmentType,result);
			}else if(ch.equals("3")) {
				System.out.println("Enter the Appointment Type : ");
				appointmentType = sc.next();
				List<String> result = new ArrayList<String>();
				result = Arrays.asList(damsobj.listAppointmentAvailability(appointmentType));
				System.out.println(appointmentType+"-"+result);
				clientLogger(id,"List Appointment Availibility",appointmentType,String.join(",", result));
			}else if(ch.equals("4")) {
				System.out.println("Enter the Patient ID : ");
				patientID = sc.next();
				System.out.println("Enter the Appointment ID : ");
				appointmentID = sc.next();
				System.out.println("Enter the Appointment Type : ");
				appointmentType = sc.next();
				String result = damsobj.bookAppointment(patientID, appointmentID, appointmentType);
				System.out.println(result);
				clientLogger(id,"Book Appointment",patientID+" "+appointmentID+" "+appointmentType,result);
			}else if(ch.equals("5")) {
				System.out.println("Enter the Patient ID : ");
				patientID = sc.next();
				List<String> result = new ArrayList<String>();
				result = Arrays.asList(damsobj.getAppointmentSchedule(patientID));
				System.out.println(result);
				clientLogger(id,"Get Appointment Schedule",patientID,String.join(",", result));
			}else if(ch.equals("6")) {
				System.out.println("Enter the Patient ID : ");
				patientID = sc.next();
				System.out.println("Enter the Appointment ID : ");
				appointmentID = sc.next();
				String result = damsobj.cancelAppointment(patientID, appointmentID);
				System.out.println(result);
				clientLogger(id,"Cancel Appointment",patientID+" "+appointmentID,result);
			}else if(ch.equals("7")) {
				System.out.println("Enter the Patient ID : ");
				patientID = sc.next();
				System.out.println("Enter the Old Appointment ID : ");
				appointmentID = sc.next();
				System.out.println("Enter the Old Appointment Type : ");
				appointmentType = sc.next();
				System.out.println("Enter the New Appointment ID : ");
				newAppointmentID = sc.next();
				System.out.println("Enter the New Appointment Type : ");
				newAppointmentType = sc.next();
				//newAppointmentType = appointmentType;
				String result = damsobj.swapAppointment(patientID, appointmentID, appointmentType, newAppointmentID, newAppointmentType);
				System.out.println(result);
				clientLogger(id,"Swap Appointment",patientID+" "+appointmentID+" "+appointmentType+" "+newAppointmentID+" "+newAppointmentType,result);
			}else if(ch.equals("0")) {
				break;
			}else {
				System.out.println("Please Enter the right choice");
			}
		}
	}
	
	public static void clientLogger(String user,String requestType, String params,String result) throws IOException {
		final String log_dir = System.getProperty("user.dir");
		String log_file = log_dir;
		String user_loc = user.substring(0,3);
		String user_of = user.substring(3,4);
		if(user_loc.equals("MTL")) {
			if(user_of.equals("A")) {
				log_file = log_dir+"\\src\\Client\\log\\montreal\\admin\\"+user+".txt";
			}else if(user_of.equals("P")) {
				log_file = log_dir+"\\src\\Client\\log\\montreal\\patient\\"+user+".txt";
			}
		}else if(user_loc.equals("QUE")) {
			if(user_of.equals("A")) {
				log_file = log_dir+"\\src\\Client\\log\\quebec\\admin\\"+user+".txt";
			}else if(user_of.equals("P")) {
				log_file = log_dir+"\\src\\Client\\log\\quebec\\patient\\"+user+".txt";
			}
		}else if(user_loc.equals("SHE")) {
			if(user_of.equals("A")) {
				log_file = log_dir+"\\src\\Client\\log\\sherbrooke\\admin\\"+user+".txt";
			}else if(user_of.equals("P")) {
				log_file = log_dir+"\\src\\Client\\log\\sherbrooke\\patient\\"+user+".txt";
			}
		}
		File file=new File(log_file); 
		file.createNewFile(); //create a log file if not exist..
		FileWriter fw = new FileWriter(log_file,true); 
		PrintWriter pw = new PrintWriter(fw);
		Date logDate = new Date(); 
		String fmt_str = "yyyy-MM-dd hh:mm:ss a"; //specific date format to add in log file
		DateFormat df = new SimpleDateFormat(fmt_str);
		String log_date= df.format(logDate);
		pw.println(log_date+" | "+requestType+" | "+ params +" | "+ result); //add the lof into logfile
		pw.close();
	}
	
	public static void assignServer(String id) {
		String hospital = id.substring(0, 3);
		try {
			String name = "frontend";
			damsobj = DAMSHelper.narrow(ncRef.resolve_str(name));
//			if(hospital.equals("MTL")){
//				String name = "montreal";
//				damsobj = DAMSHelper.narrow(ncRef.resolve_str(name));
//			}else if(hospital.equals("QUE")){
//				String name = "quebec";
//				damsobj = DAMSHelper.narrow(ncRef.resolve_str(name));
//			}else if(hospital.equals("SHE")){
//				String name = "sherbrooke";
//				damsobj = DAMSHelper.narrow(ncRef.resolve_str(name));
//			}
		}catch(Exception e) {
			e.printStackTrace();
		}
	}
	
	public static boolean checkAdmin(String id) {
		if(id.equals("MTLA0000") || id.equals("QUEA0000") || id.equals("SHEA0000")) {
			System.out.println("Enter your password : ");
			Scanner sc = new Scanner(System.in);
			String pass = sc.next();
			if(pass.equals("password")) {
				System.out.println("Welcome Admin "+id);
				return true;
			}else {
				System.out.println("Wrong Password\nAgain Enter your password : ");
				pass = sc.next();
				if(pass.equals("password")) {
					System.out.println("Welcome Admin "+id);
					return true;
				}else {
					System.out.println("You are not Authorised admin");
					return false;
				}
			}
		}else {
			System.out.println("You are not Authorised admin");
			return false;
		}
	}
	
	public static boolean checkID(String id) {
		if(id.length()==8) {
			String hospital = id.substring(0, 3);
			String user = id.substring(3, 4);
			String num = id.substring(4,8);
			int count = 0;
			if(hospital.equals("MTL") || hospital.equals("QUE") || hospital.equals("SHE"))
				count++;
			if(user.equals("A") || user.equals("P"))
				count++;
			try{
				Integer.parseInt(num);
				count++;
			}catch(Exception e) {
				System.out.println("Please Enter Valid ID");
			}
			if(count==3) {
				return true;
			}
			else { 
				System.out.println("Invalid ID");
				return false;
			}
		}else {
			System.out.println("Invalid ID");
			return false;
		}
	}

}
