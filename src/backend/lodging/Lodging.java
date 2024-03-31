package src.backend.lodging;

import java.io.Serializable;
import java.util.Date;

import src.backend.users.Manager;

public class Lodging implements LodgingInterface, Serializable {

    private String roomImage;
    private String roomName;
    private String area;
    private int noOfPersons;
    private int noOfReviews;
    private int stars;
    private String manager;
    //private Date availableDates;

    Lodging()
    {
        super();
    }


    public String getManager()
    {
        return manager;
    }

    public String getRoomImage()
    {
        return roomImage;
    }

    public String getRoomName()
    {
        return roomName;
    }

    public String getArea()
    {
        return area;
    }

    public int getNumberOfPersons()
    {
        return noOfPersons;
    }

    public int getNumberOfReviews()
    {
        return noOfReviews;
    }

    public int getStars()
    {
        return stars;
    }
}  