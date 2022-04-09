package ReplicaManager3.Server;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.util.*;

import ReplicaManager3.Implementation.Montreal_Class;
import ReplicaManager3.Implementation.Quebec_Class;
import ReplicaManager3.Implementation.Sherbrooke_Class;


public class Sherbrooke{
	public static Sherbrooke_Class monclass;
	public static Sherbrooke_Class backupClass;
	public static void main(String args[]) {
		try {
			
			//creating servent and register it with orb
			monclass = new Sherbrooke_Class();

			Runnable task = () -> {
				receive(monclass);
			};
			Thread thread = new Thread(task);
			thread.start();
			
			Runnable task1 = () -> {
				receiveRequestFromReplicaManager(monclass);
			};
			Thread thread1 = new Thread(task1);
			thread1.start();

		}
		catch (Exception e) {
			e.printStackTrace();
		}
		System.out.println("Montreal Server Closed");
		
	}
	public static void receive(Sherbrooke_Class mon) {
		DatagramSocket socket= null;
		String send_data = "";
		//String result = "";
		
		try {
			socket= new DatagramSocket(2222);
			byte [] msg = new byte[100000];
			System.out.println("SHE_udp_server started 2222");
			while(true){
				DatagramPacket request = new DatagramPacket(msg, msg.length);
				socket.receive(request);
				String snt = new String(request.getData(),0,request.getLength());
	//  operation+";"+userid+";"+appointmentType+";"+appointmentId+";"+capacity+";"+newAppointmentID+";"+newAppointmentType;	
				System.out.println("inside server "+snt);
				String[] spt = snt.split(";");
				String operation = spt[0];
				String userID = spt[1];
				String appointmentType = spt[2];
				String appointmentID = spt[3];
				
				int capacity = Integer.parseInt(spt[4]);
				String newAppointmentid = spt[5];
				String newAppointmenttype = spt[6];
				
				if(operation.equals("listAvailability")) {
					String result = mon.listAppointmentAvailability(userID,appointmentType);		
					send_data = result;
				}
				
				else if(operation.equals("swapappointment")) {
					
					String result = mon.swapAppointment(userID, appointmentID, appointmentType,newAppointmentid, newAppointmenttype);		
					send_data = result;
				}
				
				else if(operation.equals("removeappointment")) {
					String result = mon.removeAppointment(userID, appointmentID, appointmentType, capacity);
					send_data = result;
				}
				
				else if(operation.equals("addappointment")) {
					String result = mon.addAppointment(userID,appointmentID,appointmentType,capacity);		
					send_data = result;
				}
				else if(operation.equals("bookappointment")) {
					String result = mon.bookAppointment(userID,appointmentID,appointmentType);		
					send_data = result;
				}
				else if(operation.equals("cancelappointment")) {
					String result = mon.cancelAppointment(userID, appointmentID, appointmentType);
					send_data = result;
				}
				else if(operation.equals("getappointment")) {
					String result = mon.getAppointmentSchedule(userID);
					send_data = result;
				}

				
				byte[] send_client = send_data.getBytes();
				DatagramPacket reply = new DatagramPacket(send_client, send_data.length(), request.getAddress(),request.getPort());
				socket.send(reply);
			}
		}
		catch (IOException e){
			System.out.println("IO exception: " + e.getMessage());
		}
		finally {
			if(socket!= null) {
				socket.close();
			}
		}
		
	}

	public static void receiveRequestFromReplicaManager(Sherbrooke_Class mtlobj) {
		 String result= "";
		 DatagramSocket  ds=null;
		 try {
			 ds = new DatagramSocket(2223);
			 byte[] receive = new byte[100000];
			 System.out.println("UDP Server for SHE is running on port : 2223");
			 while(true) {
				 DatagramPacket DpReceive = null;
				 DpReceive = new DatagramPacket(receive, receive.length);
				 ds.receive(DpReceive);
				 String received_func_str = new String(DpReceive.getData(),0,DpReceive.getLength());
				 System.out.println("Request Received : "+received_func_str);
				 //System.out.println(received_func_str);//<----------------------------------------------------------------------------------------------testing
				 String[] temp = received_func_str.split(";");
				 String func = temp[0];
				 //System.out.println(func); //<----------------------------------------------------------------------------------------------testing
				 //String[] received_parameters = temp[1].split(",");
				 List<String> parameters = new ArrayList<String>(Arrays.asList(temp[1].split("=")));
				 //System.out.println(received_parameters); //<----------------------------------------------------------------------------------------------testing
				 //System.out.println(parameters);
				 if(func.equals("addAppointment")) {
					 String dataString = parameters.get(3);
					 int cap = Integer.parseInt(dataString);
					 //String[] cap = {parameters.get(3)};
					 result = mtlobj.addAppointment(parameters.get(0), parameters.get(1), parameters.get(2), cap);
				 }else if(func.equals("removeAppointment")) {
					 System.out.println("parameter 0 : " + parameters.get(0) + "parameter 1 " + parameters.get(1) + parameters.get(2) + parameters.get(3));
					 result = mtlobj.removeAppointment(parameters.get(0),parameters.get(1),parameters.get(2),0);
				 }else if(func.equals("listAppointmentAvailability")) {
					 result = mtlobj.listAppointmentAvailability(parameters.get(0),parameters.get(2));
					 //result = String.join(",", ls).trim();
				 }else if(func.equals("bookAppointment")) {
					 result = mtlobj.bookAppointment(parameters.get(0),parameters.get(1),parameters.get(2));
				 }else if(func.equals("getAppointmentSchedule")) {
					 result = mtlobj.getAppointmentSchedule(parameters.get(0));
					 //result = String.join(",", ls).trim();
				 }else if(func.equals("cancelAppointment")) {
					 result = mtlobj.cancelAppointment(parameters.get(0), parameters.get(1),parameters.get(2));
				 }else if(func.equals("swapAppointment")) {
					 result = mtlobj.swapAppointment(parameters.get(0), parameters.get(1), parameters.get(2), parameters.get(4), parameters.get(5));
				 }else if(func.equals("fault message")) {
					 result = mtlobj.handleFault();
					 backupClass = new Sherbrooke_Class();
					 monclass = backupClass;
				 }
				 //System.out.println(result); //<----------------------------------------------------------------------------------------------testing
				 result+="@";
				 System.out.println("Result response : "+result);
				 byte[] send_result = result.getBytes();
				 DatagramPacket send_reply = new DatagramPacket(send_result,send_result.length,DpReceive.getAddress(),DpReceive.getPort());
				 ds.send(send_reply); 
				 System.out.println("Response sent to Replica Manager");
			 }
		 }catch(Exception e) {
			 System.out.println(e);
		 } finally {
			 if(ds!=null)
				 ds.close();
		 }
	}

}
