package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Map;

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

            switch(action)
            {
                case ADD_DATES:
                    String namelodge = (String) in.readObject();
                    String manager = (String) in.readObject();
                    String from = (String) in.readObject();
                    String to = (String) in.readObject();
                    worker.addDates(namelodge, manager, from, to);
                    break;
                case ADD_LODGING:
                    lodge = (Lodging) in.readObject();
                    worker.addLodge(lodge);
                    break;
                case VIEW_BOOKINGS:
                    ArrayList<Lodging> bookings = new ArrayList<Lodging>();
                    String managerName = (String) in.readObject();
                    for (Lodging l : worker.getLodges())
                    {
                        if (l.getManager().equals(managerName))
                        {
                            bookings.add(l);
                        }
                    }
                    worker.viewBookings(bookings);
                    break;
                case VIEW_RESERVATIONS_PER_AREA:
                    // TODO: Implement this for part B!
                    break;
                case FILTER:
                    String mapid = (String) in.readObject();
                    Map<String, Object> map = (Map<String, Object>) in.readObject();
                    
                    worker.manageFilters(mapid, map);
                    break;
                case BOOK:
                    String roomName = (String) in.readObject();
                    String username = (String) in.readObject();
                    Calendar datefrom = (Calendar) in.readObject();
                    Calendar dateto = (Calendar) in.readObject();
                    worker.makeBooking(roomName, username, datefrom, dateto);
                    break;
                case VIEW:
                    break;
                default:
                    System.err.println("Invalid request.");
            }
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
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