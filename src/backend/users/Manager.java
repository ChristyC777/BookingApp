package src.backend.users;

import java.util.Date;

import src.backend.lodging.Lodging;

public class Manager extends User implements ManagerInterface  {


    public Manager(String username, String password)
    {
        super(username, password);
    }
    
    /**
     * Προσθήκη ενός καλύματος.
     * @param lodge -> Το κάλυμα προς πρόσθεση.
     */
    public void addLodging(Lodging lodge)
    {

    }

    /**
     * Αφαίρεση ενός καλύματος.
     * @param lodge -> Το κάλυμα προς αφαίρεση.
     */
    public void removeLodging(Lodging lodge)
    {

    }

    /**
     * Επιστρέφει τις κρατήσεις των καλυμάτων στην ιδιοκτησία του διαχειριστή.
     */
    public void getBookings()
    {

    }

    public void addBookingDate(Date startPeriod, Date endPeriod) 
    {

    }
    
}
