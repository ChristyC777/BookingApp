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
import java.util.HashSet;
import java.util.List;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

import java.util.Calendar;

import src.backend.booking.Booking;
import src.backend.lodging.Lodging;
import src.backend.utility.daterange.DateRange;
import src.backend.utility.filterdata.FilterData;

public class Worker {

    private static String REDUCERIP;
    private int port;
    private ServerSocket providerSocket;
	private Socket connection = null;
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private ArrayList<Lodging> lodges;
    private ArrayList<Booking> bookings;
    private ArrayList<Thread> workerThreads;

    public Worker(int port)
    {
        this.port = port;
        this.lodges = new ArrayList<Lodging>();
        this.bookings = new ArrayList<Booking>();
        this.workerThreads = new ArrayList<Thread>();
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
                workerThreads.add(workerThread);
			}
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

    /**
     * Adds availability dates to a lodge.
     * @param roomName -> the lodge the user wants to book.
     * @param manager -> the name of the manager
     * @param startPeriod -> first day of availability
     * @param endPeriod -> last day of availability
     */ 
    public synchronized void addDates(String roomName, String manager, String startPeriod, String endPeriod) throws ParseException
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
                
                DateRange dateRange = new DateRange(from, to);
                lodge.setDateRange(dateRange);

                System.out.println(lodge);
            }
            else
            {
                // TODO:
                //////////////////////////////////////////////////////////////////////////////////////
                System.out.println("You're not the manager of this room so you can't add dates"); // We need to see how we'll use the streams to return a message
                //////////////////////////////////////////////////////////////////////////////////////
            }
        }
        else 
        {   
            // TODO:
            ////////////////////////////////////////////////
            System.out.println("Room does not exist"); // We need to see how we'll use the streams to return a message
            ////////////////////////////////////////////////
        }
    }

    /**
     * Makes a Booking.
     * @param roomName -> the lodge the user wants to book.
     * @param username -> the name of the user
     * @param startPeriod -> check-in date
     * @param endPeriod -> check-out date
     */ 
    public synchronized void makeBooking(String roomName, String username, String startPeriod, String endPeriod) throws ParseException
    {
        Lodging lodge = lodges.stream().filter(room -> room.getRoomName().equals(roomName)).findFirst().orElse(null);
        if (lodge != null)
        {
            // Create calendar instances
            Calendar from = Calendar.getInstance();
            Calendar to = Calendar.getInstance();
            
            // Create date formatter, non-lenient
            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
            dateFormat.setLenient(false);
            
            from.setTime(dateFormat.parse(startPeriod));
            to.setTime(dateFormat.parse(endPeriod));
            DateRange dateRange = new DateRange(from, to);
            if (this.addBooking(dateRange, username, lodge))
            {
                System.out.println("Booking successfully submitted!");
            }
            else 
            {
                System.out.println("Booking failed.");
            }
        }
        else 
        {
            System.out.println("Couldn't find the specified lodging!");
        }
    }
    
    /**
     * Adds a booking.
     * @param dateRange -> the lodge the manager wants to add
     * @param userName ->
     * @param lodgeName -> the name of the lodge to be booked
     */ 
    public synchronized boolean addBooking(DateRange dateRange, String userName, Lodging lodge) {

        // Check whether the booking is within the lodging's availability.
        if (!lodge.getDateRange().isWithinRange(dateRange.getFrom(), dateRange.getTo()))
        {
            System.out.println("The specified date range is not within the lodge's availability dates!");
            return false;
        }       

        // Check whether the booking conflicts with another booking.
        for (Booking booking : bookings) {
            if (booking.getLodge().equals(lodge) && booking.getDateRange().isWithinRange(dateRange.getFrom(), dateRange.getTo())) {
                System.out.println("Booking conflict! The lodge is already booked for the specified date range.");
                return false;
            }
        }

        // Add a new booking.
        Booking newBooking = new Booking(dateRange, userName, lodge);
        bookings.add(newBooking);
        System.out.println("Booking has been confirmed!");
        return true;
    }

    /**
     * Returns a list of bookings belonging to the currently connected manager.
     * @return the list of bookings.
     */ 
    public synchronized ArrayList<Lodging> getBookings(String manager)
    {
        List<Lodging> managerBookings = bookings.stream()
                .filter(booking -> booking.getLodge().getManager().equals(manager))
                .map(Booking::getLodge)
                .collect(Collectors.toList());

        return (ArrayList<Lodging>) managerBookings;
    }

    /**
     * Adds a lodge.
     * @param lodge -> the lodge the manager wants to add
     */ 
    public synchronized void addLodge(Lodging lodge)
    {
        this.lodges.add(lodge);
        System.out.printf("Lodging \"%s\" has been added succesfully!%n", lodge.getRoomName());
        System.out.println(lodges.size());
    }

    public synchronized ArrayList<Lodging> getLodges()
    {
        return this.lodges;
    }

    /**
     * Views all bookings of manager.
     * @param manager -> the name of the manager
     */ 
    public synchronized void viewBookings(String mapid, String manager)
    {
       ArrayList<Lodging> filteredLodges = this.getBookings(manager);
       Map(mapid, filteredLodges);
    }

    /**
     * Filters rooms according to the characteristics given.
     * @param map -> includes the characteristics of the desired room(s)
     */ 
    public synchronized ArrayList<Lodging> filterRooms(Map<String, Object> map) 
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

    /**
     * Takes the results of the rooms that have the desired characteristics and starts the map-reducing procedure
     * @param mapid -> the name of the user 
     * @param map -> includes the characteristics of the desired room(s)
     */ 
    public synchronized void manageFilters(String mapid, Map<String, Object> map)
    {
        ArrayList<Lodging> filters = filterRooms(map);
        Map(mapid, filters);
    }

    /**
     * Creates a Map with the following format: {roomName: frequency}  
     * and sends it to the Reducer.
     * @param mapid -> the name of the user 
     * @param filter -> arraylist of the filtered room(s)
     */ 
    public synchronized void Map(String mapid, ArrayList<Lodging> filter)
    {
        HashMap<Lodging, Integer> count = new HashMap<Lodging, Integer>(); // {"room1":1, "room2":1, "room3":1}
        Set<Lodging> filtereredUniques = new HashSet<Lodging>(filter);
        for (Lodging lodge : filtereredUniques)
        {
            count.put(lodge, 1);
        }
        FilterData filterData = new FilterData(mapid, count);
        try
        {
            Socket reducer = new Socket(REDUCERIP, 7778);

            ObjectOutputStream output = new ObjectOutputStream(reducer.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(reducer.getInputStream());

            output.writeObject(filterData);
            output.flush();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        Scanner input = new Scanner(System.in);
        
        System.out.printf("Please enter the desired port for the worker to run on: ");
        int port = input.nextInt();
        input.nextLine(); // consume new line

        System.out.printf("Please enter the IP address that the Reducer is running on: ");
        REDUCERIP = input.nextLine();

        new Worker(port).openServer();
    }
}

