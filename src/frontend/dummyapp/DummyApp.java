package src.frontend.dummyapp;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.json.simple.parser.ParseException;
import static src.shared.ClientActions.*;

import src.backend.lodging.Lodging;
import src.backend.users.Guest;
import src.backend.users.User;

public class DummyApp {

    private final Object lock = new Object();
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

    DummyApp() { }
    public static void main(String[] args) throws IOException, ParseException {

        User user = null;
        Scanner input = new Scanner(System.in);
        System.out.println("Are you a registered user of this app?(Y/N)");
        String id = input.nextLine().trim().toLowerCase();
        if (id.equals("y"))
        {     
            System.out.print("Please enter your username: ");
            String username = input.nextLine().trim();
            System.out.print("Please enter your password: ");
            String password = input.nextLine().trim();
            System.out.println("Waiting for identification...");
            user = new Guest(username, password);
            boolean flag = user.login(username, password, "Guest");
            // If the account doesn't exist
            if (flag == false)
            {
                System.out.println("Sorry, we didn't find any account under those credentials.");
                System.out.println("Would you like to create a new account? (Y/N)");
                String decision = input.nextLine().trim().toLowerCase();
                // New Account creation
                if (decision.equals("y"))
                {
                    boolean creating_account = true;
                    while (creating_account == true){
                        System.out.print("Please enter your username: ");
                        username = input.nextLine().trim();
                        System.out.print("Please enter your password: ");
                        password = input.nextLine().trim();;
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
        else if (id.equals("n"))
        {
            System.out.println("Would you like to create a new account? (Y/N)");
                String decision = input.nextLine().trim().toLowerCase();
                // New Account creation
                if (decision.equals("y"))
                {
                    System.out.println("Would you like to sign up as a guest or as a user? (G - guest, U - User)");
                    String ans = input.nextLine().trim().toLowerCase();
                    if (ans.equals("g"))
                    {
                        System.out.println("Creating an account for you...");
                        user = new Guest();
                        System.out.printf("Your unique id is: %s%nPlease save this message. Your code won't be given to you again.%n", user.getUUID());
                    }
                    else
                    {
                        boolean creating_account = true;
                        while (creating_account == true)
                        {
                            System.out.print("Please enter your username: ");
                            String username = input.nextLine().trim();
                            System.out.print("Please enter your password: ");
                            String password = input.nextLine().trim();
                            user = new Guest(username, password);
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
        input.nextLine(); // consume newline
        while (option > 5 && option < 1) 
        {
            System.out.println("There is no such option!!! Please try again!!!");
            Menu();
            System.out.print("Enter your answer: ");
            option = input.nextInt();
            input.nextLine(); // consume newline
        }

        try (Socket connection = new Socket("localhost", 7777)) {
            try {
            	out = new ObjectOutputStream(connection.getOutputStream());
            	in = new ObjectInputStream(connection.getInputStream());

                out.writeObject(VIEW);
                out.flush();
                
                /////////////////////////////////////////////////////////////////////////
                /////////////////////////// SYNCHRONIZED CODE ///////////////////////////
                ////////////////////////////////////////////////////////////////////////

                Map<String, Object> filtered_rooms = null;
                try {
                    filtered_rooms = (Map<String, Object>) in.readObject();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }

                String firstKey = filtered_rooms.keySet().iterator().next();
                Map<Lodging, Integer> room_list = (Map<Lodging, Integer>) filtered_rooms.get(firstKey);
                System.out.println("Here are the rooms that match your preferences!!!");
                for (Map.Entry<Lodging, Integer> item : room_list.entrySet())
                {
                    System.out.println(item.getKey());
                }

                System.out.println("These are our rooms");

                switch(option)
                {
                    case 1:
                        System.out.print("Please enter the name of the room you would like to book: ");
                        String roomName = input.nextLine().trim();

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
                            input.nextLine(); // consume newline
                            while (answer > 4 && answer < 1) 
                            {
                                System.out.println("There is no such option!!! Please try again!!!");
                                filters();
                                System.out.print("Enter your answer: ");
                                answer = input.nextInt();
                                input.nextLine(); // consume newline
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
                                        input.nextLine(); // consume newline
                                    }
                                    map.put("stars", answer);
                                    break;
                                case 2:
                                    System.out.print("Please select the area you want to go to: ");
                                    String area = input.nextLine();
                                    map.put("area", area);
                                    break;
                                case 3:
                                    System.out.print("Please select the number of people: ");
                                    while (answer < 1) 
                                    {
                                        System.out.println("Invalid number!!! Please try again!!!");
                                        System.out.print("Please select the number of people: ");
                                        answer = input.nextInt();
                                        input.nextLine(); // consume newline
                                    }
                                    map.put("noOfPersons", answer);
                                    break;
                                case 4:
                                    System.out.print("Please select the name of the room: ");
                                    String name = input.nextLine();
                                    map.put("roomName", name);
                                    break;
                            }
                            System.out.println("Would you like to add more filters? Y/N");
                            String ans = input.nextLine().trim().toLowerCase();
                            if (ans.equals("n"))
                            {
                                add_filter = false;
                            }
                        }

                        out.writeObject(FILTER);
                        out.flush();

                        if (user.getUsername().equals(null))
                        {
                            out.writeObject(user.getUUID());
                            out.flush();
                        }
                        else 
                        {
                            out.writeObject(user.getUsername());
                            out.flush();
                        }

                        out.writeObject(map);
                        out.flush();
        
                        /////////////////////////////////////////////////////////////////////////
                        /////////////////////////// SYNCHRONIZED CODE ///////////////////////////
                        ////////////////////////////////////////////////////////////////////////

                        filtered_rooms = null;
                        try {
                            filtered_rooms = (Map<String, Object>) in.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        firstKey = filtered_rooms.keySet().iterator().next();
                        room_list = (Map<Lodging, Integer>) filtered_rooms.get(firstKey);
                        System.out.println("Here are the rooms that match your preferences!!!");
                        for (Map.Entry<Lodging, Integer> item : room_list.entrySet())
                        {
                            System.out.println(item.getKey());
                        }
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
