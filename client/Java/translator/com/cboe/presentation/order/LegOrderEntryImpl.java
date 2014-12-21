//
// -----------------------------------------------------------------------------------
// Source file: LegOrderEntryImpl.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.order.LegOrderEntry;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiOrder.LegOrderEntryStruct;
import com.cboe.idl.cmiOrder.LegOrderEntryStructV2;

import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.user.ExchangeFirmFactory;

import com.cboe.domain.util.PriceFactory;

class LegOrderEntryImpl extends AbstractBusinessModel implements LegOrderEntry
{
    protected Integer productKey;
    protected Price mustUsePrice;
    protected ExchangeFirm clearingFirm;
    protected Character coverage;
    protected Character positionEffect;
    protected Character sellShortIndicator;

    protected LegOrderEntryImpl()
    {
        super();
    }

    public LegOrderEntryImpl(LegOrderEntryStructV2 legOrderEntryStruct)
    {
        this();
        checkParam(legOrderEntryStruct, "LegOrderEntryStructV2");
        setStruct(legOrderEntryStruct);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            if(obj instanceof LegOrderEntry)
            {
                LegOrderEntry castedObject = (LegOrderEntry)obj;
                isEqual = getProductKey().equals(castedObject.getProductKey());
            }
        }
        return isEqual;
    }

    public int hashCode()
    {
        return productKey.intValue();
    }

    public Object clone() throws CloneNotSupportedException
    {
        LegOrderEntryImpl clonedObject = new LegOrderEntryImpl();
        clonedObject.setStruct(this.getStruct());
        return clonedObject;
    }

    public Integer getProductKey()
    {
        return productKey;
    }

    public Price getMustUsePrice()
    {
        return mustUsePrice;
    }

    public ExchangeFirm getClearingFirm()
    {
        return clearingFirm;
    }

    public Character getCoverage()
    {
        return coverage;
    }

    public Character getPositionEffect()
    {
        return positionEffect;
    }

    public Character getSellShortIndicator()
    {
        return sellShortIndicator;
    }
    
    
    /**
     * Gets the underlying struct
     * @return LegOrderEntryStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public LegOrderEntryStructV2 getStruct()
    {
        LegOrderEntryStruct legOrderEntryStruct = new LegOrderEntryStruct();
        legOrderEntryStruct.clearingFirm = getClearingFirm().getExchangeFirmStruct();
        legOrderEntryStruct.coverage = getCoverage();
        legOrderEntryStruct.mustUsePrice = getMustUsePrice().toStruct();
        legOrderEntryStruct.positionEffect = getPositionEffect();
        legOrderEntryStruct.productKey = getProductKey();
        
        LegOrderEntryStructV2 legOrderEntryStructV2 = new LegOrderEntryStructV2();
        legOrderEntryStructV2.legOrderEntry = legOrderEntryStruct;
        legOrderEntryStructV2.side = getSellShortIndicator();
        
        return legOrderEntryStructV2;
    }

    public String toString()
    {
        StringBuffer  stringBuf = new StringBuffer();
        stringBuf.append("LegOrderEntryImpl: product key = " + this.getProductKey());
        stringBuf.append(", mustUsePrice = " + this.getMustUsePrice());
        stringBuf.append(", clearing Firm = " + this.getClearingFirm());
        stringBuf.append(", coverage = " + this.getCoverage());
        stringBuf.append(", position effect = " + this.getPositionEffect());
        stringBuf.append(", sell short indicator = " + this.getSellShortIndicator());
        return stringBuf.toString();
    }

    protected void setStruct(LegOrderEntryStructV2 struct)
    {
        clearingFirm = ExchangeFirmFactory.createExchangeFirm(struct.legOrderEntry.clearingFirm);
        coverage = new Character(struct.legOrderEntry.coverage);
        mustUsePrice = PriceFactory.create(struct.legOrderEntry.mustUsePrice);
        positionEffect = new Character(struct.legOrderEntry.positionEffect);
        productKey = new Integer(struct.legOrderEntry.productKey);
        sellShortIndicator = new Character(struct.side);
    }
}