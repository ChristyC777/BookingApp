package src.backend.utility.response;

import java.io.Serializable;

public class Response implements Serializable {
    
    private String mapid;
    private Object obj;
    private String message;


    public Response(String mapid, Object obj)
    {
        this.mapid = mapid;
        this.obj = obj;
    }

    public Response(String mapid, String message)
    {
        this.mapid = mapid;
        this.message = message;
    }

    public void setMapID(String mapid)
    {
        this.mapid = mapid;
    }

    public void setObject(Object obj)
    {
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

    public String getMessage()
    {
        return this.message;
    }

    public void setMessage(String message)
    {
        this.message = message;
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
