package src.frontend.consoleapp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Scanner;

import javax.swing.text.Style;
import javax.swing.text.StyledEditorKit;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import static src.shared.ClientActions.*;

import src.backend.booking.Booking;
import src.backend.lodging.Lodging;
import src.backend.users.Manager;
import src.backend.users.User;
import src.backend.utility.daterange.DateRange;
import src.backend.utility.response.Response;

public class ConsoleApp {

    private final static int SERVERPORT = 7777;
    private static String HOST_ADDRESS;
    private static ObjectInputStream in;
    private static ObjectOutputStream out;

    ConsoleApp() {}

    public static void main(String[] args) throws IOException, ParseException {

        Scanner input = new Scanner(System.in);

        System.out.print("Enter the IP address of Master: ");
        HOST_ADDRESS = input.nextLine();

        System.out.print("Are you a user of this App? (Y/N)\n>> ");
        String id = input.nextLine().trim().toLowerCase();
        User user = null;
        if (id.equals("y")) {
            System.out.print("Please enter your username: ");
            String username = input.nextLine();
            System.out.print("Please enter your password: ");
            String password = input.nextLine();
            System.out.println("Waiting for identification...");
            user = new Manager(username, password);
            boolean flag = user.login(username, password, "Manager");
            // If the account doesn't exist
            if (flag == false) {
                System.out.println("Sorry, no account was found with the provided credentials.");
                System.out.println("It is important for you as a manager to create an account.");
                System.out.print("Would you like to create a new account? (Y/N)\n>> ");
                String decision = input.nextLine().trim().toLowerCase();
                // New Account creation
                if (decision.equals("y")) {
                    boolean creating_account = true;
                    while (creating_account == true) {
                        System.out.print("Please enter your username: ");
                        username = input.nextLine();
                        System.out.print("Please enter your password: ");
                        password = input.nextLine();
                        flag = user.login(username, password, "Manager");
                        if (flag == true) {
                            System.out.println("Sorry, this user exists!!! Try again.");
                        } else {
                            user = new Manager(username, password);
                            user.addUser(user);
                            creating_account = false;
                        }
                    }
                } else // else exit the app
                {
                    System.out.println("Closing App...");
                    System.exit(0);
                }
            } // esle if the account exist we proceed
        } else {
            System.out.print("Would you like to create a new account? (Y/N)\n>> ");
            String decision = input.nextLine().trim().toLowerCase();
            // New Account creation
            if (decision.equals("y")) {
                boolean creating_account = true;
                while (creating_account == true) {
                    System.err.print("Please enter your username: ");
                    String username = input.nextLine();
                    System.out.print("Please enter your password: ");
                    String password = input.nextLine();
                    user = new User(username, password);
                    boolean flag = user.login(username, password, "Manager");
                    if (flag == true) {
                        System.out.println("Sorry this user exists!!! Try again");
                    } else {
                        user = new User(username, password);
                        user.addUser(user);
                        creating_account = false;
                    }
                }
            } else // else exit the app
            {
                System.out.println("Closing App...");
                System.exit(0);
            }
        }
        boolean exit = false;
        while (exit == false) {
            Menu();
            System.out.print("Enter your answer: ");
            int option = input.nextInt();
            input.nextLine(); // consume newline
            while (option > 4 && option < 1) {
                System.out.println("There is no such option!!! Please try again!!!");
                Menu();
                System.out.print("Enter your answer: ");
                option = input.nextInt();
                input.nextLine(); // consume newline
            }

            try {

                // Create gson object for json object handling
                Gson gson = new Gson();

                Socket connection;
                Response response = null;

                switch (option) {
                    case 1:
                        System.out.print("Please enter the file path for your json file: ");
                        String fileName = input.nextLine();

                        // Reading file
                        File file = new File(fileName);
                        FileReader fileReader = new FileReader(file);

                        Object obj = new JSONParser().parse(fileReader);

                        JSONObject jobj = (JSONObject) obj;

                        Lodging lodge = gson.fromJson(jobj.toString(), Lodging.class);
                        lodge.setManager(user.getUsername());

                        connection = new Socket(HOST_ADDRESS, SERVERPORT);

                        out = new ObjectOutputStream(connection.getOutputStream());
                        in = new ObjectInputStream(connection.getInputStream());

                        out.writeObject(ADD_LODGING);
                        out.flush();

                        out.writeObject(user.getUsername());
                        out.flush();

                        out.writeObject(lodge);
                        out.flush();

                        try {
                            String message = (String) in.readObject();
                            System.out.println(message);
                         } catch (ClassNotFoundException e) {
                             e.printStackTrace();
                         }

                        connection.close();
                        break;

                    case 2: // Manager wants to add dates of availability for a lodge

                        boolean input_wrong = true;
                        String name = null;
                        String fromInput = null;
                        String toInput = null;
                        Calendar from = Calendar.getInstance();
                        Calendar to = Calendar.getInstance();
                        while (input_wrong) // Till the input is right
                        {
                            // Ask for the name of the lodge
                            System.out.print("Enter the name of the lodge you want to add the available dates to: ");
                            name = input.nextLine();

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
                            dateFormat.setLenient(false);

                            // Ask for the dates of availability
                            System.out.print("\n#### Add available booking dates! ####\n");

                            System.out.print("\nInput starting date of availability (DD/MM/YYYY):\n>> ");
                            fromInput = input.nextLine(); 
                            from.setTime(dateFormat.parse(fromInput));

                            System.out.print("Input ending date of availability (DD/MM/YYYY):\n>> ");
                            toInput = input.nextLine();
                            to.setTime(dateFormat.parse(toInput));
                            
                            // Compare then so that the date of from is always smaller than the date of to
                            if (from.compareTo(to) < 0) {
                                input_wrong = false;
                            } else {
                                System.out.println("Wrong dates, please try again!");
                            }
                        }
                        connection = new Socket(HOST_ADDRESS, SERVERPORT);

                        out = new ObjectOutputStream(connection.getOutputStream());
                        in = new ObjectInputStream(connection.getInputStream());

                        // Send the action
                        out.writeObject(ADD_DATES);
                        out.flush();

                        // Send the name of the room
                        out.writeObject(name);
                        out.flush();

                        // Send the username of the manager
                        out.writeObject(user.getUsername());
                        out.flush();

                        // Send starting date of availability
                        out.writeObject(fromInput);
                        out.flush();

                        // Send ending date of availability
                        out.writeObject(toInput);
                        out.flush();

                        try {
                            String message = (String) in.readObject();
                            System.out.println("\n\n#### " + message + " ####");
                         } catch (ClassNotFoundException e) {
                             e.printStackTrace();
                         }

                        connection.close();
                        break;

                    case 3:
                    
                        connection = new Socket(HOST_ADDRESS, SERVERPORT);
                        
                        out = new ObjectOutputStream(connection.getOutputStream());
                        in = new ObjectInputStream(connection.getInputStream());
                        
                        out.writeObject(VIEW_BOOKINGS);
                        out.flush();

                        out.writeObject(user.getUsername());
                        out.flush();
                    
                        System.out.println("\nAwaiting for a response...");

                        response = null;

                        // await for a response
                        try {
                            response = (Response) in.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        System.out.println("\nResponse received!\n");
                        
                        // Retrieve response 
                        HashMap<Lodging, Integer> filtered_rooms = (HashMap<Lodging, Integer>) response.getResponse();

                        if (filtered_rooms.size() > 0)
                        {
                            System.out.println("Found the following bookings:"); 
                            for (HashMap.Entry<Lodging, Integer> item : filtered_rooms.entrySet()) { // {"lodge1":2, "lodge5":6}
                                ArrayList<Booking> bookings = item.getKey().getBookings();
                                System.out.println(item.getKey());
                                for (Booking booking : bookings)
                                {
                                    System.out.println(booking);    
                                }
                            }
                        }
                        else
                        {
                            System.out.println("No bookings found.");
                        }
                        
                        // Stop the output and await a keypress so the results don't scroll up too far
                        System.out.print("\nPress 'Enter' to continue...");
                        input.nextLine();

                        connection.close();
                        break;
                    case 4:
                        input_wrong = true;
                        name = null;
                        fromInput = null;
                        toInput = null;
                        from = Calendar.getInstance();
                        to = Calendar.getInstance();
                        while (input_wrong) // Till the input is right
                        {

                            SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");

                            // Ask for the dates of availability
                            System.out.print("\n#### Select the dates! ####\n");

                            System.out.print("\nInput starting date (DD/MM/YYYY):\n>> ");
                            fromInput = input.nextLine(); 
                            from.setTime(dateFormat.parse(fromInput));

                            System.out.print("Input ending date (DD/MM/YYYY):\n>> ");
                            toInput = input.nextLine();
                            to.setTime(dateFormat.parse(toInput));
                            
                            // Compare then so that the date of from is always smaller than the date of to
                            if (from.compareTo(to) < 0) {
                                input_wrong = false;
                            } else {
                                System.out.println("Wrong dates, please try again!");
                            }
                        }
                        DateRange dateRange = new DateRange(from, to);
                        connection = new Socket(HOST_ADDRESS, SERVERPORT);

                        out = new ObjectOutputStream(connection.getOutputStream());
                        in = new ObjectInputStream(connection.getInputStream());

                        out.writeObject(VIEW_RESERVATIONS_PER_AREA);
                        out.flush();
                        
                        out.writeObject(user.getUsername());
                        out.flush();

                        out.writeObject(dateRange);
                        out.flush();
                        
                        System.out.println("\nAwaiting for a response...");

                        response = null;

                        // await for a response
                        try {
                            response = (Response) in.readObject();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }

                        System.out.println("\nResponse received!\n");
                        
                        // Retrieve response 
                        
                        if (response == null)
                        {
                            System.out.println("Selected period is out of range of any booking periods.");
                        }
                        else
                        {
                            HashMap<String, Integer> found_rooms = (HashMap<String, Integer>) response.getResponse();
    
                            if (found_rooms.size() > 0)
                            {
                                System.out.println("Found the following bookings:"); 
                                System.out.printf("For the given period of time (%s - %s) %n", fromInput, toInput, "we found the following booking(s): %n");
                                for (HashMap.Entry<String, Integer> item : found_rooms.entrySet()) { // {"lodge1":2, "lodge5":6}
                                    System.out.printf("%s : %d%n", item.getKey(), item.getValue());
                                }
                            }
                            else
                            {
                                System.out.println("No bookings found.");
                            }
                        }
                        
                        // Stop the output and await a keypress so the results don't scroll up too far
                        System.out.print("\nPress 'Enter' to continue...");
                        input.nextLine();

                        connection.close();
                        
                        break;                        
                    case 5:
                        exit = true;
                        break;
                }
            } catch (IOException | java.text.ParseException e) {
                e.printStackTrace();
            } 
        }
    }

    public static void Menu()
    {
        System.out.println("\n###################### MENU ######################\n");
        System.out.println("Please select from the following options (1-4)");
        System.out.println("1. Add a room");
        System.out.println("2. Update dates");
        System.out.println("3. View bookings");
        System.out.println("4. View bookings per area");
        System.out.println("5. Exit");
    }
}
