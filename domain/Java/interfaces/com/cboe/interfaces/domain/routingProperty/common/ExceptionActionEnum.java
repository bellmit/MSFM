package com.cboe.interfaces.domain.routingProperty.common;

public enum ExceptionActionEnum
{
    THROW("TH"),
    BACK_TO_ORIGIN("BO"),
    RE_ROUTE("RR");
    
    public String propertyValue;

    ExceptionActionEnum(String propertyValue)
    {        
        this.propertyValue = propertyValue;
    }

    public String toString()
    {
        return propertyValue;
    }

    public static ExceptionActionEnum findActionEnum(String propertyValue)
    {
        ExceptionActionEnum retVal = ExceptionActionEnum.THROW;
        if(propertyValue != null && propertyValue.length() > 0)
        {
            for(ExceptionActionEnum routingAction : ExceptionActionEnum.values())
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
