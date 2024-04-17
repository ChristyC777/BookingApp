package src.backend.mapreducer;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

import src.backend.utility.filterdata.FilterData;

public class MapReducerHandler implements Runnable{
    
    private ObjectOutputStream out;
    private ObjectInputStream in;
    private Socket requestSocket;
    private MapReducer mapReducer;

    public MapReducerHandler(Socket requestSocket, MapReducer mapReducer)
    {
        this.requestSocket = requestSocket;
        this.mapReducer = mapReducer;
    }

    public boolean differentMapid(FilterData filter_results)
    {
        // While there is a thread inside mapReducer and this mapid is different from the one asking return true
        if (mapReducer.getCurrentMapid()!=null && !mapReducer.getCurrentMapid().equals(filter_results.getMapID()))
        {
            return true;
        }
        return false;
    }

    @Override
    public void run()
    {
        try {
            this.out = new ObjectOutputStream(requestSocket.getOutputStream());
            this.in = new ObjectInputStream(requestSocket.getInputStream());
            
            FilterData filter_results = (FilterData) in.readObject();

            synchronized(mapReducer) // signal (mapReducer)
            {
                while(differentMapid(filter_results)) // 
                {
                    try {
                        mapReducer.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    
                }
                mapReducer.setCurrentMapid(filter_results.getMapID());
            }   
            mapReducer.Reduce(filter_results.getMapID(), filter_results.getFilters());
            requestSocket.close();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
