package src.backend.master;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Master {

    // finalResult    

    Master(){
        // num_of_workers
    }

    public static void main(String[] args) {
        System.out.println("I'm waiting for a connection request");
        try{
            ServerSocket ss = new ServerSocket(4444);
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
