package ReplicaManager2.Server;



import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.rmi.Remote;
import java.rmi.RemoteException;
import java.rmi.server.UnicastRemoteObject;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

import org.omg.CORBA.ORB;


public class AppointmentManagement extends UnicastRemoteObject implements AppointmentManagementInterface
{
	public static final int Montreal_ServerPort = 9022;
    public static final int Quebec_ServerPort = 9023;
    public static final int Sherbrooke_ServerPort = 9024;
    public static final String Appointment_ServerMontreal = "Montreal";
    public static final String Appointment_ServerQuebec = "Quebec";
    public static final String Appointment_ServerSherbrooke = "Sherbrooke";
	private String ServerID;
    private String ServerName;
    // HashMap
    private Map<String, Map<String, AppointmentModel>> AllAppointments;
    private Map<String, Map<String, List<String>>> ClientAppointments;
    private Map<String, ClientModel> ServerClients;

    public AppointmentManagement(String ServerID, String ServerName) throws RemoteException 
    {
        super();
        this.ServerID = ServerID;
        this.ServerName = ServerName;
        AllAppointments = new ConcurrentHashMap<>();
        AllAppointments.put(AppointmentModel.Physician, new ConcurrentHashMap<>());
        AllAppointments.put(AppointmentModel.Surgon, new ConcurrentHashMap<>());
        AllAppointments.put(AppointmentModel.Dental, new ConcurrentHashMap<>());
        ClientAppointments = new ConcurrentHashMap<>();
        ServerClients = new ConcurrentHashMap<>();
        
          addTestData();
                
    }
    
  
    private void addTestData() 
    {
    	
      ClientModel testPatient = new ClientModel(ServerID + "P0001");
      //ClientModel testAdmin = new ClientModel(ServerID + "M0001");
      ServerClients.put(testPatient.getClientID(), testPatient);
     // ServerClients.put(testAdmin.getClientID(), testAdmin);
       
     ClientAppointments.put(testPatient.getClientID(), new ConcurrentHashMap<>());
//      ClientAppointments.put(testAdmin.getClientID(), new ConcurrentHashMap<>());

      AppointmentModel samplePhy = new AppointmentModel(AppointmentModel.Physician, ServerID + "M210322", 7);
      samplePhy.addRegisteredClientID(testPatient.getClientID());
  //    samplePhy.addRegisteredClientID(testAdmin.getClientID());
      ClientAppointments.get(testPatient.getClientID()).put(samplePhy.getAppointmentType(), new ArrayList<>());
      ClientAppointments.get(testPatient.getClientID()).get(samplePhy.getAppointmentType()).add(samplePhy.getAppointmentID());
//      ClientAppointments.get(testAdmin.getClientID()).put(samplePhy.getAppointmentType(), new ArrayList<>());
//      ClientAppointments.get(testAdmin.getClientID()).get(samplePhy.getAppointmentType()).add(samplePhy.getAppointmentID());


      AppointmentModel sampleSur = new AppointmentModel(AppointmentModel.Surgon, ServerID + "A220322", 15);
      sampleSur.addRegisteredClientID(testPatient.getClientID());
  //    sampleSur.addRegisteredClientID(testAdmin.getClientID());
      ClientAppointments.get(testPatient.getClientID()).put(sampleSur.getAppointmentType(), new ArrayList<>());
      ClientAppointments.get(testPatient.getClientID()).get(sampleSur.getAppointmentType()).add(sampleSur.getAppointmentID());
//      ClientAppointments.get(testAdmin.getClientID()).put(sampleSur.getAppointmentType(), new ArrayList<>());
//      ClientAppointments.get(testAdmin.getClientID()).get(sampleSur.getAppointmentType()).add(sampleSur.getAppointmentID());


      AppointmentModel sampleDen = new AppointmentModel(AppointmentModel.Dental, ServerID + "E230322", 20);
      sampleDen.addRegisteredClientID(testPatient.getClientID());
//      sampleDen.addRegisteredClientID(testAdmin.getClientID());
      ClientAppointments.get(testPatient.getClientID()).put(sampleDen.getAppointmentType(), new ArrayList<>());
      ClientAppointments.get(testPatient.getClientID()).get(sampleDen.getAppointmentType()).add(sampleDen.getAppointmentID());
//      ClientAppointments.get(testAdmin.getClientID()).put(sampleDen.getAppointmentType(), new ArrayList<>());
//      ClientAppointments.get(testAdmin.getClientID()).get(sampleDen.getAppointmentType()).add(sampleDen.getAppointmentID());

      
      AllAppointments.get(AppointmentModel.Physician).put(samplePhy.getAppointmentID(), samplePhy);
      AllAppointments.get(AppointmentModel.Surgon).put(sampleSur.getAppointmentID(), sampleSur);
      AllAppointments.get(AppointmentModel.Dental).put(sampleDen.getAppointmentID(), sampleDen);
  }
  
