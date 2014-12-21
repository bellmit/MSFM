package com.cboe.domain.routingProperty.properties;

import java.lang.reflect.InvocationTargetException;
import java.util.Comparator;
import java.util.List;

import com.cboe.domain.routingProperty.AbstractRoutingPropertyGroup;
import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.RoutingPropertyTypeImpl;
import com.cboe.domain.routingProperty.common.StringListBasePropertyImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.Validator;
import com.cboe.interfaces.domain.routingProperty.common.StringListBaseProperty;
import com.cboe.domain.routingProperty.key.ExternalExchangeFirmClassKey;
import com.cboe.util.ExceptionBuilder;

public class LinkageRouterAssignmentGroup extends AbstractRoutingPropertyGroup implements Cloneable
{
    public static BasePropertyType ROUTING_PROPERTY_TYPE = RoutingPropertyTypeImpl.LINKAGE_ROUTER_ASSIGNMENT;
    
    public static final String LINKAGE_ROUTER_VENDOR = "Linkage Router Vendor";

    private StringListBaseProperty routerVendors;

    public LinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey)
    {
        super(basePropertyKey);
    }

    public LinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup);
    }

    public LinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, int versionNumber)
    {
        super(basePropertyKey, versionNumber);
    }

    public LinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, List<Validator> validators)
    {
        super(basePropertyKey, validators);
    }

    public LinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, PropertyServicePropertyGroup propertyGroup,
                                List<Validator> validators)
            throws DataValidationException, InvocationTargetException
    {
        super(basePropertyKey, propertyGroup, validators);
    }

    public LinkageRouterAssignmentGroup(BasePropertyKey basePropertyKey, int versionNumber, List<Validator> validators)
    {
        super(basePropertyKey, versionNumber, validators);
    }

    /**
     * Gets the BasePropertyType for this group that identifies the type of this group.
     */
    public BasePropertyType getType()
    {
        return ROUTING_PROPERTY_TYPE;
    }
    
    
    protected void initializeProperties()
    {
        routerVendors = new StringListBasePropertyImpl(getPropertyCategoryType(), LINKAGE_ROUTER_VENDOR, getPropertyKey(), getType());
    }

    public BaseProperty getProperty(String name) throws DataValidationException
    {
        if (name.equals(LINKAGE_ROUTER_VENDOR))
        {
            return routerVendors;
        }
        else
        {
            throw ExceptionBuilder.dataValidationException("Unknown RoutingPropertyName: " + name +
                                                           ". Could not find class type to handle.", 0);
        }
    }

    public BaseProperty[] getAllProperties()
    {
        BaseProperty[] properties = new BaseProperty[1];
        properties[0] = routerVendors;

        return properties;
    }

    public Object clone() throws CloneNotSupportedException
    {
        LinkageRouterAssignmentGroup newGroup = (LinkageRouterAssignmentGroup) super.clone();
        newGroup.routerVendors = (StringListBaseProperty) routerVendors.clone();

        return newGroup;
    }

    public String[] getRouterVendors()
    {
        return routerVendors.getStringListValue();
    }

    public void setRouterVendor(String[]  stringListValue)
    {
        routerVendors.setStringListValue(stringListValue);
        firePropertyChange();
    }
    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {ExternalExchangeFirmClassKey.TRADING_SESSION_PROPERTY_NAME,ExternalExchangeFirmClassKey.EXTERNAL_EXCHANGE_PROPERTY_NAME, ExternalExchangeFirmClassKey.EXCHANGE_FIRM_PROPERTY_NAME, ExternalExchangeFirmClassKey.PRODUCT_CLASS_PROPERTY_NAME };
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }
}
