//
// -----------------------------------------------------------------------------------
// Source file: RoutingPropertyFormatter.java
//
// PACKAGE: com.cboe.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.formatters;

import java.beans.IntrospectionException;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;

import org.omg.CORBA.UserException;

import com.cboe.idl.cmiConstants.ProductKeys;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;

import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.common.ContingencyType;
import com.cboe.interfaces.presentation.common.formatters.ProductClassFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.ProductFormatStrategy;
import com.cboe.interfaces.presentation.common.formatters.RoutingPropertyFormatStrategy;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.interfaces.presentation.product.SessionProduct;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductHelper;

import com.cboe.domain.routingProperty.RoutingPropertyHelper;
import com.cboe.domain.routingProperty.common.ComplexProductClass;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.SimpleProductClass;

/**
 * @author Thomas Morrow
 * @since Jan 16, 2008
 */
public class RoutingPropertyFormatter extends Formatter implements RoutingPropertyFormatStrategy
{
    private static final String EMPTY_STRING = "";

    public RoutingPropertyFormatter()
    {
        addStyle(FULL_NAME_TYPE_STYLE_NAME, FULL_NAME_TYPE_STYLE_DESCRIPTION);
        addStyle(NAME_VALUE_PAIR_STYLE_NAME, NAME_VALUE_PAIR_STYLE_DESCRIPTION);
        addStyle(LIST_STYLE_NAME, LIST_STYLE_DESCRIPTION);

        setDefaultStyle(FULL_NAME_TYPE_STYLE_NAME);
    }

    public String format(BasePropertyGroup basePropertyGroup)
    {
        return format(basePropertyGroup, FULL_NAME_TYPE_STYLE_NAME);
    }

    public String format(BaseProperty baseProperty)
    {
    	return format(baseProperty, FULL_NAME_TYPE_STYLE_NAME);
    }
    
    public String format(BaseProperty baseProperty, String styleName)
    {
    	validateStyle(styleName);
    	String value = null;
    	
    	if (styleName.equals(FULL_NAME_TYPE_STYLE_NAME) || styleName.equals(NAME_VALUE_PAIR_STYLE_NAME) )
        {
            value = baseProperty.getPropertyName();
        }
    	else if(styleName.equals(LIST_STYLE_NAME))
    	{
    		try
            {
	            PropertyDescriptor[] descriptors = baseProperty.getPropertyDescriptors();
	            StringBuilder sb = new StringBuilder(100);
                for (PropertyDescriptor descriptor : descriptors)
                {
                	Object listValue = getPropertyDescriptorValue(baseProperty, descriptor);
                	Object [] properties = (Object[])listValue;
                	for(int i = 0; i < properties.length; i++){
                		if (properties[i] instanceof ContingencyType)
                        {
                			sb.append(format((ContingencyType)properties[i]));
                        }
                		else
                		{
                			sb.append(properties[i].toString());
                		}
                		if(i != properties.length-1){
                			sb.append(",");
                		}	
                	}
                }
                value = sb.toString();
            }
            catch (IntrospectionException e)
            {
	            // TODO Auto-generated catch block
	            e.printStackTrace();
            }
        }
    	return value;
    	
    }
    
    public String format(BasePropertyGroup basePropertyGroup, String styleName)
    {
        validateStyle(styleName);

        String value = null;

        if (styleName.equals(FULL_NAME_TYPE_STYLE_NAME))
        {
            value = basePropertyGroup.getType().getFullName();
        }
        else if (styleName.equals(NAME_VALUE_PAIR_STYLE_NAME))
        {
            try
            {
                PropertyDescriptor[] descriptors = basePropertyGroup.getPropertyDescriptors();
                StringBuilder sb = new StringBuilder(100);
                for (PropertyDescriptor descriptor : descriptors)
                {
                    Object propertyValue = getPropertyDescriptorValue(basePropertyGroup, descriptor);
                    if (propertyValue instanceof SessionClassStruct)
                    {
                        sb.append(format((SessionClassStruct)propertyValue));
                    }
                    else if (propertyValue instanceof SessionProductStruct)
                    {
                        sb.append(format((SessionProductStruct)propertyValue));
                    }
                    else if (propertyValue instanceof SimpleComplexProductClass)
                    {
                        sb.append(format((SimpleComplexProductClass)propertyValue));
                    }
                    else if (propertyValue instanceof SimpleProductClass){
                    	sb.append(format((SimpleProductClass)propertyValue));
                    }
                    else if (propertyValue instanceof ComplexProductClass){
                    	sb.append(format((ComplexProductClass)propertyValue));
                    }
                    else
                    {
                        String displayName = descriptor.getDisplayName();
                        sb.append(RoutingPropertyHelper.firstCharToUpper(displayName)).append("=[");

                        //  HACK ALERT!!!!  This ugly hack allows proper sorting of the Post and Station
                        //  key values, so that Post 10 is after Post 9, and Station 10 is after Station 9.
                        if (propertyValue instanceof Integer)
                        {
                            if (((Integer)propertyValue).intValue() < 10)
                            {
                                if (displayName.equalsIgnoreCase("post")
                                        || displayName.equalsIgnoreCase("station"))
                                {
                                    sb.append("0");
                                }
                            }
                        }
                        sb.append(propertyValue).append(']');
                    }
                    sb.append(' ');
                }
                value = sb.toString();
            }
            catch (IntrospectionException e)
            {
                GUILoggerHome.find().exception(e);
                value = EMPTY_STRING;
            }
        }
        return value;
    }

