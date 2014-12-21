// -----------------------------------------------------------------------------------
// Source file: BustTradeImpl.java
//
// PACKAGE: com.cboe.internalPresentation.trade
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.trade;

import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.trade.BustTradeStruct;
import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.internalPresentation.trade.BustTradeModel;
import com.cboe.interfaces.internalPresentation.trade.AtomicTrade;
import com.cboe.interfaces.internalPresentation.trade.BustTrade;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.util.CBOEIdImpl;

import com.cboe.domain.util.TradeReportStructBuilder;

public class BustTradeImpl extends AbstractMutableBusinessModel implements BustTradeModel
{
    private BustTradeStruct struct;
    private AtomicTrade atomicTrade;
    private CBOEId atomicTradeId;
    private String reason;

    protected BustTradeImpl()
    {
        super();
    }

    public BustTradeImpl(BustTradeStruct struct)
    {
        this();
        setBustTradeStruct(struct);
    }

    public BustTradeImpl(BustTradeStruct struct, AtomicTrade atomicTrade)
    {
        this(struct);
        setAtomicTrade(atomicTrade, false);
    }

    public int hashCode()
    {
        return getAtomicTradeId().hashCode();
    }

    public CBOEId getAtomicTradeId()
    {
        if ( this.atomicTradeId == null)
        {
            if (getAtomicTrade() != null)
            {
                this.atomicTradeId = getAtomicTrade().getAtomicTradeId();
            }
            else
            {
                this.atomicTradeId = new CBOEIdImpl(getBustTradeStruct().atomicTradeId);
            }
        }
        return atomicTradeId;
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);
        if ( !isEqual && obj instanceof BustTrade )
        {
            isEqual = this.getAtomicTradeId().equals(((BustTrade)obj).getAtomicTradeId());
        }
        return isEqual;
    }

    public Object clone() throws CloneNotSupportedException
    {
        BustTradeStruct clonedStruct = TradeReportStructBuilder.cloneBustTradeStruct(getBustTradeStruct());
        if ( getAtomicTrade() != null)
        {
            AtomicTrade clonedAtomicTrade = (AtomicTrade)getAtomicTrade().clone();
            return new BustTradeImpl(clonedStruct,clonedAtomicTrade);
        }
        else
        {
            return new BustTradeImpl(clonedStruct);
        }
    }

    /**
     * Sets the atomic trade for this bust trade
     * @param atomicTrade to set
     */
    public void setAtomicTrade(AtomicTrade atomicTrade)
    {
        setAtomicTrade(atomicTrade, true);
    }

    /**
     * Sets the atomic trade for this bust trade
     * @param atomicTrade to set
     * @param fireEvents boolean flag, indicates if property change events should be fired
     */
    protected void setAtomicTrade(AtomicTrade atomicTrade, boolean fireEvents)
    {
        if ( getAtomicTrade() == null ||
             (getAtomicTrade() != null && !getAtomicTrade().equals(atomicTrade)) )
        {
            AtomicTrade oldAtomicTrade = getAtomicTrade();
            this.atomicTrade = atomicTrade;
            this.atomicTradeId = null;
            checkState(getBustTradeStruct());
            getBustTradeStruct().atomicTradeId = atomicTrade.getAtomicTradeStruct().atomicTradeId;
            if (fireEvents)
            {
                setModified(true);
                firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldAtomicTrade, atomicTrade);
            }
        }
    }

    /**
     * Gets the atomic trade for this bust trade
     */
    public AtomicTrade getAtomicTrade()
    {
        return this.atomicTrade;
    }

    /**
     * Gets the atomic trade id struct for this bust trade
     */
    public CboeIdStruct getAtomicTradeIdStruct()
    {
        checkState(getBustTradeStruct());
        return getBustTradeStruct().atomicTradeId;
    }

    /**
     * Sets the atomic trade id struct for this bust trade
     */
    public void setAtomicTradeIdStruct(CboeIdStruct idStruct)
    {
        checkParam(idStruct, "AtomicTradeIdStruct");
        checkState(getBustTradeStruct());
        CboeIdStruct oldStruct = getBustTradeStruct().atomicTradeId;
        checkState(oldStruct);
        if ( oldStruct != idStruct &&
             (oldStruct.highCboeId != idStruct.highCboeId ||
              oldStruct.lowCboeId != idStruct.lowCboeId ))
        {
            getBustTradeStruct().atomicTradeId = idStruct;
            this.atomicTrade = null; //  ????????????
            this.atomicTradeId = null;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldStruct, idStruct);
        }
    }

    /**
     * Sets the busted quantity
     */
    public void setBustedQuantity(int qty)
    {
        checkState(getBustTradeStruct());
        if ( getBustedQuantity() != qty)
        {
            int oldQty = getBustedQuantity();
            getBustTradeStruct().bustedQuantity = qty;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldQty, qty);
        }
    }

    /**
     * Gets the busted quantity
     */
    public int getBustedQuantity()
    {
        checkState(getBustTradeStruct());
        return getBustTradeStruct().bustedQuantity;
    }

    public String getReason()
    {
        return reason;
    }

    public void setReason(String reason)
    {
        if ( getReason() == null ||
             (getReason() != null && !getReason().equals(reason) ) )
        {
            String oldReason = getReason();
            this.reason = reason;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldReason, reason);
        }
    }

    /**
     * Sets the buyer Reinstate Requested attribute for this bust trade
     */
    public void setBuyerReinstateRequested(boolean reinstateRequested)
    {
        checkState(getBustTradeStruct());
        if ( isBuyerReinstateRequested() != reinstateRequested )
        {
            boolean oldValue = isBuyerReinstateRequested();
            getBustTradeStruct().buyerReinstateRequested = reinstateRequested;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, reinstateRequested);
        }
    }

    /**
     * Gets the buyer Reinstate Requested attribute for this bust trade
     */
    public boolean isBuyerReinstateRequested()
    {
        checkState(getBustTradeStruct());
        return getBustTradeStruct().buyerReinstateRequested;
    }


    /**
     * Sets the seller Reinstate Requested attribute for this bust trade
     */
    public void setSellerReinstateRequested(boolean reinstateRequested)
    {
        checkState(getBustTradeStruct());
        if (isSellerReinstateRequested() != reinstateRequested)
        {
            boolean oldValue = isSellerReinstateRequested();
            getBustTradeStruct().sellerReinstateRequested = reinstateRequested;
            setModified(true);
            firePropertyChange(MutableBusinessModel.DATA_CHANGE_EVENT, oldValue, reinstateRequested);
        }
    }

    /**
     * Gets the seller Reinstate Requested attribute for this bust trade
     */
    public boolean isSellerReinstateRequested()
    {
        checkState(getBustTradeStruct());
        return getBustTradeStruct().sellerReinstateRequested;
    }

    /**
     * Sets the BustTradeStruct wrapped by this object
     */
    public void setBustTradeStruct(BustTradeStruct struct)
    {
        this.struct = struct;
        this.atomicTrade = null;
    }

    /**
     * Gets the BustTradeStruct wrapped by this object
     */
    public BustTradeStruct getBustTradeStruct()
    {
        return this.struct;
    }

}
