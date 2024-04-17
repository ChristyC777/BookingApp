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
            return ((from.compareTo(dateFrom) <= 0) && (to.compareTo(dateTo) >= 0)); //from < from_client (e.g. 17/04/2024 <= 18/04/2024) && to >= to_client (17/04/2026 >= 16/04/2026)
        }
        return false;
    }
    
}
