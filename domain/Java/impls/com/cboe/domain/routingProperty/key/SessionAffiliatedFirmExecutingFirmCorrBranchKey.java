package com.cboe.domain.routingProperty.key;

import java.util.Comparator;

import com.cboe.domain.routingProperty.ForcedPropertyDescriptorComparator;
import com.cboe.domain.routingProperty.common.AffiliatedFirmAcronymImpl;
import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.interfaces.domain.routingProperty.BasePropertyType;
import com.cboe.interfaces.domain.routingProperty.common.AffiliatedFirmAcronym;

public class SessionAffiliatedFirmExecutingFirmCorrBranchKey extends
		AbstractBasePropertyKey {
    public static final String CORRESPONDENT_PROPERTY_NAME = "correspondent";
    public static final String BRANCH_PROPERTY_NAME = "branch";
    // public static final String AFFILIATED_FIRM_PROPERTY_NAME = "affiliatedFirm";
    public static final String AFFILIATED_FIRM_PROPERTY_NAME = "affiliatedFirmAcronym";
    public static final String EXECUTING_FIRM_PROPERTY_NAME = "executingFirm";
    public static final String EXECUTING_EXCHANGE_PROPERTY_NAME = "executingExchange";
    public static final String EXECUTING_FIRM_EXCHANGE_PROPERTY_NAME = "executingFirmAndExchange";// This is for displaying the firm as string value in the gui
    private static final int EXECUTING_EXCHANGE_PROPERTY_KEY_POSITION = 0;
    private static final int EXECUTING_FIRM_PROPERTY_KEY_POSITION = 1;
    private static final int CORRESPONDENT_PROPERTY_KEY_POSITION = 2;
    private static final int BRANCH_PROPERTY_KEY_POSITION        = 3;
   
    protected ExchangeFirmStruct executingExchangeFirmStruct;// This is struct is for executing firm and NOT the affiliated firm
    protected String executingFirmNumber;
    protected String executingExchange;
    protected String correspondent;   
    protected String branch;
    protected String executingExchangeFirm;
    protected FirmStruct myFirmStruct;

    public SessionAffiliatedFirmExecutingFirmCorrBranchKey(BasePropertyType type)
    {
        super(type);
        executingFirmNumber ="";
        executingExchange="";
        correspondent = "";
        branch = "";
    }

    public SessionAffiliatedFirmExecutingFirmCorrBranchKey(String propertyName, String sessionName, String affiliatedFirmAcronym, String exchangeAcronym,
                                 String executingFirm,String executingExchange,String correspondent, String branch)
    {
        super(propertyName, sessionName, affiliatedFirmAcronym, exchangeAcronym);
        this.correspondent  = correspondent;
        this.branch = branch;
        this.executingFirmNumber = executingFirm;
        this.executingExchange = executingExchange;

        // added for GUI
        setExecutingFirm(new FirmStruct(0,0,new ExchangeFirmStruct(this.executingExchange, this.executingFirmNumber),"","",true,true,null,0));
    }

    public SessionAffiliatedFirmExecutingFirmCorrBranchKey(String propertyKey) throws DataValidationException
    {
        super(propertyKey);
    }

    public Object clone() throws CloneNotSupportedException
    {
    	SessionAffiliatedFirmExecutingFirmCorrBranchKey newKey =  (SessionAffiliatedFirmExecutingFirmCorrBranchKey) super.clone();
    	newKey.setCorrespondent(getCorrespondent());
        newKey.setBranch(getBranch());
        newKey.setAffiliatedFirm(getAffiliatedFirm());
        newKey.setExecutingFirmNumber(getExecutingFirmNumber());
        newKey.setExecutingExchange(getExecutingExchange());
        // added for GUI 
        newKey.setExecutingFirm(getExecutingFirm());
        return newKey;
    }

    public String getExecutingExchange() 
    {
		return executingExchange;
	}

	public void setExecutingExchange(String executingExchange) 
	{
		this.executingExchange = executingExchange;
		resetPropertyKey();
	}

	public String getExecutingFirmNumber() 
	{
		return executingFirmNumber;
	}

	public void setExecutingFirmNumber(String executingFirm) 
	{
		this.executingFirmNumber = executingFirm;
		resetPropertyKey();
	}
	
	public String getExecutingFirmAndExchange()
	{
		this.executingExchangeFirm = this.executingExchange+"."+this.executingFirmNumber;
		return this.executingExchange+"."+this.executingFirmNumber;
	}
	
	public void setExecutingFirmAndExchange(String executingExchangeFirm)
	{
		this.executingExchangeFirm = executingExchangeFirm;
	}
	
    public ExchangeFirmStruct getExecutingExchangeFirmStruct()
    {
        if(executingExchangeFirmStruct== null)
        {
        	executingExchangeFirmStruct = new ExchangeFirmStruct(getExecutingExchange(), getExecutingFirmNumber());
        }
        return executingExchangeFirmStruct;
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
        return new AffiliatedFirmAcronymImpl(getFirmNumber(), getExchangeAcronym());
    }

    public void setAffiliatedFirmAcronym(AffiliatedFirmAcronym firm)
    {
        setFirmNumber(firm.getFirmAcronym());
        setExchangeAcronym(firm.getExchangeAcronym());
    }

    public FirmStruct getExecutingFirm()
    {
        return myFirmStruct;
    }
    
    public void setExecutingFirm(FirmStruct firm)
    {
        this.myFirmStruct = firm;
        setExecutingFirmNumber(firm.firmNumber.firmNumber);
        setExecutingExchange(firm.firmNumber.exchange);
    }

    
    public Comparator getPropertyDescriptorSortComparator()
    {
        String[] forcedEntries = {TRADING_SESSION_PROPERTY_NAME, AFFILIATED_FIRM_PROPERTY_NAME,EXECUTING_FIRM_PROPERTY_NAME,
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
        index++;
        String[] keyElements = splitPropertyKey(propertyKey);
        this.executingFirmNumber = getKeyElement(keyElements, (index+EXECUTING_FIRM_PROPERTY_KEY_POSITION));
        this.executingExchange = getKeyElement(keyElements,(index+EXECUTING_EXCHANGE_PROPERTY_KEY_POSITION));
        this.correspondent = getKeyElement(keyElements, (index+CORRESPONDENT_PROPERTY_KEY_POSITION));
        this.branch = getKeyElement(keyElements, (index+BRANCH_PROPERTY_KEY_POSITION));
        
        //added for GUI
        setExecutingFirm(new FirmStruct(0,0,new ExchangeFirmStruct(this.executingExchange, this.executingFirmNumber),"","",true,true,null,0));
        return (index+BRANCH_PROPERTY_KEY_POSITION);
    }

    protected String createPropertyKey()
    {
        Object[] propertyKeyElements = {createBasePropertyKey(),getExecutingExchange(),getExecutingFirmNumber(),
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
        if(keyElement.equalsIgnoreCase(EXECUTING_FIRM_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + EXECUTING_FIRM_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(EXECUTING_EXCHANGE_PROPERTY_NAME))
        {
            //noinspection PointlessArithmeticExpression
            index = parentSize + EXECUTING_EXCHANGE_PROPERTY_KEY_POSITION;
        }
        else if(keyElement.equalsIgnoreCase(CORRESPONDENT_PROPERTY_NAME))
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
	        case EXECUTING_FIRM_PROPERTY_KEY_POSITION:
	        case EXECUTING_EXCHANGE_PROPERTY_KEY_POSITION:
	            fieldName = EXECUTING_FIRM_PROPERTY_NAME;
	            break;
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
