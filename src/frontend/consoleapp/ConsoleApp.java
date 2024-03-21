package src.frontend.consoleapp;

import java.io.IOException;
import java.net.Socket;

public class ConsoleApp {

    private Master master;

    public static void main(String[] args) throws IOException{

        Socket connectToMaster = new Socket(master.getSocket());
    }



}
