package src.backend.users;

import java.util.Map;

import src.backend.lodging.Lodging;
import java.util.HashMap;

public class Guest extends User implements GuestInterface {

    private HashMap<String, Integer> ratings;

    public Guest()
    {
        super();
        ratings = new HashMap<String, Integer>();
    }

    public Guest (String username, String password)
    {
        super(username, password);
        ratings = new HashMap<String, Integer>();
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

    public void search(Map<Integer, String> filter)
    {
        // TODO: PART B 
    }

    public void book(Lodging lodge)
    {
        // TODO: PART B
    }

    public void rate(int stars)
    {
        // TODO: PART B
    }
    
}
