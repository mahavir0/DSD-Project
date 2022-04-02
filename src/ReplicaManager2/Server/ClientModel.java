package ReplicaManager2.Server;

public class ClientModel 
{
    public static final String Client_Admin = "Admin";
    public static final String Client_Patient = "Patient";
    public static final String ClientServer_Montreal = "Montreal";
    public static final String ClientServer_Qubec = "Qubec";
    public static final String ClientServer_Sherbrooke = "Sherbrooke";
    private String ClientType;
    private String ClientID;
    private String ClientServer;

    public ClientModel(String ClientID) 
    {
        this.ClientID = ClientID;
        this.ClientType = detectClientType();
        this.ClientServer = detectClientServer();
    }

    private String detectClientServer()
    {
        if (ClientID.substring(0, 3).equalsIgnoreCase("MTL")) 
        {
            return ClientServer_Montreal;
        } 
        else if (ClientID.substring(0, 3).equalsIgnoreCase("QUE")) 
        {
            return ClientServer_Qubec;
        }
        else
        {
            return ClientServer_Sherbrooke;
        }
    }

    private String detectClientType()
    {
        if (ClientID.substring(3, 4).equalsIgnoreCase("A")) 
        {
            return Client_Admin;
        }
        else
        {
            return Client_Patient;
        }
    }

    public String getClientType()
    {
        return ClientType;
    }

    public void setClientType(String ClientType)
    {
        this.ClientType = ClientType;
    }

    public String getClientID() 
    {
        return ClientID;
    }

    public void setClientID(String ClientID) 
    {
        this.ClientID = ClientID;
    }

    public String getClientServer() 
    {
        return ClientServer;
    }

    public void setClientServer(String ClientServer)
    {
        this.ClientServer = ClientServer;
    }

    @Override
    public String toString() 
    {
        return getClientType() + "(" + getClientID() + ") on " + getClientServer() + " Server.";
    }
}
