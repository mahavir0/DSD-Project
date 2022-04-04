package ReplicaManager2.Server;

import java.util.List;
import java.util.Map;

public class CommonOutput {
	public static final String general_fail = "failed";
	public static final String general_success = "successful";
	public static final String addAppointment_fail_cannot_decrease_capacity = "Cannot decrease capacity";
	public static final String addAppointment_success_added = "Appointment added successfully";
	public static final String addAppointment_success_capacity_updated = "Appointment updated successfully";
	public static final String removeAppointment_fail_no_such_Appointment = "No such Appointment";
	public static final String bookAppointment_fail_no_such_Appointment = "No such Appointment";
	public static final String bookAppointment_fail_no_capacity = "Appointment is full";
	public static final String bookAppointment_fail_weekly_limit = "Weekly limit reached";
	public static final String cancelAppointment_fail_not_registered_in_Appointment = "You are not registered in Appointment";
	public static final String cancelAppointment_fail_no_such_Appointment = "No such Appointment";
	public static final String swapAppointment_fail_no_such_Appointment = "No such Appointment";
	public static final String swapAppointment_fail_not_registered_in_Appointment = "You are not registered in Appointment";
	private static final String SUCCESS = "Success:";
	private static final String FAIL = "Fail:";

	private static String standardOutput(boolean isSuccess, String method, String output) {
		if (isSuccess)
			return output+";Success";
		else
			return output+";Failed";
	}

	public static String addAppointmentOutput(boolean isSuccess, String reason) {
		if (isSuccess) {
			if (reason == null) {
				reason = general_success;
			}
		} else {
			if (reason == null) {
				reason = general_fail;
			}
		}
		return standardOutput(isSuccess, "addAppointment", reason);
	}

	/**
	 * Format of each string in allAppointmentIDsWithCapacity --> AppointmentID+ one space + remainingCapacity
	 */
	public static String listAppointmentAvailabilityOutput(boolean isSuccess, List<String> allAppointmentIDsWithCapacity, String reason) {
		if (isSuccess) {
			reason = general_success;
			if (allAppointmentIDsWithCapacity.size() > 0) {
				StringBuilder reasonBuilder = new StringBuilder();
				for (String Appointment :
						allAppointmentIDsWithCapacity) {
					if (Appointment.length() > 10) {
						reasonBuilder.append(Appointment).append("@");
					}
				}
				if (reasonBuilder.length() > 0)
					reason = reasonBuilder.toString();
				if (reason.endsWith("@"))
					reason = reason.substring(0, reason.length() - 1);
			}
		} else {
			reason = general_fail;
		}
		return standardOutput(isSuccess, "listAppointmentAvailability", reason);
	}

	public static String removeAppointmentOutput(boolean isSuccess, String reason) {
		if (isSuccess) {
			reason = general_success;
		} else {
			if (reason == null) {
				reason = general_fail;
			}
		}
		return standardOutput(isSuccess, "removeAppointment", reason);
	}

	public static String bookAppointmentOutput(boolean isSuccess, String reason) {
		if (isSuccess) {
			reason = general_success;
		} else {
			if (reason == null) {
				reason = general_fail;
			}
		}
		return standardOutput(isSuccess, "bookAppointment", reason);
	}

	//Format for output AppointmentType+ one space + AppointmentID
	public static String getBookingScheduleOutput(boolean isSuccess, Map<String, List<String>> Appointments, String reason) {
		if (isSuccess) {
			reason = general_success;
			if (Appointments.size() > 0) {
				StringBuilder reasonBuilder = new StringBuilder();
				for (String AppointmentType :
						Appointments.keySet()) {
					for (String AppointmentID :
							Appointments.get(AppointmentType)) {
						reasonBuilder.append(AppointmentType).append(" ").append(AppointmentID).append("@");
					}
				}
				reason = reasonBuilder.toString();
				if (!reason.equals(""))
					reason = reason.substring(0, reason.length() - 1);
			}
		} else {
			if (reason == null) {
				reason = general_fail;
			}
		}
		return standardOutput(isSuccess, "getBookingSchedule", reason);
	}

	public static String cancelAppointmentOutput(boolean isSuccess, String reason) {
		if (isSuccess) {
			reason = general_success;
		} else {
			if (reason == null) {
				reason = general_fail;
			}
		}
		return standardOutput(isSuccess, "cancelAppointment", reason);
	}


	public static String swapAppointmentOutput(boolean isSuccess, String reason) {
		if (isSuccess) {
			reason = general_success;
		} else {
			if (reason == null) {
				reason = general_fail;
			}
		}
		return standardOutput(isSuccess, "swapAppointment", reason);
	}
}