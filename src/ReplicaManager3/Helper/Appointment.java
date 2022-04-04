package ReplicaManager3.Helper;

import java.util.List;

public class Appointment  {
	
    private String appointmentId ;
   // private Integer out_of_city;
    private String appointmentType;
	private Integer numofappointment;
//		appointment.put("PHYSICIAN", new Appointment("MTLM012022",10)); 
	//appointment.put("DENTIST",new Appointment("MTLM012022",10)); 
	
	
//    public Appointment(String appointmentId , String appointmentType, Integer numofappointment) {
//    	this.appointmentId = appointmentId;
//    	this.appointmentType = appointmentType;
//    	this.numofappointment = numofappointment;
//    }
//    
	
    public Appointment(int numofappointment) {
    	//this.appointmentId = appointmentId;
    	this.numofappointment = numofappointment;
    	//this.appointmentType = appointmentType;
    }
    
    public String checkRegion(String appointmentId) {
    	String region;
    	char check = appointmentId.charAt(0);
    	char check1 = 'M';
    	char check2 = 'Q';
    	char check3 = 'S';
    	if (check == check1) {
    		region = "Montreal";
    	}
    	else if (check == check2) {
    		region = "Quebec";
    	}
    	else {
    		region = "Sherbroke";
    	}
    	return region;
    	
    }
    
    public String getappointmentType() {
		return appointmentId;
	}

	public String setappointmentType(String appointmentType) {
		return this.appointmentType = appointmentType;
	}
    
    
	public String getAppointmentId() {
		return appointmentId;
	}

	public String setAppointmentId(String appointmentId) {
		return this.appointmentId = appointmentId;
	}

//	public String getAppointmentType() {
//		return appointmentType;
//	}
//
//	public void setAppointmentType(String appointmentType) {
//		this.appointmentType = appointmentType;
//	}

	public Integer getNumofappointment() {
		return numofappointment;
	}

	public int setNumofappointment(Integer numofappointment) {
		return this.numofappointment = numofappointment;
	}
    
	public String toString() {
        return "Appointment{"+ "Number of Appoinntment='" + numofappointment +'}';
	}
	
	public static void main(String args[]) {
		Appointment a = new Appointment(10);
		String b = a.toString();
		System.out.println(b);
		//appointment.put("PHYSICIAN", new Appointment("MTLM012022",10)); 

	}
    
}
