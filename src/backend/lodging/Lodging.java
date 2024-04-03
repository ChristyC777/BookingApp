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

    @Override
    public String toString()
    {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        String fromFormattedDate = dateFormat.format(from.getTime());
        String toFormattedDate = dateFormat.format(to.getTime());
        return String.format(
            """ 
            ###################
            #### ROOM DATA ####
            ###################

            Name: %s
            Stars: %d
            Area: %s
            Number of people: %d
            Number of reviews: %d
            Available from: %s
            Available to: %s""",
            this.getRoomName(),
            this.getStars(),
            this.getArea(),
            this.getNumberOfPersons(),
            this.getNumberOfReviews(),
            fromFormattedDate,
            toFormattedDate
        );
    }
}  