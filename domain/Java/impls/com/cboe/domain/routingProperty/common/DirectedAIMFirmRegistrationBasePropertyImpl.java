package com.cboe.domain.routingProperty.common;

import java.util.ArrayList;
import java.util.List;

import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.routingProperty.AbstractBaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.DirectedAIMFirmRegistration;
import com.cboe.interfaces.domain.routingProperty.common.DirectedAIMFirmRegistrationBaseProperty;

public class DirectedAIMFirmRegistrationBasePropertyImpl extends AbstractBaseProperty implements DirectedAIMFirmRegistrationBaseProperty 
{	
	public static final int IS_REGISTERED_INDEX = 0;
    public static final int LAST_UPDATED_TIME_INDEX = 1;
     
     /**
      * IS_REGISTERED_AND_LAST_UPDATED_VALUE is the property name used when PropertyChangeEvents are fired
      */
     public static final String IS_REGISTERED_AND_LAST_UPDATED_VALUE = "IsRegisteredAndLastUpdatedTime";

     private DirectedAIMFirmRegistration dAIMRegistration;

     public DirectedAIMFirmRegistrationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                        BasePropertyType type)
     {
         this(propertyCategory, propertyName, key, type, false, System.currentTimeMillis());
     }

     public DirectedAIMFirmRegistrationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                           BasePropertyType type, boolean isRegistered, long lastUpdateTime)
     {
         this(propertyCategory, propertyName, key, type, new DirectedAIMFirmRegistrationImpl(isRegistered, lastUpdateTime));
     }

    public DirectedAIMFirmRegistrationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                                BasePropertyType type, List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, false, System.currentTimeMillis(), validators);
    }

    public DirectedAIMFirmRegistrationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                                BasePropertyType type, boolean isRegistered, long lastUpdateTime,
                                                List<Validator> validators)
    {
        this(propertyCategory, propertyName, key, type, new DirectedAIMFirmRegistrationImpl(isRegistered, lastUpdateTime),
             validators);
    }

    public DirectedAIMFirmRegistrationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                                BasePropertyType type, DirectedAIMFirmRegistration destination)
    {
        super(propertyCategory, propertyName, key, type);
        setDirectedAIMFirmRegistration(destination);
    }

    public DirectedAIMFirmRegistrationBasePropertyImpl(String propertyCategory, String propertyName, BasePropertyKey key,
                                                BasePropertyType type, DirectedAIMFirmRegistration destination,
                                                List<Validator> validators)
    {
        super(propertyCategory, propertyName, key, type, validators);
        setDirectedAIMFirmRegistration(destination);
    }

    public int compareTo(Object object)
     {
         return dAIMRegistration.toString().compareTo(object.toString());
     }

     /**
      * Parses the field values from the passed String
      * @param value to parse
      */
     protected void decodeValue(String value)
     {
         if(value != null && value.length() > 0)
         {
             String[] parts = BasicPropertyParser.parseArray(value);
             dAIMRegistration = new DirectedAIMFirmRegistrationImpl(Boolean.parseBoolean(parts[IS_REGISTERED_INDEX]),
                                                   Long.parseLong(parts[LAST_UPDATED_TIME_INDEX]));
         }

     }

     /**
      * Get all the Property values as a List of Strings
      * @return List of Strings
      */
     protected List<String> getEncodedValuesAsStringList()
     {
         List<String> valueList = new ArrayList<String>(2);
         valueList.add(IS_REGISTERED_INDEX, Boolean.toString(dAIMRegistration.getIsRegistered()));
         valueList.add(LAST_UPDATED_TIME_INDEX, Long.toString(dAIMRegistration.getLastUpdatedTime()));
         return valueList;
     }

     public DirectedAIMFirmRegistration getDirectedAIMFirmRegistration()
     {
         return dAIMRegistration;
     }

     public void setDirectedAIMFirmRegistration(DirectedAIMFirmRegistration destination)
     {
    	 DirectedAIMFirmRegistration oldValue = this.dAIMRegistration;
         this.dAIMRegistration = destination;
         firePropertyChange(IS_REGISTERED_AND_LAST_UPDATED_VALUE, oldValue, destination);
     }

     public String toString()
     {
         StringBuffer buffer = new StringBuffer();
         buffer.append(getPropertyName()).append("=");
         buffer.append(dAIMRegistration.toString());
         return buffer.toString();
     }

     public boolean getIsRegistered() 
     {
    	 return dAIMRegistration.getIsRegistered();
     }
	
     public long getLastUpdatedTime() 
     {
    	 return dAIMRegistration.getLastUpdatedTime();
     }
	
     public void setIsRegistered(boolean isRegistered) 
     {
    	 this.dAIMRegistration.setIsRegistered(isRegistered);
     }
	
     public void setLastUpdatedTime(long lastUpdateTime) 
     {
    	 this.dAIMRegistration.setLastUpdatedTime(lastUpdateTime);
     }
}
