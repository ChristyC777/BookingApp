package src.backend.lodging;

import java.util.Calendar;

public class DateRange {

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

    public boolean isWithinRange(Calendar datefrom, Calendar dateto) {
        return ((from.compareTo(datefrom) < 0) && (to.compareTo(dateto)>=0));
    }
    
}
