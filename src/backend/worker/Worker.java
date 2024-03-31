package src.backend.worker;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Worker extends Thread  {

    private final static int SERVERPORT = 7778;
    private ServerSocket providerSocket;
	private Socket connection = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Worker(int numberOfWorkers)
    {
        for (int i = 0; i<numberOfWorkers;i++)
        {
            String thread_name = "thread" + Integer.toString(i+1);
            Thread thread = new Thread(new WorkerHandler(thread_name, SERVERPORT + 1)); // should this be i + 1?
            thread.start();
        }
        openServer();
    }

    void openServer() {
		try {
            in = new ObjectInputStream(in);
			providerSocket = new ServerSocket(SERVERPORT);

			while (true) {
				connection = providerSocket.accept();

                // You take the parameter and see which worker does the master want to connect to
                // You pass the connection to that worker_thread to handle the request  
			}
		} catch (IOException ioException) {
			ioException.printStackTrace();
		} finally {
			try {
				providerSocket.close();
			} catch (IOException ioException) {
				ioException.printStackTrace();
			}
		}
	}

}

