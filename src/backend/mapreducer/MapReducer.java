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
    private String mapid;
    HashMap<String, Object> final_results;

    MapReducer(int num_of_workers)
    {
        this.num_of_workers = num_of_workers;
    }

    public void setMapid(String mapid)
    {
        this.mapid = mapid;
    }

    public boolean allAnswers(int count, int num_of_workers, String mapid)
    {
        if (getCounter()==getNumberOfWorkers())
        {
            return true;
        }
        return false;
    }

    public synchronized void  increaseCount()
    {
        this.count += 1;
    }

    public synchronized void waitThreads(String mapid)
    {

    }

    public int getNumberOfWorkers()
    {
        return num_of_workers;
    }

    public int getCounter()
    {
        return count;
    }

    /**
     * Reducer function that takes a mapping and produces an aggregated mapping.
     * @param mapid -> the ID of the specific request.
     * @param filter_results -> the filters to apply the reduction on.
     */
    public void Reduce(String mapid, Map<Lodging, Integer> filter_results)
    {

        synchronized(this)
        {
            while(!allAnswers(count, num_of_workers, mapid))
            {
                try {
                    wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            HashMap<Lodging, Integer> counts = new HashMap<Lodging, Integer>(); // Creates {"room1":3, "room5":10} 
            for (Map.Entry<Lodging, Integer> item : filter_results.entrySet()) {
                Lodging lodge = item.getKey();
                int count = item.getValue();
                counts.put(lodge, counts.getOrDefault(lodge, 0) + count);
            }
            final_results.put(mapid, counts);
            // TODO: Have these be sent to ConsoleApp
            System.out.println("MapID: " + mapid);
            System.out.println("Rooms found: \n\n" + counts);
        }
        notifyAll();

        

        //{mapid: {"room1":5, "room2": 3, "room7": 2}} -> <mapid, final_results>

        // TODO: When all the workers have send results for this mapid release the lock 

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
        // Unlock the function
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
