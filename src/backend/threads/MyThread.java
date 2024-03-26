package src.backend.threads;

public class MyThread extends Thread 
{
    private int sleepTime;

    MyThread(String threadName)
    {
        super(threadName);
        sleepTime = (int)(Math.random() * 5000);
    }

    @Override
    public void run() {
        
        try{
            Thread.sleep(sleepTime);
        }
        catch (InterruptedException e)
        {
            System.out.println(e);
        }
    }

}