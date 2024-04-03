package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
import java.util.HashMap;
import java.util.Scanner;
import java.util.Calendar;

import src.backend.lodging.Lodging;

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
        this.lodges = new ArrayList<Lodging>();
    }

    void openServer() {
		try {
			providerSocket = new ServerSocket(port);

			while (true)
            {
				connection = providerSocket.accept();

                /* Stream contains: | ACTION | LODGE |
                 * (read in that order)
                 */ 


                // You take the parameter and see which worker does the master want to connect to
                // You pass the connection to that worker_thread to handle the request  
                Thread workerThread = new Thread(new WorkerHandler(this, connection));
                workerThread.start();
                System.out.println("Worker thread started successfully!");
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

    public void addDates(String roomName, String manager, Calendar startPeriod, Calendar endPeriod)
    {
        Lodging lodge = lodges.stream().filter(room -> room.getRoomName() == roomName).findFirst().orElse(null);
        if (lodge!=null)
        {
            if(lodge.getManager().equals(manager))
            {
                lodge.setFrom(startPeriod);
                lodge.setTo(endPeriod);
                System.out.println(lodge.getFrom());
                System.out.println(lodge.getTo());
            }
            else
            {
                //////////////////////////////////////////////////////////////////////////////////////
                System.out.println("You're not the manager of this room so you can't add dates"); // We need to see how we'll use the streams to return a message
                //////////////////////////////////////////////////////////////////////////////////////
            }
        }
        else 
        {   ////////////////////////////////////////////////
            System.out.println("Room does not exist"); // We need to see how we'll use the streams to return a message
            ////////////////////////////////////////////////
        }
    }

    public void addLodge(Lodging lodge)
    {
        this.lodges.add(lodge);
        System.out.printf("Lodging \"%s\" has been added succesfully!%n", lodge.getRoomName());
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

    public void manageFilters(Map<String, Object> map)
    {

    }

    public Map<String, Object> Map(String mapid, ArrayList<Lodging> filter)
    {
        Map<Lodging, Integer> count = new HashMap<Lodging, Integer>(); // {"room1":1, "room2":1, "room3":1}
        for (Lodging lodge : filter)
        {
            count.put(lodge, 1);
        }
        Map<String, Object> k2_v2 = new HashMap<String, Object>(); // {mapid: {"room1":1, "room2":1, "room3":1}}
        k2_v2.put(mapid, count);
        return k2_v2;
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.printf("Please enter the desired port for the worker to run on: ");
        int port = input.nextInt();
        new Worker(port).openServer();
    }

}

