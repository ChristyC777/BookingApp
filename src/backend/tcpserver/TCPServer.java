package src.backend.tcpserver;

import java.net.ServerSocket;
import java.net.Socket;

import src.backend.master.Master;

public class TCPServer extends Thread {
    
    private final static int SERVERPORT = 7777;
    
    public static void main(String[] args) {

        Master master = new Master("MasterEntity", 5);

        master.start();
        System.out.println("I'm waiting for a connection request...");
        try {
            ServerSocket serverSocket = new ServerSocket(SERVERPORT);
            Socket soc = serverSocket.accept();
            System.out.println("You're connected to Master! How may I asssist you?");
        } catch(Exception e) {
            System.out.println(e);
        }
    }
}
