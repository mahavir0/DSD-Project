package ReplicaManager2.Server;

public class Message {
	public String FrontIpAddress,Function , UserID, newAppointmentID, newAppointmentType, AppointmentID, AppointmentType; 
	public int Capacity, sequenceId; 
		  
	public Message(int sequenceId, String FrontIpAddress, String Function, String UserID, String newAppointmentID,
					String newAppointmentType,String oldAppointmentID,String oldAppointmentType,int Capacity) 
	{ 
		this.sequenceId = sequenceId; 
		this.FrontIpAddress = FrontIpAddress; 
		this.Function = Function; 
		this.UserID = UserID; 
		this.newAppointmentID = newAppointmentID; 
		this.newAppointmentType = newAppointmentType; 
		this.AppointmentID = oldAppointmentID; 
		this.AppointmentType = oldAppointmentType; 
		this.Capacity = Capacity; 
	}
    @Override
    public String toString() {
		return sequenceId + ";" + FrontIpAddress + ";" +Function + ";" +UserID + ";" +newAppointmentID + 
		";" +newAppointmentType + ";" +AppointmentID + ";" +AppointmentType + ";" +Capacity;
    }
}