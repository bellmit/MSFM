package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractAffiliatedFirmPropertyGroup;
import com.cboe.domain.routingProperty.AffiliatedFirmPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;
import com.cboe.domain.routingProperty.common.DirectedAIMFirmRegistrationBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyFactory;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;
import com.cboe.util.ExceptionBuilder;

public class DirectedAIMFirmRegistrationGroup extends AbstractAffiliatedFirmPropertyGroup 
{
    public static final BasePropertyType AFFILIATED_FIRM_PROPERTY_TYPE = AffiliatedFirmPropertyTypeImpl.DIRECTED_AIM_AFFILIATED_FIRM_REGISTRATION;
    protected static final String AFFILIATED_FIRM_PROPERTY = "AffiliatedFirmProperty";
    public static final String FIRM_REGISTERED_FOR_DIRECTED_AIM = "FirmRegisteredForDirectedAIM";
	public static final String DIRECTED_AIM_REGISTRATION_DATE = "DirectedAIMRegistrationDate";
	
       
    protected BasePropertyFactory affiliatedFirmPropertyFactory;
    protected BooleanBaseProperty isRegisteredProperty;
    protected DirectedAIMFirmRegistrationDateParameter lastUpdatedTime;
  
    public DirectedAIMFirmRegistrationGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public DirectedAIMFirmRegistrationGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup) throws DataValidationException,
            InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public DirectedAIMFirmRegistrationGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public DirectedAIMFirmRegistrationGroup(BasePropertyKey basePropertyKey,
            List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public DirectedAIMFirmRegistrationGroup(BasePropertyKey basePropertyKey,
            PropertyServicePropertyGroup propertyGroup, List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public DirectedAIMFirmRegistrationGroup(BasePropertyKey basePropertyKey, int versionNumber,
            List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    public boolean isRegistered() {
        return isRegisteredProperty.getBooleanValue();
    }
    
    public long getLastUpdatedTime() {
        return lastUpdatedTime.getDirectedAIMFirmRegistrationDate();
    }
    
    
    public BaseProperty getProperty(String name) throws DataValidationException
    {
    	if(name.equals(FIRM_REGISTERED_FOR_DIRECTED_AIM))
		{
			return isRegisteredProperty;
		}
		else if(name.equals(DIRECTED_AIM_REGISTRATION_DATE))
		{
			return lastUpdatedTime;
		}
		else
		{
			throw ExceptionBuilder.dataValidationException("Unknown AffiliatedFirmPropertyName: " + name +
                    ". Could not find class type to handle.", 0);
		}
    }

    public BaseProperty[] getAllProperties()
    {
    	BaseProperty[] properties = new BaseProperty[2];
        properties[DirectedAIMFirmRegistrationBasePropertyImpl.IS_REGISTERED_INDEX] = isRegisteredProperty;
        properties[DirectedAIMFirmRegistrationBasePropertyImpl.LAST_UPDATED_TIME_INDEX] = lastUpdatedTime;

        return properties;
    }

    public Object clone() throws CloneNotSupportedException
    {
    	DirectedAIMFirmRegistrationGroup newGroup = (DirectedAIMFirmRegistrationGroup) super.clone();
         newGroup.isRegisteredProperty      = (BooleanBasePropertyImpl ) isRegisteredProperty.clone();
         newGroup.lastUpdatedTime   = (DirectedAIMFirmRegistrationDateParameter) lastUpdatedTime.clone();
         return newGroup;
    }

    protected void initializeProperties()
    {
    	 //workstation = new DeviationWorkstationParameter (getPropertyCategoryType(), WORKSTATION, getPropertyKey(), getType());
    	isRegisteredProperty = new BooleanBasePropertyImpl(getPropertyCategoryType(), FIRM_REGISTERED_FOR_DIRECTED_AIM     , getPropertyKey(), getType());

    	lastUpdatedTime = new DirectedAIMFirmRegistrationDateParameter(getPropertyCategoryType(), DIRECTED_AIM_REGISTRATION_DATE     , getPropertyKey(), getType());

       
        try
        {
        	isRegisteredProperty.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + FIRM_REGISTERED_FOR_DIRECTED_AIM + " category="
                          + getPropertyCategoryType() + "type=" + getType(), e);
        }

        try
        {
        	lastUpdatedTime.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + DIRECTED_AIM_REGISTRATION_DATE + " category=" +
                          getPropertyCategoryType() + "type=" + getType(), e);
        }

    }

    /**
     * Gets the BasePropertyType for this group that identifies the type of this group.
     */
    public BasePropertyType getType()
    {
        return AFFILIATED_FIRM_PROPERTY_TYPE;
    }
}
