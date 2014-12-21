package com.cboe.interfaces.domain.routingProperty.common;

public enum ExceptionTypeEnum
{
    SYSTEM_EXCEPTION("SE"),
    COMMUNICATION_EXCEPTION("CE"),
    TRANSACTION_FAILED_EXCEPTION("TFE"),
    AUTHORIZATION_EXCEPTION("AE"),    
    DATA_VALIDATION_EXCEPTION( "DVE"),
    NOT_ACCEPTED_EXCEPTION("NAE");
   
    

        
    
    public String propertyValue;

    ExceptionTypeEnum(String propertyValue)
    {
          this.propertyValue = propertyValue;
    }
    
    public String toString()
    {
        return propertyValue;
    }

    public static ExceptionTypeEnum findTypeEnum(String propertyValue)
    {
        ExceptionTypeEnum retVal = null;
        if(propertyValue != null && propertyValue.length() > 0)
        {
            for(ExceptionTypeEnum exType : ExceptionTypeEnum.values())
            {
                if(propertyValue.equals(exType.propertyValue))
                {
                    retVal = exType;
                    break;
                }
            }
        }
        return retVal;
    }
    
}
