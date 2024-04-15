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
        if (!filter_results.getMapID().equals(mapReducer.getCurrentMapid()))
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
            
            if(mapReducer.getCurrentMapid() == null)
            {
                mapReducer.setCurrentMapid(filter_results.getMapID());
            }

            synchronized(this)
            {
                while(differentMapid(filter_results))
                {
                    try {
                        wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                mapReducer.setCurrentMapid(filter_results.getMapID());
            }

            if(mapReducer.getCurrentMapid() == null)
            {
                mapReducer.setCurrentMapid(filter_results.getMapID());
            }
            
            mapReducer.Reduce(filter_results.getMapID(), filter_results.getFilters());
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
