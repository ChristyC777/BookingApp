
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Master {

    // finalResult    

    Master(){
        // num_of_workers
    }

    public static void main(String[] args) {
        System.out.println("I'm waiting for a connection request");
        try{
            ServerSocket ss = new ServerSocket(4444);
            Socket soc = ss.accept();
            System.out.println("You're connected to Master! How may I asssist you?");
        }catch(Exception e){
            System.out.println(e);
        }
    }

    // Booking_request
    // Filter_request
    // function_which_gives_a_command_to_the_workers
    // function_which_returns_result_to_the_application
    // function_which_creates_threads_and_communicates_with_worker
}

class Reducer {

    //map(key,value) -> [(key2,value2)]
    //reduce(key2,[value2]) -> [final_result]
    
}
