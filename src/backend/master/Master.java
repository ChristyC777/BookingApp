 package src.backend.master;

import java.io.*;
import java.net.*;
import java.util.*;

import src.backend.lodging.Lodging;
import src.backend.worker.Worker;
import static src.shared.ClientActions.*;

public class Master {

    private final static int SERVERPORT = 7777;
    private int numberOfWorkers;
    private ArrayList<Thread> masterThreads = new ArrayList<>();
    private ServerSocket providerSocket;
	private Socket connection = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
  
    public Master()
    {

    }


    void openServer() {
		try {

			providerSocket = new ServerSocket(SERVERPORT, 10);
            System.out.printf("Master server now listening to port %d.%n", SERVERPORT);

			while (true) {
                System.out.println("Awaiting connection...");
				connection = providerSocket.accept();
                System.out.println("Successfully connected!");
                // out = new ObjectOutputStream(connection.getOutputStream());
                // in = new ObjectInputStream(connection.getInputStream());

                Thread requestThread = new Thread(new RequestHandler(connection, this));
                requestThread.start();
                masterThreads.add(requestThread);

                System.out.println(requestThread.getName() + " has started!");
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


    public void assignRoom(Lodging room) 
    {
        long workerID = selectWorker(room.getRoomName());
        try {
            
            // Establish a connection with Worker
            Socket new_connection = new Socket("localhost", 7778);
            out = new ObjectOutputStream(new_connection.getOutputStream());

            // Write the lodge that needs to be added
            out.writeObject(room);
            out.flush();

            // Write the worker's ID
            out.writeLong(workerID);
            out.flush();

            // Write Action
            out.writeObject(ADD_LODGING);
            out.flush();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally
        {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

    public void removeRoom(Lodging room)
    {
        long workerID = selectWorker(room.getRoomName());
        try {

            // Establish connection with Worker
            Socket new_connection = new Socket("localhost", 7778);
            out = new ObjectOutputStream(new_connection.getOutputStream());
            
            // Write the worker's ID 
            out.writeLong(workerID);
            out.flush();

            // Write the action taking place
            out.writeObject(REMOVE_LODGING);
            out.flush();

            // Write the lodge that needs to be added
            out.writeObject(room);
            out.flush();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally
        {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void viewBookings(String manager)
    {
        try {

            // Establish connection with Worker
            Socket new_connection = new Socket("localhost", 7778);
            out = new ObjectOutputStream(new_connection.getOutputStream());

            // Write the action taking place
            out.writeObject(VIEW_BOOKINGS);
            out.flush();

            // Write the manager that wants to see the bookings
            out.writeObject(manager);
            out.flush();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally
        {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }   
    }

    
    public void updateDates(String roomName, Date startPeriod, Date endPeriod)
    {

    }

    public void filterRooms(Map<String, Object> filters)
    {
        try {

            // Establish connection with Worker
            Socket new_connection = new Socket("localhost", 7778);
            out = new ObjectOutputStream(new_connection.getOutputStream());

            // Write the action taking place
            out.writeObject(FILTER);
            out.flush();

            // Write the manager that wants to see the bookings
            out.writeObject(filters);
            out.flush();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally
        {
            try {
                out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }   
    }

    
    public void addRating(int rating) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addRating'");
    }
    
    public long H(String roomName)
    {
        String room = roomName.replaceAll("\\s","");
        long hash_value = 0;
        final char[] s = room.toCharArray();
        final int n = s.length;

        for (int i = 0; i < n; i++) {
            hash_value += (int)(s[i]);
        }
        return hash_value;
    }
    
    public long selectWorker(String roomName) {
        return H(roomName) % numberOfWorkers;
    }

    public static void main(String[] args) {
        new Master().openServer();;
    }

}
