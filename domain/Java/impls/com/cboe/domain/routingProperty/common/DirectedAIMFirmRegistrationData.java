package com.cboe.domain.routingProperty.common;

import java.util.List;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;

public class DirectedAIMFirmRegistrationData extends StringBasePropertyImpl
{   
    private String registrationData; // boolean with time as long separated by "\u0001"
    private boolean isRegistered;
    private long lastUpdatedTime;
    
    public DirectedAIMFirmRegistrationData(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey, BasePropertyType type)
    {
        super(propertyCategory, propertyName, basePropertyKey, type);
    }
    
    public DirectedAIMFirmRegistrationData(String propertyCategory, String propertyName, BasePropertyKey basePropertyKey, BasePropertyType type, List<Validator> validators)
    {
        super(propertyCategory, propertyName, basePropertyKey, type, validators);
    }

    public long getLastUpdatedTime() 
    {
        return lastUpdatedTime;
    }
    
    public Boolean getRegistrationValue() 
    {
        return isRegistered; // isRegistered;
    }
   
    public void setRegistationData(boolean p_isReg, long p_lastUpdatedtime) {
        isRegistered = p_isReg;
        lastUpdatedTime = p_lastUpdatedtime;
        StringBuffer regData = new StringBuffer();
        registrationData = regData.append(p_isReg).append('\u0001').append(p_lastUpdatedtime).toString();
    }
    
    public void setRegistrationData(String registrationData)
    {
        this.registrationData = registrationData;
        if (this.registrationData != null && this.registrationData.length() > 0) {
            String[] parts = BasicPropertyParser.parseArray(this.registrationData);
            if (parts.length > 1) {
                isRegistered = Boolean.parseBoolean(parts[0]);
                lastUpdatedTime = Long.parseLong(parts[1]);
            }
        }
    }
    
    public String getRegistrationData()
    {
        return registrationData;
    }

    public Object clone() throws CloneNotSupportedException
    {
        return super.clone();
    }

    public int compareTo(Object other)
    {
        return super.compareTo(other);
    }
}

