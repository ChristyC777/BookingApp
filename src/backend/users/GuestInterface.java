package src.backend.users;

import java.util.Map;

import src.backend.lodging.Lodging;

public interface GuestInterface {
    
    /** 
     * Αποστολή φίλτρου στον Master ασύγχρονα και εμφάνιση αποτελεσμάτων.
     * @param filter -> Σύνολο φίλτρων αναζήτησης καταλυμάτων. 
     */ 
    public void search(Map<Integer, String> filter);

    /** 
     * Κράτηση για ένα κατάλυμα {@link gr.aueb.ebookingapp.backend.lodging.Lodging}από εκείνα που επέστρεψε η search().
     * @param lodge -> Κατάλυμα για το οποίο γίνεται η κράτηση.
     */ 
    public void book(Lodging lodge);

    /** 
     * Βαθμολόγηση καταλύματος.
     * @param stars -> Βαθμολογία χρήστη για το κατάλυμα (1 - 5 αστέρια).
     */  
    public void rate(int stars);
}
