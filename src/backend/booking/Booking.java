package src.backend.booking;

import src.backend.lodging.DateRange;
import src.backend.lodging.Lodging;

public class Booking {
    private DateRange dateRange;
    private String userName;
    private Lodging lodgeName;

    public Booking(){}

    public Booking(DateRange dateRange, String userName, Lodging lodgeName) {
        this.dateRange = dateRange;
        this.userName = userName;
        this.lodgeName = lodgeName;
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

    public Lodging getLodgeName() {
        return lodgeName;
    }

    public void setLodgeName(Lodging lodgeName) {
        this.lodgeName = lodgeName;
    }

}
