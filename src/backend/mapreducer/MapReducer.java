package src.backend.mapreducer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;


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

    public Map<String, Object> Reduce(String mapid, Map<Lodging, Integer> filter_results)
    {

        Map<Lodging, Integer> counts = new HashMap<>(); // Creates {"room1":3, "room5":10}
        Map<String, Object> final_results = new HashMap<String, Object>(); 
        for (Map.Entry<Lodging, Integer> item : filter_results.entrySet()) {
            Lodging lodge = item.getKey();
            int count = item.getValue();
            counts.put(lodge, counts.getOrDefault(lodge, 0) + count);
        }
        final_results.put(mapid, counts);
        return final_results;
        //{mapid: {"room1":5, "room2": 3, "room7": 2}} -> <mapid, final_results>
    }

    void openServer() 
    {
            try {
                providerSocket = new ServerSocket(SERVERPORT);
                    
                connection = providerSocket.accept();
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
                String mapid = (String) in.readObject();
                Map<Lodging, Integer> filter_results = (Map<Lodging, Integer>) in.readObject();
                Reduce(mapid, filter_results);

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
