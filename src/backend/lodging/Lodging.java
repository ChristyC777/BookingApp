package src.backend.lodging;

import java.io.Serializable;
import java.util.ArrayList;

import src.backend.booking.Booking;
import src.backend.utility.daterange.DateRange;

public class Lodging implements LodgingInterface, Serializable {

    private String roomImage;
    private String roomName;
    private String area;
    private int noOfPersons;
    private int noOfReviews;
    private double stars;
    private int price;
    private String manager;
    private DateRange dateRange;
    private ArrayList<Booking> bookings;

    Lodging()
    {
        super();
        bookings = new ArrayList<Booking>();
    }

    public synchronized void addRating(Integer rating)
    {
        noOfReviews++;
        stars = (stars + rating) / 2;
        stars = ((double) Math.round(stars * 10.0f)) / 10.0f;
    }

    public void setPrice(int price)
    {
        this.price = price;
    }

    public int getPrice()
    {
        return price;
    }

    public ArrayList<Booking> getBookings()
    {
        return bookings;
    }

    public void addBooking(Booking booking)
    {
        bookings.add(booking);
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

    public double getStars()
    {
        return stars;
    }

    @Override
    public String toString()
    {
        String fromFormattedDate = dateRange.getFrom().getTime().toString();
        String toFormattedDate = dateRange.getTo().getTime().toString();
        if (fromFormattedDate==null )
        {
            fromFormattedDate = "unknown";
        }
        if(toFormattedDate==null)
        {
            toFormattedDate = "unknown";
        }
        return String.format(
            """ 
            ###################
            #### ROOM DATA ####
            ###################

            Name: %s
            Stars: %.1f
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

    @Override
    public boolean equals(Object other)
    {
        if (other == this) {
            return true;
        }
 
        if (!(other instanceof Lodging)) {
            return false;
        }

        // Cast 'other' to Lodging so that we can call Lodging's methods
        Lodging otherLodge = (Lodging) other;

        if (!this.roomImage.equals(otherLodge.roomImage))
        {
            return false;
        }

        if (!this.roomName.equals(otherLodge.roomName))
        {
            return false;
        }

        if (!this.area.equals(otherLodge.area))
        {
            return false;
        }

        if (!(this.noOfPersons == otherLodge.noOfPersons))
        {
            return false;
        }

        if (!(this.noOfReviews == otherLodge.noOfReviews))
        {
            return false;
        }

        if (!(this.stars == otherLodge.stars))
        {
            return false;
        }
        
        if (!this.manager.equals(otherLodge.manager))
        {
            return false;
        }

        return true;
    }
}  