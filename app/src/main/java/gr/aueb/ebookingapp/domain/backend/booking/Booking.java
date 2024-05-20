package gr.aueb.ebookingapp.domain.backend.booking;

import java.io.Serializable;

import gr.aueb.ebookingapp.domain.backend.lodging.Lodging;
import gr.aueb.ebookingapp.domain.backend.utility.daterange.DateRange;

public class Booking implements Serializable
{
    private DateRange dateRange;
    private String userName;
    private Lodging lodge;

    public Booking(){}

    public Booking(DateRange dateRange, String userName, Lodging lodge) {
        this.dateRange = dateRange;
        this.userName = userName;
        this.lodge = lodge;
    }

    public DateRange getDateRange() {
        return dateRange;
    }

    public void setDateRange(DateRange dateRange) {
        this.dateRange = dateRange;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public Lodging getLodge() {
        return lodge;
    }

    public void setLodge(Lodging lodge) {
        this.lodge = lodge;
    }

    @Override
    public String toString()
    {
        String fromFormattedDate = getDateRange().getFrom().getTime().toString();
        String toFormattedDate = getDateRange().getTo().getTime().toString();
        if (fromFormattedDate==null )
        {
            fromFormattedDate = "unknown";
        }
        if(toFormattedDate==null)
        {
            toFormattedDate ="unknown";
        }
        return String.format("%n######################%n#### BOOKING DATA ####%n######################%n%nName: %s%nCheck-in date: %s%nCheck-out date: %s%n", this.getUserName(), fromFormattedDate, toFormattedDate);
    }

}
