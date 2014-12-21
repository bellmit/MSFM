//
// -----------------------------------------------------------------------------------
// Source file: EnablementFactory.java
//
// PACKAGE: com.cboe.internalPresentation.user
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.user;

import java.util.*;

import com.cboe.idl.property.PropertyStruct;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.property.PropertyGroup;
import com.cboe.interfaces.domain.property.PropertyServiceProperty;
import com.cboe.interfaces.domain.property.PropertyServicePropertyGroup;
import com.cboe.interfaces.internalPresentation.user.Enablement;
import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;

import com.cboe.presentation.common.logging.GUILoggerHome;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.presentation.api.APIHome;

import com.cboe.internalPresentation.common.formatters.PropertyCategoryTypes;

import com.cboe.domain.property.PropertyFactory;
import com.cboe.domain.property.BasicPropertyParser;
import com.cboe.domain.property.PropertyServicePropertyImpl;
import com.cboe.domain.property.PropertyServiceFacadeHome;
import com.cboe.domain.property.PropertyServicePropertyGroupImpl;

public class EnablementFactory
{
    private static final String TEST_CLASS_PROPERTY_KEY = "TestClasses";
    private static final String MDX_PROPERTY_KEY = "MDX";
    private static final String TRADINGFIRM_PROPERTY_KEY = "TradingFirm";

    private EnablementFactory(){}

    public static Enablement create(SessionProductClass sessionProductClass, short operationType, boolean enabled)
    {
        return new EnablementImpl(sessionProductClass, operationType, enabled);
    }

    public static Enablement create(SessionProductClass sessionProductClass, short operationType)
    {
        return new EnablementImpl(sessionProductClass, operationType);
    }

    public static Enablement create(String sessionName, int classKey, short operationType, boolean enabled)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException, NotFoundException
    {
        SessionProductClass spc = ProductHelper.getSessionProductClassCheckInvalid(sessionName, classKey); 
//             SessionProductClass spc =  APIHome.findProductQueryAPI().getClassByKeyForSession(sessionName, classKey);
        return create(spc, operationType, enabled);
    }

    public static Enablement create(String sessionName, int classKey, short operationType)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException, NotFoundException
    {
        SessionProductClass spc = ProductHelper.getSessionProductClassCheckInvalid(sessionName, classKey); 
//                SessionProductClass spc = APIHome.findProductQueryAPI().getClassByKeyForSession(sessionName, classKey);
        return create(spc, operationType);
    }

    public static Enablement create(PropertyServiceProperty property)
            throws DataValidationException, AuthorizationException, SystemException, CommunicationException, NotFoundException
    {
        String sessionName;
        int    classKey;
        short  operationType;

        List nameList = property.getNameList();
        // Name is composed of session, classKey and operation type
        sessionName = (String) nameList.get(0);
        try
        {
            classKey = Integer.parseInt((String) nameList.get(1));
            operationType = Short.parseShort((String) nameList.get(2));
        }
        catch (NumberFormatException nfe)
        {
            GUILoggerHome.find().exception(nfe);
            throw new IllegalArgumentException("Property does not contain the proper data to make an Enablement");
        }

        Enablement enablement = create(sessionName, classKey, operationType);
        ((EnablementImpl)enablement).setPropertyDefinition(property.getPropertyDefinition());

        return enablement;
    }

    public static ArrayList createEnablementList(PropertyGroup propertyGroup)
            throws DataValidationException, AuthorizationException, SystemException, NotFoundException,
            CommunicationException
    {
        ArrayList list = new ArrayList(100);

        Map properties = propertyGroup.getProperties();
        Iterator iterator = properties.values().iterator();
        while (iterator.hasNext())
        {
            PropertyServiceProperty property = (PropertyServiceProperty) iterator.next();
            Enablement enablement = create(property);
            list.add(enablement);
        }

        return list;
    }

    public static Property createProperty(Enablement enablement)
    {
        Property property = ((EnablementImpl)enablement).getProperty();
        return property;
    }

