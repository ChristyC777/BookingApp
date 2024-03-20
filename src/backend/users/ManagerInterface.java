package src.backend.Users;

public isrc.backend.usersgerInterface {
    
    /**
     * Προσθήκη ενός καλύματος.
     * @param lodge -> Το κάλυμα προς πρόσθεση.
     */
    public void addLodging(Lodging lodge);

    /**
     * Αφαίρεση ενός καλύματος.
     * @param lodge -> Το κάλυμα προς αφαίρεση.
     */
    public void removeLodging(Lodging lodge);

    /**
     * Επιστρέφει τις κρατήσεις των καλυμάτων στην ιδιοκτησία του διαχειριστή.
     */
    public void getBookings();

    /**
     * Προσθήκη διαθέσιμων ημερομηνιών προς ενοικίαση για συγκεκριμένο κατάλυμα.
     */
    public void addBookingDate(Date startPeriod, Date endPeriod);
}
