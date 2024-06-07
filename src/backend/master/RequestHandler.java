package src.backend.master;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Collections;

import src.backend.lodging.Lodging;
import src.backend.utility.daterange.DateRange;
import src.backend.utility.filterdata.FilterData;
import src.backend.utility.response.Response;
import src.shared.ClientActions;

public class RequestHandler implements Runnable {

    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket requestSocket;
    private Master master;
    private String username;
    

    public RequestHandler(Socket request, Master master)
    {
        this.requestSocket = request;
        this.master = master;
    }

    public ObjectOutputStream getOut()
    {
        return this.out;
    }

    public ObjectInputStream getIn()
    {
        return this.in;
    }

    public void setUsername(String username)
    {
        this.username = username;
    }

    public String getUsername()
    {
        return username;
    }

    @Override
    public void run()
    {
        try{
            this.out = new ObjectOutputStream(requestSocket.getOutputStream());
            this.in = new ObjectInputStream(requestSocket.getInputStream());
            
            ClientActions action = (ClientActions) in.readObject();
            Response response = null;
            String mapid;
            String username;

            switch(action)
            {
                case ADD_DATES:
                    String namelodge = (String) in.readObject();
                    String manager = (String) in.readObject();
                    String startPeriod = (String) in.readObject();
                    String endPeriod = (String) in.readObject();
                    setUsername(manager);
                    master.addHandler(this);
                    master.updateDates(namelodge, manager, startPeriod, endPeriod);
                    break;
                case ADD_LODGING:
                    manager = (String) in.readObject(); 
                    Lodging lodge = (Lodging) in.readObject();
                    setUsername(manager);
                    master.addHandler(this);
                    master.assignRoom(lodge, manager);
                    break;
                case VIEW_BOOKINGS:
                    manager = (String) in.readObject();
                    setUsername(manager);
                    master.addHandler(this);
                    master.viewBookings(manager);
                    break;
                case VIEW_RESERVATIONS_PER_AREA:
                    manager = (String) in.readObject();
                    DateRange dates = (DateRange) in.readObject();
                    setUsername(manager);
                    master.addHandler(this);
                    master.bookingPerArea(manager, dates);
                    break;
                case FILTER:
                    mapid = (String) in.readObject(); 
                    setUsername(mapid);
                    master.addHandler(this);
                    HashMap<String, Object> map = (HashMap<String, Object>) in.readObject();
                    master.filterRooms(mapid, map);
                    break;
                case BOOK:
                    String roomName = (String) in.readObject();
                    username = (String) in.readObject();
                    String from = (String) in.readObject();
                    String to = (String) in.readObject();
                    setUsername(username);
                    master.addHandler(this);
                    master.makeBooking(roomName, username, from, to);
                    break;
                case FINAL_FILTERS:
                    FilterData final_filters = (FilterData) in.readObject();
                    HashMap<Lodging, Integer> dummymap = final_filters.getFilters();
                    if (dummymap == null || dummymap.isEmpty())
                    {
                        Response nullResponse = null;
                        out.writeObject(nullResponse);
                        out.flush();
                        break;
                    }
                    List<Map.Entry<Lodging, Integer>> entryList = new ArrayList<>(dummymap.entrySet());
                    List<Map.Entry<Lodging, Integer>> selectedEntries;

                    if (entryList.size() <= 4) {
                        selectedEntries = entryList;
                    } else {
                        Collections.shuffle(entryList);
                        selectedEntries = entryList.subList(0, 4);
                    }

                    HashMap<Lodging, Integer> selectedMap = new HashMap<>();
                    for (Map.Entry<Lodging, Integer> entry : selectedEntries) {
                        selectedMap.put(entry.getKey(), entry.getValue());
                    }
                    FilterData filteredResults = new FilterData(final_filters.getMapID(), selectedMap);
                    master.notifyOfResults(filteredResults);
                    break;
                case RATE:
                    username = (String) in.readObject();
                    String lodgeName = (String) in.readObject();
                    Integer rating = (Integer) in.readObject();
                    setUsername(username);
                    master.addHandler(this);
                    master.addRating(username, lodgeName, rating);
                    break;
                case HOMEPAGE_LODGES:
                    username = (String) in.readObject();
                    setUsername(username);
                    master.addHandler(this);
                    master.randomLodgeAssortment(username);
                    break;
                default:
                    System.err.println("Invalid request.");
                    break;
            }
        

        }catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.err.println("An error has occurred while attempting to read the provided file.");
        }
    }
}
