//
// -----------------------------------------------------------------------------------
// Source file: TradeReportImpl.java
//
// PACKAGE: com.cboe.internalPresentation.trade;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.trade;

import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.TradeReportStructV2;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.internalPresentation.trade.AtomicTrade;
import com.cboe.interfaces.internalPresentation.trade.AtomicTradeModel;
import com.cboe.interfaces.internalPresentation.trade.TradeReport;
import com.cboe.interfaces.internalPresentation.trade.TradeReportModel;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.DateImpl;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.util.CBOEIdImpl;

import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.TradeReportStructBuilder;

/**
 * Provides impl for TradeReportModel as wrapper for TradeReportStruct
 */
public class TradeReportImpl extends AbstractMutableBusinessModel implements TradeReportModel
{
    private TradeReportStructV2 tradeReportStruct;
    private Price price = null;;
    private CBOEId tradeId = null;
    private SessionProduct product = null;
    private AtomicTrade[] parties;

    /**
     * Default constructor. Impl does NOT contain a struct
     */
    private TradeReportImpl()
    {
        super();
    }

    /**
     * Default constructor that accepts struct to contain
     * @param struct to contain
     */
    protected TradeReportImpl(TradeReportStructV2 struct)
    {
        this();
        setTradeReportStruct(struct);
    }

    /**
     * Return trade id hashcode
     */
    public int hashCode()
    {
        return getTradeId().hashCode();
    }

