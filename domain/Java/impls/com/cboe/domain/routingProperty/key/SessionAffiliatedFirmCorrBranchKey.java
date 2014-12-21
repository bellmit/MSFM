package com.cboe.domain.routingProperty.key;

import java.util.*;

import com.cboe.exceptions.DataValidationException;

import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.common.AffiliatedFirmAcronym;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.AffiliatedFirmAcronymImpl;

public class SessionAffiliatedFirmCorrBranchKey  extends AbstractBasePropertyKey
{
    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    public static final String BRANCH_PROPERTY_NAME = "branch";
    // public static final String AFFILIATED_FIRM_PROPERTY_NAME = "affiliatedFirm";
    public static final String AFFILIATED_FIRM_PROPERTY_NAME = "affiliatedFirmAcronym";

    private static final int CORRESPONDENT_PROPERTY_KEY_POSITION = 0;
    private static final int BRANCH_PROPERTY_KEY_POSITION        = 1;
   
    protected String correspondent;   
    protected String branch;

    public SessionAffiliatedFirmCorrBranchKey(BasePropertyType type)
    {
        super(type);
        correspondent = "";
        branch = "";
    }

    public SessionAffiliatedFirmCorrBranchKey(String propertyName, String sessionName, String affiliatedFirmAcronym, String exchangeAcronym,
                                 String correspondent, String branch)
    {
        super(propertyName, sessionName, affiliatedFirmAcronym, exchangeAcronym);
        this.correspondent  = correspondent;
        this.branch = branch;
    }

    public SessionAffiliatedFirmCorrBranchKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
    	SessionAffiliatedFirmCorrBranchKey newKey =  (SessionAffiliatedFirmCorrBranchKey) super.clone();
    	newKey.setCorrespondent(getCorrespondent());
        newKey.setBranch(getBranch());
        newKey.setAffiliatedFirm(getAffiliatedFirm());
        return newKey;
    }

    public String getCorrespondent()
    {
        return correspondent;
    }

    public void setCorrespondent(String correspondent)
    {
        this.correspondent = correspondent;
        resetPropertyKey();
    }

    public String getBranch()
    {
        return branch;
    }

    public void setBranch(String branch)
    {
        this.branch = branch;
        resetPropertyKey();
    }

    /**
     * The following get and set methods are for reflection only, i.e. they create a GUI editor component that is
     * labeled as AffiliatedFirm. The underlying data object that is set or retrieved is the firm number, since it
     * is what holds the affiliated firm acronym. Also, when the firm value is updated, we must set the exchange acronym
     * manually, since the editor value is a string representing the firm acronym, not a true firm struct from which
     * the firm exchange could be retrieved.
     *
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
        return new AffiliatedFirmAcronymImpl(getFirmNumber(),getExchangeAcronym());
    }

    public void setAffiliatedFirmAcronym(AffiliatedFirmAcronym firm)
    {
        setFirmNumber(firm.getFirmAcronym());
        setExchangeAcronym(firm.getExchangeAcronym());
    }


    
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME, AFFILIATED_FIRM_PROPERTY_NAME,
                                  CORRESPONDENT_PROPERTY_NAME, BRANCH_PROPERTY_NAME};
        return new ForcedPropertyDescriptorComparator(forcedEntries);
    }


    /**
     * Parses the propertyKey to find the separate key values.
     *
     * Returns the index of the last key value used from the propertyKey's
     * parts (does not count the index of propertyName, which is always the
     * last part of the propertyKey).
     *
     * @param propertyKey
     * @throws DataValidationException
     */
    protected int parsePropertyKey(String propertyKey) throws DataValidationException
    {
        int index = super.parsePropertyKey(propertyKey);

        String[] keyElements = splitPropertyKey(propertyKey);

        this.correspondent = getKeyElement(keyElements, ++index);

        this.branch = getKeyElement(keyElements, ++index);
        
        return index;
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),
                                        getCorrespondent(), getBranch(), getPropertyName()};

        return RoutingKeyHelper.createPropertyKey(propertyKeyElements);
    }

    @Override
    protected int getMaskSize()
    {
        return super.getMaskSize();
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
        int index;
        int parentSize = super.getMaskSize();
        if(keyElement.equalsIgnoreCase(CORRESPONDENT_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + CORRESPONDENT_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(BRANCH_PROPERTY_NAME))
        {
            index = parentSize + BRANCH_PROPERTY_KEY_POSITION;
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
            case CORRESPONDENT_PROPERTY_KEY_POSITION:
                fieldName = CORRESPONDENT_PROPERTY_NAME;
                break;
            case BRANCH_PROPERTY_KEY_POSITION:
                fieldName = BRANCH_PROPERTY_NAME;
                break;
            default:
                fieldName = super.getKeyComponentName(maskIndex);
                break;
        }
        return fieldName;
    }
}
