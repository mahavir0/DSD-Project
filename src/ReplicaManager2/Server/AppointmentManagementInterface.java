package ReplicaManager2.Server;

import java.rmi.Remote;
import java.rmi.RemoteException;

public interface AppointmentManagementInterface extends Remote {

	void testHashMap() throws RemoteException;
	
	String startBackupReplica() throws RemoteException;
	
    String AddAppointment(String AppointmentID, String AppointmentType, int Capacity) throws RemoteException;

    String RemoveAppointment(String AppointmentID, String AppointmentType) throws RemoteException;

    String ListAppointmentAvailability(String AppointmentType) throws RemoteException;

    String BookAppointment(String customerID, String AppointmentID, String AppointmentType) throws RemoteException;

    String getAppointmentSchedule(String customerID) throws RemoteException;

    String CancelAppointment(String customerID, String AppointmentID, String AppointmentType) throws RemoteException;

    String SwapAppointment(String customerID, String newAppointmentID, String newAppointmentType, String oldAppointmentID, String oldAppointmentType) throws RemoteException;

    String shutDown() throws RemoteException;

}
