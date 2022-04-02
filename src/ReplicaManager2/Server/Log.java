package ReplicaManager2.Server;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class Log
{
    public static final int Log_Server = 1;
 //   public static final int Log_Client= 0;

//    public static void ClientLog(String ClientID, String Action, String RequestParams, String Response) throws IOException 
//    {
//        FileWriter fileWriter = new FileWriter(getFileName(ClientID, Log_Client), true);
//        PrintWriter printWriter = new PrintWriter(fileWriter);
//        printWriter.println("DATE: " + getFormattedDate() + " Client Action: " + Action + " | RequestParameters: " + RequestParams + " | Server Response: " + Response);
//
//        printWriter.close();
//    }
//
//    public static void ClientLog(String ClientID, String Msg) throws IOException 
//    {
//        FileWriter fileWriter = new FileWriter(getFileName(ClientID, Log_Client), true);
//        PrintWriter printWriter = new PrintWriter(fileWriter);
//        printWriter.println("DATE: " + getFormattedDate() + " " + Msg);
//
//        printWriter.close();
//    }

    public static void ServerLog(String ServerID, String ClientID, String RequestType, String RequestParams, String ServerResponse) throws IOException 
    {

        if (ClientID.equals("null")) 
        {
            ClientID = "Admin";
        }
        FileWriter fileWriter = new FileWriter(getFileName(ServerID, Log_Server), true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("DATE: " + getFormattedDate() + " ClientID: " + ClientID + " | RequestType: " + RequestType + " | RequestParameters: " + RequestParams + " | ServerResponse: " + ServerResponse);

        printWriter.close();
    }

    public static void ServerLog(String ServerID, String Msg) throws IOException 
    {

        FileWriter fileWriter = new FileWriter(getFileName(ServerID, Log_Server), true);
        PrintWriter printWriter = new PrintWriter(fileWriter);
        printWriter.println("DATE: " + getFormattedDate() + " " + Msg);

        printWriter.close();
    }

//    public static void DeleteALogFile(String ID) throws IOException
//    {
//
//        String fileName = getFileName(ID, Log_Client);
//        File file = new File(fileName);
//        file.delete();
//    }

    private static String getFileName(String ID, int LogType)
    {
        final String dir = System.getProperty("user.dir");
        String fileName = dir;
        if (LogType == Log_Server) 
        {
            if (ID.equalsIgnoreCase("MTL")) 
            {
                fileName = dir + "\\src\\Logs\\Server\\Montreal.txt";
            }
            else if (ID.equalsIgnoreCase("QUE")) 
            {
                fileName = dir + "\\src\\Logs\\Server\\Quebec.txt";
            }
            else if (ID.equalsIgnoreCase("SHE")) 
            {
                fileName = dir + "\\src\\Logs\\Server\\Sherbrooke.txt";
            }
            
        } 
        else
        {
            fileName = dir + "\\src\\Logs\\Client\\" + ID + ".txt";
        }
        return fileName;
    }

    private static String getFormattedDate() 
    {
        Date date = new Date();

        String strDateFormat = "yyyy-MM-dd hh:mm:ss a";

        DateFormat dateFormat = new SimpleDateFormat(strDateFormat);

        return dateFormat.format(date);
    }

}
