/**
 * 
 */
package com.cboe.domain.optionsLinkage;

import java.util.Comparator;

import com.cboe.domain.util.PriceFactory;
import com.cboe.idl.sweepAutoLink.SweepElementStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.optionsLinkage.SweepElement;

/**
 * This is used as domain object for Sweep functionality.
 * There are three possible types of objects:
 * 1. CBOE Tradable (can be Order or Quote) - <i>cboeTradable</i> will be true for this type.
 * 2. Auction Response - <i>auctionResponse</i> will be true for this type.
 * 3. Away Exchange - <i>awayExchange</i> will be true for this type.
 * 
 * If the <i>cboeTradable</i> is turned ON/true then the <i>referenceTradable</i> should contain reference 
 * to <code>Order</code> or <code>Quote</code> object.
 * 
 * If the <i>auctionResponse</i> is turned ON/true then the <i>referenceTradable</i> should contain reference 
 * to <code>AuctionResponse(Order with contingency of AuctionResponse)</code> object.
 * 
 * If the <i>awayExchange</i> is turned ON/true then the <i>referenceTradable</i> should contain the  
 * name of Exchange as <code>String</code>(text).
 * 
 * @author misbahud
 * 
 */
public class SweepElementImpl implements SweepElement
{
    /* In case of Away Exchange Object the referenceTradable will be String (Exchange name) */
    private Object referenceTradable;
    private String exchangeName;
    private int preferenceOrder;
    private Price sweepPrice;
    private int quantity;
    private boolean cboeTradable; // if this flag is false then referenceTradable variable will only contain name (as String).
    private boolean auctionResponse; // if this flag is false then referenceTradable variable will only contain name (as String).
    private boolean awayExchange;
    private int routeQuantity;
    private boolean preDeterminedTradePrice;
    private boolean incomingTradable;
    private boolean quantityUsed = false;
    private int remainingQuantity;
    private boolean lockedTradable = false;
    

    public SweepElementImpl(Object p_referenceTradable, String p_exchangeName, int p_preferenceOrder, Price p_sweepPrice,
            int p_quantity, boolean p_cboeTradable, boolean p_auctionResponse,
            boolean p_awayExchange)
    {
        referenceTradable = p_referenceTradable;
        exchangeName = p_exchangeName;
        preferenceOrder = p_preferenceOrder;
        sweepPrice = p_sweepPrice;
        quantity = p_quantity;
        cboeTradable = p_cboeTradable;
        auctionResponse = p_auctionResponse;
        awayExchange = p_awayExchange;
        remainingQuantity = p_quantity;
    }

