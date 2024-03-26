package src.backend.worker;

import java.util.stream.Stream;

public interface WorkerInterface {

    public void processRequest(String inputStream);

    public void storeLodging(String inputStream);

    // public void search();
    
}
