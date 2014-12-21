package com.cboe.domain.util;

public class TypeUserSessionClassContainer
{
    private short type;
    private int userKey;
    private String sessionName;
    private int classKey;
    private int hashCode;

    public TypeUserSessionClassContainer(short type, int userKey, String sessionName, int classKey)
    {
        if(sessionName == null) throw new IllegalArgumentException("Session name cannot be null.");
        
        this.type = type;
        this.userKey = userKey;
        this.sessionName = sessionName;
        this.classKey = classKey;
        
        int x = (type << 16) + type;
        int y = sessionName.hashCode();   
        hashCode = (classKey + x) ^ (y + userKey);
    }

    public short getType() { return type; }
    public int getUserKey() { return userKey; }
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
            TypeUserSessionClassContainer rhs = (TypeUserSessionClassContainer) o;
            if(hashCode() == rhs.hashCode())
            {
                if(rhs.type == type && rhs.userKey == userKey &&
                   rhs.sessionName.equals(sessionName) && rhs.classKey == classKey)
                {
                    return true;
                }
            }
        }
    
        return false;
    }
}
