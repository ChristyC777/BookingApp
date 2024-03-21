package src.backend.master;

import java.util.ArrayList;
import java.util.Date;

public interface MasterInterface {
    /**
     * Ανάθεση ενός καλύματος.
     * 
     */
    public void assignRoom();

    /**
     * Αφαίρεση ενός καλύματος.
     * 
     */
    public void removeRoom();

    /**
     * Επιστρέφει τις κρατήσεις των καλυμάτων ενός συγκεκριμένου καλύματος.
     * @param RoomName -> Όναμα καλύματος 
     */
    public ArrayList<String> viewBookings(String roomName);

    /**
     * Ανανέωση διαθέσιμων ημερομηνιών προς ενοικίαση για συγκεκριμένο κατάλυμα.
     * @param roomName -> Δωμάτιο προς ενοικίαση, 
     * @param startPeriod -> ημερομηνία άφιξης
     * @param endPeriod -> ημερομηνία αποχώρησης
     */
    public void updateDates(String roomName, Date startPeriod, Date endPeriod);

    /**
     * Επιστρέφει τα καλύματα που πληρούν τις προδιαγραφές που περιγράφονται στα filters.
     * @param filters -> επιθυμητές προδιαγραφές δωματίου
     */
    public ArrayList<String> filterRooms(ArrayList<String> filters);

    /**
     * Προσθέτει κριτική του ενοικιαστή για συγκεκριμένο κατάλυμα.
     * @param filters -> επιθυμητές προδιαγραφές δωματίου
     */
    public void addRating(String roomName);

}