    /**
     * Determines if this is equal to obj
     * @param obj to check equality with
     */
    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if ( !isEqual && obj instanceof TradeReport )
        {
            isEqual = this.getTradeId().equals(((TradeReport)obj).getTradeId());
        }
        return isEqual;
    }

    /**
     * Clones this impl with the same instance of the struct
     */
    public Object clone() throws CloneNotSupportedException
    {
        TradeReportStructV2 struct = getTradeReportStruct();
        return new TradeReportImpl(TradeReportStructBuilder.cloneTradeReportStructV2(struct));
    }

    /**
     * Gets the qty for this TradeReport
     */
    public int getQuantity()
    {
        return getTradeReportStruct().tradeReport.quantity;
    }

    /**
     * Sets the qty this TradeReport is for
     * @param quantity to set
     */
    public void setQuantity(int quantity)
    {
        if (getTradeReportStruct().tradeReport.quantity != quantity)
        {
            int oldQuantity = getTradeReportStruct().tradeReport.quantity;
            getTradeReportStruct().tradeReport.quantity = quantity;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldQuantity, quantity);
        }
    }

    /**
     * Gets the Trade ID for this TradeReport
     */
    public CBOEId getTradeId()
    {
        if(tradeId == null)
        {
            checkParam(getTradeIdStruct(), "TradeId");
            tradeId = new CBOEIdImpl(getTradeIdStruct());
        }
        return tradeId;
    }

    /**
     * Sets the TradeId for this TradeReport
     * @param tradeId to set
     */
    public void setTradeId(CBOEId tradeId)
    {
        checkParam(tradeId, "tradeId");
        if(!getTradeId().equals(tradeId))
        {
            CBOEId oldObject = getTradeId();
            setModified(true);
            setTradeIdStruct(tradeId.getStruct(), false);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldObject, tradeId);
        }
    }

    /**
     * Gets the SessionProduct for this TradeReport
     */
    public SessionProduct getSessionProduct() throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        if (product == null)
        {
            checkState(getSessionName());

            if (getSessionName().length() > 0 && getProductKey() > 0 )
            {
                product = APIHome.findProductQueryAPI().getProductByKeyForSession(getSessionName(), getProductKey());
            }
            else
            {
                product = APIHome.findProductQueryAPI().getDefaultSessionProduct();
            }
        }
        return product;
    }

    /**
     * Sets the SessionProduct for this TradeReport
     * @param product this TradeReport should represent
     */
    public void setSessionProduct(SessionProduct product) throws SystemException, CommunicationException, AuthorizationException, DataValidationException, NotFoundException
    {
        checkParam(product, "product");
        if (!getSessionProduct().equals(product))
        {
            SessionProduct oldProduct = getSessionProduct();
            setProductKey(product.getProductKey(), false);
            setSessionName(product.getTradingSessionName(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldProduct, product);
        }
    }

    /**
     * Gets the price for this TradeReport
     */
    public Price getPrice()
    {
        if (price == null)
        {
            checkState(getPriceStruct());
            price = PriceFactory.create(getPriceStruct());
        }
        return price;
    }

    /**
     * Sets the price for this TradeReport
     * @param price to set
     */
    public void setPrice(Price price)
    {
        checkParam(price, "price");
        if (!getPrice().equals(price))
        {
            Price oldPrice = getPrice();
            setPriceStruct(price.toStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldPrice, price);
        }
    }

    /**
     * Gets the trade source
     */
    public String getTradeSource()
    {
        checkState(getTradeReportStruct().tradeReport.tradeSource);
        return getTradeReportStruct().tradeReport.tradeSource;
    }

    /**
     * Sets the trade source
     * @param tradeSource to set to this TradeReport
     */
    public void setTradeSource(String tradeSource)
    {
        checkParam(tradeSource, "tradeSource");
        if (!getTradeSource().equals(tradeSource))
        {
            String oldObject = getTradeSource();
            getTradeReportStruct().tradeReport.tradeSource = tradeSource;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldObject, tradeSource);
        }
    }

    /**
     * Gets the trade type
     */
    public char getTradeType()
    {
        return getTradeReportStruct().tradeReport.tradeType;
    }

    /**
     * Sets the trade type
     * @param tradeType to set to this TradeReport
     */
    public void setTradeType(char tradeType)
    {
        Character oldObject = new Character(getTradeType());
        if ( oldObject.charValue() != tradeType )
        {
            getTradeReportStruct().tradeReport.tradeType = tradeType;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldObject, new Character(tradeType));
        }
    }

    /**
     * Determines if this TradeReport is bustable
     */
    public boolean isBustable()
    {
        return getTradeReportStruct().tradeReport.bustable;
    }

    /**
     * Sets whether this TradeReport is bustable
     */
    public void setBustable(boolean bustable)
    {
        boolean oldValue = isBustable();
        if ( oldValue != bustable )
        {
            getTradeReportStruct().tradeReport.bustable = bustable;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, bustable);
        }
    }

    /**
     * Gets the business date for this TradeReport
     */
    public Date getBusinessDate()
    {
        checkState(getBusinessDateStruct());
        return new DateImpl(getBusinessDateStruct());
    }

    /**
     * Sets the business date for this TradeReport
     * @param businessDate to set
     */
    public void setBusinessDate(Date businessDate)
    {
        checkParam(businessDate, "businessDate");
        Date oldDate = getBusinessDate();
        if ( !oldDate.equals(businessDate) )
        {
            setBusinessDateStruct(businessDate.getDateStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldDate, businessDate);
        }
    }

    /**
     * Gets the time traded for this TradeReport
     */
    public DateTime getTimeTraded()
    {
        checkState(getTimeTradedStruct());
        return new DateTimeImpl(getTimeTradedStruct());
    }

    /**
     * Sets the time traded for this TradeReport
     * @param timeTraded to set
     */
    public void setTimeTraded(DateTime timeTraded)
    {
        checkParam(timeTraded, "timeTraded");
        DateTime oldTime = getTimeTraded();
        if ( !oldTime.equals(timeTraded) )
        {
            setTimeTradedStruct(timeTraded.getDateTimeStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTime, timeTraded);
        }
    }

    /**
     * Sets the parties to this TradeReport
     */
    public void setParties(AtomicTrade[] parties)
    {
        checkParam(parties, "parties");

        AtomicTrade[] oldParties = getParties();
        AtomicTradeStruct [] structs = new AtomicTradeStruct[parties.length];
        for (int i = 0; i < parties.length; i++)
        {
            structs[i] = parties[i].getAtomicTradeStruct();
        }
        setAtomicTradeStructs(structs, false);
        firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldParties, parties);
    }

    /**
     * Gets the parties to this TradeReport
     */
    public AtomicTrade[] getParties()
    {
        return getMutableParties();
    }

    /**
     * Gets the parties as Mutable objects to this TradeReport
     */
    public AtomicTradeModel[] getMutableParties()
    {
        if(parties == null)
        {
            AtomicTradeStruct[] structs = getAtomicTradeStructs();
            parties = new AtomicTradeModel[structs.length];
            for(int i = 0; i < structs.length; i++)
            {
                parties[i] = TradeReportFactory.createAtomicTradeModel(structs[i], this);
            }
        }
        return (AtomicTradeModel[])parties;
    }

    /**
     * Gets the contained struct
     */
    public TradeReportStructV2 getTradeReportStruct()
    {
        return tradeReportStruct;
    }
    
    

    /**
     * Gets the PriceStruct for this TradeReport
     */
    protected PriceStruct getPriceStruct()
    {
        return getTradeReportStruct().tradeReport.price;
    }

    /**
     * Sets the price for this TradeReport
     * @param price to set
     * @param fireEvents True if property chane events should be fired, false otherwise
     */
    protected void setPriceStruct(PriceStruct price, boolean fireEvents)
    {
        checkParam(price, "price");
        PriceStruct oldPrice = getPriceStruct();
        if ( oldPrice.type != price.type ||
             oldPrice.whole != price.whole ||
             oldPrice.fraction != price.fraction)
        {
            getTradeReportStruct().tradeReport.price = price;
            this.price = null;

            setModified(true);
            if (fireEvents)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldPrice, price);
            }
        }
    }

    /**
     * Gets the Session Name for this TradeReport
     */
    protected String getSessionName()
    {
        return getTradeReportStruct().tradeReport.sessionName;
    }

    /**
     * Sets the session name for this TradeReport
     * @param sessionName for the product this TradeReport should represent
     * @param fireEvents True if property chane events should be fired, false otherwise
     */
    protected void setSessionName(String sessionName, boolean fireEvents)
    {
        checkParam(sessionName, "sessionName");

        if(sessionName.length() <= 0)
        {
            throw new IllegalArgumentException("sessionName argument cannot be empty.");
        }
        if (!getSessionName().equals(sessionName))
        {
            String oldObject = getSessionName();
            getTradeReportStruct().tradeReport.sessionName = sessionName;
            this.product = null;

            for(int i=0; i<getTradeReportStruct().tradeReport.parties.length; i++)
            {
                getTradeReportStruct().tradeReport.parties[i].sessionName = sessionName;
            }
            this.parties = null;

            setModified(true);
            if (fireEvents)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldObject, sessionName);
            }
        }
    }

    /**
     * Gets the Product Key for this TradeReport
     */
    public int getProductKey()
    {
        return getTradeReportStruct().tradeReport.productKey;
    }

    /**
     * Sets the product key for this TradeReport
     * @param productKey for the product this TradeReport should represent
     * @param fireEvents True if property chane events should be fired, false otherwise
     */
    protected void setProductKey(int productKey, boolean fireEvents)
    {
        if (getProductKey() != productKey)
        {
            int oldProductKey = getProductKey();
            getTradeReportStruct().tradeReport.productKey = productKey;
            this.product = null;

            setModified(true);
            if (fireEvents)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldProductKey, productKey);
            }
        }
    }

    /**
     * Gets the CboeIdStruct from the TradeReportStruct
     */
    protected CboeIdStruct getTradeIdStruct()
    {
        return getTradeReportStruct().tradeReport.tradeId;
    }

    /**
     * Sets the TradeId for this TradeReport
     * @param tradeId to set
     * @param fireEvents True if property change events should be fired, false otherwise
     */
    protected void setTradeIdStruct(CboeIdStruct tradeId, boolean fireEvents)
    {
        checkParam(tradeId, "tradeId");
        CboeIdStruct oldTradeId = getTradeIdStruct();
        if ( oldTradeId.highCboeId != tradeId.highCboeId ||
             oldTradeId.lowCboeId != tradeId.lowCboeId )
        {
            getTradeReportStruct().tradeReport.tradeId = tradeId;
            this.tradeId = null;

            setModified(true);
            if (fireEvents)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTradeId, tradeId);
            }
        }
    }

    /**
     * Gets the DateStruct representing the businessDate of the TradeReport
     */
    protected DateStruct getBusinessDateStruct()
    {
        return getTradeReportStruct().tradeReport.businessDate;
    }

    /**
     * Sets the business date for this TradeReport
     * @param businessDate to set
     * @param fireEvents True if property change events should be fired, false otherwise
     */
    protected void setBusinessDateStruct(DateStruct businessDate, boolean fireEvents)
    {
        checkParam(businessDate, "businessDate");
        DateStruct oldDate = getBusinessDateStruct();
        if(!StructBuilder.isEqual(oldDate, businessDate))
        {
            getTradeReportStruct().tradeReport.businessDate = businessDate;

            setModified(true);
            if (fireEvents)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldDate, businessDate);
            }
        }
    }

    /**
     * Gets the time traded for this TradeReport
     */
    protected DateTimeStruct getTimeTradedStruct()
    {
        return getTradeReportStruct().tradeReport.timeTraded;
    }

    /**
     * Sets the time traded for this TradeReport
     * @param timeTraded to set
     * @param fireEvents True if property change events should be fired, false otherwise
     */
    protected void setTimeTradedStruct(DateTimeStruct timeTraded, boolean fireEvents)
    {
        checkParam(timeTraded, "timeTraded");
        DateTimeStruct oldTime = getTimeTradedStruct();
        if(!StructBuilder.isEqual(oldTime, timeTraded))
        {
            getTradeReportStruct().tradeReport.timeTraded = timeTraded;

            setModified(true);
            if (fireEvents)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTime, timeTraded);
            }
        }
    }

    /**
     * Gets the AtomicTradeStructs for this TradeReport
     */
    protected AtomicTradeStruct[] getAtomicTradeStructs()
    {
        return getTradeReportStruct().tradeReport.parties;
    }

    /**
     * Sets the AtomicTradeStructs for this TradeReport
     * @param parties to set
     * @param fireEvents True if property change events should be fired, false otherwise
     */
    protected void setAtomicTradeStructs(AtomicTradeStruct[] parties, boolean fireEvents)
    {
        checkParam(parties, "parties");
        AtomicTradeStruct[] oldParties = getTradeReportStruct().tradeReport.parties;
        AtomicTradeStruct[] newParties = new AtomicTradeStruct[parties.length];
        for (int i = 0; i < parties.length; i++)
        {
            newParties[i] = TradeReportStructBuilder.cloneAtomicTradeStruct(parties[i]);
        }
        getTradeReportStruct().tradeReport.parties = newParties;
        this.parties = null;

        setModified(true);
        if (fireEvents)
        {
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldParties, parties);
        }
    }

    /**
     * Sets the TradeReportStruct that this contains
     * @param tradeReportStruct to contain
     */
    protected void setTradeReportStruct(TradeReportStructV2 tradeReportStruct)
    {
        checkParam(tradeReportStruct, "tradeReportStruct");
        this.tradeReportStruct = tradeReportStruct;
        tradeId = null;
        price = null;
        product = null;
        parties = null;
    }

    /* (non-Javadoc)
     * @see com.cboe.interfaces.internalPresentation.trade.TradeReportModel#setAsofIndicator(boolean)
     */
    public void setAsofIndicator(boolean asOfTrade)
    {
        boolean oldValue = isAsofTrade();
        if ( oldValue != asOfTrade )
        {
            getTradeReportStruct().settlementTradeReport.asOfFlag = asOfTrade;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, asOfTrade);
        }
        
    }

    public boolean isAsofTrade()
    {
        return getTradeReportStruct().settlementTradeReport.asOfFlag;
    }
    
    /* (non-Javadoc)
     * @see com.cboe.interfaces.internalPresentation.trade.TradeReportModel#setSettlementDate(com.cboe.interfaces.domain.dateTime.Date)
     */
    public void setSettlementDate(Date settlementDate)
    {
        checkParam(settlementDate, "settlementDate");
        Date oldDate = getSettlementDate();
        if ( !oldDate.equals(settlementDate) )
        {
            setSettlementDateStruct(settlementDate.getDateStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldDate, settlementDate);
        }
        
    }


    public Date getSettlementDate()
    {
        checkState(getSettlementDateStruct());
        return new DateImpl(getSettlementDateStruct());
        
    }

    protected DateStruct getSettlementDateStruct()
    {
        return getTradeReportStruct().settlementTradeReport.settlementDate;
    }

    protected void setSettlementDateStruct(DateStruct settlementDate, boolean fireEvents)
    {
        checkParam(settlementDate, "businessDate");
        DateStruct oldDate = getSettlementDateStruct();
        if(!StructBuilder.isEqual(oldDate, settlementDate))
        {
            getTradeReportStruct().settlementTradeReport.settlementDate = settlementDate;

            setModified(true);
            if (fireEvents)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldDate, settlementDate);
            }
        }
    }


    /* (non-Javadoc)
     * @see com.cboe.interfaces.internalPresentation.trade.TradeReportModel#setTransactionTime(com.cboe.interfaces.domain.dateTime.DateTime)
     */
    public void setTransactionTime(DateTime asofDate)
    {
        checkParam(asofDate, "transactionTime");
        DateTime oldDate = getTransactionTime();
        if ( !oldDate.equals(asofDate) )
        {
            setTransactionDateTimeStruct(asofDate.getDateTimeStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldDate, asofDate);
        }
    }


    public DateTime getTransactionTime()
    {
        checkState(getTransactionDateTimeStruct());
        return new DateTimeImpl(getTransactionDateTimeStruct());
        
    }

    protected DateTimeStruct getTransactionDateTimeStruct()
    {
        return getTradeReportStruct().settlementTradeReport.transactionTime;
    }

    protected void setTransactionDateTimeStruct(DateTimeStruct transactionTime, boolean fireEvents)
    {
        checkParam(transactionTime, "transactionTime");
        DateTimeStruct oldDate = getTransactionDateTimeStruct();
        if(!StructBuilder.isEqual(oldDate, transactionTime))
        {
            getTradeReportStruct().settlementTradeReport.transactionTime = transactionTime;

            setModified(true);
            if (fireEvents)
            {
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldDate, transactionTime);
            }
        }
    }


}