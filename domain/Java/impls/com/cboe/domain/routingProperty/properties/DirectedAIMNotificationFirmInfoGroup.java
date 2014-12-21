package com.cboe.domain.routingProperty.properties;

import java.beans.IntrospectionException;
import java.lang.reflect.InvocationTargetException;
import java.text.ParseException;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractFirmPropertyGroup;
import com.cboe.domain.routingProperty.FirmPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.BooleanBasePropertyImpl;
import com.cboe.domain.util.ExtensionsHelper;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiConstants.ExtensionFields;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.BooleanBaseProperty;
import com.cboe.util.ExceptionBuilder;

public class DirectedAIMNotificationFirmInfoGroup  extends AbstractFirmPropertyGroup 
{
    public static final BasePropertyType FIRM_PROPERTY_TYPE = FirmPropertyTypeImpl.DIRECTED_AIM_NOTIFICATION_FIRM_INFO_PARAM;
    
    public static final String INFO = "DirectedAIMNotificationInfo";
    public static final String SHOW_FIRM = "ShowExecutingFirm";
    public static final String SHOW_CORR_FIRM = "ShowCorrespondentFirm";
    public static final String SHOW_CMTA_FIRM = "ShowCMTAFirm";

    private static final int DAIM_FIRM_PARAM_SHOW_FIRM_INDEX = 0;
    private static final int DAIM_FIRM_PARAM_SHOW_CORR_FIRM_INDEX = 1;
    private static final int DAIM_FIRM_PARAM_SHOW_CMTA_INDEX = 2;
    private static final int DAIM_FIRM_PARAM_INDEX_MAX = 3;

    private BooleanBaseProperty[] directedAIMFirmInfoParameters;

    public DirectedAIMNotificationFirmInfoGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public DirectedAIMNotificationFirmInfoGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public DirectedAIMNotificationFirmInfoGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public DirectedAIMNotificationFirmInfoGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public DirectedAIMNotificationFirmInfoGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                             List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public DirectedAIMNotificationFirmInfoGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    /**
     * Gets the BasePropertyType for this group that identifies the type of this group.
     */
    public BasePropertyType getType()
    {
        return FIRM_PROPERTY_TYPE;
    }

    protected void initializeProperties()
    {
        if(directedAIMFirmInfoParameters == null)
        {
            directedAIMFirmInfoParameters = new BooleanBaseProperty[DAIM_FIRM_PARAM_INDEX_MAX];
        }

        directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_FIRM_INDEX]  = new BooleanBasePropertyImpl(getPropertyCategoryType(), SHOW_FIRM, getPropertyKey(), getType());

        directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CORR_FIRM_INDEX]  = new BooleanBasePropertyImpl(getPropertyCategoryType(), SHOW_CORR_FIRM, getPropertyKey(), getType());

        directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CMTA_INDEX]  = new BooleanBasePropertyImpl(getPropertyCategoryType(), SHOW_CMTA_FIRM, getPropertyKey(), getType());

        try
        {
        	directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_FIRM_INDEX].initializeDefaultValues();
            directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CORR_FIRM_INDEX].initializeDefaultValues();
            directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CMTA_INDEX].initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + INFO + " category="
                          + getPropertyCategoryType() + "type=" + getType(), e);
        }
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if(name.equals(SHOW_FIRM))
        {
            return directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_FIRM_INDEX];
        }
        else if(name.equals(SHOW_CORR_FIRM))
        {
            return directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CORR_FIRM_INDEX];
        }
        else if(name.equals(SHOW_CMTA_FIRM))
        {
            return directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CMTA_INDEX];
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown FirmPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[ DAIM_FIRM_PARAM_INDEX_MAX ];
        properties[ DAIM_FIRM_PARAM_SHOW_FIRM_INDEX ] = directedAIMFirmInfoParameters[ DAIM_FIRM_PARAM_SHOW_FIRM_INDEX ];
        properties[ DAIM_FIRM_PARAM_SHOW_CORR_FIRM_INDEX ] = directedAIMFirmInfoParameters[ DAIM_FIRM_PARAM_SHOW_CORR_FIRM_INDEX ];
        properties[ DAIM_FIRM_PARAM_SHOW_CMTA_INDEX ] = directedAIMFirmInfoParameters[ DAIM_FIRM_PARAM_SHOW_CMTA_INDEX ];

        return properties;
    }

    public String getDirectedAIMNotificationFirmInfo()
    {
    	String extensions="";

    	try{
            if(directedAIMFirmInfoParameters != null)
            {
                ExtensionsHelper helper= new ExtensionsHelper(extensions);
                if(directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_FIRM_INDEX ] != null)
                {
                    helper.setValue(ExtensionFields.EXECUTING_FIRM,new Boolean(directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_FIRM_INDEX ].getBooleanValue()).toString());
                }
                if(directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CORR_FIRM_INDEX ] != null)
                {
                    helper.setValue(ExtensionFields.CORRESPONDENT_FIRM,new Boolean(directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CORR_FIRM_INDEX ].getBooleanValue()).toString());
                }
                if(directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CMTA_INDEX ] != null)
                {
                    helper.setValue(ExtensionFields.CMTA_FIRM,new Boolean(directedAIMFirmInfoParameters[DAIM_FIRM_PARAM_SHOW_CMTA_INDEX ].getBooleanValue()).toString());
                }
                extensions = helper.toString();
            }
    	}
    	catch(ParseException e)
    	{
    		Log.exception(e);
    	}
    	return extensions;
    }
}
