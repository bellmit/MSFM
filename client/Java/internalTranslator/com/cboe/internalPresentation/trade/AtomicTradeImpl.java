// -----------------------------------------------------------------------------------
// Source file: AtomicTradeImpl.java
//
// PACKAGE: com.cboe.internalPresentation.trade
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.trade;

import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.trade.AtomicTradeStruct;
import com.cboe.idl.trade.TradeReportStruct;
import com.cboe.interfaces.internalPresentation.trade.*;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.domain.dateTime.DateTime;
import com.cboe.interfaces.presentation.util.CBOEId;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.dateTime.DateTimeImpl;
import com.cboe.presentation.util.CBOEIdImpl;

import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.TradeReportStructBuilder;

public class AtomicTradeImpl extends AbstractMutableBusinessModel implements AtomicTradeModel
{
    //This fields contained in TradeReportStruct and required for Trade Bust
    private int productKey;
    private CBOEId tradeId;

    private AtomicTradeStruct atomicTradeStruct;
    private CBOEId atomicTradeId;
    private DateTime entryTime;
    private DateTime lastUpdateTime;
    private AtomicTradeSide buyerSide;
    private AtomicTradeSide sellerSide;
    private AtomicTradeSideModel buyerSideModel;
    private AtomicTradeSideModel sellerSideModel;

    protected AtomicTradeImpl()
    {
        super();
    }

    public AtomicTradeImpl(AtomicTradeStruct struct)
    {
        this();
        setAtomicTradeStruct(struct);
    }

    public AtomicTradeImpl(AtomicTradeStruct struct, TradeReport tradeReport)
    {
        this();
        setAtomicTradeStruct(struct);
        setTradeId(tradeReport.getTradeId());
        setProductKey(tradeReport.getProductKey());
    }

    public AtomicTradeImpl(AtomicTradeStruct struct, TradeReportStruct tradeReportStruct)
    {
        this();
        setAtomicTradeStruct(struct);
        setTradeId(new CBOEIdImpl(tradeReportStruct.tradeId));
        setProductKey(tradeReportStruct.productKey);
    }

    public AtomicTradeImpl(AtomicTradeStruct struct, CBOEId tradeId, int productKey)
    {
        this();
        setAtomicTradeStruct(struct);
        setTradeId(tradeId);
        setProductKey(productKey);
    }

    public int hashCode()
    {
        return getAtomicTradeId().hashCode();
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if ( !isEqual && obj instanceof AtomicTrade )
        {
            isEqual = this.getAtomicTradeId().equals(((AtomicTrade)obj).getAtomicTradeId());
        }
        return isEqual;
    }

    public Object clone() throws CloneNotSupportedException
    {
        AtomicTradeImpl clonedObject;
        AtomicTradeStruct clonedStruct = TradeReportStructBuilder.cloneAtomicTradeStruct(getAtomicTradeStruct());
        if ( getTradeId() != null )
        {
            CBOEId clonedTradeId = (CBOEId)getTradeId().clone();
            clonedObject = new AtomicTradeImpl(clonedStruct, clonedTradeId, getProductKey());
        }
        else
        {
            clonedObject = new AtomicTradeImpl(clonedStruct);
        }
        return clonedObject;
    }

    public int getProductKey()
    {
        return productKey;
    }

