package ReplicaManager2.Server;


import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.rmi.registry.LocateRegistry;
import java.rmi.registry.Registry;



public class ServerInstance
{

    private String ServerID;
    private String ServerName;
    private int ServerUdpPort;
    public static final int Server_Montreal = 2964;
    public static final int Server_Sherbrooke = 2965;
    public static final int Server_Quebec = 2966;
    public static final String AppointmentManagement_RegisterName = "Appointment_Management";
    private int serverRegistryPort;

    public ServerInstance(String ServerID , String[] args) throws Exception 
    {
        this.ServerID = ServerID;
        switch (ServerID)
        {
            case "MTL":
                ServerName = AppointmentManagement.Appointment_ServerMontreal;
                ServerUdpPort = AppointmentManagement.Montreal_ServerPort;
                serverRegistryPort = Server_Montreal;
                break;
            case "QUE":
                ServerName = AppointmentManagement.Appointment_ServerQuebec;
                ServerUdpPort = AppointmentManagement.Quebec_ServerPort;
                serverRegistryPort = Server_Quebec;
                break;
            case "SHE":
                ServerName = AppointmentManagement.Appointment_ServerSherbrooke;
                ServerUdpPort = AppointmentManagement.Sherbrooke_ServerPort; 
                serverRegistryPort = Server_Sherbrooke;
                break;
        }
        AppointmentManagement remoteObject = new AppointmentManagement(ServerID , ServerName);
        Registry registry = LocateRegistry.createRegistry(serverRegistryPort);
        registry.bind(AppointmentManagement_RegisterName, remoteObject);
        
        System.out.println(ServerName + "--------Server is Started-------");
        Log.ServerLog(ServerID, " Server is Up & Running");
        Runnable task = () -> 
        {
            listenForRequest(remoteObject, ServerUdpPort, ServerName, ServerID);
        };
        Thread thread = new Thread(task);
        thread.start();

    }
 
    private static void listenForRequest(AppointmentManagement obj, int ServerUdpPort, String ServerName, String ServerID) 
    {
        DatagramSocket Socket = null;
        String sendingResult = "";
        try
        {
            Socket = new DatagramSocket(ServerUdpPort);
            byte[] buffer = new byte[1000]; 
            System.out.println(ServerName + " UDP Server has Started on port number " + Socket.getLocalPort());
            Log.ServerLog(ServerID, " UDP Server has Started on port number" + Socket.getLocalPort());
            while (true) 
            {
                DatagramPacket request = new DatagramPacket(buffer, buffer.length);
                Socket.receive(request);
                String sentence = new String(request.getData(), 0 , request.getLength());
                String[] parts = sentence.split(";");
                String method = parts[0];
                String PatientID = parts[1];
                String AppointmentType = parts[2];
                String AppointmentID = parts[3];
                if (method.equalsIgnoreCase("removeappointment")) 
                {
                    Log.ServerLog(ServerID, PatientID, " UDP request received " + method + " ", " appointmentID: " + AppointmentID + " appointmentType: " + AppointmentType + " ", " ...");
                    String result = obj.removeAppointmentUDP(AppointmentID, AppointmentType, PatientID);
                    sendingResult = result + ";";
                }
                else if (method.equalsIgnoreCase("listappointmentAvailability")) 
                {
                    Log.ServerLog(ServerID, PatientID, " UDP request received " + method + " ", " appointmentType: " + AppointmentID + " ", " ...");
                    String result = obj.listAppointmentAvailabilityUDP(AppointmentType);
                    sendingResult = result + ";";
                }
                else if (method.equalsIgnoreCase("bookappointment")) 
                {
                    Log.ServerLog(ServerID, PatientID, " UDP request received " + method + " ", " appointmentID: " + AppointmentID + " appointmentType: " + AppointmentType + " ", " ...");
                    String result = obj.BookAppointment(PatientID, AppointmentID, AppointmentType);
                    sendingResult = result + ";";
                }
                else if (method.equalsIgnoreCase("cancelappointment")) 
                {
                    Log.ServerLog(ServerID, PatientID, " UDP request received " + method + " ", " appointmentID: " + AppointmentID + " appointmentType: " + AppointmentType + " ", " ...");
                    String result = obj.CancelAppointment(PatientID, AppointmentID, AppointmentType);
                    sendingResult = result + ";";
                }
                byte[] sendData = sendingResult.getBytes();
                DatagramPacket reply = new DatagramPacket(sendData, sendingResult.length(), request.getAddress(),
                request.getPort());
                Socket.send(reply);
                Log.ServerLog(ServerID, PatientID, " UDP reply sent " + method + " ", " appointmentID: " + AppointmentID + " appointmentType: " + AppointmentType + " ", sendingResult);
            }
        } 
        catch (SocketException e) 
        {
            System.out.println("SocketException: " + e.getMessage());
        }
        catch (IOException e) 
        {
            System.out.println("IOException: " + e.getMessage());
        }
        finally
        {
            if (Socket != null)
            {
                Socket.close();
            }
        }
    }
//    private void addTestData(AppointmentManagement remoteObject) 
//    {
//        switch (ServerID)
//        {
//            case "MTL":
//                	remoteObject.addNewAppointment("MTLM030322", AppointmentModel.Physician, 2);
//                	remoteObject.addNewAppointment("MTLM040322", AppointmentModel.Dental, 1);
//                	remoteObject.addNewAppointment("MTLE050322", AppointmentModel.Dental, 1);
//                	break;
//               
//            case "QUE":
//            		remoteObject.addNewPatientToClients("QUEP0002");
//            		remoteObject.addNewPatientToClients("QUEP0003");
//            		remoteObject.AddAppointment("MTLM070322", AppointmentModel.Physician, 2);
//            		break;
//                
//            case "SHE":
//                  	remoteObject.addNewPatientToClients("SHEP0002");
//                  	break;
//        }
//    }
 
}
