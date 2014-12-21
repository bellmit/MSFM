package com.cboe.domain.routingProperty.key;

import java.util.*;

import com.cboe.idl.cmiErrorCodes.DataValidationCodes;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.common.AffiliatedFirmAcronym;

import com.cboe.util.ExceptionBuilder;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.AffiliatedFirmAcronymImpl;
import com.cboe.domain.routingProperty.common.SimpleComplexProductClass;
import com.cboe.domain.routingProperty.common.TradingSessionName;

public class SessionAffiliatedFirmClassKey extends AbstractBasePropertyKey
{
    public static final String PRODUCT_CLASS_PROPERTY_NAME = "simpleComplexProductClass";
    // public static final String AFFILIATED_FIRM_PROPERTY_NAME = "affiliatedFirm";
    public static final String AFFILIATED_FIRM_PROPERTY_NAME = "affiliatedFirmAcronym";
    private static final int PRODUCT_CLASS_PROPERTY_KEY_POSITION = 1;

    protected SimpleComplexProductClass productClass;

    public SessionAffiliatedFirmClassKey(BasePropertyType type)
    {
        super(type);
    }

    public SessionAffiliatedFirmClassKey(String propertyName, String sessionName, String exchangeAcronym, String affiliatedFirmAcronym,
                               int classKey)
    {
        /**
         * The affiliated firm acronym becomes the firm number in the super class. It cannot be blank because that would
         * cause this property to be treated as a session property, not firm.
         */
        super(propertyName, sessionName, affiliatedFirmAcronym, exchangeAcronym);
        productClass = new SimpleComplexProductClass(sessionName, classKey);
    }

    public SessionAffiliatedFirmClassKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public int getClassKey()
    {
        return productClass.getClassKey();
    }

    public Object clone() throws CloneNotSupportedException
    {
    	SessionAffiliatedFirmClassKey newKey = (SessionAffiliatedFirmClassKey) super.clone();
        newKey.productClass = new SimpleComplexProductClass(getTradingSession().sessionName, getClassKey());
        return newKey;
    }

    public void setClassKey(int classKey)
    {
        productClass = new SimpleComplexProductClass(getTradingSession().sessionName, classKey);
        resetPropertyKey();
    }

    public void setSimpleComplexProductClass(SimpleComplexProductClass productClass)
    {
        setTradingSession(new TradingSessionName(productClass.getTradingSession()));
        this.productClass = productClass;
        resetPropertyKey();
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {getSessionName(), getExchangeAcronym(), getAffiliatedFirm(),
                                        getClassKey(), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    /**
     * The following get and set methods are for reflection only, i.e. they create a GUI editor component that is
     * labeled as AffiliatedFirm. The underlying data object that is set or retrieved is the firm number, since it
     * is what holds the affiliated firm acronym. Also, when the firm value is updated, we must set the exchange acronym
     * manually, since the editor value is a string representing the firm acronym, not a true firm struct from which
     * the firm exchange could be retrieved as is the case with the SessionFirmClassKey parent class.
     *
     * @return
     */
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
        return new AffiliatedFirmAcronymImpl(getFirmNumber(), getExchangeAcronym());
    }

    public void setAffiliatedFirmAcronym(AffiliatedFirmAcronym firm)
    {
        setFirmNumber(firm.getFirmAcronym());
        setExchangeAcronym(firm.getExchangeAcronym());
    }

    /**
     * Parses the propertyKey to find the separate key values.
     *
     * Returns the index of the last key value used from the propertyKey's
     * parts (does not count the index of propertyName, which is always the
     * last part of the propertyKey).
     *
     * @param propertyKey string representation of property key
     * @throws DataValidationException
     */
    protected int parsePropertyKey(String propertyKey) throws DataValidationException
    {
        int index;
        String[] keyElements = splitPropertyKey(propertyKey);

        index = super.parsePropertyKey(propertyKey);

        /**
         * For this key type, the affiliated firm acronym was passed in as the firm number, so after parsing the key in the
         * parent class, set the firm acronym to what got parsed into the firm number field of parent class.
         */
 
        try
        {
            productClass = new SimpleComplexProductClass(tradingSessionName.sessionName,
                                                              Integer.parseInt(getKeyElement(keyElements, ++index)));
        }
        catch (NumberFormatException e)
        {
            String detailMsg = buildNumberFormatExceptionMessage(propertyKey, "classKey",
                                                                 getKeyElement(keyElements, index));
            throw ExceptionBuilder.dataValidationException(detailMsg, DataValidationCodes.INVALID_PRODUCT_CLASS);
        }

        return index;
    }

    /**
     * Allows the Routing Property to determine the order of the PropertyDescriptors.
     * @return comparator to use for sorting the returned PropertyDescriptors from getPropertyDescriptors().
     */
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {AFFILIATED_FIRM_PROPERTY_NAME, PRODUCT_CLASS_PROPERTY_NAME };
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }

    public SimpleComplexProductClass getSimpleComplexProductClass()
    {
        return productClass;
    }

    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize() + 1;
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int index;
        int parentSize = super.getMaskSize();
        if(keyElement.equalsIgnoreCase(PRODUCT_CLASS_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + PRODUCT_CLASS_PROPERTY_KEY_POSITION;
        }
        else
        {
            index = super.getMaskIndex(keyElement);
        }
        return index;
    }

    @Override
    public String getKeyComponentName(int maskIndex)
    {
        String fieldName;
        switch(maskIndex - super.getMaskSize())
        {
            case PRODUCT_CLASS_PROPERTY_KEY_POSITION:
                fieldName = PRODUCT_CLASS_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
