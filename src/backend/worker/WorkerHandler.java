package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.HashMap;

import src.backend.lodging.Lodging;
import src.backend.utility.response.Response;
import src.shared.ClientActions;

public class WorkerHandler implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket requestSocket;
    private Worker worker;

    public WorkerHandler(Worker worker, Socket requestSocket)
    {
        this.worker = worker;
        this.requestSocket = requestSocket;
    }

    @Override
    public void run()
    {
        try{
                this.out = new ObjectOutputStream(requestSocket.getOutputStream());
                this.in = new ObjectInputStream(requestSocket.getInputStream());
            
                // Stream contains: | *ACTION* | <REST OF THE STREAM> |
                // (read in that order)

                // Action
                ClientActions action = (ClientActions) in.readObject();

                System.out.println("Incoming request: " + action);

                Lodging lodge;
                String mapid;
                String message;

            switch(action)
            {
                case ADD_DATES:
                    // Stream contains: | *LODGE_NAME* | MANAGER_NAME | FROM_DATE | TO_DATE 
                    // (read in that order)
                    String lodgeName = (String) in.readObject();
                    String manager = (String) in.readObject();
                    String from = (String) in.readObject();
                    String to = (String) in.readObject();

                    String success = worker.addDates(lodgeName, manager, from, to);
                    Response response = new Response(manager, success);

                    out.writeObject(response);
                    out.flush();
                    
                    break;
                case ADD_LODGING:
                    // Stream contains: | *MANAGER_NAME* | LODGE_NAME | 
                    // (read in that order)
                    manager = (String) in.readObject();
                    lodge = (Lodging) in.readObject();
                    message = worker.addLodge(lodge);
                    response = new Response(manager, message);

                    out.writeObject(response);
                    out.flush();
                    
                    break;
                case VIEW_BOOKINGS:
                    // Stream contains: | *MANAGER_NAME* | 
                    String managerName = (String) in.readObject();
                    worker.viewBookings(managerName);
                    break;
                case VIEW_RESERVATIONS_PER_AREA:
                    // TODO: Implement this for part B!
                    break;
                case FILTER:
                    // Stream contains: | *USERNAME* | 
                    mapid = (String) in.readObject();
                    HashMap<String, Object> map = (HashMap<String, Object>) in.readObject();
                    worker.manageFilters(mapid, map);
                    break;
                case BOOK:
                    // Stream contains: | *LODGE_NAME* | USERNAME | FROM_DATE | TO_DATE
                    // read in that order
                    String roomName = (String) in.readObject();
                    String username = (String) in.readObject();
                    String datefrom = (String) in.readObject();
                    String dateto = (String) in.readObject();
                    message = worker.makeBooking(roomName, username, datefrom, dateto);

                    response = new Response(username, message);

                    out.writeObject(response);
                    out.flush();
                    break;
                default:
                    System.err.println("Invalid request.");
            }
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
}