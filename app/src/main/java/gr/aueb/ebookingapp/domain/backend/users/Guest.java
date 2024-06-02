package gr.aueb.ebookingapp.domain.backend.users;

import java.util.HashMap;

public class Guest extends User {

    private HashMap<String, Integer> ratings;
    public Guest()
    {
        super();
    }

    public Guest (String username, String password)
    {
        super(username, password);
    }

    public void addRatings(String lodgeName, Integer rating)
    {
        ratings.put(lodgeName, rating);
    }

    public boolean hasRated(String lodgeName)
    {
        if (ratings.containsKey(lodgeName))
        {
            return true;
        }
        return false;
    }

    
}
