package com.cboe.domain.routingProperty.key;

import java.util.Comparator;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.exceptions.DataValidationException;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;

public class SessionFirmAffiliatedFirmCorrBranchKey extends AbstractBasePropertyKey
{
	public static final String AFF_FIRM_PROPERTY_NAME = "affiliatedfirm";
    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    public static final String BRANCH_PROPERTY_NAME = "branch";
    
    // Order of fields in the Key
    public static final int SESSION_KEY_POSITION  = RoutingKeyHelper.SESSION_KEY_POSITION;
    public static final int AFF_FIRM_PROPERTY_NAME_KEY_POSITION = 1; // Affiliated Firm
    public static final int EXCHANGE_KEY_POSITION = 2; // Exchange associated with the Firm.
    public static final int FIRM_KEY_POSITION     = 3; // ExecutingFirm eg., PAX.671
    public static final int CORRESPONDENT_PROPERTY_KEY_POSITION = 4; // Correspondent 
    public static final int BRANCH_PROPERTY_KEY_POSITION        = 5; // Branch Acronym

    protected String affiliatedfirm;
    protected String branch;
    protected String correspondent;
   
    public SessionFirmAffiliatedFirmCorrBranchKey(BasePropertyType type)
    {
        super(type);
        this.affiliatedfirm = "";
        this.correspondent = "";
        this.branch = "";
    }
    
    public SessionFirmAffiliatedFirmCorrBranchKey(String propertyName, String sessionName, String affiliatedFirm,
    								String firmAcronym, String exchangeAcronym, String correspondent, String branch)
    {
        super(propertyName, sessionName, firmAcronym, exchangeAcronym);
        this.affiliatedfirm = firmAcronym;
        this.correspondent = correspondent;
        this.branch = branch;
    }

    public SessionFirmAffiliatedFirmCorrBranchKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
    	SessionFirmAffiliatedFirmCorrBranchKey newKey =  (SessionFirmAffiliatedFirmCorrBranchKey) super.clone();
    	newKey.affiliatedfirm = getAffiliatedFirm();
        newKey.correspondent = getCorrespondent();
        newKey.branch = getBranch();
        return newKey;
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

    public String getCorrespondent()
    {
        return correspondent;
    }

    public void setCorrespondent(String correspondent)
    {
        this.correspondent = correspondent;
        resetPropertyKey();
    }

    public String getAffiliatedFirm() {
		return affiliatedfirm;
	}

	public void setAffiliatedFirm(String affiliatedfirm) {
		this.affiliatedfirm = affiliatedfirm;
	}
	
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME, AFF_FIRM_PROPERTY_NAME, FIRM_PROPERTY_NAME,
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

        this.affiliatedfirm = getKeyElement(keyElements, ++index);
        
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
        return super.getMaskSize() + 2;
    }

    @Override
    public int getMaskIndex(String keyElement)
    {
    	int index;
        if (keyElement.equalsIgnoreCase(TRADING_SESSION_PROPERTY_NAME))
        {
            index = SESSION_KEY_POSITION;
        }
        else if (keyElement.equalsIgnoreCase(AFF_FIRM_PROPERTY_NAME))
        {
            index = AFF_FIRM_PROPERTY_NAME_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(EXCHANGE_NAME))
        {
            index = EXCHANGE_KEY_POSITION;
        }
        else if (keyElement.equalsIgnoreCase(FIRM_PROPERTY_NAME))
        {
            index = FIRM_KEY_POSITION;
        }
        else if (keyElement.equalsIgnoreCase(CORRESPONDENT_PROPERTY_NAME))
        {
            index = CORRESPONDENT_PROPERTY_KEY_POSITION;
        }
        else if (keyElement.equalsIgnoreCase(BRANCH_PROPERTY_NAME))
        {
            index = BRANCH_PROPERTY_KEY_POSITION;
        }
        else
        {
            index = -1;
        }
        return index;
    }

    @Override
    public String getKeyComponentName(int maskIndex)
    {
    	 String fieldName = "Unknown Field Name";
         switch (maskIndex)
         {
             case SESSION_KEY_POSITION:
                 fieldName = TRADING_SESSION_PROPERTY_NAME;
                 break;
             case AFF_FIRM_PROPERTY_NAME_KEY_POSITION:
                 fieldName = AFF_FIRM_PROPERTY_NAME;
                 break;
             case 2:
             case 3:
                 fieldName = FIRM_PROPERTY_NAME;
                 break;
             case CORRESPONDENT_PROPERTY_KEY_POSITION:
                 fieldName = CORRESPONDENT_PROPERTY_NAME;
                 break;
             case BRANCH_PROPERTY_KEY_POSITION:
                 fieldName = BRANCH_PROPERTY_NAME;
                 break;
         }
         return fieldName;
    }
}
