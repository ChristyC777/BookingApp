package src.backend.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Date;

import src.shared.ClientActions;

import src.backend.lodging.Lodging;

public class RequestHandler implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket requestSocket;
    private Master master;

    public RequestHandler(Socket request, Master master)
    {
        this.requestSocket = request;
        this.master = master;
    }

    @Override
    public void run()
    {
        try{
            this.out = new ObjectOutputStream(requestSocket.getOutputStream());
            this.in = new ObjectInputStream(requestSocket.getInputStream());
            
            ClientActions action = (ClientActions) in.readObject();
            Lodging lodge;

            switch(action)
            {
                case ADD_DATES:
                    lodge = (Lodging) in.readObject();
                    master.updateDates(lodge.getRoomName(), new Date(), new Date());
                    break;
                case ADD_LODGING:
                    lodge = (Lodging) in.readObject();
                    master.assignRoom(lodge.getRoomName());
                    break;
                case REMOVE_LODGING:
                    lodge = (Lodging) in.readObject();
                    master.removeRoom(lodge.getRoomName());
                    break;
                case VIEW_BOOKINGS:
                    master.viewBookings();
                    break;
                case VIEW_RESERVATIONS_PER_AREA:
                    // TODO: Implement this for part B!
                    break;
                case FILTER:
                    // TODO: Read filter map from input stream
                    master.filterRooms(null);
                    break;
                case BOOK:
                    break;
                case VIEW:
                    break;
                default:
                    System.err.println("Invalid request.");
            }


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
