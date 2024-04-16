package src.frontend.dummyapp;

import java.io.*;
import java.net.Socket;
import java.util.*;

import org.json.simple.parser.ParseException;
import static src.shared.ClientActions.*;

import src.backend.lodging.Lodging;
import src.backend.users.Guest;
import src.backend.users.User;
import src.backend.utility.response.Response;

import java.text.SimpleDateFormat;

public class DummyApp {

    private final static int SERVERPORT = 7777;
    private final static String HOST = "localhost";
	private static ObjectInputStream in;
	private static ObjectOutputStream out;

    DummyApp() { }
    public static void main(String[] args) throws IOException, ParseException, java.text.ParseException {

        User user = null;
        Scanner input = new Scanner(System.in);
        System.out.print("Are you a registered user of this app? (Y/N)\n>> ");
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
                System.out.print("Would you like to create a new account? (Y/N)\n>> ");
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

                            System.out.println("Account succesfully created!");

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
            System.out.print("Would you like to create a new account? (Y/N)\n>> ");
                String decision = input.nextLine().trim().toLowerCase();
                // New Account creation
                if (decision.equals("y"))
                {
                    System.out.print("Would you like to sign up as a guest or as a user? (G - guest, U - User)\n>> ");
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
        
        boolean exit = false;
        while (exit == false) {
            try {
                    Menu();
                    System.out.print("Enter your answer: ");
                    int option = input.nextInt();
                    input.nextLine(); // consume newline
                    while (option > 3 && option < 1) 
                    {
                        System.out.println("There is no such option!!! Please try again!!!");
                        Menu();
                        System.out.print("Enter your answer: ");
                        option = input.nextInt();
                        input.nextLine(); // consume newline
                    }

                    Socket connection;

                    Response response = null;

                    switch(option)
                    {
                        case 1:
                            String fromInput = null;
                            String toInput = null;
                            String name = null;
                            boolean input_wrong = true;
                            Calendar from = Calendar.getInstance();
                            Calendar to = Calendar.getInstance();
                            while (input_wrong) // Till the input is right
                            {
                                // Ask for the name of the lodge
                                System.out.print("Enter the name of the lodge you want to book: ");
                                name = input.nextLine();

                                SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                                dateFormat.setLenient(false);

                                System.out.print("Input check-in date (DD/MM/YYYY):\n>> ");
                                fromInput = input.nextLine();
                                from.setTime(dateFormat.parse(fromInput));

                                System.out.print("Input check-out date (DD/MM/YYYY)\n>> ");
                                toInput = input.nextLine();
                                to.setTime(dateFormat.parse(toInput));
                                
                                // Compare then so that the check-in is always smaller than the check-out date
                                if (from.compareTo(to) < 0) {
                                    input_wrong = false;
                                } else {
                                    System.out.println("Check-in date was after check-out date, please try again.");
                                }
                            }
                            
                            connection = new Socket(HOST, SERVERPORT);

                            out = new ObjectOutputStream(connection.getOutputStream());
                            in = new ObjectInputStream(connection.getInputStream());

                            out.writeObject(BOOK);
                            out.flush();
                                                    
                            // Send the name of the room
                            out.writeObject(name);
                            out.flush();
                            
                            // Send the name of the user
                            if (user.getUsername() == null)
                            {
                                out.writeObject(user.getUUID());
                                out.flush();
                            }
                            else 
                            {
                                out.writeObject(user.getUsername());
                                out.flush();
                            }

                            // Send starting date of availability
                            out.writeObject(fromInput);
                            out.flush();

                            // Send ending date of availability
                            out.writeObject(toInput);
                            out.flush();

                            // TODO: Message for room successfully or failed to book
                            connection.close();
                            break;

                        case 2:
                            HashMap<String, Object> map = new HashMap<String, Object>();
                            boolean add_filter = true;
                            while (add_filter) {
                                System.out.println("Please select from the following options (1-5)");
                                filters();
                                System.out.print("Enter your answer: ");
                                int answer = input.nextInt();
                                input.nextLine(); // consume newline
                                while (answer > 5 && answer < 1) 
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
                                        if (!(map.containsKey("stars") || map.size() > 4))
                                        {
                                            System.out.print("Please select the number of stars (1-5): ");
                                            answer = input.nextInt();
                                            input.nextLine(); 
                                            while (answer > 5 && answer < 1) 
                                            {
                                                System.out.println("There is no such option!!! Please try again!!!");
                                                System.out.print("Please select the number of stars (1-5): ");
                                                answer = input.nextInt();
                                                input.nextLine(); 
                                            }
                                            map.put("stars", answer);
                                        }
                                        else 
                                        {
                                            if (map.containsKey("stars"))
                                            {
                                                System.out.println("You've already added that field.");
                                            }
                                            else 
                                            {
                                                System.out.println("You've completed all fields");
                                            }
                                        }
                                        break;
                                    case 2:
                                        if (!(map.containsKey("area") || map.size() > 4)) 
                                        {   
                                            System.out.print("Please select the area you want to go to: ");
                                            String area = input.nextLine();
                                            map.put("area", area);
                                        }
                                        else 
                                        {
                                            if (map.containsKey("area"))
                                            {
                                                System.out.println("You've already added that field.");
                                            }
                                            else 
                                            {
                                                System.out.println("You've completed all fields");
                                            }
                                        }
                                        break;
                                    case 3:
                                        if (!(map.containsKey("noOfPersons") || map.size() > 4)) 
                                        {
                                            System.out.print("Please select the number of people: ");
                                            answer = input.nextInt();
                                            input.nextLine(); 
                                            while (answer < 1) 
                                            {
                                                System.out.println("Invalid number!!! Please try again!!!");
                                                System.out.print("Please select the number of people: ");
                                                answer = input.nextInt();
                                                input.nextLine(); 
                                            }
                                            map.put("noOfPersons", answer);
                                        }
                                        else 
                                        {
                                            if (map.containsKey("noOfPersons"))
                                            {
                                                System.out.println("You've already added that field.");
                                            }
                                            else 
                                            {
                                                System.out.println("You've completed all fields");
                                            }
                                        }
                                        break;
                                    case 4:
                                        if (!(map.containsKey("roomName") || map.size() > 4))
                                        {   
                                            System.out.print("Please select the name of the room: ");
                                            name = input.nextLine();
                                            map.put("roomName", name);
                                        }
                                        else
                                        {
                                            if (map.containsKey("roomName"))
                                            {
                                                System.out.println("You've already added that field.");
                                            }
                                            else 
                                            {
                                                System.out.println("You've completed all fields");
                                            }
                                        }
                                        break;

                                    case 5:
                                        map.clear();
                                        break;
                                }
                                System.out.print("Would you like to add more filters? (Y/N)\n>> ");
                                String ans = input.nextLine().trim().toLowerCase();
                                if (ans.equals("n"))
                                {
                                    add_filter = false;
                                }
                            }

                            connection = new Socket(HOST, SERVERPORT);

                            out = new ObjectOutputStream(connection.getOutputStream());
                            in = new ObjectInputStream(connection.getInputStream());
                            
                            out.writeObject(FILTER);
                            out.flush();

                            // Send the name of the user
                            if (user.getUsername() == null)
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

                            System.out.println("Awaiting for a response...");

                            response = null;

                            // await for a response
                            try {
                                response = (Response) in.readObject();
                            } catch (ClassNotFoundException e) {
                                e.printStackTrace();
                            }

                            // System.out.println("Response received!\nFound the following rooms: ");

                            // Retrieve response
                            HashMap<Lodging, Integer> filtered_rooms = (HashMap<Lodging, Integer>) response.getResponse();
                            
                            System.err.println("\n\n\nThese are the filtered rooms that I received: " + filtered_rooms.toString() + "\n\n\n");

                            for (HashMap.Entry<Lodging, Integer> item : filtered_rooms.entrySet())
                            {
                                System.out.println("\n" + item.getKey());
                            }
                            
                            // Stop the output and await a keypress so the results don't scroll up too far
                            System.out.print("\nPress 'Enter' to continue...");
                            input.nextLine();

                            connection.close();
                            break;
                        case 3:
                            exit = true;
                            break;
                    }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void Menu()
    {
        System.out.println("\n###################### MENU ######################\n");
        System.out.println("Please select from the following options (1-3):");
        System.out.println("1. Book a room");
        System.out.println("2. Use filters");
        System.out.println("3. Exit App");
    }

    public static void filters()
    {
        System.out.println("1. Star");
        System.out.println("2. Area");
        System.out.println("3. Number of people");
        System.out.println("4. Name of room");
        System.out.println("5. Clear all filters");
    }

}
