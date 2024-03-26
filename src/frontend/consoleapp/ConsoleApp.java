package src.frontend.consoleapp;
import src.backend.master.Master;

import java.io.IOException;
import java.net.Socket;

public class ConsoleApp {

    private static Master master;

    public static void main(String[] args) throws IOException{

        master = new Master();
    }



}
