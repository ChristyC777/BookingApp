package BackEnd.Threads;

public class ThreadCreationDemo implements Runnable {
    
    int sleepTime;
    int n;
    
    @Override
    public void run() {
        
        for (int i = 0; i < n; i++) {
            System.out.println("Thread: " + i);
            try {
                sleepTime = (int)(Math.random() * 5000);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}