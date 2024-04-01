package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;

import src.backend.lodging.Lodging;
import src.shared.ClientActions;

public class Worker {

    private final static int SERVERPORT = 7778;
    private ServerSocket providerSocket;
	private Socket connection = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    ArrayList<Thread> workerThreads = new ArrayList<Thread>();

    public Worker(int numberOfWorkers)
    {
        for (int i = 0; i<numberOfWorkers; i++)
        {
            Thread thread = new Thread(new WorkerHandler(i, SERVERPORT + i + 1, this));
            thread.start();
            workerThreads.add(thread);
        }
        openServer();
    }

    void openServer() {
		try {
			providerSocket = new ServerSocket(SERVERPORT);

			while (true)
            {
				connection = providerSocket.accept();
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());

                // Stream contains: | *WORKERID* | ACTION | LODGE |
                // (read in that order
                
                // WorkerID
                int workerID = (int) in.readObject();

                // You take the parameter and see which worker does the master want to connect to
                // You pass the connection to that worker_thread to handle the request  
                workerThreads.get(workerID);
			}
		} catch (IOException e) {
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
			try {
				providerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    public void viewBookings(ArrayList<Lodging> bookings)
    {

    }

    public void filterRooms(Map<String, Object> map) 
    {
        
    }

}

