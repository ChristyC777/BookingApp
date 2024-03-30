package src.backend.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Map;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import src.backend.lodging.Lodging;

public class RequestHandlerManager implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket requestSocket;

    public RequestHandlerManager(Socket request)
    {
        this.requestSocket = request;
    }

    @Override
    public void run()
    {
        try{
            this.out = new ObjectOutputStream(requestSocket.getOutputStream());
            this.in = new ObjectInputStream(requestSocket.getInputStream());
            Lodging input = (Lodging) in.readObject();

            System.out.println(input.getRoomName());

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        } finally {
            // Close the streams and socket
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
