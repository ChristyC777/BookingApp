 package src.backend.master;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;

import src.backend.worker.Worker;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Master extends Thread {

    private final static int SERVERPORT = 7777;
    private int numberOfWorkers;
    private ArrayList<Thread> threads = new ArrayList<>();
    private ServerSocket providerSocket;
	private Socket connection = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
  
    public Master(String name, int numberOfWorkers){
        super(name);
        this.numberOfWorkers = numberOfWorkers;

        // When a master is created so are the threads of workers

    }

    public int getNumOfWorkers()
    {
        return numberOfWorkers;
    }

    void openServer() {
		try {

			providerSocket = new ServerSocket(SERVERPORT, 10);
            System.out.printf("Master server now listening to port %d.%n", SERVERPORT);

			while (true) {
                System.out.println("Awaiting connection...");
				connection = providerSocket.accept();
                System.out.println("Successfully connected!");
                // out = new ObjectOutputStream(connection.getOutputStream());
                // in = new ObjectInputStream(connection.getInputStream());

                Thread requestThread = new Thread(new RequestHandlerManager(connection));
                threads.add(requestThread);
                requestThread.start();
                System.out.println(requestThread.getName() + " has started!");
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

    public void startWorkers()
    {
        Worker worker = new Worker(this.getNumOfWorkers());
    }

    public void assignRoom(String JsonFile) 
    {
        JSONParser parser = new JSONParser();
        try
        {
            Object obj = parser.parse(new FileReader(JsonFile));

            JSONObject jsonObject = (JSONObject) obj;
            String roomName = (String) jsonObject.get("roomName");
            long worker = selectWorker(roomName);
            // code for sending it to the Worker class
        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }
    }

    public void removeRoom(String JsonFile)
    {
        JSONParser parser = new JSONParser();
        try
        {
            Object obj = parser.parse(new FileReader(JsonFile));

            JSONObject jsonObject = (JSONObject) obj;
            String roomName = (String) jsonObject.get("roomName");
            long worker = selectWorker(roomName);
            // code for sending it to the Worker class
        }
        catch (IOException | ParseException e)
        {
            e.printStackTrace();
        }
    }

    public ArrayList<String> viewBookings(String RoomName)
    {
        return null;
    }

    
    public void updateDates(String roomName, Date startPeriod, Date endPeriod)
    {

    }

    public ArrayList<String> filterRooms(ArrayList<String> filters)
    {
        return null;
    }

    
    public void addRating(int rating) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addRating'");
    }
    
    public long H(String roomName)
    {
        String room = roomName.replaceAll("\\s","");
        long hash_value = 0;
        final char[] s = room.toCharArray();
        final int n = s.length;

        for (int i = 0; i < n; i++) {
            hash_value += (int)(s[i]);
        }
        return hash_value;
    }
    
    public long selectWorker(String roomName) {
        return H(roomName) % numberOfWorkers;
    }

    public static void main(String[] args) {
        Master master = new Master("Master", 5);
        master.openServer();
        Worker worker = new Worker(master.getNumOfWorkers());
    }

}
