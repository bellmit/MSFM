/**
 * 
 */
package com.cboe.interfaces.domain.routingProperty.common;

/**
 * @author misbahud
 *
 */
public enum NamedRoutingActionEnum
{
    UNSPECIFIED("Unspecified", "U"),
    Success("Success", "S"),
    Failure("Failure", "F"),
    TE("Trade Engine", "T"),
    NORD("Floor", "N"),
    OMT("OMT", "O"),
    REJECT("Reject", "R"),
    CANCEL("Cancel", "C");

    public String description;
    public String propertyValue;

    NamedRoutingActionEnum(String description, String propertyValue)
    {
        this.description = description;
        this.propertyValue = propertyValue;
    }

    public String getPropertyValue()
    {
        return propertyValue;
    }

    public String toString()
    {
        return description;
    }

    public static NamedRoutingActionEnum findNamedRoutingActionEnum(String propertyValue)
    {
        NamedRoutingActionEnum retVal = NamedRoutingActionEnum.UNSPECIFIED;
        if(propertyValue != null && propertyValue.length() > 0)
        {
            for(NamedRoutingActionEnum routingAction : NamedRoutingActionEnum.values())
            {
                if(propertyValue.equals(routingAction.propertyValue))
                {
                    retVal = routingAction;
                    break;
                }
            }
        }
        return retVal;
    }
}
