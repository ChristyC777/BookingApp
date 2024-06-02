package gr.aueb.ebookingapp.dao;

import java.util.ArrayList;
import java.util.Optional;

import src.backend.users.Guest;

public class MemoryGuestDAO implements GuestDAO
{
    protected static ArrayList<Guest> guestList;

    public MemoryGuestDAO(){}

    @Override
    public void initialize() {
        guestList = new ArrayList<Guest>();
        Guest guest1 = new Guest("JohnWick",
                "Excommunicado");
        guestList.add(guest1);
    }

    @Override
    public Guest find(String username, String password) {
        Guest found_guest = (Guest) guestList.stream().filter(dummyguest -> dummyguest.getUsername().equals(username)).findFirst().orElse(null);
        return found_guest;
    }


    @Override
    public Guest findGuest(String username) {
        Optional<Guest> optionalGuest = guestList.stream()
                .filter(dummyguest -> dummyguest.getUsername().equals(username))
                .findFirst();
        if(optionalGuest.isPresent())
        {
            return optionalGuest.get();
        }
        Guest found_guest = (Guest) guestList.stream().filter(dummyguest -> dummyguest.getUUID().equals(username));
        return found_guest;
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