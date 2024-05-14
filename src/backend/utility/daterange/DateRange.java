package src.backend.utility.daterange;

import java.io.Serializable;
import java.util.Calendar;

public class DateRange implements Serializable{

    private Calendar from;
    private Calendar to; 

    public DateRange(Calendar from, Calendar to)
    {
        this.from = from;
        this.to = to;
    }

    public Calendar getFrom()
    {
        return this.from;
    }

    public Calendar getTo()
    {
        return this.to;
    }
    
    public boolean isWithinRange(Calendar dateFrom, Calendar dateTo) {
        if (dateFrom!=null && dateTo!=null)
        {   
            if (!(from.compareTo(dateFrom) <= 0))
            {
                return false;
            }
            if(!(to.compareTo(dateTo) >= 0))
            {
                return false;
            }
            return true;
        }
        return false;
    }

    public boolean checkReservations(Calendar dateFrom, Calendar dateTo) {
        if (dateFrom != null && dateTo != null) {
            // Check if the given period overlaps with any existing reservation
            if ((from.compareTo(dateFrom) <= 0) && (to.compareTo(dateTo) >= 0)) {
                return false; // There is an overlap, reservation not allowed
            }
            return true; // No overlap, reservation allowed
        }
        return false; 
    }
    
}
