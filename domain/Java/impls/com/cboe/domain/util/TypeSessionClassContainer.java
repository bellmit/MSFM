package com.cboe.domain.util;

public class TypeSessionClassContainer
{
    private short type;
    private String sessionName;
    private int classKey;
    private int hashCode;
    
    private String displayString;

    public TypeSessionClassContainer(short type, String sessionName, int classKey)
    {
        if(sessionName == null) throw new IllegalArgumentException("Session name cannot be null.");
        
        this.type = type;
        this.sessionName = sessionName;
        this.classKey = classKey;
        
        displayString = null;
        
        int x = (type << 16) + type;
        int y = sessionName.hashCode();
        hashCode = (classKey + x) ^ y;
    }

    public short getType() { return type; }
    public String getSessionName() { return sessionName; }
    public int getClassKey() { return classKey; }

    public int hashCode()
    {
       return hashCode;
    }

    public boolean equals(Object o)
    {
        if(o == null) return false;
    
        if(getClass().equals(o.getClass()))
        {
            TypeSessionClassContainer rhs = (TypeSessionClassContainer) o;
            if(hashCode() == rhs.hashCode())
            {
                if(rhs.type == type && rhs.sessionName.equals(sessionName) && rhs.classKey == classKey)
                {
                    return true;
                }
            }
        }
    
        return false;
    }
    
    public String toString()
    {
        if(displayString == null)
        {
            displayString = type + ':' + sessionName + ':' + this.classKey;
        }
        return displayString;
    }
}
