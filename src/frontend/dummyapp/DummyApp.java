package src.frontend.dummyapp;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.google.gson.Gson;

import src.backend.lodging.Lodging;

public class DummyApp {

	private static ObjectInputStream in;
	private static ObjectOutputStream out;

    DummyApp() { }
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
                    System.out.println("Please enter the name of the room you would like to book: ");
                    String roomName = input.next();

                    System.out.println("Please select one of the following dates");
                    
                    // To be completed

                    System.out.println("Room successfully booked!!!");
                    break;

                case 2:

                    System.out.println("Please select from the following filters ");
                    filters();


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
