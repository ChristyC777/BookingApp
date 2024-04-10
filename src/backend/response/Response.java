package src.backend.response;

public class Response {
    
    private Object obj;

    public Response(Object obj)
    {
        this.obj = obj;
    }


    public Object getResponse() {
        return this.obj;
    }

    public void setResponse(Object obj) {
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
