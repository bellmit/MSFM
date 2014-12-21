//
// -----------------------------------------------------------------------------------
// Source file: LegOrderDetailImpl.java
//
// PACKAGE: com.cboe.presentation.order;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.order;

import java.util.*;

import com.cboe.idl.cmiConstants.CoverageTypes;
import com.cboe.idl.cmiConstants.PositionEffects;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiOrder.LegOrderDetailStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.presentation.order.LegOrderDetail;
import com.cboe.interfaces.presentation.order.MutableLegOrderDetail;
import com.cboe.interfaces.presentation.user.ExchangeFirm;

import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.user.ExchangeFirmFactory;

import com.cboe.domain.util.PriceFactory;

class LegOrderDetailImpl extends AbstractMutableBusinessModel implements MutableLegOrderDetail
{
    protected Integer productKey;
    protected Price mustUsePrice;
    protected ExchangeFirm clearingFirm;
    protected Character coverage;
    protected Character positionEffect;
    protected Character side;
    protected Integer originalQuantity;
    protected Integer tradedQuantity;
    protected Integer cancelledQuantity;
    protected Integer leavesQuantity;
  

    private static final Comparator myStdComparator = new Comparator()
    {
        public int compare(Object detail1, Object detail2)
        {
            LegOrderDetail castedDetail1 = (LegOrderDetail)detail1;
            LegOrderDetail castedDetail2 = (LegOrderDetail)detail2;

            return castedDetail1.getProductKey().compareTo(castedDetail2.getProductKey());
        }
    };


    public LegOrderDetailImpl(int productKey)
    {
        super();
        setComparator(myStdComparator);
        this.productKey = new Integer(productKey);
        initializeDefaults();
    }

    public LegOrderDetailImpl(LegOrderDetailStruct legOrderDetailStruct)
    {
        super();
        checkParam(legOrderDetailStruct, "LegOrderDetailStruct");
        setComparator(myStdComparator);
        setStruct(legOrderDetailStruct);
    }

    public boolean equals(Object obj)
    {
        boolean isEqual = super.equals(obj);

        if(!isEqual)
        {
            if(obj instanceof LegOrderDetail)
            {
                LegOrderDetail castedObject = (LegOrderDetail)obj;
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
        return new LegOrderDetailImpl(getStruct());
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

    public Character getSide()
    {
        return side;
    }

    public Integer getOriginalQuantity()
    {
        return originalQuantity;
    }

    public Integer getTradedQuantity()
    {
        return tradedQuantity;
    }

    public Integer getCancelledQuantity()
    {
        return cancelledQuantity;
    }

    public Integer getLeavesQuantity()
    {
        return leavesQuantity;
    }

    
    public void setCancelledQuantity(Integer newValue)
    {
        Integer oldValue = getCancelledQuantity();
        cancelledQuantity = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_CANCELLED_QUANTITY, oldValue, newValue);
    }

    public void setClearingFirm(ExchangeFirm newValue)
    {
        ExchangeFirm oldValue = getClearingFirm();
        clearingFirm = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_CLEARING_FIRM, oldValue, newValue);
    }

    public void setCoverage(Character newValue)
    {
        Character oldValue = getCoverage();
        coverage = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_COVERAGE, oldValue, newValue);
    }

    public void setLeavesQuantity(Integer newValue)
    {
        Integer oldValue = getLeavesQuantity();
        leavesQuantity = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_LEAVES_QUANTITY, oldValue, newValue);
    }

    public void setMustUsePrice(Price newValue)
    {
        Price oldValue = getMustUsePrice();
        mustUsePrice = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_MUST_USE_PRICE, oldValue, newValue);
    }

    public void setOriginalQuantity(Integer newValue)
    {
        Integer oldValue = getOriginalQuantity();
        originalQuantity = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_ORIGINAL_QUANTITY, oldValue, newValue);
    }

    public void setPositionEffect(Character newValue)
    {
        Character oldValue = getPositionEffect();
        positionEffect = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_POSITION_EFFECT, oldValue, newValue);
    }

    public void setProductKey(Integer newValue)
    {
        Integer oldValue = getProductKey();
        productKey = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_PRODUCT_KEY, oldValue, newValue);
    }

    public void setSide(Character newValue)
    {
        Character oldValue = getSide();
        side = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_SIDE, oldValue, newValue);
    }

    public void setTradedQuantity(Integer newValue)
    {
        Integer oldValue = getTradedQuantity();
        tradedQuantity = newValue;
        setModified(true);
        firePropertyChange(PROPERTY_TRADED_QUANTITY, oldValue, newValue);
    }
  
    /**
     * Gets the underlying struct
     * @return LegOrderDetailStruct
     * @deprecated Should not use underlying, but exposed interface.
     */
    public LegOrderDetailStruct getStruct()
    {
        LegOrderDetailStruct legOrderDetailStruct = new LegOrderDetailStruct();
        legOrderDetailStruct.cancelledQuantity = getCancelledQuantity().intValue();
        legOrderDetailStruct.clearingFirm = getClearingFirm().getExchangeFirmStruct();
        legOrderDetailStruct.coverage = getCoverage().charValue();
        legOrderDetailStruct.leavesQuantity = getLeavesQuantity().intValue();
        legOrderDetailStruct.mustUsePrice = getMustUsePrice().toStruct();
        legOrderDetailStruct.originalQuantity = getOriginalQuantity().intValue();
        legOrderDetailStruct.positionEffect = getPositionEffect().charValue();
        legOrderDetailStruct.productKey = getProductKey().intValue();
        legOrderDetailStruct.side = getSide().charValue();
        legOrderDetailStruct.tradedQuantity = getTradedQuantity().intValue();

        return legOrderDetailStruct;
    }

    public String toString()
    {
        String string = "";
        string += "LegOrderDetailImpl: product key = " + this.getProductKey();
        string += ", mustUsePrice = " + this.getMustUsePrice();
        string += ", side = " + this.getSide();
        string += ", clearing Firm = " + this.getClearingFirm();
        string += ", coverage = " + this.getCoverage();
        string += ", position effect = " + this.getPositionEffect();
        return string;
    }

    protected void setStruct(LegOrderDetailStruct struct)
    {
        cancelledQuantity = new Integer(struct.cancelledQuantity);
        clearingFirm = ExchangeFirmFactory.createExchangeFirm(struct.clearingFirm);
        coverage = new Character(struct.coverage);
        leavesQuantity = new Integer(struct.leavesQuantity);
        mustUsePrice = PriceFactory.create(struct.mustUsePrice);
        originalQuantity = new Integer(struct.originalQuantity);
        positionEffect = new Character(struct.positionEffect);
        productKey = new Integer(struct.productKey);
        side = new Character(struct.side);
        tradedQuantity = new Integer(struct.tradedQuantity);
    }

    private void initializeDefaults()
    {
        cancelledQuantity = new Integer(0);
        clearingFirm = ExchangeFirmFactory.createExchangeFirm("", "");
        coverage = new Character(CoverageTypes.UNCOVERED);
        leavesQuantity = new Integer(0);

        //For now, the mustUsePrice type is always NO_PRICE
        PriceStruct mustUseStruct = new PriceStruct();
        mustUseStruct.type = PriceTypes.NO_PRICE;
        mustUsePrice = PriceFactory.create(mustUseStruct);

        originalQuantity = new Integer(0);
        positionEffect = new Character(PositionEffects.OPEN);
        side = new Character(Sides.BUY);
        tradedQuantity = new Integer(0);
   }
}