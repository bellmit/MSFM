package com.cboe.domain.util;

import com.cboe.interfaces.domain.Delimeter;

public class PropertyGroupKeyContainer extends Object
{
    private String  category;
    private String  propertyKey;
    private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public PropertyGroupKeyContainer(String category, String propertyKey)
    {
        this.category       = category;
        this.propertyKey    = propertyKey;
        hashCode = (category + propertyKey).hashCode();
    }

    public String getCategory()
    {
        return category;
    }

    public String getPropertyKey()
    {
        return propertyKey;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        boolean result = false;
        if ((obj != null) && (obj instanceof PropertyGroupKeyContainer))
        {
            PropertyGroupKeyContainer otherObj = (PropertyGroupKeyContainer) obj;
            if (    ( this.category.equals( otherObj.getCategory() ) )
                &&  ( this.propertyKey.equals( otherObj.getPropertyKey() ) )
                )
            {                
                result = true;
            }
        }
        return result;
    }

    public String toString()
    {
        return category + Delimeter.PROPERTY_DELIMETER + propertyKey;
    }
}
