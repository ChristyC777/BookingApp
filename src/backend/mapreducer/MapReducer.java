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
        openServer();
    }

    public Map<String, Object> Map(String mapid, ArrayList<Lodging> filter)
    {
        Map<Lodging, Integer> count = new HashMap<Lodging, Integer>();
        for (Lodging lodge : filter)
        {
            count.put(lodge, 1);
        }
        Map<String, Object> k2_v2 = new HashMap<String, Object>();
        k2_v2.put(mapid, count);
        return k2_v2;
    }

    public Map<String, Object> Reduce(String mapid, Map<Lodging, Integer> filter_results)
    {

        Map<Lodging, Integer> counts = new HashMap<>();
        Map<String, Object> final_results = new HashMap<String, Object>();
        for (Map.Entry<Lodging, Integer> item : filter_results.entrySet()) {
            Lodging lodge = item.getKey();
            int count = item.getValue();
            counts.put(lodge, counts.getOrDefault(lodge, 0) + count);
        }
        final_results.put(mapid, counts);
        return final_results;
    }

    void openServer() 
    {
            try {
                providerSocket = new ServerSocket(SERVERPORT);

                while (true)
                {
                    connection = providerSocket.accept();
                    out = new ObjectOutputStream(connection.getOutputStream());
                    in = new ObjectInputStream(connection.getInputStream());

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


        

}
