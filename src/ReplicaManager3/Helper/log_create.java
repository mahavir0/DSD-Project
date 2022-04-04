package ReplicaManager3.Helper;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class log_create {
	public static void client_log_create( String userid,  String action,  String response) throws IOException{
		String region_check = userid.substring(0, 3).toUpperCase();
		final String dir = System.getProperty("user.dir");
		String filename = dir;
		if(region_check.equals("MTL")) {
			filename = dir + "\\src\\ReplicaManager3\\logs\\Client\\MTL\\"+userid+".txt";	
		}
		else if(region_check.equals("SHE")) {
			filename = dir + "\\src\\ReplicaManager3\\logs\\Client\\SHE\\"+userid+".txt";	
		}
		else if(region_check.equals("QUE")) {
			filename = dir + "\\src\\ReplicaManager3\\logs\\Client\\QUE\\"+userid+".txt";	
		}
		
		Date date = new Date();
		String date_format = "yyyy-MM-dd hh:mm:ss a";
		DateFormat date_ =   new SimpleDateFormat(date_format);
		String form = date_.format(date);
		
		FileWriter file = new FileWriter(filename,true);
		PrintWriter print = new PrintWriter(file);
		print.println("Date:"+ form +"|Action:" + action+ "| Response:" + response);
		print.close();
	}
	public static void server_log_create( String userid,  String action,  String response ,String requestresult,String para ) throws IOException{
		String region_check = userid.substring(0, 3).toUpperCase();
		final String dir = System.getProperty("user.dir");
		String filename = dir;
		if(region_check.equals("MTL")) {
			filename = dir + "\\src\\ReplicaManager3\\logs\\Server\\montreal.txt";	
		}
		else if(region_check.equals("SHE")) {
			filename = dir + "\\src\\ReplicaManager3\\logs\\Server\\sherbrooke.txt";	
		}
		else if(region_check.equals("QUE")) {
			filename = dir + "\\src\\ReplicaManager3\\logs\\Server\\quebec.txt";	
		}
		
		Date date = new Date();
		String date_format = "yyyy-MM-dd hh:mm:ss a";
		DateFormat date_ =   new SimpleDateFormat(date_format);
		String form = date_.format(date);
		
		
		FileWriter file = new FileWriter(filename,true);
		PrintWriter print = new PrintWriter(file);
		print.println("DATE: "+form+"|Action: "+action+" | Parameters: "+ para +" | Action Status: "+requestresult+" | Resonse: "+ response);
		print.close();
	}
}
