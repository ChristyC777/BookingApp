package gr.aueb.ebookingapp.activity.Thread;

import static src.shared.ClientActions.*;

import android.os.Handler;
import android.os.Message;

import androidx.appcompat.app.AppCompatActivity;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

import gr.aueb.ebookingapp.activity.homepage.Homepage;
import gr.aueb.ebookingapp.dao.MemoryGuestDAO;
import src.backend.lodging.Lodging;
import src.backend.utility.response.Response;
import src.shared.ClientActions;

public class RequestHandler implements Runnable
{

    private MemoryGuestDAO guestDAO;
    private Handler handler;
    private int rating;
    private ArrayList<Lodging> lodges;
    private Response response;
    private String username;
    private String lodgeName;
    private String message;
    private static String HOST_ADDRESS = "192.168.1.12";
    private ClientActions action;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;
    private Calendar checkIn_date;
    private Calendar checkOut_date;
    private AppCompatActivity activity;
    private HashMap<String, Object> filters;
    private final static int SERVERPORT = 7777;


    public RequestHandler(AppCompatActivity activity, ClientActions action, String username, Handler handler)
    {
        this.activity = activity;
        this.action = action;
        this.username = username;
        this.handler = handler;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }

    public MemoryGuestDAO getGuestDAO() {
        return guestDAO;
    }

    public void setGuestDAO(MemoryGuestDAO guestDAO) {
        this.guestDAO = guestDAO;
    }

    public void setDates(Calendar checkIn_date, Calendar checkOut_date)
    {
        this.checkIn_date = checkIn_date;
        this.checkOut_date = checkOut_date;
    }

    public Calendar getCheckIn()
    {
        return checkIn_date;
    }

    public Calendar getCheckOut()
    {
        return checkOut_date;
    }

    public void setLodgeName(String lodgeName)
    {
        this.lodgeName = lodgeName;
    }

    public String getLodgeName()
    {
        return lodgeName;
    }

    public String getUsername()
    {
        return username;
    }
    
    public void setFilters(HashMap<String, Object> filters)
    {
        this.filters = filters;
    }

    public HashMap<String, Object> getFilters()
    {
        return filters;
    }

    public void setLodges(ArrayList<Lodging> lodges)
    {
        this.lodges= lodges;
    }

    public ArrayList<Lodging> getLodges() {
        return lodges;
    }

    public void setErrorMessage(String message)
    {
        this.message = message;
    }

    @Override
    public void run()
    {
        Socket connection;
        switch(action)
        {
            case BOOK:
                try {
                    connection = new Socket(HOST_ADDRESS, SERVERPORT);

                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());

                    out.writeObject(BOOK);
                    out.flush();

                    out.writeObject(getLodgeName());
                    out.flush();

                    out.writeObject(getUsername());
                    out.flush();

                    out.writeObject(getCheckIn());
                    out.flush();

                    out.writeObject(getCheckOut());
                    out.flush();

                    try {
                        String message = (String) in.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    connection.close();

                } catch (IOException e) {
                    throw new RuntimeException(e);
                 }
                break;
            case FILTER:
                try {
                    connection = new Socket(HOST_ADDRESS, SERVERPORT);

                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());

                    out.writeObject(FILTER);
                    out.flush();

                    out.writeObject(getUsername());
                    out.flush();

                    out.writeObject(getFilters());
                    out.flush();

                    response = null;

                    // await for a response
                    try {
                        response = (Response) in.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Lodging> lodges = new ArrayList<Lodging>();

                    // Retrieve response
                    HashMap<Lodging, Integer> filtered_rooms = (HashMap<Lodging, Integer>) response.getResponse();

                    if (filtered_rooms.size() > 0)
                    {
                        for (HashMap.Entry<Lodging, Integer> item : filtered_rooms.entrySet())
                        {
                            lodges.add(item.getKey());
                        }
                        setLodges(lodges);
                    }
                    else
                    {
                        setErrorMessage("No rooms found...");
                    }
                    connection.close();
                } catch (IOException e) {
                    throw new RuntimeException(e);
                }
                break;
            case RATE:
                try {
                    connection = new Socket(HOST_ADDRESS, SERVERPORT);

                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());

                    out.writeObject(RATE);
                    out.flush();

                    out.writeObject(getUsername());
                    out.flush();

                    out.writeObject(getLodgeName());
                    out.flush();

                    out.writeObject(getRating());
                    out.flush();

                    String message = (String) in.readObject();
                    System.out.println(message);

                    getGuestDAO().findGuest(getUsername()).addRatings(getLodgeName(), getRating());

                } catch (IOException e) {
                    throw new RuntimeException(e);
                } catch (ClassNotFoundException e) {
                    throw new RuntimeException(e);
                }
                break;
            case HOMEPAGE_LODGES:
                try {
                    connection = new Socket(HOST_ADDRESS, SERVERPORT);

                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());

                    out.writeObject(HOMEPAGE_LODGES);
                    out.flush();

                    out.writeObject(getUsername());
                    out.flush();

                    response = null;

                    // await for a response
                    try {
                        response = (Response) in.readObject();
                    } catch (ClassNotFoundException e) {
                        e.printStackTrace();
                    }

                    ArrayList<Lodging> lodges = new ArrayList<Lodging>();

                    // Retrieve response
                    HashMap<Lodging, Integer> filtered_rooms = (HashMap<Lodging, Integer>) response.getResponse();

                    if (filtered_rooms.size() > 0)
                    {
                        for (HashMap.Entry<Lodging, Integer> item : filtered_rooms.entrySet())
                        {
                            lodges.add(item.getKey());
                        }
                        Message msg = new Message();
                        msg = handler.obtainMessage();
                        msg.obj = lodges;
                        handler.handleMessage(msg);
                    }
                    else
                    {
                        setErrorMessage("No rooms found...");
                    }
                    connection.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                break;
        }
    }
}
