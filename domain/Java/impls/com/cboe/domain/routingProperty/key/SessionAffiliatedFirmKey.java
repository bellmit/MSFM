package com.cboe.domain.routingProperty.key;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.common.AffiliatedFirmAcronym;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.AffiliatedFirmAcronymImpl;

public class SessionAffiliatedFirmKey extends AbstractBasePropertyKey
{
    public static final String AFFILIATED_FIRM_PROPERTY_NAME = "affiliatedFirmAcronym";

    public SessionAffiliatedFirmKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionAffiliatedFirmKey(String propertyName, String sessionName, String affiliatedFirmAcronym, String exchangeAcronym)
    {
        super(propertyName, sessionName, affiliatedFirmAcronym, exchangeAcronym);
    }

    public SessionAffiliatedFirmKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
        SessionAffiliatedFirmKey newKey = (SessionAffiliatedFirmKey) super.clone();
        return newKey;
    }

    public String getAffiliatedFirm()
    {
        return getFirmNumber();
    }

    public void setAffiliatedFirm(String acr)
    {
        setFirmNumber(acr);
        setExchangeAcronym("");
    }

    public AffiliatedFirmAcronym getAffiliatedFirmAcronym()
    {
        return new AffiliatedFirmAcronymImpl(getFirmNumber(),getExchangeAcronym());
    }

    public void setAffiliatedFirmAcronym(AffiliatedFirmAcronym firm)
    {
        setFirmNumber(firm.getFirmAcronym());
        setExchangeAcronym(firm.getExchangeAcronym());
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getSessionName(), getExchangeAcronym(), getAffiliatedFirm(), getPropertyName()};
        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {AFFILIATED_FIRM_PROPERTY_NAME, TRADING_SESSION_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }


}
