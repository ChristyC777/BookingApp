package src.backend.lodging;

import java.io.Serializable;
import java.util.Calendar;
import java.text.SimpleDateFormat;

public class Lodging implements LodgingInterface, Serializable {

    private String roomImage;
    private String roomName;
    private String area;
    private int noOfPersons;
    private int noOfReviews;
    private int stars;
    private String manager;
    private Calendar from = null;
    private Calendar to = null;

    Lodging()
    {
        super();
    }

    public void setManager(String manager)
    {
        this.manager = manager;
    }

    public void setFrom(Calendar from)
    {
        this.from = from;
    }

    public void setTo(Calendar to)
    {
        this.to = to;
    }

    public Calendar getTo()
    {
        return to;
    }

    public Calendar getFrom()
    {
        return from;
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


    public void printRoom()
    {
        System.out.println("////////////////////////////");
        System.out.println("         ROOM DATA        ");
        System.out.println("////////////////////////////");
        System.out.printf("Name: %s%n", this.getRoomName());
        System.out.printf("Stars: %d%n", this.getStars());
        System.out.printf("Area: %s%n", this.getArea());
        System.out.printf("Number of People: %d%n", this.getNumberOfPersons());
        System.out.printf("Number of Reviews: %d%n", this.getNumberOfReviews());
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String formattedDate = dateFormat.format(from.getTime());
        System.out.printf("Available from: %s%n", formattedDate);
        formattedDate = dateFormat.format(to.getTime());
        System.out.printf("Available from: %s%n", formattedDate);
    }
}  