    private static int getServerPort(String Branch)
    {
        if (Branch.equalsIgnoreCase("MTL")) 
        {
            return Montreal_ServerPort;
        }
        else if (Branch.equalsIgnoreCase("SHE")) 
        {
            return Sherbrooke_ServerPort;
        } 
        else if (Branch.equalsIgnoreCase("QUE"))
        {
            return Quebec_ServerPort;
        }
        return 1;
    }

    @Override
    public String AddAppointment(String AppointmentID, String AppointmentType, int Capacity) 
    {
        String Response;
        if (isAppointmentOfThisServer(AppointmentID))
        {
            if (AppointmentExists(AppointmentType, AppointmentID)) 
            { 
            	if (AllAppointments.get(AppointmentType).get(AppointmentID).getAppointmentCapacity() <= Capacity) 
            	{
            	System.out.println(" Increase the capacity for the valid AppointmentID");
                AllAppointments.get(AppointmentType).get(AppointmentID).setAppointmentCapacity(Capacity);
                Response = "Success: Appointment " + AppointmentID + " Capacity increased to " + Capacity;
                try 
                {
                    Log.ServerLog(ServerID, "null", "AddAppointment", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " Capacity " + Capacity + " ", Response);     
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            } 
            else
            {
                Response = "Failed:Appointment Already Exists, Cannot Decrease Appointment Capacity";
                System.out.println("failed for adding Appointment");
                try 
                {
                    Log.ServerLog(ServerID, "null", " RMI addAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " Capacity " + Capacity + " ", Response);
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            }
        }
            else
            {
            	AppointmentModel Appointment = new AppointmentModel(AppointmentType, AppointmentID, Capacity);
                Map<String, AppointmentModel> AppointmentHashMap = AllAppointments.get(AppointmentType);
                AppointmentHashMap.put(AppointmentID, Appointment);
                AllAppointments.put(AppointmentType, AppointmentHashMap);
                Response = "Success: Appointment " + AppointmentID + " added successfully";
                try 
                {
                    Log.ServerLog(ServerID, "null", "AddAppointment ", " AppoitemntID: " + AppointmentID + " AppointmentType: " + AppointmentType + " BokingCapacity " + Capacity + " ", Response);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            }         
         }
        else
        {
        	System.out.println("can not book other server");
            Response = "Failed: Cannot Add Appointment to servers other than " + ServerName;
            try 
            {
                Log.ServerLog(ServerID, "null", "AddAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " Capacity " + Capacity + " ", Response);
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            return Response;
        }
    }
    @Override
    public String RemoveAppointment(String AppointmentID, String AppointmentType) 
    {
        String Response;
        if (isAppointmentOfThisServer(AppointmentID)) 
        {
            if (AppointmentExists(AppointmentType, AppointmentID))
            {
                List<String> registeredClients = AllAppointments.get(AppointmentType).get(AppointmentID).getRegisteredClientIDs();
                AllAppointments.get(AppointmentType).remove(AppointmentID);
                AddPatientsToNextSameAppointment(AppointmentID, AppointmentType, registeredClients);
               
                System.out.println("Remove Appoointment Successfully");
                Response = "Success: Appointment Removed Successfully";
                try 
                {
                    Log.ServerLog(ServerID, "null", "RemoveAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            }
            else 
            {
            	System.out.println("failed to remove");
                Response = "Failed: Appointment " + AppointmentID + " Does Not Exist";
                try 
                {
                    Log.ServerLog(ServerID, "null", "RemoveAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            }
        } 
        else 
        {
        	System.out.println("can not remove from other servers");
            Response = "Failed: Cannot Remove Appointment from servers other than " + ServerName;
            try 
            {
                Log.ServerLog(ServerID, "null", "RemoveAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            return Response;
        }
    }

    @Override
    public String ListAppointmentAvailability(String AppointmentType) 
    {
        String Response;
        Map<String, AppointmentModel> Appointments = AllAppointments.get(AppointmentType);
        StringBuilder builder = new StringBuilder();
        builder.append(ServerName + " Server " + AppointmentType + ":\n");
        if (Appointments.size() == 0) 
        {
            builder.append("No Appointments of Type " + AppointmentType);
        }
        else 
        {
            for (AppointmentModel Appointment :
                    Appointments.values())
            {
                builder.append(Appointment.toString() + " || ");
            }
            builder.append("\n---------------------\n");
        }
        String otherServer1, otherServer2;
        if (ServerID.equals("MTL"))
        {
            otherServer1 = sendUDPMessage(Sherbrooke_ServerPort, "listAppointmentAvailability", "null", AppointmentType, "null");
            otherServer2 = sendUDPMessage(Quebec_ServerPort, "listAppointmentAvailability", "null", AppointmentType, "null");
        } 
        else if (ServerID.equals("SHE")) 
        {
            otherServer1 = sendUDPMessage(Quebec_ServerPort, "listAppointmentAvailability", "null", AppointmentType, "null");
            otherServer2 = sendUDPMessage(Montreal_ServerPort, "listAppointmentAvailability", "null", AppointmentType, "null");
        } 
        else
        {
            otherServer1 = sendUDPMessage(Montreal_ServerPort, "listAppointmentAvailability", "null", AppointmentType, "null");
            otherServer2 = sendUDPMessage(Sherbrooke_ServerPort, "listAppointmentAvailability", "null", AppointmentType, "null");
        }
        builder.append(otherServer1).append(otherServer2);
        Response = builder.toString();
        try 
        {
            Log.ServerLog(ServerID, "null", "ListAppointmentAvailability ", " AppointmentType: " + AppointmentType + " ", Response);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return Response;
    }
    public String BookAppointment(String PatientID, String AppointmentID, String AppointmentType)
    {
        String Response;
        System.out.println("Received request to book appointment -------------0");
        CheckClientExists(AppointmentID);
        if (isAppointmentOfThisServer(AppointmentID))
        {
            AppointmentModel BookedAppointment = AllAppointments.get(AppointmentType).get(AppointmentID);
            if (BookedAppointment == null)
            {
                Response = "Failed: Appointment " + AppointmentID + " Does not exists";
                try 
                {
                    Log.ServerLog(ServerID, PatientID, "BookAppointment", " AppointmentID: " + AppointmentID + "AppointmentType: " + AppointmentType + " ", Response);
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            }
            
            if (!BookedAppointment.isFull()) 
            { 
                if (ClientAppointments.containsKey(PatientID)) 
                {
                    if (ClientAppointments.get(PatientID).containsKey(AppointmentType)) 
                    {                       
                 	if (!ClientHasAppointment(PatientID, AppointmentType, AppointmentID)) 
                   	{
                     if (isPatientOfThisServer(PatientID))
                        {        
                       	if (!exceedDayLimit(PatientID, AppointmentType, AppointmentID))
                       	
                        	System.out.println("Added new appointment for patient: "+ PatientID);
                        	ClientAppointments.get(PatientID).get(AppointmentType).add(AppointmentID);
                           }
                           
	                         else
                        	 {
                        	 	System.out.println("failed if on same day");
                        		Response = "Failed: Appointment booked for this Patient  with this AppointmentType "+AppointmentID+" for the day";
                        		ClientAppointments.get(PatientID).get(AppointmentType).remove(AppointmentID);
                                try 
                                {
                                	Log.ServerLog(ServerID, PatientID, " BookAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                                }
                                catch (IOException e) 
                                {
                                		e.printStackTrace();
                                }
                                return Response;
                        	 }
                   	}                 	
                                else 
                                {
                                    Response = "Failed: Appointment " + AppointmentID + " Already Booked";
                                    try 
                                    {
                                        Log.ServerLog(ServerID, PatientID, " RMI BookAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                                    }
                                    catch (IOException e) 
                                    {
                                        e.printStackTrace();
                                    }
                                    return Response;
                                }
                            
                            } else 
                            {
                                if (isPatientOfThisServer(PatientID))
                                    addAppointmentTypeAndAppointment(PatientID, AppointmentType, AppointmentID);
                            }
                        } 
                else 
                        {
                            if (isPatientOfThisServer(PatientID))
                                addPatientAndAppointment(PatientID, AppointmentType, AppointmentID);
                        }
                        if (AllAppointments.get(AppointmentType).get(AppointmentID).addRegisteredClientID(PatientID) == AppointmentModel.Add_Success)
                        {
                            Response = "Success: Appointment " + AppointmentID + " Booked Successfully";
                        } else if (AllAppointments.get(AppointmentType).get(AppointmentID).addRegisteredClientID(PatientID) == AppointmentModel.Appointment_Full) {
                            Response = "Failed: Appointment " + AppointmentID + " is Full";
                        } else 
                        {
                            Response = "Failed: Cannot Add You To Appointment " + AppointmentID;
                        }
                        try
                        {
                            Log.ServerLog(ServerID, PatientID, " BookAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        return Response;
                        }
        
       else 
            {
                Response = "Failed: Appointment " + AppointmentID + " is Full";
                try 
                {
                    Log.ServerLog(ServerID, PatientID, " BookAppontment ", " eventID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                } 
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            }
        } 
        else 
        {
            if (ClientHasAppointment(PatientID, AppointmentType, AppointmentID))
            {
                String ServerResponse = "Failed: Appointment " + AppointmentID + " Already Booked";
                try 
                {
                    Log.ServerLog(ServerID, PatientID, " BookAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", ServerResponse);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return ServerResponse;
            }
         	if (exceedWeekLimit(PatientID, AppointmentID.substring(4))) 
         	{
         	System.out.println("Received request to book appointment weekly check -------------022222");
             String ServerResponse = sendUDPMessage(getServerPort(AppointmentID.substring(0, 3)), "BookAppointment", PatientID, AppointmentType, AppointmentID);
             if (ServerResponse.startsWith("Success:")) 
             {
                 if (ClientAppointments.get(PatientID).containsKey(AppointmentType)) 
                 {
                     ClientAppointments.get(PatientID).get(AppointmentType).add(AppointmentID);
                 }
                 else 
                 {
                 	System.out.println("Added ------1");
                     List<String> temp = new ArrayList<>();
                     temp.add(AppointmentID);
                     ClientAppointments.get(PatientID).put(AppointmentType, temp);
                 }
             }
             try 	
             {
                 Log.ServerLog(ServerID, PatientID, " BookAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", ServerResponse);
             }
             catch (IOException e) 
             {
                 e.printStackTrace();
             }
             return ServerResponse;
         	}
         	else 
         	{
         		System.out.println("Max limit Reaches");

             Response = "Failed: You Cannot Book Appointment in Other Servers For This Week(Max Weekly Limit = 3)";
             try 
             {
                 Log.ServerLog(ServerID, PatientID, " BookAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
             }
             catch (IOException e) 
             {
                 e.printStackTrace();
             }
             return Response;
         }
        
      }
    
    }
   
    @Override
    public String getAppointmentSchedule(String PatientID)
    {
        String Response;
        if (!CheckClientExists(PatientID)) 
        {
            Response = "Appointment Schedule Empty For " + PatientID;
            try 
            {
                Log.ServerLog(ServerID, PatientID, " RMI getAppointmentSchedule ", "null", Response);
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            return Response;
        }
        Map<String, List<String>> Appointments = ClientAppointments.get(PatientID);
        if (Appointments.size() == 0) 
        {
            Response = "Appointment Schedule Empty For " + PatientID;
            try 
            {
                Log.ServerLog(ServerID, PatientID, " RMI getAppointmentSchedule ", "null", Response);
            }
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            return Response;
        }
        StringBuilder builder = new StringBuilder();
        for (String AppointmentType :
                Appointments.keySet()) 
        {
            builder.append(AppointmentType + ":\n");
            for (String AppointmentID :
                    Appointments.get(AppointmentType)) 
            {
                builder.append(AppointmentID + " || ");
            } 
            builder.append("\n-------------------------\n");
        }
        Response = builder.toString();
        try 
        {
            Log.ServerLog(ServerID, PatientID, " getAppointmentSchedule ", "null", Response);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        return Response;
    }
    @Override
    public String CancelAppointment(String PatientID, String AppointmentID, String AppointmentType) 
    {
        String Response;
        if (isAppointmentOfThisServer(AppointmentID)) 
        {
            if (isPatientOfThisServer(PatientID)) 
            {
                if (!CheckClientExists(PatientID)) 
                {
                    Response = "Failed: You " + PatientID + " Are Not Registered in " + AppointmentID;
                    System.out.println("can not cancel ------------");
                    try 
                    {
                        Log.ServerLog(ServerID, PatientID, "CancelAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                    }
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    return Response;
                } 
                else 
                {
                	if (RemoveAppointmentIfExists(PatientID, AppointmentType, AppointmentID)) 
                    {
                        AllAppointments.get(AppointmentType).get(AppointmentID).removeRegisteredClientID(PatientID);
                        Response = "Success: Appointment " + AppointmentID + " Canceled for " + PatientID;
                        System.out.println("cancel succesfuuly");
                        try 
                        {
                            Log.ServerLog(ServerID, PatientID, " RMI CancelAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                        }
                        catch (IOException e) 
                        {
                            e.printStackTrace();
                        }
                        return Response;
                    } 
                    else 
                    {
                        Response = "Failed: You " + PatientID + " Are Not Registered in " + AppointmentID;
                        try 
                        {
                            Log.ServerLog(ServerID, PatientID, " RMI CancelAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                        }
                        catch (IOException e) 
                        {
                            e.printStackTrace();
                        }
                        return Response;
                    }
                }
            } 
            else 
            {
                if (AllAppointments.get(AppointmentType).get(AppointmentID).removeRegisteredClientID(PatientID)) {
                    Response = "Success: Appointment " + AppointmentID + " Canceled for " + PatientID ;
                    try 
                    {
                        Log.ServerLog(ServerID, PatientID, " CancelAppointment ", " AppointmentID: " + AppointmentID + " ApointmnetType: " + AppointmentType + " ", Response);
                    }
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    return Response;
                } 
                else 
                {
                    Response = "Failed: You " + PatientID + " Are Not Registered in " + AppointmentID;
                    try 
                    {
                       Log.ServerLog(ServerID, PatientID, " CancelAppointment ", "AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                    } 
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    return Response;
                }
            }
        }
            else 
            {
            	if (isPatientOfThisServer(PatientID)) 
            	{
                    if (CheckClientExists(PatientID)) 
                    {
                        if (RemoveAppointmentIfExists(PatientID, AppointmentType, AppointmentID)) 
                        {
                        	Response = "Success: Appointment " + AppointmentID + " Canceled for " + PatientID;
                        	try 
                        	{
                        		Log.ServerLog(ServerID, PatientID, " RMI CancelAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                        	}
                        	catch (IOException e) 
                        	{
                        		e.printStackTrace();
                        	}
                        	return Response;
                        } 
                    }
            	}
                    Response = "Failed: You " + PatientID + " Are Not Registered in " + AppointmentID;
                    try 
                    {
                        Log.ServerLog(ServerID, PatientID, " RMI CancelAppointment ", " AppointmentID: " + AppointmentID + " AppointmentType: " + AppointmentType + " ", Response);
                    }
                    catch (IOException e) 
                    {
                        e.printStackTrace();
                    }
                    return Response;
                }
            }        
    @Override
    public String SwapAppointment(String PatientID, String newAppointmentID, String newAppointmentType, String oldAppointmentID, String oldAppointmentType) 
    {
        String Response;
        if (!CheckClientExists(PatientID)) 
        {
        	System.out.println("not Registreted in old AppointmentID222222......");
            Response = "Failed: You " + PatientID + " Are Not Registered in " + oldAppointmentID;
            try 
            {
                Log.ServerLog(ServerID, PatientID, "swapAppointment ", " oldAppointmentID: " + oldAppointmentID + " oldAppointmentType: " + oldAppointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", Response);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
            return Response;
        } 
        else
        {
            if (ClientHasAppointment(PatientID, oldAppointmentType, oldAppointmentID)) 
            {
                String BookResponse = "Failed: can not send request to book newAppointmrnt " + newAppointmentID;
                String CancelResponse = "Failed: can not send request to cancel oldAppointment " + oldAppointmentID;
                synchronized (this) 
                {
                    if (onTheSameWeek(newAppointmentID.substring(4), oldAppointmentID) && !exceedWeekLimit(PatientID, newAppointmentID.substring(4))) 
                    {
                    	CancelResponse = CancelAppointment(PatientID, oldAppointmentID, oldAppointmentType);
                        if (CancelResponse.startsWith("Success:")) 
                        {
                        	BookResponse = BookAppointment(PatientID, newAppointmentID, newAppointmentType);
                        }
                    } 
                    else 
                    {
                    	BookResponse = BookAppointment(PatientID, newAppointmentID, newAppointmentType);
                        if (BookResponse.startsWith("Success:")) 
                        {
                        	CancelResponse = CancelAppointment(PatientID, oldAppointmentID, oldAppointmentType);
                        }
                    }
                }
                if (BookResponse.startsWith("Success:") && CancelResponse.startsWith("Success:")) 
                {	
                	System.out.println("Registreted for new AppointmentID");
                    Response = "Success: Appointment " + oldAppointmentID + " swapped with " + newAppointmentID;
                }
                else if (BookResponse.startsWith("Success:") && CancelResponse.startsWith("Failed:")) 
                {
                	CancelAppointment(PatientID, newAppointmentID, newAppointmentType);
                    Response = "Failed: Your oldAppointment " + oldAppointmentID + " Could not be Canceled reason: " + CancelResponse;
                }
                else if (BookResponse.startsWith("Failed:") && CancelResponse.startsWith("Success:")) 
                {
                    String Response1 = BookAppointment(PatientID, oldAppointmentID, oldAppointmentType);
                    Response = "Failed: Your newappointment " + newAppointmentID + " Could not be Booked reason: " + BookResponse + " And your old Appointment Rolling back: " + Response1;
                }
                else
                {
                    Response = "Failed: on Both newAppointment " + newAppointmentID + " Booking reason: " + BookResponse + " and oldAppointment " + oldAppointmentID + " Canceling reason: " + CancelResponse;
                }
                try
                {
                    Log.ServerLog(ServerID, PatientID, " SwapAppointment ", " oldAppointmentID: " + oldAppointmentID + " oldAppointmentType: " + oldAppointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", Response);
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            } 
            else 
            {
            	System.out.println("Not Registreted for old AppointmentID111111.......");
                Response = "Failed: You " + PatientID + " Are Not Registered in " + oldAppointmentID;
                try 
                {
                    Log.ServerLog(ServerID, PatientID, "SwapAppointment ", " oldAppointmentID: " + oldAppointmentID + " oldAppointmentType: " + oldAppointmentType + " newAppointmentID: " + newAppointmentID + " newAppointmentType: " + newAppointmentType + " ", Response);
                }
                catch (IOException e) 
                {
                    e.printStackTrace();
                }
                return Response;
            }
        }
    }
    @Override
    public String shutDown() throws RemoteException {
        new Thread(() -> {
            try {
                Thread.sleep(100);
            } catch (InterruptedException e) {
                // ignored
            }
            System.exit(1);
        });
        System.out.println(ServerName + ">>>Shutting down");
        return "Shutting down";
    }
   // for UDP CAlls only
     
   public String removeAppointmentUDP(String oldAppointmentID, String AppointmentType, String PatientID) 
    {
	   if (!CheckClientExists(PatientID) )
        {
		   return "Failed: You " + PatientID + " Are Not Registered in " + oldAppointmentID;
        }
	   else 
	   {
            if (RemoveAppointmentIfExists(PatientID, AppointmentType, oldAppointmentID)) 
            {
                return "Success: Appointment " + oldAppointmentID + " Was Removed from " + PatientID + " Schedule";
            }
            else 
            {
                return "Failed: You " + PatientID + " Are Not Registered in " + oldAppointmentID;
            }
        }
    }
    // for UDP cAlls only
     
    public String listAppointmentAvailabilityUDP(String AppointmentType) throws RemoteException 
    {
        Map<String, AppointmentModel> Appointments = AllAppointments.get(AppointmentType);
        StringBuilder builder = new StringBuilder();
        builder.append(ServerName + " Server " + AppointmentType + ":\n");
        if (Appointments.size() == 0) 
        {
            builder.append("No Appointments of Type " + AppointmentType);
        }
        else 
        {
            for (AppointmentModel Appointment :
                    Appointments.values()) 
            {
                builder.append(Appointment.toString() + " || ");
            }
        }
        builder.append("\n-------------------\n");
        return builder.toString();
    }

    private String sendUDPMessage(int serverPort, String method, String PatientID, String AppointmentType, String AppointmentId) 
    {
        DatagramSocket Socket = null;
        String result = "";
        String dataFromClient = method + ";" + PatientID + ";" + AppointmentType + ";" + AppointmentId;
        try
        {
            Log.ServerLog(ServerID, PatientID, " UDP request sent " + method + " ", " AppointmentID: " + AppointmentId + " AppointmentType: " + AppointmentType + " ", " ... ");
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        }
        try 
        {
            Socket = new DatagramSocket();
            byte[] message = dataFromClient.getBytes();
            InetAddress aHost = InetAddress.getByName("localhost");
            DatagramPacket request = new DatagramPacket(message, dataFromClient.length(), aHost, serverPort);
            Socket.send(request);

            byte[] buffer = new byte[1000];
            DatagramPacket reply = new DatagramPacket(buffer, buffer.length);

            Socket.receive(reply);
            result = new String(reply.getData());
            String[] parts = result.split(";");
            result = parts[0];
        }
        catch (SocketException e) 
        {
            System.out.println("Socket: " + e.getMessage());
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            System.out.println("IO: " + e.getMessage());
        }
        finally 
        {
            if (Socket != null)
                Socket.close();
        }
        try
        {
            Log.ServerLog(ServerID, PatientID, " UDP reply received" + method + " ", " AppointmentID: " + AppointmentId + " AppointmentType: " + AppointmentType + " ", result);
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        return result;

    } 
    private String getNextSameAppointment(Set<String> keySet, String AppointmentType, String oldAppointmentID) 
    {
        List<String> sortedIDs = new ArrayList<String>(keySet);
        sortedIDs.add(oldAppointmentID);
        Collections.sort(sortedIDs, new Comparator<String>() 
        {
            @Override
            public int compare(String ID1, String ID2) 
            {
                Integer timeSlot1 = 0;
                switch (ID1.substring(3, 4).toUpperCase()) 
                {
                    case "M":
                        timeSlot1 = 1;
                        break;
                    case "A":
                        timeSlot1 = 2;
                        break;
                    case "E":
                        timeSlot1 = 3;
                        break;
                }
                Integer timeSlot2 = 0;
                switch (ID2.substring(3, 4).toUpperCase()) 
                {
                    case "M":
                        timeSlot2 = 1;
                        break;
                    case "A":
                        timeSlot2 = 2;
                        break;
                    case "E":
                        timeSlot2 = 3;
                        break;
                }
                Integer date1 = Integer.parseInt(ID1.substring(8, 10) + ID1.substring(6, 8) + ID1.substring(4, 6));
                Integer date2 = Integer.parseInt(ID2.substring(8, 10) + ID2.substring(6, 8) + ID2.substring(4, 6));
                int dateCompare = date1.compareTo(date2);
                int timeSlotCompare = timeSlot1.compareTo(timeSlot2);
                if (dateCompare == 0) 
                {
                    return ((timeSlotCompare == 0) ? dateCompare : timeSlotCompare);
                }
                else
                {
                    return dateCompare;
                }
            }
        });
        int index = sortedIDs.indexOf(oldAppointmentID) + 1;
        for (int i = index; i < sortedIDs.size(); i++) 
        {
            if (!AllAppointments.get(AppointmentType).get(sortedIDs.get(i)).isFull()) 
            {
                return sortedIDs.get(i);
            }
        }
        return "Failed";
    }
    
    private boolean exceedDayLimit(String PatientID, String AppointmentType, String AppointmentID) 
    {
        
        List<String> appointments = ClientAppointments.get(PatientID).get(AppointmentType);
//        System.out.println("Checking for day limit :");
//        for (String appointment: appointments) {
//        	System.out.println(appointment.substring(4));
//        	System.out.println(AppointmentID.substring(4));
//        }
        for (String appointment: appointments) 
        {
            if (appointment.substring(4).equals(AppointmentID.substring(4)))
            {
//            	System.out.println(appointment.substring(4));
//            	System.out.println(AppointmentID.substring(4) + "\n-----------------");
                return true;
            }
        }

        return false;
    }
    private boolean exceedWeekLimit(String PatientID, String AppointmentDate) 
    {

        int limit = 0;
        for (int i = 0; i < 3; i++) 
        {
           List<String> registeredIDs = new ArrayList<>();
            switch (i) 
            {
                case 0:
                    if (ClientAppointments.get(PatientID).containsKey(AppointmentModel.Physician))
                    {
                        registeredIDs = ClientAppointments.get(PatientID).get(AppointmentModel.Physician);
                    }
                    break;
                case 1:
                    if (ClientAppointments.get(PatientID).containsKey(AppointmentModel.Surgon)) 
                    {
                        registeredIDs = ClientAppointments.get(PatientID).get(AppointmentModel.Surgon);
                    }
                    break;
                case 2:
                    if (ClientAppointments.get(PatientID).containsKey(AppointmentModel.Dental))
                    {
                        registeredIDs = ClientAppointments.get(PatientID).get(AppointmentModel.Dental);
                    }
                    break;
            }
            for (String AppointmentID : registeredIDs) 
            {
            	if (onTheSameWeek(AppointmentDate, AppointmentID) && !isAppointmentOfThisServer(AppointmentID)) {
                    limit++;
                }
                if (limit == 3)
                    return false;
            }
        }
        return true;
    }

    private void AddPatientsToNextSameAppointment(String oldAppointmentID, String AppointmentType, List<String> registeredClients) {
        for (String PatientID :
                registeredClients) 
        {
            if (PatientID.substring(0, 3).equals(ServerID)) {
                RemoveAppointmentIfExists(PatientID, AppointmentType, oldAppointmentID);
            }
           
            tryToBookNextSameAppointment(PatientID, AppointmentType, oldAppointmentID);
        }
    }

    private void tryToBookNextSameAppointment(String PatientID, String AppointmentType, String oldAppointemntID) 
    {
        String Response;
        String nextSameAppointmentResult = getNextSameAppointment(AllAppointments.get(AppointmentType).keySet(), AppointmentType, oldAppointemntID);
        if (nextSameAppointmentResult.equals("Failed")) 
        {
            if (!PatientID.substring(0, 3).equals(ServerID))
            {
                sendUDPMessage(getServerPort(PatientID.substring(0, 3)), "Remove Appointment", PatientID, AppointmentType, oldAppointemntID + ":null");
            }
            Response = "Acquiring nextSameAppointment for Client (" + PatientID + "):" + nextSameAppointmentResult;
            try 
            {
                Log.ServerLog(ServerID, PatientID, " addCustomersToNextSameAppointment ", " oldAppointmentID: " + oldAppointemntID + " AppointmentType: " + AppointmentType + " ", Response);
            } 
            catch (IOException e) 
            {
                e.printStackTrace();
            }
        } 
        else 
        {
            if (PatientID.substring(0, 3).equals(ServerID)) 
            {
                BookAppointment(PatientID, nextSameAppointmentResult, AppointmentType);
            }
            else 
            {
                String oldNewAppointmentID = oldAppointemntID + ":" + nextSameAppointmentResult;
                sendUDPMessage(getServerPort(PatientID.substring(0, 3)), "removeAppointment", PatientID, AppointmentType, oldNewAppointmentID);
            }
        }
    }
	private synchronized boolean AppointmentExists(String AppointmentType, String AppointmentID) 
 	{
     return AllAppointments.get(AppointmentType).containsKey(AppointmentID);
 	}

 	private synchronized boolean isAppointmentOfThisServer(String AppointmentID) 
 	{
     return AppointmentModel.DetectAppointmentServer(AppointmentID).equals(ServerName);
 	}

 	private synchronized boolean CheckClientExists(String PatientID) 
 	{
 		if (!ServerClients.containsKey(PatientID)) 
 		{
 			addNewPatientToClients(PatientID);
 			return false;
 		}
 		else 
 		{
 			return true;
 		}
 	}

 	private synchronized boolean ClientHasAppointment(String PatientID, String AppointmentType, String AppointmentID)
 	{
 		if (ClientAppointments.get(PatientID).containsKey(AppointmentType)) 
 		{
            return ClientAppointments.get(PatientID).get(AppointmentType).contains(AppointmentID);
        } 
 		else 
 		{
            return false;
        }
    }
 	

 private boolean RemoveAppointmentIfExists(String PatientID, String AppointmentType, String AppointmentID) 
 {
     if (ClientAppointments.get(PatientID).containsKey(AppointmentType)) 
     {
      return ClientAppointments.get(PatientID).get(AppointmentType).remove(AppointmentID);
     }
     else
     {
         return false;
     }
 }

 	private synchronized void addPatientAndAppointment(String PatientID, String AppointmentType, String AppointmentID) 
 	{
 		Map<String, List<String>> temp = new ConcurrentHashMap<>();
 		List<String> temp2 = new ArrayList<>();
 		temp2.add(AppointmentID);
 		temp.put(AppointmentType, temp2);
 		ClientAppointments.put(PatientID, temp);
 	}

 	private synchronized void addAppointmentTypeAndAppointment(String PatientID, String AppointmentType, String AppointmentID) 
 	{
 		List<String> temp = new ArrayList<>();
 		temp.add(AppointmentID);
 		ClientAppointments.get(PatientID).put(AppointmentType, temp);
 	}
 	private boolean onTheSameWeek(String newAppointmentDate, String AppointmentID) 
 	{
        if (AppointmentID.substring(6, 8).equals(newAppointmentDate.substring(2, 4)) && AppointmentID.substring(8, 10).equals(newAppointmentDate.substring(4, 6))) 
        {
            int day1 = Integer.parseInt(AppointmentID.substring(4, 6));
            int day2 = Integer.parseInt(newAppointmentDate.substring(0, 2));
            if (day1 % 7 == 0) {
                day1--;
            }
            if (day2 % 7 == 0) {
                day2--;
            }
            int week1 = day1 / 7;
            int week2 = day2 / 7;
            return week1 == week2;
        }
        else 
        {
            return false;
        }
    }

 	private boolean isPatientOfThisServer(String PatientID) 
 	{
     return PatientID.substring(0, 3).equals(ServerID);
 	} 

	public Map<String, Map<String, AppointmentModel>> getAllAppointments() 
    {
        return AllAppointments;
    }

    public Map<String, Map<String, List<String>>> getClientAppointments() 
    {
        return ClientAppointments;
    }

    public Map<String, ClientModel> getServerClients() 
    {
        return ServerClients;
    }

    public void addNewAppointment(String AppointmentID, String AppointmentType, int capacity) 
    {
        AppointmentModel samplePhy = new AppointmentModel(AppointmentType, AppointmentID, capacity);
        AllAppointments.get(AppointmentType).put(AppointmentID, samplePhy);
    }

    public void addNewPatientToClients(String PatientID) 
    {
        ClientModel newPatient = new ClientModel(PatientID);
        ServerClients.put(newPatient.getClientID(), newPatient);
        ClientAppointments.put(newPatient.getClientID(), new ConcurrentHashMap<>());
    }

	
}
