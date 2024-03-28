package src.backend.worker;

public class WorkerHandler implements Runnable{

    private String threadName;
    private int port;

    public WorkerHandler(String name, int p)
    {
        this.threadName = name;
        this.port = p;
    }

    @Override
    public void run()
    {
        
    }

    
    public void processRequest(String inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'processRequest'");
    }

    
    public void storeLodging(String inputStream) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'storeLodging'");
    }
    
}