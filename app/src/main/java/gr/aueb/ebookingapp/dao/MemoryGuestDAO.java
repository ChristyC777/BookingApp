package gr.aueb.ebookingapp.dao;

import java.util.ArrayList;

import gr.aueb.ebookingapp.domain.backend.users.Guest;

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

    @Override
    public Guest find(String username, String password) {
        Guest found_guest = (Guest) guestList.stream().filter(dummyguest -> dummyguest.getUsername().equals(username));
        return found_guest;
    }

    @Override
    public Guest findGuest(String uuid) {
        Guest found_guest = (Guest) guestList.stream().filter(dummyguest -> dummyguest.getUUID().equals(uuid));
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