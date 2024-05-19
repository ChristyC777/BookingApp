package src.backend.users;

import java.util.ArrayList;
import java.util.UUID;

public class User {

    private String id;
    private String username;
    private String password;
    private static ArrayList<User> users = new ArrayList<>();
    private static ArrayList<User> guests = new ArrayList<>();

    public User(String username, String password)
    {
        this.username = username;
        this.password = password;
    }

    protected User ()
    {
        this.id = UUID.randomUUID().toString();
        guests.add(this);
    }

    public String getUUID()
    {
        return this.id;
    }

    public String getUsername()
    { 
        return this.username;
    }

    public String getPassword(){ return this.password;}

    public boolean login(String username, String password, String identifier)
    {
        User dummyuser = new User(username, password);
        if (identifier.equals("Manager"))
        {
            if(users.stream().anyMatch(user -> user.equals(dummyuser) && user instanceof Manager))
            {
                return true;
            } 
        }
        else 
        {
            if(users.stream().anyMatch(user -> user.equals(dummyuser) && user instanceof Guest))
            {
                return true;
            } 
        }
        return false;
    }

    public void addUser(User user)
    {
        users.add(user);
    }

}
