package com.cboe.domain.routingProperty.key;
// -----------------------------------------------------------------------------------
// Source file: PropertyKeyElementDefinition
//
// PACKAGE: com.cboe.domain.routingProperty.key
//
// Created: Jan 29, 2008
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.beans.IntrospectionException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.BaseProperty;
import com.cboe.interfaces.domain.routingProperty.BasePropertyKey;

import com.cboe.domain.routingProperty.common.IntegerBasePropertyImpl;
import com.cboe.domain.routingProperty.common.StringBasePropertyImpl;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

public class PropertyKeyElementDefinition
{
    public static final String TRADING_SESSION_PROPERTY_NAME = AbstractBasePropertyKey.TRADING_SESSION_PROPERTY_NAME;
    public static final String FIRM_PROPERTY_NAME            = AbstractBasePropertyKey.FIRM_PROPERTY_NAME;
    public static final String POST_PROPERTY_NAME            = SessionFirmPostStationKey.POST_PROPERTY_NAME;
    public static final String STATION_PROPERTY_NAME         = SessionFirmPostStationKey.STATION_PROPERTY_NAME;
    public static final String LEVEL_PROPERTY_NAME           = SessionFirmClassOriginLevelKey.LEVEL_PROPERTY_NAME;
    public static final String BRANCH_PROPERTY_NAME          = SessionFirmPostStationCorrBranchKey.BRANCH_PROPERTY_NAME;
    public static final String PRODUCT_CLASS_PROPERTY_NAME   = SessionClassKey.PRODUCT_CLASS_PROPERTY_NAME;

    private String                    propertyCategory;
    private BasePropertyKey           basePropertyKey;
    private BasePropertyType          basePropertyType;
    private String                    tradingSession;
    private String                    firm;
    private IntegerBasePropertyImpl   post;
    private IntegerBasePropertyImpl   station;
    private IntegerBasePropertyImpl   level;
    private StringBasePropertyImpl    branch;
    private SimpleComplexProductClass productClass;

    public PropertyKeyElementDefinition(BasePropertyKey basePropertyKey, BasePropertyType basePropertyType)
    {
        this.basePropertyKey  = basePropertyKey;
        this.basePropertyType = basePropertyType;
        this.propertyCategory = basePropertyType.getPropertyCategory();
    }

    public String getTradingSession()
    {
        return tradingSession;
    }

    public void setTradingSession(String tradingSession)
    {
        this.tradingSession = tradingSession;
    }

    public String getFirm()
    {
        return firm;
    }

    public void setFirm(String firm)
    {
        this.firm = firm;
    }

    public Integer getPost()
    {
        return getPostImpl().getIntegerValue();
    }

    public void setPost(Integer value)
    {
        getPostImpl().setIntegerValue(value);
    }

    public Integer getStation()
    {
        return getStationImpl().getIntegerValue();
    }

    public void setStation(Integer value)
    {
        getStationImpl().setIntegerValue(value);
    }

    public Integer getLevel()
    {
        return getLevelImpl().getIntegerValue();
    }

    public void setLevel(Integer value)
    {
        getLevelImpl().setIntegerValue(value);
    }

    public String getBranch()
    {
        return getBranchImpl().getStringValue();
    }

    public void setBranch(String value)
    {
        getBranchImpl().setStringValue(value);
    }

    public SimpleComplexProductClass getSimpleComplexProductClass()
    {
        return productClass;
    }

    public void setSimpleComplexProductClass(SimpleComplexProductClass productClass)
    {
        this.productClass = productClass;
    }

    public Object clone() throws CloneNotSupportedException
    {
        PropertyKeyElementDefinition copy = new PropertyKeyElementDefinition(basePropertyKey, basePropertyType);

        copy.tradingSession = tradingSession == null ? null : new String(tradingSession);
        copy.firm           = firm           == null ? null : new String(firm);
        copy.post           = post           == null ? null : (IntegerBasePropertyImpl) post.clone();
        copy.station        = station        == null ? null : (IntegerBasePropertyImpl) station.clone();
        copy.level          = level          == null ? null : (IntegerBasePropertyImpl) level.clone();
        copy.branch         = branch         == null ? null : (StringBasePropertyImpl ) branch.clone();
        copy.productClass   = productClass   == null ? null : new SimpleComplexProductClass(productClass.getTradingSession(),
                                                                                            productClass.getClassKey());
        return copy;
    }

    private IntegerBasePropertyImpl getPostImpl()
    {
        if(post == null)
        {
            post = new IntegerBasePropertyImpl(propertyCategory, POST_PROPERTY_NAME, 
                                               basePropertyKey, basePropertyType);
//                                               new SessionFirmPostStationKey(basePropertyType), basePropertyType);
            initializeDefaultValues(post);
        }
        return post;
    }

    private IntegerBasePropertyImpl getStationImpl()
    {
        if(station == null)
        {
            station = new IntegerBasePropertyImpl(propertyCategory, STATION_PROPERTY_NAME,
                                                  basePropertyKey, basePropertyType);
//                                                  new SessionFirmPostStationKey(basePropertyType), basePropertyType);
            initializeDefaultValues(station);
        }
        return station;
    }

    private IntegerBasePropertyImpl getLevelImpl()
    {
        if(level == null)
        {
            level = new IntegerBasePropertyImpl(propertyCategory, LEVEL_PROPERTY_NAME,
                                                basePropertyKey, basePropertyType);
            initializeDefaultValues(level);
        }
        return level;
    }

    private StringBasePropertyImpl getBranchImpl()
    {
        if(branch == null)
        {
            branch = new StringBasePropertyImpl(propertyCategory, BRANCH_PROPERTY_NAME,
                                                basePropertyKey, basePropertyType);
//                                                new SessionFirmCorrBranchKey(basePropertyType), basePropertyType);
            initializeDefaultValues(branch);
        }
        return branch;
    }

    private void initializeDefaultValues(BaseProperty baseProperty)
    {
        try
        {
            baseProperty.initializeDefaultValues();
        }
        catch(IntrospectionException e)
        {
            Log.exception("Can't get default values for property=" + baseProperty.getPropertyName() + " category=" +
                          propertyCategory + "basePropertyType=" + basePropertyType, e);
        }
    }
}
