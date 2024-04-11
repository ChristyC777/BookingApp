package src.backend.utility.response;

import java.io.Serializable;

public class Response implements Serializable {
    
    private String mapid;
    private Object obj;

    public Response(String mapid, Object obj)
    {
        this.mapid = mapid;
        this.obj = obj;
    }


    public String getMapID()
    {
        return this.mapid;
    }

    public Object getResponse()
    {
        return this.obj;
    }

    public void setResponse(Object obj)
    {
        this.obj = obj;
    }

    public synchronized boolean hasResponse()
    {
        if (obj==null)
        {
            return false;
        }
        return true;
    }

}
