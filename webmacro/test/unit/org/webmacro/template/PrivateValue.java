package org.webmacro.template;

public class PrivateValue
{
    private long value;
    private Object object;


    public long getValue ()
    {
        return value;
    }


    public void setValue (long value)
    {
        this.value = value;
    }


    public Object getObject ()
    {
        return object;
    }


    public void setObject (Object object)
    {
        this.object = object;
    }
}
