package src.backend.mapreducer;

import static src.shared.ClientActions.FINAL_FILTERS;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.UnknownHostException;
import java.util.*;

import src.backend.lodging.Lodging;
import src.backend.utility.filterdata.FilterData;

public class MapReducer {
  
    private static String MASTERIP;
    private final static int MASTERPORT = 7777;
    
    private final static int SERVERPORT = 7778;
    private ServerSocket providerSocket;
	private Socket connection = null;
    private int count = 0;
    private int num_of_workers;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private String currentMapid;
    FilterData final_results;
    HashMap<Lodging, Integer> total_answers; // Creates {"room1":3, "room5":10} 


    MapReducer(int num_of_workers)
    {
        this.num_of_workers = num_of_workers;
        this.final_results = null;
        this.total_answers = new HashMap<Lodging, Integer>();
    }

    public synchronized void setCurrentMapid(String currentMapid)
    {
        this.currentMapid = currentMapid;
    }

    public synchronized String getCurrentMapid()
    {
        return this.currentMapid;
    }

    public void reset()
    {
        this.currentMapid = null;
        this.count = 0;
        this.total_answers.clear();
        this.final_results = null;
    }

    public synchronized boolean allAnswers()
    {
        if (getCounter()==getNumberOfWorkers())
        {
            return true;
        }
        return false;
    }

    public synchronized void increaseCount()
    {
        this.count++;
    }

    public int getNumberOfWorkers()
    {
        return num_of_workers;
    }

    public int getCounter()
    {
        return count;
    }

    public synchronized void updateResults(Map<Lodging, Integer> filter_results)
    {
        for (Map.Entry<Lodging, Integer> item : filter_results.entrySet()) {
            Lodging lodge = item.getKey();
            int count = item.getValue();
            if (total_answers.containsKey(lodge))
            {
                int updatedValue = total_answers.get(lodge) + count;
                total_answers.put(lodge, updatedValue);
            }
            else 
            {
                total_answers.put(lodge, count);
            }
        }
    }

    /**
     * Reducer function that takes a mapping and produces an aggregated mapping.
     * @param mapid -> the ID of the specific request.
     * @param filter_results -> the filters to apply the reduction on.
     */
    public synchronized void Reduce(String mapid, Map<Lodging, Integer> filter_results)
    {
        
        updateResults(filter_results);
        increaseCount();

        // TODO: Have these be sent to ConsoleApp
        System.out.println("MapID: " + mapid);
        System.out.println("Rooms found: \n\n" + total_answers);
    
        if (allAnswers())
        {
            sendResults();
        }
    }

    void openServer() 
    {
        try {
            providerSocket = new ServerSocket(SERVERPORT, 10);
            
            System.out.printf("Reducer now listening to port %d.%n", SERVERPORT);

            while(true)
            {
                connection = providerSocket.accept();
                Thread reduceThread = new Thread(new MapReducerHandler(connection, this));
                
                reduceThread.start();
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

    
    public synchronized void sendResults()
    {
        final_results = new FilterData(currentMapid, total_answers);
        // Create a socket to send the results back to the master
        try {
            Socket masterSocket = new Socket(MASTERIP, MASTERPORT);
            
            out = new ObjectOutputStream(masterSocket.getOutputStream());
            in = new ObjectInputStream(masterSocket.getInputStream());

            out.writeObject(FINAL_FILTERS);
            out.flush();

            out.writeObject(final_results);
            out.flush();

        } catch (UnknownHostException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        synchronized(this)
        {
            this.notifyAll();
        }
        reset();
    }

    public static void main(String[] args) {
        
        Scanner input = new Scanner(System.in);
        
        System.out.print("Enter the Master's IP address: ");
        MASTERIP = input.nextLine();
        
        System.out.print("Enter the number of workers: ");
        int workers = input.nextInt();
        input.nextLine();

        MapReducer mapreducer = new MapReducer(workers);
        mapreducer.openServer();
        System.out.printf("Reducer listening to port %d%n.", SERVERPORT);
    }
        

}
