package ReplicaManager3.Helper;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;




public class forward_request {
	//mtlPort,"bookappointment",patientID,newAppointmentType,newAppointmentID,0);
	public static String send_request(int port , String operation , String userid , String appointmentType , String appointmentId ,int capacity) {
		System.out.println("mtl forward");
		DatagramSocket socket = null;
		String result = "";
		String client_msg = operation+";"+userid+";"+appointmentType+";"+appointmentId+";"+capacity+ ";" + "_" + ";" + "_" ;
		try {
			System.out.println("clientmsg:"+client_msg );
			socket = new DatagramSocket();
			byte[] msg = client_msg.getBytes();
			InetAddress ip = InetAddress.getByName("localhost");
			DatagramPacket req = new DatagramPacket(msg,client_msg.length(),ip,port);
			socket.send(req);
			
			byte[] buffer = new byte[100000];
			DatagramPacket ans = new DatagramPacket(buffer,buffer.length);
			socket.receive(ans);
			result = new String(ans.getData());
			result = result.trim();
			System.out.println("Result----------------->" + result );
			//String[] sep = result.split(";");
			//result = sep[0];
			System.out.println("mtl forward:"+result);
		}
		catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} 
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}	
		finally {
			if(socket != null) {
				socket.close();
			}
		}
		return result;
	}

	public static String send_request_(int port , String operation , String userid , String oldAppointmentID , String oldAppointmentType, String newAppointmentID , String newAppointmentType) {
		
		DatagramSocket socket = null;
		String result = "";
		String client_msg = operation+";"+userid+";"+oldAppointmentType+";"+oldAppointmentID+";"+0 +";" + newAppointmentID+ ";" + newAppointmentType   ;		
		try {
			System.out.println("Okay"+client_msg);
			socket = new DatagramSocket();
			byte[] msg = client_msg.getBytes();
			InetAddress ip = InetAddress.getByName("localhost");
			DatagramPacket req = new DatagramPacket(msg,client_msg.length(),ip,port);
			socket.send(req);
			
			byte[] buffer = new byte[1000000];
			DatagramPacket ans = new DatagramPacket(buffer,buffer.length);
			socket.receive(ans);
			result = new String(ans.getData());
			
			result = result.trim();
			System.out.println("Result --------------------> " + result);
			//String[] sep = result.split(";");
			//result = sep[0];
			
		}
		catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} 
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}	
		finally {
			if(socket != null) {
				socket.close();
			}
		}
		return result;
	}
}




/*
public class forward_request {
	//mtlPort,"bookappointment",patientID,newAppointmentType,newAppointmentID,0);
	public static String send_request(int port , String operation , String userid , String appointmentType , String appointmentId ,int capacity) {
		
		DatagramSocket socket = null;
		String result = "";
		String client_msg = operation+";"+userid+";"+appointmentType+";"+appointmentId+";"+capacity+ ";" + "_" + ";" + "_" ;
		try {
			socket = new DatagramSocket();
			byte[] msg = client_msg.getBytes();
			InetAddress ip = InetAddress.getByName("localhost");
			DatagramPacket req = new DatagramPacket(msg,client_msg.length(),ip,port);
			socket.send(req);
			
			byte[] buffer = new byte[100000];
			DatagramPacket ans = new DatagramPacket(buffer,buffer.length);
			socket.receive(ans);
			result = new String(ans.getData());
			result = result.trim();
			String[] sep = result.split(";");
			result = sep[0];
		}
		catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} 
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}	
		finally {
			if(socket != null) {
				socket.close();
			}
		}
		return result;
	}

	public static String send_request_(int port , String operation , String userid , String oldAppointmentID , String oldAppointmentType, String newAppointmentID , String newAppointmentType) {
		
		DatagramSocket socket = null;
		String result = "";
		String client_msg = operation+";"+userid+";"+oldAppointmentType+";"+oldAppointmentID+";"+0 +";" + newAppointmentID+ ";" + newAppointmentType   ;		
		try {
			socket = new DatagramSocket();
			byte[] msg = client_msg.getBytes();
			InetAddress ip = InetAddress.getByName("localhost");
			DatagramPacket req = new DatagramPacket(msg,client_msg.length(),ip,port);
			socket.send(req);
			
			byte[] buffer = new byte[1000000];
			DatagramPacket ans = new DatagramPacket(buffer,buffer.length);
			socket.receive(ans);
			result = new String(ans.getData());
			result = result.trim();
			String[] sep = result.split(";");
			result = sep[0];
		}
		catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} 
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}	
		finally {
			if(socket != null) {
				socket.close();
			}
		}
		return result;
	}
}

 /*
public class forward_request {
	//mtlPort,"bookappointment",patientID,newAppointmentType,newAppointmentID,0);
	public static String send_request(int port , String operation , String userid , String appointmentType , String appointmentId ,int capacity) {
		
		DatagramSocket socket = null;
		String result = "";
		String client_msg = operation+";"+userid+";"+appointmentType+";"+appointmentId+";"+capacity+ ";" + "_" + ";" + "_" ;
		try {
			socket = new DatagramSocket();
			byte[] msg = client_msg.getBytes();
			InetAddress ip = InetAddress.getByName("localhost");
			DatagramPacket req = new DatagramPacket(msg,client_msg.length(),ip,port);
			socket.send(req);
			
			byte[] buffer = new byte[100000];
			DatagramPacket ans = new DatagramPacket(buffer,buffer.length);
			socket.receive(ans);
			result = new String(ans.getData());
			result = result.trim();
			String[] sep = result.split(";");
			result = sep[0];
		}
		catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} 
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}	
		finally {
			if(socket != null) {
				socket.close();
			}
		}
		return result;
	}

	public static String send_request_(int port , String operation , String userid , String oldAppointmentID , String oldAppointmentType, String newAppointmentID , String newAppointmentType) {
		
		DatagramSocket socket = null;
		String result = "";
		String client_msg = operation+";"+userid+";"+oldAppointmentType+";"+oldAppointmentID+";"+0 +";" + newAppointmentID+ ";" + newAppointmentType   ;		
		try {
			socket = new DatagramSocket();
			byte[] msg = client_msg.getBytes();
			InetAddress ip = InetAddress.getByName("localhost");
			DatagramPacket req = new DatagramPacket(msg,client_msg.length(),ip,port);
			socket.send(req);
			
			byte[] buffer = new byte[1000000];
			DatagramPacket ans = new DatagramPacket(buffer,buffer.length);
			socket.receive(ans);
			result = new String(ans.getData());
			result = result.trim();
			String[] sep = result.split(";");
			result = sep[0];
		}
		catch (SocketException e) {
			System.out.println("Socket: " + e.getMessage());
		} 
		catch (IOException e) {
			System.out.println("IO: " + e.getMessage());
		}	
		finally {
			if(socket != null) {
				socket.close();
			}
		}
		return result;
	}
}
*/