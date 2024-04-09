package src.backend.utility.daterange;

import java.io.Serializable;
import java.util.Calendar;

public class DateRange implements Serializable{

    private Calendar from;
    private Calendar to; 
    private boolean locked;

    public DateRange(Calendar from, Calendar to)
    {
        this.from = from;
        this.to = to;
        this.locked = false;
    }

    public Calendar getFrom()
    {
        return this.from;
    }

    public Calendar getTo()
    {
        return this.to;
    }

    public boolean isLocked() {
        return locked;
    }

    public void lock() {
        this.locked = true;
    }

    public void unlock() {
        this.locked = false;
    }
    
    public boolean isWithinRange(Calendar dateFrom, Calendar dateTo) {
        return ((from.compareTo(dateFrom) <= 0) && (to.compareTo(dateTo) >= 0));
    }
    
}