    private String format(SessionProductStruct struct)
    {
        String retVal = EMPTY_STRING;
        if (struct != null)
        {
            int productKey = struct.productStruct.productKeys.productKey;
            SessionProduct sp;
            if (productKey == ProductKeys.DEFAULT_PRODUCT_KEY)
            {
                sp = APIHome.findProductQueryAPI().getDefaultSessionProduct(struct.sessionName);
            }
            else
            {
                sp = ProductHelper.getSessionProduct(struct.sessionName, productKey);
            }
        //    retVal = CommonFormatFactory.getProductFormatStrategy().format(sp, ProductFormatStrategy.FULL_PRODUCT_NAME);
            retVal = CommonFormatFactory.getProductFormatStrategy().format(sp, ProductFormatStrategy.FULL_PRODUCT_NAME_AND_EXP_DATE_WITH_EXP_OPTION_TYPE);
        }

        return retVal;
    }

    private String format(SessionClassStruct struct)
    {
        String retVal = EMPTY_STRING;
        if (struct != null)
        {
            retVal = format(struct.classStruct.classKey);
        }
        return retVal;
    }

    private String format(SimpleComplexProductClass simpleComplexProductClass)
    {
        String retVal = EMPTY_STRING;
        if (simpleComplexProductClass != null)
        {
            retVal = format(simpleComplexProductClass.getClassKey());
        }
        return retVal;
    }
    
    private String format(SimpleProductClass simpleProductClass)
    {
        String retVal = EMPTY_STRING;
        if (simpleProductClass != null)
        {
            retVal = format(simpleProductClass.getClassKey());
        }
        return retVal;
    }
    
    private String format(ComplexProductClass complexProductClass)
    {
        String retVal = EMPTY_STRING;
        if (complexProductClass != null)
        {
            retVal = format(complexProductClass.getClassKey());
        }
        return retVal;
    }

    private String format(int classKey)
    {
        String returnValue;
        try
        {
            ProductClass productClass = ProductHelper.getProductClassCheckInvalid(classKey);
            returnValue = CommonFormatFactory.getProductClassFormatStrategy().format(productClass, ProductClassFormatStrategy.CLASS_TYPE_NAME_INVALID);
        }
        catch (UserException e)
        {
            GUILoggerHome.find().exception(e);
            returnValue = Integer.toString(classKey);
        }
        return returnValue;
    }
    
    
    private String format(ContingencyType type)
    {
        String returnValue = CommonFormatFactory.getContingencyFormatStrategy().format(type);
        return returnValue;
    }
    
    private Object getPropertyDescriptorValue(BasePropertyGroup group, PropertyDescriptor propertyDescriptor)
    {
        Object returnValue = null;

        try
        {
            returnValue = group.getFieldValue(propertyDescriptor);
        }
        catch (IllegalAccessException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        catch (InvocationTargetException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }

        return returnValue;
    }

    private Object getPropertyDescriptorValue(BaseProperty property, PropertyDescriptor propertyDescriptor)
    {
        Object returnValue = null;

        try
        {
            returnValue = property.getFieldValue(propertyDescriptor);
        }
        catch (IllegalAccessException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
        catch (InvocationTargetException e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }

        return returnValue;
    }

    
    
    
}
