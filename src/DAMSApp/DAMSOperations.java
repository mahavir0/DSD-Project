package DAMSApp;

/**
 * DAMSApp/DAMSOperations.java . Generated by the IDL-to-Java compiler
 * (portable), version "3.2" from src/DAMS.idl Saturday, April 2, 2022 1:26:42
 * PM EDT
 */

public interface DAMSOperations {
	String addAppointment(String adminID, String appointmentID, String appoinmentType, String capacity);

	String removeAppointment(String adminID, String appointmentID, String appoinmentType);

	String listAppointmentAvailability(String adminID, String appoinmentType);

	String bookAppointment(String patientID, String appoinmentID, String appointmentType);

	String getAppointmentSchedule(String patientID);

	String cancelAppointment(String patientID, String appoinmentID, String appointmentType);

	String swapAppointment(String patientID, String oldAppointmentID, String oldAppoinmentType, String newAppointmentID,
			String newAppoinmentType);

	void shutdown();
} // interface DAMSOperations
