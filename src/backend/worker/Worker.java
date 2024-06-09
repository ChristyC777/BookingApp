package src.backend.worker;

import static src.shared.ClientActions.*;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Map;
import java.util.Optional;
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
import src.shared.BookingResponse;
import src.shared.ClientActions;

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
    private boolean locklodge;
    private boolean lockdates;
    private boolean lockbook;
    private boolean lockbookingsperarea;
    private boolean lockrating;
    private String messagelodge;
    private String messagedates;
    private String messagebook;
    private String messagerating;


    public Worker(int port)
    {
        this.port = port;
        this.lodges = new ArrayList<Lodging>();
        this.bookings = new ArrayList<Booking>();
        this.workerThreads = new ArrayList<Thread>();
    }

    public synchronized void addRating(String lodgeName, Integer rating)
    {
        lodges.stream().filter(lodge -> lodge.getRoomName().equals(lodgeName)).findFirst().get().addRating(rating);
        synchronized(this)
        {
            setMessage(RATE, "Succesfully added rating!");
            setLocked(RATE,false);
            this.notifyAll();

        }
    }

    public synchronized String getMessage(ClientActions action)
    {
        switch (action) {
            case ADD_LODGING:
                return messagelodge;
            case BOOK:
                return messagebook;
            case ADD_DATES:
                return messagedates;
            case RATE:
                return messagerating;
        }
        return null;
    }

    public synchronized void setMessage(ClientActions action, String message)
    {
        switch (action) {
            case ADD_LODGING:
                messagelodge = message;
                break;
            case BOOK:
                messagebook = message;
                break;
            case ADD_DATES:
                messagedates = message;
                break;
            case RATE:
                messagerating = message;
                break;
        }
    }

    public void setLocked(ClientActions action, boolean lock)
    {
        switch (action) {
            case ADD_LODGING:
                locklodge = lock;
                break;
            case BOOK:
                lockbook = lock;
                break;
            case ADD_DATES:
                lockdates = lock;
                break;
            case VIEW_RESERVATIONS_PER_AREA:
                lockbookingsperarea = lock;
                break;
            case RATE:
                lockrating = lock;
                break;
        }
    }

    public boolean getLocked(ClientActions action)
    {
        switch (action) {
            case ADD_LODGING:
                return locklodge;
            case BOOK:
                return lockbook;
            case ADD_DATES:
                return lockdates;
            case VIEW_RESERVATIONS_PER_AREA:
                return lockbookingsperarea;
            case RATE:
                return lockrating;
        }
        return (Boolean) null;
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

    public synchronized void bookingsPerArea(String manager, Calendar startPeriod, Calendar endPeriod)
    {

        ArrayList<Lodging> bookings_found = (ArrayList<Lodging>) bookings.stream()
        .filter(lodge -> lodge.getLodge().getManager().equals(manager))
        .filter(lodge -> {
            Calendar from = lodge.getDateRange().getFrom();
            Calendar to = lodge.getDateRange().getTo();
            return !(from.before(startPeriod) || to.after(endPeriod));
        })
        .map(Booking::getLodge)
        .collect(Collectors.toList());
        System.out.println(bookings_found);
        Map(manager, bookings_found, VIEW_RESERVATIONS_PER_AREA);
    }

    /**
     * Adds availability dates to a lodge.
     * @param roomName -> the lodge the user wants to book.
     * @param manager -> the name of the manager
     * @param startPeriod -> first day of availability
     * @param endPeriod -> last day of availability
     * @return Message to inform whether the update was successful or not,
     * and if not, the reason it was unsuccessful.
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

                setMessage(ADD_DATES,"#### Successfully updated dates! ####");
            }
            else
            {
                setMessage(ADD_DATES,"#### You're not the manager so you can't add dates for this lodge! ####");
            }
        }
        else 
        {           

            setMessage(ADD_DATES," #### Room does not exist. ####"); 
        }

        synchronized(this)
        {
            setLocked(ADD_DATES,false);
            this.notifyAll();
        }
    }

    /**
     * Makes a Booking.
     * @param roomName -> the lodge the user wants to book.
     * @param username -> the name of the user
     * @param startPeriod -> check-in date
     * @param endPeriod -> check-out date
     * @return Message to inform whether the booking was successful or not,
     * and if not, the reason it was unsuccessful.
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
            BookingResponse bookingResult = this.addBooking(dateRange, username, lodge);
            switch(bookingResult)
            {
                case BOOKING_NOT_WITHIN_AVAILABILITY:
                    setMessage(BOOK, String.format("%nBooking for \"%s\" failed.%nReason: The specified date range is not within the lodge's availability dates!", lodge.getRoomName()));
                    break;
                case BOOKING_CONFLICT:
                    setMessage(BOOK, String.format("%nBooking for \"%s\" failed.%nReason: Booking conflict! The lodge is already booked for the specified date range.", lodge.getRoomName()));
                    break;
                case BOOKING_SUCCESS:
                    setMessage(BOOK, String.format("%nBooking for \"%s\" successfully submitted!", lodge.getRoomName()));
                    break;
                case NULL_DATERANGE: 
                    setMessage(BOOK, String.format("%nAvailability dates have not been set!"));
                    break;
                default:
                    setMessage(BOOK, String.format("%n#### An unexpected error while processing this booking has occurred. ####"));
                    break;
            }
        }
        else 
        {
            setMessage(BOOK, "\nBooking failed!\nReason: Couldn't find the specified lodging!");
        }
        synchronized(this)
        {
            setLocked(BOOK,false);
            this.notifyAll();
        }
    }
    
    /**
     * Adds a booking.
     * @param dateRange -> the lodge the manager wants to add
     * @param userName ->
     * @param lodgeName -> the name of the lodge to be booked
     * @returns 
     */ 
    public BookingResponse addBooking(DateRange dateRange, String userName, Lodging lodge) {

        if(dateRange.getFrom() == null || dateRange.getTo()==null)
        {
            return BookingResponse.NULL_DATERANGE;
        }
        // Check whether the booking is within the lodging's availability.
        if (!lodge.getDateRange().isWithinRange(dateRange.getFrom(), dateRange.getTo()))
        {
            return BookingResponse.BOOKING_NOT_WITHIN_AVAILABILITY;
        }       

        // Check whether the booking conflicts with another booking.
        for (Booking booking : bookings) {
            if (booking.getLodge().equals(lodge) && (booking.getDateRange().isReservationAllowed(dateRange.getFrom(), dateRange.getTo())==false)) {
                return BookingResponse.BOOKING_CONFLICT;
            }
        }

        // Add a new booking.
        Booking newBooking = new Booking(dateRange, userName, lodge);
        bookings.add(newBooking);
        lodge.addBooking(newBooking);
        System.out.printf("Booking for \"%s\" has been confirmed!%n", lodge.getRoomName());
        return BookingResponse.BOOKING_SUCCESS;
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
        if (this.lodges.contains(lodge))
        {
            setMessage(ADD_LODGING, String.format("#### Lodging \"%s\" already exists! ####%n", lodge.getRoomName()));
        }
        else
        {
            this.lodges.add(lodge);
            setMessage(ADD_LODGING,String.format("#### Lodging \"%s\" has been added succesfully! ####", lodge.getRoomName()));
        }
        synchronized(this)
        {
            setLocked(ADD_LODGING,false);
            this.notifyAll();
        }

    }

    public synchronized ArrayList<Lodging> getLodges()
    {
        return this.lodges;
    }

    /**
     * Views all bookings of manager.
     * @param manager -> the name of the manager
     */ 
    public synchronized void viewBookings(String manager)
    {
       ArrayList<Lodging> filteredLodges = this.getBookings(manager);
       Map(manager, filteredLodges, VIEW_BOOKINGS);
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
                                            return room.getStars() >= (int) value;
                                        case "area":
                                            String stringValue = (String) value;
                                            return room.getArea().toLowerCase().equals(stringValue.toLowerCase());
                                        case "noOfPersons":
                                            return room.getNumberOfPersons() >= (int) value;
                                        case "roomPrice":
                                            return room.getPrice() >= (int) value;
                                        case "date":
                                            HashMap<String, String> dateMap = (HashMap<String, String>) value;

                                            String stringDateFrom = dateMap.get("dateFrom");
                                            String stringDateTo = dateMap.get("dateTo");

                                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                                            Calendar dateFrom = Calendar.getInstance();
                                            Calendar dateTo = Calendar.getInstance();

                                            try {
                                                dateFrom.setTime(dateFormat.parse(stringDateFrom));
                                                dateTo.setTime(dateFormat.parse(stringDateTo));
                                            } catch (ParseException e) {
                                                // TODO Auto-generated catch block
                                                e.printStackTrace();
                                            }

                                            /* Check if date is within range */
                                            return room.getDateRange().isWithinRange(dateFrom, dateTo);

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
        Map(mapid, filters, FILTER);
    }

    /**
     * Creates a Map with the following format: {roomName: frequency}  
     * and sends it to the Reducer.
     * @param mapid -> the name of the user 
     * @param filter -> arraylist of the filtered room(s)
     */ 
    public synchronized void Map(String mapid, ArrayList<Lodging> filter, ClientActions action)
    {
        HashMap<Lodging, Integer> count = new HashMap<Lodging, Integer>(); // {"room1":1, "room2":1, "room3":1}
        FilterData filterData = null;
        if (action == VIEW_RESERVATIONS_PER_AREA)
        {
            count = new HashMap<Lodging, Integer>();
            for (Lodging lodge : filter) {
                if (count.containsKey(lodge)) {
                    count.put(lodge, count.get(lodge) + 1); // Increment count if already exists
                } else 
                {
                count.put(lodge, 1); // Add if doesn't exist
                }
            }
            filterData = new FilterData(mapid, count);
        }
        else 
        {
            Set<Lodging> filtereredUniques = new HashSet<Lodging>(filter);
            for (Lodging lodge : filtereredUniques)
            {
                count.put(lodge, 1);
            }
            filterData = new FilterData(mapid, count);
    
            System.out.println("MapID: " + filterData.getMapID() + "\nData:\n" + filterData.getFilters().toString());
        }

        try
        {
            Socket reducer = new Socket(REDUCERIP, 7778);

            ObjectOutputStream output = new ObjectOutputStream(reducer.getOutputStream());
            ObjectInputStream input = new ObjectInputStream(reducer.getInputStream());

            output.writeObject(action);
            output.flush();

            output.writeObject(filterData);
            output.flush();
        }catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    public synchronized void randomLodgeAssortment(String username)
    {
        Map(username, lodges, FILTER);
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