    public void setProductKey(int key)
    {
        if ( getProductKey() != key)
        {
            int oldKey = getProductKey();
            this.productKey = key;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldKey, key);
        }
    }

    public CBOEId getTradeId()
    {
        return tradeId;
    }

    public void setTradeId(CBOEId tradeId)
    {
        checkState(getAtomicTradeStruct());
        checkParam(tradeId, "tradeID");
        if ( getTradeId() == null || (getTradeId() != null && !getTradeId().equals(tradeId)) )
        {
            CBOEId oldTradeId = getTradeId();
            this.tradeId = tradeId;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTradeId, tradeId);
        }
    }


    public void setAtomicTradeId(CBOEId tradeId)
    {
        checkState(getAtomicTradeStruct());
        checkParam(tradeId, "atomicTradeID");
        if (!getAtomicTradeId().equals(tradeId))
        {
            CBOEId oldObject = getAtomicTradeId();
            this.atomicTradeId = tradeId;
            setModified(true);
            setAtomicTradeIdStruct(tradeId.getStruct(), false);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldObject, tradeId);
        }
    }

    public CBOEId getAtomicTradeId()
    {
        if (atomicTradeId == null)
        {
            atomicTradeId = new CBOEIdImpl(getAtomicTradeIdStruct());
        }
        return atomicTradeId;
    }

    protected CboeIdStruct getAtomicTradeIdStruct()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().atomicTradeId;
    }

    protected void setAtomicTradeIdStruct(CboeIdStruct tradeId, boolean fireEvents)
    {
        checkState(getAtomicTradeStruct());
        CboeIdStruct oldTradeId = getAtomicTradeIdStruct();
        if ( !StructBuilder.isEqual(oldTradeId, tradeId) )
        {
            getAtomicTradeStruct().atomicTradeId = tradeId;
            if (fireEvents)
            {
                this.atomicTradeId = null;
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTradeId, tradeId);
            }
        }
    }

    public void setMatchedSequenceNumber(int seqNum)
    {
        checkState(getAtomicTradeStruct());
        int oldNumber = getAtomicTradeStruct().matchedSequenceNumber;
        if ( oldNumber != seqNum )
        {
            getAtomicTradeStruct().matchedSequenceNumber = seqNum;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldNumber, seqNum);
        }
    }

    public int getMatchedSequenceNumber()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().matchedSequenceNumber;
    }

    public void setActive(boolean flag)
    {
        checkState(getAtomicTradeStruct());
        boolean oldValue = isActive();
        if (oldValue != flag)
        {
            getAtomicTradeStruct().active = flag;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, flag);
        }
    }

    public boolean isActive()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().active;
    }

    public void setEntryTime(DateTime entryTime)
    {
        checkState(getAtomicTradeStruct());
        checkParam(entryTime, "entryTime");
        DateTime oldTime = getEntryTime();
        if ( !oldTime.equals(entryTime) )
        {
            this.entryTime = entryTime;
            setEntryTimeStruct(entryTime.getDateTimeStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTime, entryTime);
        }

    }

    public DateTime getEntryTime()
    {
        if (entryTime == null)
        {
            if (getEntryTimeStruct() != null)
            {
                entryTime = new DateTimeImpl(getEntryTimeStruct());
            }
            else
            {
                throw new IllegalStateException("TimeTraded DateTimeStruct in TradeReportStruct can not be NULL");
            }
        }
        return entryTime;
    }

    protected void setEntryTimeStruct(DateTimeStruct entryTime, boolean fireEvents)
    {
        checkState(getAtomicTradeStruct());
        checkParam(entryTime, "entryTime");
        DateTimeStruct oldTime = getEntryTimeStruct();
        if ( !StructBuilder.isEqual(oldTime, entryTime) )
        {
            getAtomicTradeStruct().entryTime = entryTime;
            if (fireEvents)
            {
                this.entryTime = null;
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTime, entryTime);
            }
        }
    }

    protected DateTimeStruct getEntryTimeStruct()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().entryTime;
    }

    public void setEntryType(char type)
    {
        checkState(getAtomicTradeStruct());
        Character oldObject = new Character(getEntryType());
        if ( oldObject.charValue() != type )
        {
            getAtomicTradeStruct().entryType = type;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldObject, new Character(type));
        }
    }

    public char getEntryType()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().entryType;
    }

    public void setLastUpdateTime(DateTime lastUpdateTime)
    {
        checkState(getAtomicTradeStruct());
        checkParam(lastUpdateTime, "lastUpdateTime");
        DateTime oldTime = getLastUpdateTime();
        if ( !oldTime.equals(lastUpdateTime) )
        {
            this.lastUpdateTime = lastUpdateTime;
            setLastUpdateTimeStruct(lastUpdateTime.getDateTimeStruct(), false);
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTime, lastUpdateTime);
        }
    }

    public DateTime getLastUpdateTime()
    {
        if (lastUpdateTime == null)
        {
            if (getLastUpdateTimeStruct() != null)
            {
                lastUpdateTime = new DateTimeImpl(getLastUpdateTimeStruct());
            }
            else
            {
                throw new IllegalStateException("LastUpdateTime DateTimeStruct in AtomicTradeStruct can not be NULL");
            }
        }
        return lastUpdateTime;
    }
    protected void setLastUpdateTimeStruct(DateTimeStruct time, boolean fireEvents)
    {
        checkState(getAtomicTradeStruct());
        checkParam(time, "time");
        DateTimeStruct oldTime = getLastUpdateTimeStruct();
        if ( !StructBuilder.isEqual(oldTime, time) )
        {
            getAtomicTradeStruct().lastUpdateTime = time;
            if (fireEvents)
            {
                this.lastUpdateTime = null;
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldTime, time);
            }
        }
    }

    protected DateTimeStruct getLastUpdateTimeStruct()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().lastUpdateTime;
    }

    public void setLastEntryType(char lastEntryType)
    {
        checkState(getAtomicTradeStruct());
        if (getLastEntryType() != lastEntryType)
        {
            Character oldValue = new Character(getLastEntryType());
            getAtomicTradeStruct().lastEntryType = lastEntryType;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, new Character(lastEntryType));
        }
    }

    public char getLastEntryType()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().lastEntryType;
    }

    public void setQuantity(int quantity)
    {
        checkState(getAtomicTradeStruct());
        if (getQuantity() != quantity)
        {
            int oldValue = getQuantity();
            getAtomicTradeStruct().quantity = quantity;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, quantity);
        }
    }

    public int getQuantity()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().quantity;
    }

    public void setBuyerSide(AtomicTradeSide side)
    {
    }

    public AtomicTradeSideModel getMutableBuyerSide()
    {
        if (buyerSideModel == null)
        {
            buyerSideModel = TradeReportFactory.createAtomicTradeBuySideModel(getAtomicTradeStruct());
        }
        return buyerSideModel;
    }

    public AtomicTradeSide getBuyerSide()
    {
        if (buyerSide == null)
        {
            buyerSide = TradeReportFactory.createAtomicTradeBuySide(getAtomicTradeStruct());
        }
        return buyerSide;
    }

    public void setSellerSide(AtomicTradeSide side)
    {
    }

    public AtomicTradeSide getSellerSide()
    {
        if (sellerSide == null)
        {
            sellerSide = TradeReportFactory.createAtomicTradeSellSide(getAtomicTradeStruct());
        }
        return sellerSide;
    }

    public AtomicTradeSideModel getMutableSellerSide()
    {
        if (sellerSideModel == null)
        {
            sellerSideModel = TradeReportFactory.createAtomicTradeSellSideModel(getAtomicTradeStruct());
        }
        return sellerSideModel;
    }

    public void setSessionName(String sessionName)
    {
        checkState(getAtomicTradeStruct());
        checkParam(sessionName, "sessionName");
        if (!getSessionName().equals(sessionName))
        {
            String oldValue = getSessionName();
            getAtomicTradeStruct().sessionName = sessionName;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, sessionName);
        }
    }

    public String getSessionName()
    {
        checkState(getAtomicTradeStruct());
        return getAtomicTradeStruct().sessionName;
    }

    public AtomicTradeStruct getAtomicTradeStruct()
    {
        return this.atomicTradeStruct;
    }

    protected void setAtomicTradeStruct(AtomicTradeStruct struct)
    {
        this.atomicTradeStruct = struct;
        entryTime = null;
        lastUpdateTime = null;
        buyerSide = null;
        sellerSide = null;
        buyerSideModel = null;
        sellerSideModel = null;
    }

}
