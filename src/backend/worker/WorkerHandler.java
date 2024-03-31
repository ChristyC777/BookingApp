package src.backend.worker;

import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

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
                    worker.updateDates(lodge.getRoomName(), new Date(), new Date());
                    break;
                case ADD_LODGING:
                    lodge = (Lodging) in.readObject();
                    worker.assignRoom(lodge);
                    System.out.printf("Lodging \"%s\" has been added succesfully!%n", lodge.getRoomName());
                    break;
                case REMOVE_LODGING:
                    lodge = (Lodging) in.readObject();
                    worker.removeRoom(lodge.getRoomName());
                    break;
                case VIEW_BOOKINGS:
                    worker.viewBookings();
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
        }
    }

    public void processRequest(String inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processRequest'");
    }

    
    public void storeLodging(String inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeLodging'");
    }
    
}