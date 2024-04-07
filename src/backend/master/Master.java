 package src.backend.master;

import java.io.*;
import java.net.*;
import java.util.*;

import src.backend.lodging.Lodging;
import static src.shared.ClientActions.*;

public class Master {

    private final static int SERVERPORT = 7777;
    private int numberOfWorkers;
    private ArrayList<Thread> masterThreads = new ArrayList<>();
    private ArrayList<WorkerNode> workerNodes = new ArrayList<WorkerNode>(); // "<IP: Port>"
    private ServerSocket providerSocket;
	private Socket connection = null;
    private ObjectInputStream in;
    private ObjectOutputStream out;
  
    public Master()
    {
    }

    public void setWorkers(int workers)
    {
        numberOfWorkers = workers;
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
		}
	}


    public void assignRoom(Lodging room) 
    {
        int workerID = selectWorker(room.getRoomName());
        try {
            
            // Establish a connection with Worker
            Socket new_connection = new Socket(workerNodes.get(workerID).getIP(), workerNodes.get(workerID).getPort());
            out = new ObjectOutputStream(new_connection.getOutputStream());
            in = new ObjectInputStream(new_connection.getInputStream());

            // Write Action
            out.writeObject(ADD_LODGING);
            out.flush();
            
            // Write the lodge that needs to be added
            out.writeObject(room);
            out.flush();

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void makeBooking(String roomName, String username, Calendar from, Calendar to)
    {
        int workerID = selectWorker(roomName);
        try 
        {
            Socket new_connection = new Socket(workerNodes.get(workerID).getIP(), workerNodes.get(workerID).getPort());
            out = new ObjectOutputStream(new_connection.getOutputStream());
            in = new ObjectInputStream(new_connection.getInputStream());

            // Write the action taking place
            out.writeObject(BOOK);
            out.flush();

            // Write the name of the lodge
            out.writeObject(roomName);
            out.flush();
    
            // Write the username 
            out.writeObject(username);
            out.flush();

            // Write the check-in date 
            out.writeObject(from);
            out.flush();

            // Write the check-out date 
            out.writeObject(to);
            out.flush();
        }
        catch(IOException e)
        {
            e.printStackTrace();
        }
    }

    public void viewBookings(String manager)
    {
        try {

            // Establish connection with Worker
            for (WorkerNode workerID : workerNodes)
            {
                Socket new_connection = new Socket(workerID.getIP(), workerID.getPort());
                in = new ObjectInputStream(new_connection.getInputStream());
                out = new ObjectOutputStream(new_connection.getOutputStream());

                // Write the action taking place
                out.writeObject(VIEW_BOOKINGS);
                out.flush();
    
                // Write the manager that wants to see the bookings
                out.writeObject(manager);
                out.flush();
            }

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateDates(String roomName, String manager, String startPeriod, String endPeriod)
    {
        int workerID = selectWorker(roomName);
        try {
            // Establish connection with Worker

                Socket new_connection = new Socket(workerNodes.get(workerID).getIP(), workerNodes.get(workerID).getPort());
                in = new ObjectInputStream(new_connection.getInputStream());
                out = new ObjectOutputStream(new_connection.getOutputStream());

                // Write the action taking place
                out.writeObject(ADD_DATES);
                out.flush();

                // Write the name of the lodge
                out.writeObject(roomName);
                out.flush();
    
                // Write the manager that wants to see the bookings
                out.writeObject(manager);
                out.flush();

                // Write the starting date of availability
                out.writeObject(startPeriod);
                out.flush();

                // Write the ending date of availability
                out.writeObject(endPeriod);
                out.flush();
            

        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void filterRooms(String mapid, Map<String, Object> filters)
    {
        try {

            // Establish connection with Worker
            for (WorkerNode workerID : workerNodes)
            {
                Socket new_connection = new Socket(workerID.getIP(), workerID.getPort());
                out = new ObjectOutputStream(new_connection.getOutputStream());

                // Write the action taking place
                out.writeObject(FILTER);
                out.flush();

                // Write mapid
                out.writeObject(mapid);
                out.flush();
    
                // Write the manager that wants to see the bookings
                out.writeObject(filters);
                out.flush();
            }


        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }   
    }

    public int getNumberOfWorkers()
    {
        return this.numberOfWorkers;
    }

    public void addWorkerNode(WorkerNode worker)
    {
        this.workerNodes.add(worker);
    }

    
    public void addRating(int rating) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'addRating'");
    }
    
    public int H(String roomName)
    {
        String room = roomName.replaceAll("\\s","");
        int hash_value = 0;
        final char[] s = room.toCharArray();
        final int n = s.length;

        for (int i = 0; i < n; i++) {
            hash_value += (int)(s[i]);
        }
        return hash_value;
    }
    
    public int selectWorker(String roomName) {
        return H(roomName) % numberOfWorkers;
    }

    public static void main(String[] args) {
        Master master = new Master();
        master.setWorkers(Integer.parseInt(args[0]));
        
        Scanner input = new Scanner(System.in);
        String workerIP;
        int workerPort;
        WorkerNode workerNode;

        for (int i=0; i < master.getNumberOfWorkers(); i++)
        {
            System.out.printf("Enter the IP address of Worker node %d: ", i+1);
            workerIP = input.next();

            System.out.printf("Enter the port of Worker node %d: ", i+1);
            workerPort = input.nextInt();
            
            workerNode = new WorkerNode(workerIP, workerPort);
            master.addWorkerNode(workerNode);
        }

        master.openServer();
    }

    private static class WorkerNode
    {
        private String ip;
        private int port;
        
        private WorkerNode(String ip, int port)
        {
            this.ip = ip;
            this.port = port;
        }
    
        private String getIP()
        {
            return this.ip;
        }

        private int getPort()
        {
            return this.port;
        }
    }

}


