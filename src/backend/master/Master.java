package src.backend.master;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.stream.Stream;

import src.backend.worker.Worker;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

public class Master extends Thread implements MasterInterface {

    private final static int SERVERPORT = 7777;
    private int numberOfWorkers;
    private String inputStream;
    private ArrayList<Thread> workerThreads = new ArrayList<Thread>();
    
    // public final static MyThread master = new MyThread("Master");

    
    public Master(String name, int numberOfWorkers){
        super(name);
        this.numberOfWorkers = numberOfWorkers;

        // When a master is created so are the threads of workers
        for (int i = 0; i < numberOfWorkers; i++){
            Worker worker = new Worker("worker"+ Integer.toString(i));
            worker.start();
            workerThreads.add(worker);
        }
    }

    @Override
    public void run()
    {
        inputStream = "Michael";
        
        while(true)
        {
            System.out.println("I'm waiting for a filter...");
            synchronized (inputStream)
            {
                try {
                    inputStream.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                inputStream = "";

                inputStream.notifyAll();
            }
        }
    }

    @Override
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

    @Override
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

    @Override
    public ArrayList<String> viewBookings(String RoomName)
    {
        return null;
    }

    @Override
    public void updateDates(String roomName, Date startPeriod, Date endPeriod)
    {

    }

    @Override
    public ArrayList<String> filterRooms(ArrayList<String> filters)
    {
        return null;
    }

    @Override
    public void addRating(int rating) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addRating'");
    }

    @Override
    public long H(String roomName) {
        String room = roomName.replaceAll("\\s","");
        long hash_value = 0;
        final char[] s = roomName.toCharArray();
        final int n = s.length;

        for (int i = 0; i < n; i++) {
            hash_value += (int)(s[i]);
        }
        return hash_value;
    }

    @Override
    public long selectWorker(String roomName) {
        return H(roomName) % numberOfWorkers;
    }

    

}

class Reducer {

    public Map<Integer, String> Map()
    {
        return null;
    }

    public Map<Integer, ArrayList<String>> Reduce()
    {
        return null;
    } 
    
}