    public SweepElementImpl(Object p_referenceTradable, String p_exchangeName, int p_preferenceOrder, Price p_sweepPrice,
            int p_quantity, boolean p_cboeTradable, boolean p_auctionResponse,
            boolean p_awayExchange, boolean p_incomingTradable)
    {
        this(p_referenceTradable, p_exchangeName, p_preferenceOrder, p_sweepPrice,
                p_quantity, p_cboeTradable, p_auctionResponse,
                p_awayExchange);
        incomingTradable = p_incomingTradable;
    }
    
    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#getReferenceTradable()
     */
    public Object getReferenceTradable()
    {
        return referenceTradable;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#setReferenceTradable(java.lang.Object)
     */
    public void setReferenceTradable(Object p_referenceTradable)
    {
        referenceTradable = p_referenceTradable;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#getExchangeName()
     */
    public String getExchangeName()
    {
        return exchangeName;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#setExchangeName(java.lang.String)
     */
    public void setExchangeName(String p_exchangeName)
    {
        exchangeName = p_exchangeName;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#getPreferenceOrder()
     */
    public int getPreferenceOrder()
    {
        return preferenceOrder;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#setPreferenceOrder(int)
     */
    public void setPreferenceOrder(int p_preferenceOrder)
    {
        preferenceOrder = p_preferenceOrder;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#getSweepPrice()
     */
    public Price getSweepPrice()
    {
        return sweepPrice;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#setSweepPrice(com.cboe.interfaces.domain.Price)
     */
    public void setSweepPrice(Price p_sweepPrice)
    {
        sweepPrice = p_sweepPrice;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#getQuantity()
     */
    public int getQuantity()
    {
        if(isQuantityUsed())
        {
            return getRemainingQuantity();
        }
        else
        {
            return quantity;
        }
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#setQuantity(int)
     */
    public void setQuantity(int p_quantity)
    {
        quantity = p_quantity;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#isCboeTradable()
     */
    public boolean isCboeTradable()
    {
        return cboeTradable;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#setCboeTradable(boolean)
     */
    public void setCboeTradable(boolean p_cboeTradable)
    {
        cboeTradable = p_cboeTradable;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#isAuctionResponse()
     */
    public boolean isAuctionResponse()
    {
        return auctionResponse;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#setAuctionResponse(boolean)
     */
    public void setAuctionResponse(boolean p_auctionResponse)
    {
        auctionResponse = p_auctionResponse;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#isAwayExchange()
     */
    public boolean isAwayExchange()
    {
        return awayExchange;
    }

    /* (non-Javadoc)
     * @see com.cboe.businessServices.optionsLinkage.SweepElement#setAwayExchange(boolean)
     */
    public void setAwayExchange(boolean p_awayExchange)
    {
        awayExchange = p_awayExchange;
    }
    
    

    public int getRouteQuantity()
    {
        return routeQuantity;
    }


    public void setRouteQuantity(int p_routeQuantity)
    {
        routeQuantity = p_routeQuantity;
    }


    public boolean isPreDeterminedTradePrice()
    {
        return preDeterminedTradePrice;
    }

    public void setPreDeterminedTradePrice(boolean p_preDeterminedTradePrice)
    {
        preDeterminedTradePrice = p_preDeterminedTradePrice;
    }

    public boolean isIncomingTradable()
    {
        return incomingTradable;
    }

    public void setIncomingTradable(boolean p_incomingTradable)
    {
        incomingTradable = p_incomingTradable;
    }

    public boolean isQuantityUsed()
    {
        return quantityUsed;
    }

    public void setQuantityUsed(boolean p_quantityUsed)
    {
        quantityUsed = p_quantityUsed;
    }

    public int getRemainingQuantity()
    {
        return remainingQuantity;
    }

    public void setRemainingQuantity(int p_remainingQuantity)
    {
        remainingQuantity = p_remainingQuantity;
    }
    
    public int getOriginalQuantity()
    {
        return quantity;
    }

    public boolean isLockedTradable()
    {
        return lockedTradable;
    }

    public void setLockedTradable(boolean p_lockedTradable)
    {
        lockedTradable = p_lockedTradable;   
    }
    
    @Override
    public boolean equals(Object p_obj)
    {
        if(!(p_obj instanceof SweepElementImpl))
            return false;
        SweepElementImpl sweepObj = (SweepElementImpl)p_obj;
        return exchangeName.equals(sweepObj.exchangeName) 
                && preferenceOrder == sweepObj.preferenceOrder
                && sweepPrice.equals(sweepObj.sweepPrice)
                && quantity == sweepObj.quantity;
    }

    @Override
    public int hashCode()
    {
        return exchangeName.hashCode() + preferenceOrder + sweepPrice.hashCode() + quantity;
    }

    /**
     * Sorts the specified sweep element list into ascending order, 
     * according to the <i>natural ordering</i> of its elements.
     * 
     */
    public int compareTo(Object p_arg0)
    {
        SweepElementImpl sweepObj = (SweepElementImpl)p_arg0;
        int priceComp = Long.valueOf(sweepPrice.toLong()).compareTo(Long.valueOf(sweepObj.sweepPrice.toLong()));
        if (priceComp != 0) 
            return priceComp;
        return (preferenceOrder < sweepObj.preferenceOrder ? -1 :
                (preferenceOrder == sweepObj.preferenceOrder ? 0 : 1)
                );
    }


    @Override
    public String toString()
    {
        StringBuffer sweepElementBuffer = new StringBuffer();
        sweepElementBuffer.append("Exchange Name: ");
        sweepElementBuffer.append(exchangeName);
        sweepElementBuffer.append(", Preference Order: ");
        sweepElementBuffer.append(preferenceOrder);
        sweepElementBuffer.append(", Sweep Price: ");
        sweepElementBuffer.append(sweepPrice);
        sweepElementBuffer.append(", Quantity: ");
        sweepElementBuffer.append(quantity);
        sweepElementBuffer.append(", Route Quantity: ");
        sweepElementBuffer.append(routeQuantity);
        sweepElementBuffer.append(", Quantity Used: ");
        sweepElementBuffer.append(quantityUsed);
        sweepElementBuffer.append(", Remaining Quantity: ");
        sweepElementBuffer.append(remainingQuantity);
        sweepElementBuffer.append(", Locked Tradable: ");
        sweepElementBuffer.append(lockedTradable);
        return sweepElementBuffer.toString();
    }


    public SweepElement copy()
    {
        SweepElement copySweepElement = new SweepElementImpl(this.referenceTradable, this.exchangeName, this.preferenceOrder, this.sweepPrice,
                this.quantity, this.cboeTradable, this.auctionResponse,
                this.awayExchange);
        copySweepElement.setRouteQuantity(this.routeQuantity);
        copySweepElement.setPreDeterminedTradePrice(this.preDeterminedTradePrice);
        copySweepElement.setIncomingTradable(this.incomingTradable);
        copySweepElement.setQuantityUsed(this.quantityUsed);
        copySweepElement.setRemainingQuantity(this.remainingQuantity);
        copySweepElement.setLockedTradable(this.lockedTradable);
        return copySweepElement;
    }
    
    /**
     * Converts the current instance to Struct.
     * 
     * @return
     */
    public SweepElementStruct toStruct()
    {
        SweepElementStruct struct = new SweepElementStruct();
        struct.exchangeName = this.exchangeName;
        struct.preferenceOrder = this.preferenceOrder;
        if(this.sweepPrice != null)
        {
            struct.sweepPrice = this.sweepPrice.toStruct();
        }
        else
        {
            struct.sweepPrice = PriceFactory.getNoPrice().toStruct();
        }
        struct.volume = this.quantity;
        struct.cboeTradable = this.cboeTradable;
        struct.awayExchange = this.awayExchange;
        struct.routeVolume = this.routeQuantity;
        return struct;
    }
    
    /**
     * Converts the Struct into <code>SweepElement</code> object.
     * CAUTION: Because Struct doesn't contains details
     * for CBOE Tradable or Auction Response so the conversion
     * for these types will not be accurate. 
     * 
     * @param sweepStruct
     * @return
     */
    public static SweepElement valueOf(SweepElementStruct sweepStruct)
    {
        Price structPrice = PriceFactory.create(sweepStruct.sweepPrice);
        SweepElement newSweepElement = new SweepElementImpl(sweepStruct.exchangeName, sweepStruct.exchangeName, sweepStruct.preferenceOrder, structPrice,
                                            sweepStruct.volume, sweepStruct.cboeTradable, false,
                                            sweepStruct.awayExchange);
        newSweepElement.setRouteQuantity(sweepStruct.routeVolume);
        return newSweepElement;
    }
    
    /** 
     * Sorts the specified sweep element list into descending order, 
     * according to the <i>reverse ordering</i> of its elements. e.g.
     * 
     * <table>
     * <tr><td>Price</td>  <td>-</td>  <td>Preference</td></tr>
     * <tr><td>=====</td>  <td>-</td>  <td>==========</td></tr>
     * <tr><td>1.05</td>  <td>-</td>  <td>   0       </td></tr>
     * <tr><td>1.05</td>  <td>-</td>  <td>   1       </td></tr>
     * <tr><td>1.00</td>  <td>-</td>  <td>   1       </td></tr>
     * <tr><td>1.00</td>  <td>-</td>  <td>   2       </td></tr>
     * <tr><td>0.95</td>  <td>-</td>  <td>   1       </td></tr>
     * </table>
     */
    public static final Comparator<SweepElement> SWEEP_ELEMENT_DESCENDING = new Comparator<SweepElement>()
    {
        public int compare(SweepElement p_arg0, SweepElement p_arg1)
        {
            SweepElementImpl sweepObj1 = (SweepElementImpl)p_arg0;
            SweepElementImpl sweepObj2 = (SweepElementImpl)p_arg1;
          int priceComp = Long.valueOf(sweepObj2.sweepPrice.toLong()).compareTo(Long.valueOf(sweepObj1.sweepPrice.toLong()));
          if (priceComp != 0) 
              return priceComp;
          return (sweepObj1.preferenceOrder < sweepObj2.preferenceOrder ? -1 :
                  (sweepObj1.preferenceOrder == sweepObj2.preferenceOrder ? 0 : 1)
                  );
        }
    };
    
    /**
     * Sorts the specified sweep element list into ascending order, 
     * according to the <i>natural ordering</i> of its elements. e.g.
     * 
     * <table>
     * <tr><td>Price</td>  <td>-</td>  <td>Preference</td></tr>
     * <tr><td>=====</td>  <td>-</td>  <td>==========</td></tr>
     * <tr><td>1.15</td>   <td>-</td>  <td>   0       </td></tr>
     * <tr><td>1.15</td>   <td>-</td>  <td>   1       </td></tr>
     * <tr><td>1.15</td>   <td>-</td>  <td>   2       </td></tr>
     * <tr><td>1.16</td>   <td>-</td>  <td>   1       </td></tr>
     * <tr><td>1.18</td>   <td>-</td>  <td>   1       </td></tr>
     * </table>
     */
    public static final Comparator<SweepElement> SWEEP_ELEMENT_ASCENDING = new Comparator<SweepElement>()
    {
        public int compare(SweepElement p_arg0, SweepElement p_arg1)
        {
            SweepElementImpl sweepObj1 = (SweepElementImpl)p_arg0;
            SweepElementImpl sweepObj2 = (SweepElementImpl)p_arg1;
          int priceComp = Long.valueOf(sweepObj1.sweepPrice.toLong()).compareTo(Long.valueOf(sweepObj2.sweepPrice.toLong()));
          if (priceComp != 0) 
              return priceComp;
          return (sweepObj1.preferenceOrder < sweepObj2.preferenceOrder ? -1 :
                  (sweepObj1.preferenceOrder == sweepObj2.preferenceOrder ? 0 : 1)
                  );
        }
    };

}
