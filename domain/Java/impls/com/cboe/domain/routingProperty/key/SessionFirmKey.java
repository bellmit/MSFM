package com.cboe.domain.routingProperty.key;
// -----------------------------------------------------------------------------------
// Source file: SessionFirmKey
//
// PACKAGE: com.cboe.domain.firmRoutingProperty.test.key
// 
// Created: Jul 21, 2006 9:38:53 AM
// -----------------------------------------------------------------------------------
// Copyright (c) 1999-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

//todo: remove this class if it's not needed
public class SessionFirmKey extends AbstractBasePropertyKey
{
    private boolean isExecutingFirm;
    private boolean isCorrFirm;
    private boolean isCmtaFirm;
    private boolean shortSaleMarking;

    public SessionFirmKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionFirmKey(String propertyName, String sessionName, String firmAcronym, String exchangeAcronym)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
    }

    public SessionFirmKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        return (SessionFirmKey) super.clone();
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(), getPropertyName()};
        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {FIRM_PROPERTY_NAME, TRADING_SESSION_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public void setShowExecutingFirm(boolean execFirm)
    {
        this.isExecutingFirm = execFirm;
    }

    public void setCorrespondentFirm(boolean corrFirm)
    {
        this.isCorrFirm = corrFirm;
    }

    public void setCMTAFirm(boolean cmtaFirm)
    {
        this.isCmtaFirm = cmtaFirm;
    }
    
    public void setShortSaleMarking(boolean p_shortSaleMarking)
    {
        shortSaleMarking = p_shortSaleMarking;
    }

    public boolean isShowExecutingFirm()
    {
        return this.isExecutingFirm;
    }

    public boolean isCorrespondentFirm()
    {
        return this.isCorrFirm;
    }

    public boolean isCMTAFirm()
    {
        return this.isCmtaFirm;
    }

    public boolean isShortSaleMarking()
    {
        return shortSaleMarking;
    }
}
