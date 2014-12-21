package com.cboe.domain.routingProperty.key;

import com.cboe.domain.AbstractFirmPropertyHandler;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

/**
 * SessionDefaultFirmClassKey is used to set a firm property that can only be set at default
 * level rather than individual firm level. 
 */
public class SessionDefaultFirmClassKey extends SessionFirmClassKey
{
    public SessionDefaultFirmClassKey(BasePropertyType p_type)
    {
        super(p_type);
        this.firmNumber = AbstractFirmPropertyHandler.DEFAULT_STR_VALUE;
    }

    public SessionDefaultFirmClassKey(String p_propertyKey) throws DataValidationException
    {
        super(p_propertyKey);
        this.firmNumber = AbstractFirmPropertyHandler.DEFAULT_STR_VALUE;
    }
    
    public SessionDefaultFirmClassKey(String p_propertyName, String p_firmAcronym,
            String p_exchangeAcronym, String p_sessionName, int p_classKey)
    {
        super(p_propertyName, p_firmAcronym, p_exchangeAcronym, p_sessionName, p_classKey);
        this.firmNumber = AbstractFirmPropertyHandler.DEFAULT_STR_VALUE;
    }
    
    public void setFirmNumber(String firmNumber)
    {
        this.firmNumber = AbstractFirmPropertyHandler.DEFAULT_STR_VALUE;
        resetPropertyKey();
    }
    
    public String getFirmNumber()
    {
        this.firmNumber = AbstractFirmPropertyHandler.DEFAULT_STR_VALUE;
        return firmNumber;
    }
}
