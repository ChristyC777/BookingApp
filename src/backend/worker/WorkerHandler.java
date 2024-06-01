package src.backend.worker;

import static src.shared.ClientActions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.ParseException;
import java.util.Calendar;
import java.util.HashMap;

import src.backend.lodging.Lodging;
import src.backend.utility.daterange.DateRange;
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

                    synchronized(worker)
                    {
                        while(worker.getLocked(ADD_DATES))
                        {
                            try {
                                worker.wait();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }

                    if (worker.getLocked(ADD_DATES)==false)
                    {
                        worker.setLocked(ADD_DATES,true);
                    }
                    worker.addDates(lodgeName, manager, from, to);
                    String success = worker.getMessage(ADD_DATES);
                    Response response = new Response(manager, success);

                    out.writeObject(response);
                    out.flush();

                    break;
                case ADD_LODGING:
                    // Stream contains: | *MANAGER_NAME* | LODGE_NAME | 
                    // (read in that order)
                    manager = (String) in.readObject();
                    lodge = (Lodging) in.readObject();

                    synchronized(worker)
                    {
                        while(worker.getLocked(ADD_LODGING))
                        {
                            try {
                                worker.wait();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    if (worker.getLocked(ADD_LODGING)==false)
                    {
                        worker.setLocked(ADD_LODGING,true);
                    }

                    worker.addLodge(lodge);
                    message = worker.getMessage(ADD_LODGING);
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
                    manager = (String) in.readObject();
                    DateRange dates = (DateRange) in.readObject();
                    Calendar from_date = dates.getFrom();
                    Calendar to_date = dates.getTo();
                    worker.bookingsPerArea(manager, from_date, to_date);
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

                    synchronized(worker)
                    {
                        while(worker.getLocked(BOOK))
                        {
                            try {
                                worker.wait();
                            } catch (InterruptedException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                        }
                    }
                    if (worker.getLocked(BOOK)==false)
                    {
                        worker.setLocked(BOOK, true);
                    }
                    worker.makeBooking(roomName, username, datefrom, dateto);
                    message = worker.getMessage(BOOK);
                    response = new Response(username, message);

                    out.writeObject(response);
                    out.flush();
                    break;
                case HOMEPAGE_LODGES:
                    username = (String) in.readObject();
                    worker.randomLodgeAssortment(username);
                    break;
                default:
                    System.err.println("Invalid request.");
                    break;
            }
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }
    
}