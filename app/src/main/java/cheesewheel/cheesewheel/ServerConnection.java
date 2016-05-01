package cheesewheel.cheesewheel;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ServerConnection{

    private URL url;
    public ServerConnection(){
        try{
            url = new URL("http://172.16.18.59/cgi-bin/cgie22.cgi");
        }
        catch(Exception e){
            System.out.println("Could not connect to server!");
        }
    }

    public String send(String data){
        try{
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setDoInput(true);
            conn.setDoOutput(true);
            conn.setRequestProperty("User-Agent", "Mozilla/5.0 (Windows NT 6.1; "
                    + "WOW64) AppleWebKit/537.11 (KHTML, "
                    + "like Gecko) Chrome/23.0.1271.95 Safari/537.11");
            DataOutputStream outStream = new DataOutputStream(conn.getOutputStream());

            outStream.writeBytes(data);

            BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));

            String aLine;
            String aString = "";
            while((aLine = in.readLine()) != null){
                System.out.println(aLine);
                aString = aString + aLine;
            }
            outStream.close();
            in.close();
            return aString;
        }
        catch(Exception e){
            System.out.println("Server connection error: " + e);
        }
        return null;
    }
}