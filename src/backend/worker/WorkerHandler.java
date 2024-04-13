package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.HashMap;

import src.backend.lodging.Lodging;
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
            
                // Stream contains: | MAPID | *ACTION* | *LODGE* |
                // (read in that order)

                // Action
                ClientActions action = (ClientActions) in.readObject();

                System.out.println("Action: " + action);
                // Lodge
                Lodging lodge;
                String mapid;

            switch(action)
            {
                case ADD_DATES:
                    String lodgeName = (String) in.readObject();
                    String manager = (String) in.readObject();
                    String from = (String) in.readObject();
                    String to = (String) in.readObject();

                    worker.addDates(lodgeName, manager, from, to);
                    
                    break;
                case ADD_LODGING:
                    lodge = (Lodging) in.readObject();
                    // int prev = worker.getLodges().size();
                    // int now = -1;
                    // synchronized(this)
                    // {
                    //     try {
                    //         wait();
                    //     } catch (InterruptedException e) {
                    //         e.printStackTrace();
                    //     }
                        worker.addLodge(lodge);
                    //     now = worker.getLodges().size();
                    // }
                    // if (now == prev + 1)
                    // {
                    //     out.writeObject("Successfully added room!");
                    //     out.flush();
                    // }
                    // else 
                    // {
                    //     out.writeObject("Failed to add room!");
                    //     out.flush();
                    // }
                    break;
                case VIEW_BOOKINGS:
                    mapid = (String) in.readObject();
                    String managerName = (String) in.readObject();
                    worker.viewBookings(mapid, managerName);
                    break;
                case VIEW_RESERVATIONS_PER_AREA:
                    // TODO: Implement this for part B!
                    break;
                case FILTER:
                    mapid = (String) in.readObject();
                    HashMap<String, Object> map = (HashMap<String, Object>) in.readObject();
                    
                    worker.manageFilters(mapid, map);
                    break;
                case BOOK:
                    String roomName = (String) in.readObject();
                    String username = (String) in.readObject();
                    String datefrom = (String) in.readObject();
                    String dateto = (String) in.readObject();
                    worker.makeBooking(roomName, username, datefrom, dateto);
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