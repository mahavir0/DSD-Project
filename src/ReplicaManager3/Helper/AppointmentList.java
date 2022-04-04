package ReplicaManager3.Helper;

public class AppointmentList {
	private String userId;
	private String appointmentType;
	private String appointmentID;
	private Integer outofcity;
	
	public AppointmentList( String appointmentID,String appointmentType ) {
		super();
		//this.userId = userId;
		this.appointmentType = appointmentType;
		this.appointmentID = appointmentID;
		//this.outofcity = outofcity;
	}

//	public String getUserId() {
//		return userId;
//	}

//	public void setUserId(String userId) {
//		this.userId = userId;
//	}

	public String getAppointmentType() {
		return appointmentType;
	}

	public void setAppointmentType(String appointmentType) {
		this.appointmentType = appointmentType;
	}
	
	public Integer getoutofcity() {
		return outofcity;
	}

	public void getoutofcity(int outofcity) {
		this.outofcity = outofcity;
	}

	public String getAppointmentID() {
		return appointmentID;
	}

	public void setAppointmentID(String appointmentID) {
		this.appointmentID = appointmentID;
	}
	
	public String toString() {
		return appointmentID + "" + appointmentType ;
		
	}

	public void setNumofappointment(int capacity) {
		// TODO Auto-generated method stub
		
	}
	

}