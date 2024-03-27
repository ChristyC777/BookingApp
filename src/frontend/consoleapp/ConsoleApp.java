package src.frontend.consoleapp;
import src.backend.master.Master;

import java.io.IOException;
import java.net.Socket;
import java.util.Scanner;

public class ConsoleApp {

	ObjectInputStream in;
	ObjectOutputStream out;

    ConsoleApp(){  }
    public static void main(String[] args) throws IOException{

        Scanner input = new Scanner(System.in);
        Menu();
        System.out.print("Enter your answer: ");
        int option = input.nextInt();
        while (option > 3 && option < 1) 
        {
            System.out.println("There is no such option!!! Please try again!!!");
            Menu();
            System.out.print("Enter your answer: ");
            option = input.nextInt();
        }

        switch(option)
        {
            case 1:
                System.out.println("Please enter the file path for your json file: ");
                String json_file = input.nextLine();
                break;
            case 2:
                break;
            case 3:
                break;
        }

        Socket clientSocket = new Socket("localhost", 4444);

    }

    public static void Menu()
    {
        System.out.println("Welcome!!! Please select from the following options (1-3)");
        System.out.println("1. Add a room");
        System.out.println("2. Delete a room");
        System.out.println("3. Update dates");
    }
}
