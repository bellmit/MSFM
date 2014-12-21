package com.cboe.application.product.adapter;

public class TimedClassKey implements Comparable
{
    private long time;
    private int   key;
    private int hashcode;
    
    public TimedClassKey(long time, int key)
    {
        super();
        this.time = time;
        this.key = key;
        
        final int PRIME = 31;
        this.hashcode = 1;
        this.hashcode = PRIME * this.hashcode + key;
        this.hashcode = PRIME * this.hashcode + (int) (time ^ (time >>> 32));
        
    }
    public int getKey()
    {
        return key;
    }
    public long getTime()
    {
        return time;
    }
    @Override
    public int hashCode()
    {
    
        return hashcode;
    }
    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final TimedClassKey other = (TimedClassKey) obj;
        if (key != other.key)
            return false;
        if (time != other.time)
            return false;
        return true;
    }
    public int compareTo(Object obj)
    {
        if (this == obj)
            return 0;
        if (obj == null)
            return 1;
        if (getClass() != obj.getClass())
            return 1;
        final TimedClassKey other = (TimedClassKey) obj;
        if (time < other.time)
            return -1;
        else if (time > other.time)
            return 1;
        else {
            if (key < other.key)
                return -1;
            else if (key > other.key)
                return 1;
            else 
                return 0;
        }
    }
    
}
