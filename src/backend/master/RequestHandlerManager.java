package src.backend.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class RequestHandlerManager implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket requestSocket;

    public RequestHandlerManager(Socket request)
    {
        this.requestSocket = request;
        try {
            this.in = new ObjectInputStream(request.getInputStream());
            this.out = new ObjectOutputStream(request.getOutputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run()
    {
        try{
            Map<String, byte[]> dataMap = (Map<String, byte[]>) in.readObject();
            byte[] fileBytes = dataMap.get("add");

            String jsonData = new String(fileBytes);

            // Parse the JSON string
            JSONParser parser = new JSONParser();
            Object obj = parser.parse(jsonData);
            JSONObject jsonObject = (JSONObject) obj;

            System.out.println(jsonObject.toString());
        }catch (IOException | ClassNotFoundException | ParseException e) {
            e.printStackTrace();
        }
    }
}
