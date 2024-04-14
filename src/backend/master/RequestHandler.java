package src.backend.master;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;

import src.shared.ClientActions;

import src.backend.lodging.Lodging;
import src.backend.utility.response.Response;

public class RequestHandler implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket requestSocket;
    private Master master;
    private String username;

    public RequestHandler(Socket request, Master master)
    {
        this.requestSocket = request;
        this.master = master;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    @Override
    public void run()
    {
        try{
            this.out = new ObjectOutputStream(requestSocket.getOutputStream());
            this.in = new ObjectInputStream(requestSocket.getInputStream());
            
            ClientActions action = (ClientActions) in.readObject();
            Response response = null;
            String mapid;

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
                    Lodging lodge = (Lodging) in.readObject();
                    master.assignRoom(lodge);
                    System.out.printf("Lodging \"%s\" has been added succesfully!%n", lodge.getRoomName());
                    break;
                case VIEW_BOOKINGS:
                    mapid = Thread.currentThread().getName();
                    manager = (String) in.readObject();
                    setUsername(manager);
                    master.addHandler(this);
                    master.viewBookings(mapid, manager);
                
                    // waits until a response is available
                    try {
                        synchronized(master.getResponseInstance())
                        {
                            while(!master.getResponseInstance().hasResponse())
                            {
                                wait();
                            }
                        }
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }

                    // ensures synchronization for the current thread
                    synchronized(this)
                    {
                        response = master.getResponseInstance();
                        out.writeObject(response);
                    }
                    break;
                case VIEW_RESERVATIONS_PER_AREA:
                    // TODO: Implement this for part B!
                    break;
                case FILTER:
                    mapid = (String) in.readObject(); 
                    setUsername(mapid);
                    master.addHandler(this);
                    HashMap<String, Object> map = (HashMap<String, Object>) in.readObject();
                    master.filterRooms(mapid, map);

                    synchronized(this)
                    {
                        while(!master.getResponseInstance().hasResponse())
                        {
                            try {
                                wait();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                    
                    response = master.getResponseInstance();
                    out.writeObject(response);
                    break;
                case BOOK:
                    String roomName = (String) in.readObject();
                    String username = (String) in.readObject();
                    String from = (String) in.readObject();
                    String to = (String) in.readObject();
                    master.makeBooking(roomName, username, from, to);
                    break;
                case FINAL_FILTERS:
                    // TODO: Properly implement this
                    HashMap<String, Object> final_filters = (HashMap<String, Object>) in.readObject();
                    master.notifyOfResults(final_filters);
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
