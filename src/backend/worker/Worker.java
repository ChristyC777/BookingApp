package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Scanner;

import src.backend.lodging.Lodging;
import src.shared.ClientActions;

public class Worker {

    private int port;
    private ServerSocket providerSocket;
	private Socket connection = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    ArrayList<Thread> workerThreads = new ArrayList<Thread>();
    private ArrayList<Lodging> lodges;
    private ArrayList<Lodging> bookings;

    public Worker(int port)
    {
        this.port = port;
    }

    void openServer() {
		try {
			providerSocket = new ServerSocket(port);

			while (true)
            {
				connection = providerSocket.accept();
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());

                // Stream contains: | ACTION | LODGE |
                // (read in that order
                
                // WorkerID
                // int workerID = (int) in.readObject();

                // You take the parameter and see which worker does the master want to connect to
                // You pass the connection to that worker_thread to handle the request  
                Thread workerThread = new Thread(new WorkerHandler(this, connection));
                workerThread.start();
			}
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				providerSocket.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}

    public void addLodge(Lodging lodge)
    {
        this.lodges.add(lodge);
        System.out.printf("Lodging \"%s\" has been added succesfully!%n", lodge.getRoomName());
        System.out.println(lodges.size());
    }

    public void removeLodge(Lodging lodge)
    {
        this.lodges.remove(lodge);
        System.out.printf("Lodging \"%s\" has been removed succesfully!%n", lodge.getRoomName());
        System.out.println(lodges.size());
    }

    public ArrayList<Lodging> getLodges()
    {
        return this.lodges;
    }

    public void viewBookings(ArrayList<Lodging> bookings)
    {
        this.bookings.addAll(bookings);
        
        for (Lodging booking : bookings)
        {
            System.out.println(booking.getRoomName());
        }
    }

    public void filterRooms(Map<String, Object> map) 
    {
        
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.printf("Please enter the desired port for the worker to run on: ");
        int port = input.nextInt();
        new Worker(port).openServer();
    }

}

