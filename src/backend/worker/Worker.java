package src.backend.worker;

public class Worker extends Thread implements WorkerInterface {
    
    private long workerID;

    Worker(String name)
    {
        this.workerID = threadId();
    }

    @Override
    public void run()
    {
        System.out.println("Thread with id: " + workerID + " started!");
		
		try {
			sleep(5000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		System.out.println("Thread with id: "+ workerID + " exiting...");
		
    }

    public void processRequest(){}

    public void storeLodging(){}

    //search(filters)
}