    public static PropertyServicePropertyGroup createPropertyGroup(List enablements, int version, String exchangeAcronym)
    {
        PropertyServicePropertyGroup group;

        group = PropertyFactory.createPropertyGroup(PropertyCategoryTypes.USER_ENABLEMENT, exchangeAcronym);
        group.setVersion(version);

        for (int i=0;i<enablements.size();i++)
        {
            EnablementImpl enablement = (EnablementImpl) enablements.get(i);
            Property property = enablement.getProperty();
            group.addProperty(property);
        }

        return group;
    }

    /**
     * @return the key to be used for looking up and storing exchange-acronym wide properties
     * @since Single Acronym scrum April 14, 2005 - Shawn Khosravani
     */
    public static String getUserEnablementsKey(UserAccountModel user)
    {
        ExchangeAcronym exchangeAcronym = user.getExchangeAcronym();
        Object[] elements = {exchangeAcronym.getExchange(), exchangeAcronym.getAcronym()};
        return BasicPropertyParser.buildCompoundString(elements);
    }

    public static String getMDXKey(UserAccountModel user)
    {
        ExchangeAcronym exchangeAcronym = user.getExchangeAcronym();
        Object[] elements = {exchangeAcronym.getExchange(), exchangeAcronym.getAcronym(), MDX_PROPERTY_KEY};
        return BasicPropertyParser.buildCompoundString(elements);
    }
    
    public static String getTradingFirmKey(UserAccountModel user)
    {
        ExchangeAcronym exchangeAcronym = user.getExchangeAcronym();
        Object[] elements = {exchangeAcronym.getExchange(), exchangeAcronym.getAcronym(), TRADINGFIRM_PROPERTY_KEY};
        return BasicPropertyParser.buildCompoundString(elements);
    }

    public static PropertyServicePropertyGroup createMDXPropertyGroup(UserAccountModel user, boolean mdxEnabled)
    {
        PropertyServicePropertyGroup propGroup = new PropertyServicePropertyGroupImpl(PropertyCategoryTypes.USER_ENABLEMENT, getMDXKey(user));
        propGroup.addProperty(createMDXProperty(mdxEnabled));
        return propGroup;
    }

    public static PropertyServicePropertyGroup createTradingFirmPropertyGroup(UserAccountModel user, boolean tradingFirmEnabled)
    {
        PropertyServicePropertyGroup propGroup = new PropertyServicePropertyGroupImpl(PropertyCategoryTypes.USER_ENABLEMENT, getTradingFirmKey(user));
        propGroup.addProperty(createTradingFirmProperty(tradingFirmEnabled));
        return propGroup;
    }

    /**
     * @return the key to be used for looking up and storing "test class only" property
     * @since Single Acronym scrum - April 22, 2005 - Shawn Khosravani
     */
    public static String getTestClassOnlyKey(UserAccountModel user)
    {
        ExchangeAcronym exchangeAcronym = user.getExchangeAcronym();
        Object[] elements = {exchangeAcronym.getExchange(), exchangeAcronym.getAcronym(), TEST_CLASS_PROPERTY_KEY};
        return BasicPropertyParser.buildCompoundString(elements);
    }

    public static PropertyServicePropertyGroup createTestClassPropertyGroup(UserAccountModel user, boolean testClassOnly)
    {
        PropertyServicePropertyGroup propGroup =
                new PropertyServicePropertyGroupImpl(PropertyCategoryTypes.USER_ENABLEMENT, getTestClassOnlyKey(user));
        propGroup.addProperty(createTestClassProperty(testClassOnly));
        return propGroup;
    }

    /**
     * @param mdxEnabled: boolean - the enabled state of MDX property to build a property with
     * @return PropertyServiceProperty containing the mdxEnabled setting as its value, and keyed by the MDX_PROPERTY_KEY.
     */
    public static PropertyServiceProperty createMDXProperty(boolean mdxEnabled)
    {
        PropertyStruct prop = new PropertyStruct(MDX_PROPERTY_KEY, Boolean.toString(mdxEnabled));
        return new PropertyServicePropertyImpl(prop);
    }

    public static PropertyServiceProperty createTradingFirmProperty(boolean mdxEnabled)
    {
        PropertyStruct prop = new PropertyStruct(TRADINGFIRM_PROPERTY_KEY, Boolean.toString(mdxEnabled));
        return new PropertyServicePropertyImpl(prop);
    }
    
