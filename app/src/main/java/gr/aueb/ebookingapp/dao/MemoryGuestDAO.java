package gr.aueb.ebookingapp.dao;

import java.util.ArrayList;
import java.util.Optional;

import src.backend.users.Guest;

public class MemoryGuestDAO implements GuestDAO
{
    protected static ArrayList<Guest> guestList = new ArrayList<Guest>();

    public MemoryGuestDAO(){}

    @Override
    public void initialize() {
        Guest guest1 = new Guest("JohnWick",
                "Excommunicado");
        guestList.add(guest1);
    }

    public ArrayList<Guest> getGuestList()
    {
        return guestList;
    }

    @Override
    public Guest find(String username, String password) {
        Guest found_guest = (Guest) guestList.stream().filter(dummyguest -> dummyguest.getUsername().equals(username) && dummyguest.getPassword().equals(password)).findFirst().orElse(null);
        return found_guest;
    }

    @Override
    public Guest findGuest(String username) {
        Optional<Guest> optionalGuest = guestList.stream()
                .filter(dummyguest -> dummyguest.getUsername() != null && dummyguest.getUsername().equals(username)) /* Added check in case it is null */
                .findFirst();
        if(optionalGuest.isPresent())
        {
            return optionalGuest.get();
        }
        Optional<Guest> found_guest = guestList.stream().filter(dummyguest -> dummyguest.getUUID() != null && dummyguest.getUUID().equals(username)).findFirst(); /* Added check in case it is null */
        if(found_guest.isPresent())
        {
            return found_guest.get();
        }

        return null;
    }

    @Override
    public ArrayList<Guest> findAll() {
        return guestList;
    }

    @Override
    public void delete(Guest guest) {
        guestList.remove(guest);
    }

    @Override
    public void save(Guest newGuest) {
        guestList.add(newGuest);
    }

}