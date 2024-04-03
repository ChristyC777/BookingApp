package src.frontend.dummyapp;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.json.simple.parser.ParseException;
import static src.shared.ClientActions.*;

import com.google.gson.Gson;

import src.backend.users.Guest;
import src.backend.users.User;

public class DummyApp {

	private static ObjectInputStream in;
	private static ObjectOutputStream out;

    DummyApp() { }
    public static void main(String[] args) throws IOException, ParseException {

        Scanner input = new Scanner(System.in);
        System.out.println("Are you a registered user of this app?(Y/N)");
        String id = input.next();
        if (id.equals("Y"))
        {     
            System.out.print("Please enter your username: ");
            String username = input.next();
            System.out.print("Please enter your password: ");
            String password = input.next();
            System.out.println("Waiting for identification...");
            User user = new Guest(username, password);
            boolean flag = user.login(username, password, "Guest");
            // If the account doesn't exist
            if (flag == false)
            {
                System.out.println("Sorry, we didn't find any account under those credentials.");
                System.out.println("Would you like to create a new account? (Y/N)");
                String decision = input.next();
                // New Account creation
                if (decision.equals("Y"))
                {
                    boolean creating_account = true;
                    while (creating_account == true){
                        System.out.print("Please enter your username: ");
                        username = input.next();
                        System.out.print("Please enter your password: ");
                        password = input.next();
                        flag = user.login(username, password, "Guest");
                        if (flag == true)
                        {
                            System.out.println("Sorry, this user exists!!! Try again.");
                        }
                        else 
                        {
                            user = new Guest(username, password);
                            user.addUser(user);
                            creating_account = false;
                        }
                    }
                }
                else // else exit the app
                {
                    System.out.println("Closing App...");
                    System.exit(0);
                }
            } // esle if the account exist we proceed
        } 
        else if (id.equals("N"))
        {
            System.out.println("Would you like to create a new account? (Y/N)");
                String decision = input.next();
                // New Account creation
                if (decision.equals("Y"))
                {
                    System.out.println("Would you like to sign up as a guest or as a user? (G - guest, U - User)");
                    String ans = input.next();
                    if (ans.equals("G"))
                    {
                        System.out.println("Creating an account for you...");
                        User user = new Guest();
                        System.out.printf("Your unique id is: %s", user.getUUID(), "Please save this message. Your code won't be given to you again.");
                    }
                    else
                    {
                        boolean creating_account = true;
                        while (creating_account == true)
                        {
                            System.out.print("Please enter your username: ");
                            String username = input.next();
                            System.out.print("Please enter your password: ");
                            String password = input.next();
                            User user = new Guest(username, password);
                            boolean flag = user.login(username, password, "Guest");
                            if (flag == true)
                            {
                                System.out.println("Sorry, this user exists!!! Try again");
                            }
                            else 
                            {
                                user = new Guest(username, password);
                                user.addUser(user);
                                creating_account = false;
                            }
                        }
                    }
                }
                else // else exit the app
                {
                    System.out.println("Closing App...");
                    System.exit(0);
                }
        }
        
        Menu();
        System.out.print("Enter your answer: ");
        int option = input.nextInt();
        while (option > 5 && option < 1) 
        {
            System.out.println("There is no such option!!! Please try again!!!");
            Menu();
            System.out.print("Enter your answer: ");
            option = input.nextInt();
        }

        try (Socket connection = new Socket("192.168.1.24", 7777)) {
            try {
            	out = new ObjectOutputStream(connection.getOutputStream());
            	in = new ObjectInputStream(connection.getInputStream());

                // Create gson object for json object handling
                Gson gson = new Gson();

                switch(option)
                {
                    case 1:
                        System.out.print("Please enter the name of the room you would like to book: ");
                        String roomName = input.next();

                        System.out.println("Please select one of the following dates:");
                        
                        // To be completed

                        System.out.println("Room successfully booked!!!");
                        break;

                    case 2:
                        Map<String, Object> map = new HashMap<String, Object>();
                        boolean add_filter = true;
                        while (add_filter) {
                            System.out.println("Please select from the following filters (1-4)");
                            filters();
                            int answer = input.nextInt();
                            while (answer > 4 && answer < 1) 
                            {
                                System.out.println("There is no such option!!! Please try again!!!");
                                filters();
                                System.out.print("Enter your answer: ");
                                answer = input.nextInt();
                            }
                            switch (answer) 
                            {
                                case 1:
                                    System.out.print("Please select the number of stars (1-5): ");
                                    while (answer > 5 && answer < 1) 
                                    {
                                        System.out.println("There is no such option!!! Please try again!!!");
                                        System.out.print("Please select the number of stars (1-5): ");
                                        answer = input.nextInt();
                                    }
                                    map.put("stars", answer);
                                    break;
                                case 2:
                                    System.out.print("Please select the area you want to go to: ");
                                    String area = input.next();
                                    map.put("area", area);
                                    break;
                                case 3:
                                    System.out.print("Please select the number of people: ");
                                    while (answer < 1) 
                                    {
                                        System.out.println("Invalid number!!! Please try again!!!");
                                        System.out.print("Please select the number of people: ");
                                        answer = input.nextInt();
                                    }
                                    map.put("noOfPersons", answer);
                                    break;
                                case 4:
                                    System.out.print("Please select the name of the room: ");
                                    String name = input.next();
                                    map.put("roomName", name);
                                    break;
                            }
                            System.out.println("Would you like to add more filters? Y/N");
                            String ans = input.next();
                            if (ans.equals("N"))
                            {
                                add_filter = false;
                            }
                        }

                        out.writeObject(FILTER);
                        out.flush();

                        // TODO: MAPID

                        out.writeObject(map);
                        out.flush();
                        // To be completed

                        System.out.println("Here are the rooms that match your preferences!!!");
                        break;

                }
            } catch (IOException e) {
            	e.printStackTrace();
            } finally {
            	try {
            		in.close();
            		out.close();
            	} catch (IOException ioException) {
            		ioException.printStackTrace();
            	}
            }
        }

    }

    public static void Menu()
    {
        System.out.println("Welcome!!!Please select from the following options (1-2)");
        System.out.println("1. Book a room");
        System.out.println("2. Use filters");

    }

    public static void filters()
    {
        System.out.println("1. Star");
        System.out.println("2. Area");
        System.out.println("3. Number of people");
        System.out.println("4. Name of room");
    }

}
