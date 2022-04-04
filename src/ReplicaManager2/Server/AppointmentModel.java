package ReplicaManager2.Server;

 import static ReplicaManager2.Server.AppointmentManagement.*;

import java.util.ArrayList;
import java.util.List;

public class AppointmentModel 
{
    public static final String Appointment_Morning = "Morning";
    public static final String Appointment_Afternoon = "Afternoon";
    public static final String Appointment_Evening = "Evening";
    public static final String Physician = "Physician";
    public static final String Surgon = "Surgeon";
    public static final String Dental = "Dental";
    public static final int Appointment_Full = -1;
    public static final int Already_Registered = 0;
    public static final int Add_Success = 1;
    private int AppointmentCapacity;
    private String AppointmentType;
    private String AppointmentID;
    private String AppointmentServer;
    private String AppointmentDate;
    private String AppointmentTimeSlot;
    private List<String> RegisteredClients;

    public AppointmentModel(String AppointmentType, String AppointmentID, int AppointmentCapacity) 
    {
        this.AppointmentID = AppointmentID;
        this.AppointmentType = AppointmentType;
        this.AppointmentCapacity = AppointmentCapacity;
        this.AppointmentTimeSlot = DetectAppointmentTimeSlot(AppointmentID);
        this.AppointmentServer = DetectAppointmentServer(AppointmentID);
        this.AppointmentDate = DetectAppointmentDate(AppointmentID);
        RegisteredClients = new ArrayList<>();
    }

    public static String DetectAppointmentServer(String AppointmentID) 
    {
        if (AppointmentID.substring(0, 3).equalsIgnoreCase("MTL")) 
        {
            return Appointment_ServerMontreal;
        }
        else if (AppointmentID.substring(0, 3).equalsIgnoreCase("QUE")) 
        {
            return Appointment_ServerQuebec;
        }
        else
        {
            return Appointment_ServerSherbrooke;
        }
    }

    public static String DetectAppointmentTimeSlot(String AppointmentID)
    {
        if (AppointmentID.substring(3, 4).equalsIgnoreCase("M")) 
        {
            return Appointment_Morning;
        }
        else if (AppointmentID.substring(3, 4).equalsIgnoreCase("A")) 
        {
            return Appointment_Afternoon;
        } 
        else
        {
            return Appointment_Evening;
        }
    }

    public static String DetectAppointmentDate(String AppointmentID)
    {
        return AppointmentID.substring(4, 6) + "/" + AppointmentID.substring(6, 8) + "/20" + AppointmentID.substring(8, 10);
    }

    public String getAppointmentType() 
    {
        return AppointmentType;
    }

    public void setAppointmentype(String AppointmentType) 
    {
        this.AppointmentType = AppointmentType;
    }

    public String getAppointmentID() 
    {
        return AppointmentID;
    }

    public void setAppointmenID(String AppointmentID)
    {
        this.AppointmentID = AppointmentID;
    }

    public String getAppointmentServer() 
    {
        return AppointmentServer;
    }

    public void setEventServer(String AppointmentServer)
    {
        this.AppointmentServer = AppointmentServer;
    }

    public int getAppointmentCapacity() 
    {
        return AppointmentCapacity;
    }

    public void setAppointmentCapacity(int AppointmentCapacity) 
    {
        this.AppointmentCapacity = AppointmentCapacity;
    }

   
    public int getAppointmentRemainCapacity() {
        return AppointmentCapacity - RegisteredClients.size();
    }

    public String getAppointmentDate() 
    {
        return AppointmentDate;
    }

    public void setAppointmentDate(String AppointmentDate) 
    {
        this.AppointmentDate = AppointmentDate;
    }

    public String getAppointmentTimeSlot() 
    {
        return AppointmentTimeSlot;
    }

    public void setAppointmentTimeSlot(String AppointmentTimeSlot) 
    {
        this.AppointmentTimeSlot = AppointmentTimeSlot;
    }

    public boolean isFull()
    {
        return getAppointmentCapacity() == RegisteredClients.size();
    }

    public List<String> getRegisteredClientIDs() 
    {
        return RegisteredClients;
    }

    public void setRegisteredClientsIDs(List<String> RegisteredClientsIDs)
    {
        this.RegisteredClients = RegisteredClientsIDs;
    }

    public int addRegisteredClientID(String RegisteredClientID) 
    {
        if (!isFull())
        {
            if (RegisteredClients.contains(RegisteredClientID)) 
            {
                return Already_Registered;
            } 
            else 
            {
                RegisteredClients.add(RegisteredClientID);
                return Add_Success;
            }
        } 
        else 
        {
            return Appointment_Full;
        }
    }

    public boolean removeRegisteredClientID(String RegisteredClientID) 
    {
        return RegisteredClients.remove(RegisteredClientID);
    }

    @Override
    public String toString()
    
    {
        return " (" + getAppointmentID() + ") in the " + getAppointmentTimeSlot() + " of " + getAppointmentDate() + " Total[Remaining] Capacity: " + getAppointmentCapacity() + "[" + getAppointmentRemainCapacity() + "]";
    }
}
