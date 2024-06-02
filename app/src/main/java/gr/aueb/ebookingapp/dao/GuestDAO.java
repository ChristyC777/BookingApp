package gr.aueb.ebookingapp.dao;

import java.util.ArrayList;

import gr.aueb.ebookingapp.domain.backend.users.Guest;
public interface GuestDAO {

    void initialize();

    Guest find(String username, String password);

    Guest findGuest(String username);

    ArrayList<Guest> findAll();

    void delete(Guest guest);

    void save(Guest newGuest);

}
