//
// -----------------------------------------------------------------------------------
// Source file: ExtremelyWideQuoteWidthImpl.java
//
// PACKAGE: com.cboe.domain.tradingProperty;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.domain.tradingProperty;

import com.cboe.exceptions.DataValidationException;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.interfaces.domain.property.Property;
import com.cboe.interfaces.domain.tradingProperty.LinkageExchanges;
import com.cboe.interfaces.domain.tradingProperty.AutoLinkPreferredTieExchanges;
import com.cboe.interfaces.domain.tradingProperty.TradingPropertyType;

/**
 * Represents a TradingProperty for an New Linkage Preferred Exchange Tie Property
 */
public class AutoLinkPreferredTieExchangesImpl extends AbstractTradingProperty
        implements AutoLinkPreferredTieExchanges
{
    private TradingPropertyType tradingPropertyType;

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     */
    public AutoLinkPreferredTieExchangesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey)
    {
        super(tradingPropertyType.getName(), sessionName, classKey);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * @param tradingPropertyType type of TradingProperty that this instance represents. Since this TradingProperty
     * implementation class type can represent the data for multiple TradingProperty types, it must be told
     * which type this instance represents.
     * @param sessionName that this TradingProperty is for
     * @param classKey that this TradingProperty is for
     * @param property to initialize with
     */
    public AutoLinkPreferredTieExchangesImpl(TradingPropertyType tradingPropertyType, String sessionName, int classKey,
                                       Property property)
            throws DataValidationException
    {
        super(sessionName, classKey, property);
        this.tradingPropertyType = tradingPropertyType;
    }


    /**
     * 
     * @param tradingPropertyType
     * @param sessionName
     * @param classKey
     * @param exchange
     * @param exchangePreferredSeq
     */
    public AutoLinkPreferredTieExchangesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey,
                                       String exchange, int exchangePreferredSeq)
    {
        this(tradingPropertyType, sessionName, classKey);
        setAutoLinkTieExchangePreferredSeq(exchange, exchangePreferredSeq);
        
    }

    
    public AutoLinkPreferredTieExchangesImpl(TradingPropertyType tradingPropertyType,
                                              String sessionName, int classKey,
                                              int exchangeId, int exchangePreferredSeq)
    {
        this(tradingPropertyType, sessionName, classKey);
        setAutoLinkTieExchangePreferredSeq(exchangeId, exchangePreferredSeq);
    }


    /**
     * 
     * @param tradingPropertyType
     * @param sessionName
     * @param classKey
     * @param value
     */
    public AutoLinkPreferredTieExchangesImpl(TradingPropertyType tradingPropertyType,
                                       String sessionName, int classKey, String value)
    {
        super(tradingPropertyType.getName(), sessionName, classKey, value);
        this.tradingPropertyType = tradingPropertyType;
    }

    /**
     * Compares based on getMinimumBidRange()
     */
    public int compareTo(Object object)
    {
        int result;
        int myValue = getAutoLinkTieExchangePreferredSeq();
        int theirValue = ((AutoLinkPreferredTieExchanges) object).getAutoLinkTieExchangePreferredSeq();
        result = (myValue < theirValue ? -1 : (myValue == theirValue ? 0 : 1));
        return result;
    }

    /**
     * Overriden to return the getMinimumBidRange(), converted to a String, then the hashCode obtained of the
     * resulting String.
     */ 
    public int hashCode()
    {
        return Integer.toString(getAutoLinkTieExchangePreferredSeq()).hashCode();
    }

    /**
     * @see com.cboe.interfaces.domain.tradingProperty.TradingProperty#getTradingPropertyType()
     */
    public TradingPropertyType getTradingPropertyType()
    {
        return tradingPropertyType;
    }

    /**
     * Returns the getMinimumBidRange() as a String.
     */
    public String getPropertyName()
    {
        return Integer.toString(getAutoLinkTieExchangePreferredSeq());
    }

    public int getTieExchangePreferredSeq(int exchangeId)
    {
        return getInteger2();
    }
    
    
    public int getAutoLinkTieExchangePreferredSeq()
    {
        return getInteger2();
    }
    
    /**
     * Sets the super's double1 attribute
     */
    public void setAutoLinkTieExchangePreferredSeq(int exchangeId, int exchangePreferredSeq)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkPreferredTieExchangesImpl>> Saving Exchange Preferred Sequence value = " + exchangePreferredSeq);
        }

        setInteger1(exchangeId);
        setInteger2(exchangePreferredSeq);
    }

    public void setAutoLinkTieExchangePreferredSeq(int exchangePreferredSeq)
    {
        setInteger2(exchangePreferredSeq);
    }

    /**
     * Sets the super's double1 attribute
     */
    public void setAutoLinkTieExchangePreferredSeq(String exchange, int exchangePreferredSeq)
    {
        if(Log.isDebugOn())
        {
            Log.debug("AutoLinkPreferredTieExchangesImpl>> Saving Exchange Preferred Sequence value = " + exchangePreferredSeq);
        }

        int exchangeId = LinkageExchanges.findLinkageExchange(exchange).exchangeId;
        setInteger1(exchangeId);
        setInteger2(exchangePreferredSeq);
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.domain.tradingProperty.AutoLinkPreferredTieExchanges#getTieExchangePreferredSeqForClass()
     */
    public String getTieExchangePreferredSeqForClass()
    {
        // TODO Auto-generated method stub
        return null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.domain.tradingProperty.NewLinkagePreferredTieExchange#getTieExchangeId()
     */
    public int getAutoLinkTieExchangeId()
    {
        return getInteger1();
    }

    public void setAutoLinkTieExchangeId(int exchangeId)
    {
        setInteger1(exchangeId);
    }


    public String getAutoLinkTieExchange()
    {
        return LinkageExchanges.findLinkageExchange(getInteger1()).toString();
    }
}