    /**
     * @param testClassOnly: boolean - the enabled state of test class property to build a property with
     * @return PropertyServiceProperty containing the testClassOnly setting as its value, and keyed by the "test class"
     *         key
     * @since Single Acronym scrum - April 22, 2005 - Shawn Khosravani
     */
    public static PropertyServiceProperty createTestClassProperty(boolean testClassOnly)
    {
        PropertyStruct prop = new PropertyStruct(TEST_CLASS_PROPERTY_KEY, Boolean.toString(testClassOnly));
        return new PropertyServicePropertyImpl(prop);
    }

    /**
     * @param propGroup: PropertyServicePropertyGroup - property group to look up the "test class" property in
     * @return value of the "test class" property located in the passed propGroup
     * @since Single Acronym scrum - April 22, 2005 - Shawn Khosravani
     */
    public static String getMDXPropertyValue(PropertyServicePropertyGroup propGroup)
    {
        Property prop = propGroup.getProperty(MDX_PROPERTY_KEY);
        return prop == null ? Boolean.toString(false) : prop.getValue();
    }
    
    public static String getTradingFirmPropertyValue(PropertyServicePropertyGroup propGroup)
    {
        Property prop = propGroup.getProperty(TRADINGFIRM_PROPERTY_KEY);
        return prop == null ? Boolean.toString(false) : prop.getValue();
    }

    /**
     * @param propGroup: PropertyServicePropertyGroup - property group to look up the "test class" property in
     * @return value of the "test class" property located in the passed propGroup
     * @since Single Acronym scrum - April 22, 2005 - Shawn Khosravani
     */
    public static String getTestClassPropertyValue(PropertyServicePropertyGroup propGroup)
    {
        Property prop = propGroup.getProperty(TEST_CLASS_PROPERTY_KEY);
        return prop == null ? Boolean.toString(false) : prop.getValue();
    }

    /**
     * @return the currently saved MDX property group, or null if it fails
     */
    public static PropertyServicePropertyGroup getMDXPropertyGroup(UserAccountModel user)
            throws CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        PropertyServicePropertyGroup propGroup =
                PropertyServiceFacadeHome.find().getPropertyGroup(PropertyCategoryTypes.USER_ENABLEMENT,
                                                                  getMDXKey(user));
        return new PropertyServicePropertyGroupImpl(propGroup.getStruct());
    }
    
    public static PropertyServicePropertyGroup getTradingFirmPropertyGroup(UserAccountModel user)
            throws CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        PropertyServicePropertyGroup propGroup =
            PropertyServiceFacadeHome.find().getPropertyGroup(PropertyCategoryTypes.USER_ENABLEMENT,
                                                          getTradingFirmKey(user));
        return new PropertyServicePropertyGroupImpl(propGroup.getStruct());
    }
    

    /**
     * @return the currently saved "test class" property group, or null if it fails
     * @since Single Acronym scrum - April 22, 2005 - Shawn Khosravani
     */
    public static PropertyServicePropertyGroup getTestClassPropertyGroup(UserAccountModel user)
            throws CommunicationException, AuthorizationException, SystemException, NotFoundException
    {
        PropertyServicePropertyGroup propGroup =
                PropertyServiceFacadeHome.find().getPropertyGroup(PropertyCategoryTypes.USER_ENABLEMENT,
                                                                  getTestClassOnlyKey(user));
        return new PropertyServicePropertyGroupImpl(propGroup.getStruct());
    }

    /**
     * Make a copy of the <code>Enablement</code> array so that it can be copied to or from another acronym
     *
     * @since June 30, 2005 - Single Acronym
     * @since April 7, 2006 - moved here from UserEnablementsPanel
     */
    public static Enablement[] cloneEnablements(Enablement[] enablementsToCopy)
    {
        Enablement[] clonedEnablements = new Enablement[enablementsToCopy.length];
        for (int i = 0; i < enablementsToCopy.length; ++i)
        {
            clonedEnablements[i] = cloneEnablement(enablementsToCopy[i]);
        }
        return clonedEnablements;
    }

    public static Enablement cloneEnablement(Enablement enablement)
    {
        return EnablementFactory.create(enablement.getSessionProductClass(), enablement.getOperationType(), enablement.isEnabled());
    }

}
