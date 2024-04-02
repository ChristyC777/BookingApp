package src.backend.users;

import java.util.Map;

import src.backend.lodging.Lodging;

public class Guest extends User implements GuestInterface {


    public Guest()
    {
        super();
    }

    public Guest (String username, String password)
    {
        super(username, password);
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
