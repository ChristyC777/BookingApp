package src.backend.mapreducer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;
import java.util.Map.Entry;

import src.backend.lodging.Lodging;

public class MapReducer {
  
    private final static int SERVERPORT = 7778;
    private ServerSocket providerSocket;
	private Socket connection = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    MapReducer()
    {
    }

    public void Reduce(String mapid, Map<Lodging, Integer> filter_results)
    {

        // TODO: lock the function and allow of threads with the current mapid to get in
        Map<Lodging, Integer> counts = new HashMap<>(); // Creates {"room1":3, "room5":10}
        Map<String, Object> final_results = new HashMap<String, Object>(); 
        for (Map.Entry<Lodging, Integer> item : filter_results.entrySet()) {
            Lodging lodge = item.getKey();
            int count = item.getValue();
            counts.put(lodge, counts.getOrDefault(lodge, 0) + count);
        }
        final_results.put(mapid, counts);

        //{mapid: {"room1":5, "room2": 3, "room7": 2}} -> <mapid, final_results>
        // TODO:
        // When all the workers have send results for this mapid release the lock 
        // Create a socket to send the reults back to the master
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
                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());
                    FilterData filter_results = (FilterData) in.readObject();
                    Pair<String, HashMap<Lodging, Integer>> dataPair = new Pair<String, HashMap<Lodging, Integer>>();
                    Reduce(dataPair.getLeft(), dataPair.getRight());
                    // Reduce(dataPair.getLeft(), dataPair.getRight())
                }

            } catch (IOException | ClassNotFoundException e) {
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
        new MapReducer().openServer();
    }
        

}
