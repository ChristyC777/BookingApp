package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.Calendar;

import src.backend.lodging.Booking;
import src.backend.lodging.DateRange;
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
            System.out.printf("Worker now listening to port %d.%n", port);

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

    public void addDates(String roomName, String manager, String startPeriod, String endPeriod) throws ParseException
    {
        Lodging lodge = lodges.stream().filter(room -> room.getRoomName().equals(roomName)).findFirst().orElse(null);
        if (lodge != null)
        {
            if(lodge.getManager().equals(manager))
            {
                // Create calendar instances
                Calendar from = Calendar.getInstance();
                Calendar to = Calendar.getInstance();

                // Create date formatter, non-lenient
                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                dateFormat.setLenient(false);
                
                from.setTime(dateFormat.parse(startPeriod));
                to.setTime(dateFormat.parse(endPeriod));

                lodge.setFrom(from);
                lodge.setTo(to);

                System.out.println(lodge);
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

    public void makeBooking(String roomName, String username, Calendar from, Calendar to)
    {
        Lodging lodge = lodges.stream().filter(room -> room.getRoomName().equals(roomName)).findFirst().orElse(null);
        DateRange dateRange = new DateRange(from, to);
        Booking booking = new Booking(dateRange, username, lodge);
        if (booking.addBooking(dateRange, username, lodge))
        {
            System.out.println("Booking successfully made");
        }
        else 
        {
            System.out.println("The booking failed");
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

    public ArrayList<Lodging> filterRooms(Map<String, Object> map) 
    {
        return (ArrayList<Lodging>) lodges.stream().filter(room -> map.entrySet().stream().allMatch(entry -> 
                                    {
                                    String key = entry.getKey();
                                    Object value = entry.getValue();
                                    switch (key) {
                                        case "stars":
                                            return room.getStars() == (int) value;
                                        case "area":
                                            return room.getArea().equals(value);
                                        case "noOfPersons":
                                            return room.getNumberOfPersons() == (int) value;
                                        case "roomName":
                                            return room.getRoomName().equals(value);
                                        default: 
                                            return false;
                                    }
                                    })).collect(Collectors.toList());
    }

    public void manageFilters(String mapid, Map<String, Object> map)
    {
        ArrayList<Lodging> filters = filterRooms(map);
        Map(mapid, filters);
    }

    public void Map(String mapid, ArrayList<Lodging> filter)
    {
        Map<Lodging, Integer> count = new HashMap<Lodging, Integer>(); // {"room1":1, "room2":1, "room3":1}
        for (Lodging lodge : filter)
        {
            count.put(lodge, 1);
        }
        Map<String, Object> k2_v2 = new HashMap<String, Object>(); // {mapid: {"room1":1, "room2":1, "room3":1}}
        k2_v2.put(mapid, count);
        try
        {
            Socket reducer = new Socket("localhost", 7778);

            ObjectOutputStream output = new ObjectOutputStream(reducer.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(reducer.getInputStream());

            out.writeObject(k2_v2);
            out.flush();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        System.out.printf("Please enter the desired port for the worker to run on: ");
        int port = input.nextInt();
        new Worker(port).openServer();
    }

}

