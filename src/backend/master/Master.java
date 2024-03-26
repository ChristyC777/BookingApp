package src.backend.master;
import java.io.*;
import java.net.Proxy;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

import src.backend.threads.MyThread;
import src.backend.worker.Worker;

public class Master {

    private final static int SERVERPORT = 7777;
    public final static MyThread master = new MyThread("Master");

    // When a master is created so are the threads of workers
    public Master(int n){
        
        for (int i = 0; i<n; i++){
            Worker workers = new Worker("worker"+ Integer.toString(i));
        }
    }

    public static void main(String[] args) {
        System.out.println("I'm waiting for a connection request");
        try{
            ServerSocket ss = new ServerSocket(SERVERPORT);
            Socket soc = ss.accept();
            System.out.println("You're connected to Master! How may I asssist you?");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    /**
     * Ανάθεση ενός καλύματος.
     * 
     */
    public void assignRoom()
    {

    }

    /**
     * Αφαίρεση ενός καλύματος.
     * 
     */
    public void removeRoom()
    {

    }

    /**
     * Επιστρέφει τις κρατήσεις των καλυμάτων ενός συγκεκριμένου καλύματος.
     * @param -> RoomName: Όναμα καλύματος 
     */
    public ArrayList<String> viewBookings(String RoomName)
    {
        return null;
    }

    /**
     * Ανανέωση διαθέσιμων ημερομηνιών προς ενοικίαση για συγκεκριμένο κατάλυμα.
     */
    public void updateDates(String roomName, Date startPeriod, Date endPeriod)
    {

    }

    /**
     * Επιστρέφει τα καλύματα που πληρούν τις προδιαγραφές που περιγράφονται στα filters.
     * @param filters -> επιθυμητές προδιαγραφές δωματίου
     */
    public ArrayList<String> filterRooms(ArrayList<String> filters)
    {
        return null;
    }

    /**
     * Προσθέτει κριτική του ενοικιαστή για συγκεκριμένο κατάλυμα.
     * @param filters -> επιθυμητές προδιαγραφές δωματίου
     */
    public void addRating(String roomName)
    {
        
    }


    
}

class Reducer {

    public Map<Integer, String> Map()
    {
        return null;
    }

    public Map<Integer, ArrayList<String>> Reduce()
    {
        return null;
    } 
    
}
