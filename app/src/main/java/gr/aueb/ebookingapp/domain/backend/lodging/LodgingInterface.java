package gr.aueb.ebookingapp.domain.backend.lodging;

public interface LodgingInterface {

    /**
     * Επιστρέφει την εικόνα του καταλύματος.
     * @return Την εικόνα του καταλύματος.
     */
    public String getRoomImage();

    /**
     * Επιστρέφει το όνομα του καταλύματος.
     * @return Το όνομα του καταλύματος.
     */
    public String getRoomName();

    /**
     * Επιστρέφει την περιοχή στην οποία βρίσκεται το κατάλυμα.
     * @return Την περιοχή στην οποία εντοπίζεται το κατάλυμα.
     */
    public String getArea();

    /**
     * Επιστρέφει τον αριθμό.
     * @return Την εικόνα του δωματίου.
     */
    public int getNumberOfPersons();

    /**
     * Επιστρέφει τον αριθμό τον κριτικών για το κατάλυμα.
     * @return Τον αριθμό των κριτικών του καταλύματος.
     */
    public int getNumberOfReviews();

    /**
     * Επιστρέφει την μέση βαθμολογία των χρηστών για το κατάλυμα.
     * @return Τη μέση βαθμολογία χρηστών για το κατάλυμα (1 - 5 αστέρια).
     */
    public double getStars();
}
