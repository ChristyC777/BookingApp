package src.frontend.consoleapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import static src.shared.ManagerActions.*;
import src.backend.lodging.Lodging;

public class ConsoleApp {

	private static ObjectInputStream in;
	private static ObjectOutputStream out;

    ConsoleApp() { }
    public static void main(String[] args) throws IOException, ParseException {
        
        Scanner input = new Scanner(System.in);
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

        Socket connection = new Socket("192.168.1.24", 7777);
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

                    out.writeObject(ADD_LODGING);
                    out.flush();

                    out.writeObject(lodge);
                    out.flush(); 

                    System.out.println("Room successfully added!!!");
                    break;

                case 2:
                    System.out.println("Please enter the name of the room you would like to remove: ");
                    String roomName = input.nextLine();


                    out.writeObject(REMOVE_LODGING);
                    out.flush();

                    out.writeObject(roomName);
                    out.flush(); 

                    System.out.println("Room successfully removed!!!");
                    break;
                case 3:

                    Calendar cal = Calendar.getInstance();
        
                    System.out.println("Add available dates for booking!!! (Format: From (DD/MM/YYYY) - To (DD/MM/YYYY))");
                    String in_date = input.next(); 
                    SimpleDateFormat date = new SimpleDateFormat("dd/MM/yyyy");
                    try {
                        cal.setTime(date.parse(in_date));
                    } catch (java.text.ParseException e) {
                        e.printStackTrace();
                    }
                    System.out.println(cal.getTime());

                    out.writeObject(ADD_DATES);
                    out.flush();

                    out.writeObject(cal.getTime());
                    out.flush();                   

                    break;
                case 4:

                    System.out.print("Here are the bookings made for your room(s)!!!");

                    out.writeObject(VIEW_BOOKINGS);
                    out.flush();

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

    public static void Menu()
    {
        System.out.println("Welcome!!! Please select from the following options (1-3)");
        System.out.println("1. Add a room");
        System.out.println("2. Delete a room");
        System.out.println("3. Update dates");
        System.out.println("4. View bookings");

    }
}
