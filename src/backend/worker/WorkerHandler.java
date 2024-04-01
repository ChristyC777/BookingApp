package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import src.backend.lodging.Lodging;
import src.shared.ClientActions;

public class WorkerHandler implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket requestSocket;
    private int threadID;
    private int port;
    private Worker worker;
    private ArrayList<Lodging> lodges;

    public WorkerHandler(int threadID, int port, Worker worker)
    {
        this.threadID = threadID;
        this.port = port;
        this.worker = worker;
        this.lodges = new ArrayList<Lodging>();
    }

    @Override
    public void run()
    {
        try{
            this.out = new ObjectOutputStream(requestSocket.getOutputStream());
            this.in = new ObjectInputStream(requestSocket.getInputStream());
            
                // Stream contains: | WORKERID | *ACTION* | *LODGE* |
                // (read in that order)

                // Action
                ClientActions action = (ClientActions) in.readObject();

                // Lodge
                Lodging lodge = (Lodging) in.readObject();

            switch(action)
            {
                case ADD_DATES:
                    lodge = (Lodging) in.readObject();
                    Calendar from = (Calendar) in.readObject();
                    Calendar to = (Calendar) in.readObject();
                    lodge.setFrom(from);
                    lodge.setTo(to);
                    break;
                case ADD_LODGING:
                    lodge = (Lodging) in.readObject();
                    lodges.add(lodge);
                    System.out.printf("Lodging \"%s\" has been added succesfully!%n", lodge.getRoomName());
                    break;
                case REMOVE_LODGING:
                    lodge = (Lodging) in.readObject();
                    lodges.remove(lodge);
                    System.out.printf("Lodging \"%s\" has been removed succesfully!%n", lodge.getRoomName());
                    break;
                case VIEW_BOOKINGS:
                    ArrayList<Lodging> bookings = new ArrayList<Lodging>();
                    String managerName = (String) in.readObject();
                    for (Lodging l : lodges)
                    {
                        if (l.getManager().equals(managerName))
                        {
                            bookings.add(l);
                        }
                    }
                    worker.viewBookings();
                    break;
                case VIEW_RESERVATIONS_PER_AREA:
                    // TODO: Implement this for part B!
                    break;
                case FILTER:
                    // TODO: Read filter map from input stream
                    Map<String, Object> map = (Map<String, Object>) in.readObject();
                    for (Map.Entry<String, Object> item : map.entrySet())
                    {
                        String key = item.getKey();
                        Object value = item.getValue();
                        switch (key) {
                            case "roomName":
                                break;
                            case "stars":
                                break;
                            case "area":
                                break;
                            case "noOfPersons":
                                break;
                        }
                    }
                    worker.filterRooms(map);
                    break;
                case BOOK:
                    break;
                case VIEW:
                    break;
                default:
                    System.err.println("Invalid request.");
            }
        } catch (IOException | ClassNotFoundException e)
        {
            e.printStackTrace();
        }
    }
    
}