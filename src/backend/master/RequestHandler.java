package src.backend.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Calendar;
import java.util.Map;

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
                    String namelodge = (String) in.readObject();
                    String manager = (String) in.readObject();
                    String startPeriod = (String) in.readObject();
                    String endPeriod = (String) in.readObject();
                    master.updateDates(namelodge, manager, startPeriod, endPeriod);
                    break;
                case ADD_LODGING:
                    lodge = (Lodging) in.readObject();
                    master.assignRoom(lodge);
                    System.out.printf("Lodging \"%s\" has been added succesfully!%n", lodge.getRoomName());
                    break;
                case VIEW_BOOKINGS:
                    manager = (String) in.readObject();
                    master.viewBookings(manager);
                    break;
                case VIEW_RESERVATIONS_PER_AREA:
                    // TODO: Implement this for part B!
                    break;
                case FILTER:
                    String mapid = (String) in.readObject(); 
                    Map<String, Object> map = (Map<String, Object>) in.readObject();
                    master.filterRooms(mapid, map);
                    break;
                case BOOK:
                    String roomName = (String) in.readObject();
                    String username = (String) in.readObject();
                    Calendar from = (Calendar) in.readObject();
                    Calendar to = (Calendar) in.readObject();
                    master.makeBooking(roomName, username, from, to);
                    break;
                case VIEW:
                    break;
                default:
                    System.err.println("Invalid request.");
            }


        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("An error has occurred while attempting to read the provided file.");
        }
    }
}
