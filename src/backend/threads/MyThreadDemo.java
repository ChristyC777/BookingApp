

public class MyThreadDemo implements Runnable {
    
    int sleepTime;
    int n;
    
    @Override
    public void run() {
        
        for (int i = 0; i < n; i++) {
            System.out.println("Thread Runnable: " + i);
            try {
                sleepTime = (int)(Math.random() * 5000);
                Thread.sleep(sleepTime);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    
}