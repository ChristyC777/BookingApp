package src.backend.utility.filterdata;

import java.io.Serializable;
import java.util.HashMap;

import src.backend.lodging.Lodging;

public class FilterData implements Serializable {

    private String mapid;
    private HashMap<Lodging, Integer> filters;
    private HashMap<String, Integer> bookings;

    public FilterData(String mapid, HashMap<Lodging, Integer> filters)
    {
        this.mapid = mapid;
        this.filters = filters;
    }

    public void setBookings(HashMap<String, Integer> bookings) {
        this.bookings = bookings;
    }

    public HashMap<String, Integer> getBookings() {
        return bookings;
    }

    public String getMapID() {
        return this.mapid;
    }

    public HashMap<Lodging,Integer> getFilters() {
        return this.filters;
    }

}
