package src.frontend.consoleapp;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;
import static src.shared.ClientActions.*;

import src.backend.lodging.Lodging;
import src.backend.users.Manager;
import src.backend.users.User;

public class ConsoleApp {

	private static ObjectInputStream in;
	private static ObjectOutputStream out;

    ConsoleApp() { }
    public static void main(String[] args) throws IOException, ParseException 
    {
        
        Scanner input = new Scanner(System.in);
        System.out.println("Are you a user of this App? (Y/N)");
        String id = input.next();
        User user = null;
        if (id.equals("Y"))
        {
            System.out.print("Please enter your username: ");
            String username = input.next();
            System.out.print("Please enter your password: ");
            String password = input.next();
            System.out.println("Waiting for identification...");
            user = new Manager(username, password);
            boolean flag = user.login(username, password, "Manager");
            // If the account doesn't exist
            if (flag == false)
            {
                System.out.println("It is important for you as a manager to create an account.");
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
                        flag = user.login(username, password, "Manager");
                        if (flag == true)
                        {
                            System.out.println("Sorry, this user exists!!! Try again.");
                        }
                        else 
                        {
                            user = new Manager(username, password);
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
                    boolean creating_account = true;
                    while (creating_account == true){
                        System.err.print("Please enter your username: ");
                        String username = input.next();
                        System.out.print("Please enter your password: ");
                        String password = input.next();
                        user = new User(username, password);
                        boolean flag = user.login(username, password, "Manager");
                        if (flag == true)
                        {
                            System.out.println("Sorry this user exists!!! Try again");
                        }
                        else 
                        {
                            user = new User(username, password);
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
        }
        boolean exit = false;
        while (exit == false)
        {
            Menu();
            System.out.print("Enter your answer: ");
            int option = input.nextInt();
            while (option > 4 && option < 1) 
            {
                System.out.println("There is no such option!!! Please try again!!!");
                Menu();
                System.out.print("Enter your answer: ");
                option = input.nextInt();
    
            }
    
            Socket connection = new Socket("localhost", 7777);

            try {
                out = new ObjectOutputStream(connection.getOutputStream());
                in = new ObjectInputStream(connection.getInputStream());
    
                // Create gson object for json object handling
                Gson gson = new Gson();
    
                switch(option)
                {
                    case 1:
                        System.out.println("Please enter the file path for your json file: ");
                        String fileName = input.next();
                        
                        // Reading file
                        File file = new File(fileName);
                        FileReader fileReader = new FileReader(file);
    
                        Object obj = new JSONParser().parse(fileReader);
    
                        JSONObject jobj = (JSONObject) obj;
    
                        Lodging lodge = gson.fromJson(jobj.toString(), Lodging.class);
                        lodge.setManager(user.getUsername());
    
                        out.writeObject(ADD_LODGING);
                        out.flush();
    
                        out.writeObject(lodge);
                        out.flush(); 
    
                        System.out.println("Room successfully added!!!");
                        break;
    
                    case 2: // Manager wants to add dates of availability for a lodge

                        boolean input_wrong = true;
                        String name = null;
                        Calendar from = Calendar.getInstance();
                        Calendar to = Calendar.getInstance();
                        while (input_wrong) // Till the input is right
                        {
                            // Ask for the name of the lodge 
                            System.out.println("Enter the name of the lodge you want to add the available dates to: ");
                            name = input.next();
                
                            // Ask for the dates of availability
                            System.out.println("Add available dates for booking!!!");
                            System.out.println("Input starting date of availability (DD/MM/YYYY)");
                            String in_date = input.next(); 
                            SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
                            try {
                                from.setTime(date.parse(in_date));
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }
                            System.out.println(from.getTime());
        
                            System.out.println("Input ending date of availability (DD/MM/YYYY)");
                            date = new SimpleDateFormat("dd/MM/yyyy");

                            try {
                                to.setTime(date.parse(in_date));
                            } catch (java.text.ParseException e) {
                                e.printStackTrace();
                            }

                            // Compare then so that the date of from is always smaller than the date of to
                            if (from.compareTo(to) < 0)
                            {
                                input_wrong = false;
                            }
                            else
                            {
                                System.out.println("Wrong dates please try again");
                            }
                        }
                        
                        System.out.println(to.getTime());
        
                        // Send the action
                        out.writeObject(ADD_DATES);
                        out.flush();
    
                        // Send the name of the room 
                        out.writeChars(name);
                        out.flush();
                        
                        // Send the username of the manager
                        out.writeChars(user.getUsername());
                        out.flush();
    
                        // Send starting date of availability
                        out.writeObject(from);
                        out.flush();   
                        
                        // Send ending date of availability
                        out.writeObject(to);
                        out.flush();
    
                        break;
                    case 3:
    
                        System.out.print("Here are the bookings made for your room(s)!!!");
    
                        out.writeObject(VIEW_BOOKINGS);
                        out.flush();
    
                        break;
    
                    // TODO: case 5 for part B
                    case 4:
                        exit = true;
                        break;
    
                }
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                    out.close();
                    connection.close();
                } catch (IOException ioException) {
                    ioException.printStackTrace();
                }
            }
    
        }
    }

    public static void Menu()
    {
        System.out.println("");
        System.out.println("////////////////////// MENU //////////////////////\n");
        System.out.println("Please select from the following options (1-5)");
        System.out.println("1. Add a room");
        System.out.println("2. Update dates");
        System.out.println("3. View bookings"); 
        System.out.println("4. Exit");

    }
}
