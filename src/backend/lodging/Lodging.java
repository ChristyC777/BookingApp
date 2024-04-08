package src.backend.lodging;

import java.io.Serializable;
import java.sql.Date;
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
    private DateRange dateRange;

    Lodging()
    {
        super();
    }

    public void setManager(String manager)
    {
        this.manager = manager;
    }

    public void setDateRange(DateRange dateRange)
    {
        this.dateRange = dateRange;
    }

    public DateRange getDateRange()
    {
        return dateRange;
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
        String fromFormattedDate = dateRange.getFrom().getTime().toString();
        String toFormattedDate = dateRange.getTo().getTime().toString();
        return String.format(
            """ 
            ###################
            #### ROOM DATA ####
            ###################

            Name: %s
            Stars: %d
            Area: %s
            Manager: %s
            Number of people: %d
            Number of reviews: %d
            Available from: %s
            Available to: %s""",
            this.getRoomName(),
            this.getStars(),
            this.getArea(),
            this.getManager(),
            this.getNumberOfPersons(),
            this.getNumberOfReviews(),
            fromFormattedDate,
            toFormattedDate
        );
    }
}  