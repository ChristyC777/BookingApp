package src.backend.booking;

import src.backend.lodging.DateRange;
import src.backend.lodging.Lodging;

public class Booking {
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

}
