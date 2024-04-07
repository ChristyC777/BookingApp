package src.backend.mapreducer;

import java.io.Serializable;
import java.util.HashMap;

import src.backend.lodging.Lodging;

public class FilterData implements Serializable {

    private String mapid;
    private HashMap<Lodging, Integer> filters;

    public FilterData(String mapid, HashMap<Lodging, Integer> filters)
    {
        this.mapid = mapid;
        this.filters = filters;
    }

    public String getMapid() {
        return this.mapid;
    }

    public HashMap<Lodging,Integer> getFilters() {
        return this.filters;
    }

}
