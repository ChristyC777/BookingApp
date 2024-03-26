package src.frontend.consoleapp;
import src.backend.master.Master;

import java.io.IOException;
import java.net.Socket;

public class ConsoleApp {

    public static void main(String[] args) throws IOException{

        Master master = new Master("MasterServer", 2);
    
        Socket clientSocket = new Socket("127.0.0.1", 7777);
    }

}
