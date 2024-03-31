package src.backend.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

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
            System.err.println("An error has occurred while attempting to read the provided file.");
        } finally {
            // Close the streams
            try {
                in.close();
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
