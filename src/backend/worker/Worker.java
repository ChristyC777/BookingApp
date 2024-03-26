package src.backend.worker;

import java.util.Map;
import java.util.stream.Stream;

public class Worker extends Thread implements WorkerInterface {

    private String inputStream;
    
    public Worker(String name)
    {
        super(name);
    }

    @Override
    public void run()
    {
        System.out.println(getName() + " has started!");

        inputStream = "Bob";
		
        while(true)
        {
            synchronized (inputStream)
            {
                try {
                    inputStream.wait();
                } catch (InterruptedException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }

                processRequest(inputStream);
                
                // storeLodging(inputStream);

                System.out.println(getName() + " is exiting...");
            }
        }
    }

    @Override
    public void processRequest(String inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processRequest'");
    }

    @Override
    public void storeLodging(String inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeLodging'");
    }

    //search(filters)
}

