package com.cboe.domain.util;

import com.cboe.interfaces.domain.Side;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiConstants.Sides;

/**
 * This class represents a sell side of a position or trade.
 *
 */
public class SellSide extends SideBaseImpl {
    /**
     * Compares for equality.
     */
    public boolean equals(Object otherSide) {
        return otherSide instanceof SellSide;
    }
    /**
     * author Tom Lynch
     * @return com.cboe.utils.Side
     */
    public Side getOtherSide() {
        return SideFactory.getBuySide();
    }

    /**
     * author Tom Lynch
     * @return boolean
     */
    public boolean isSellSide() {
        return true;
    }

    /**
     * return a boolean to indicate if aSide is a sell side as myself.
     * @param aSide
     * @return
     */
    public boolean isSameSide(Side aSide)
    {
        return aSide.isSellSide();
    }

    /**
     * This method returns the sell string to represent the side.
     * @return java.lang.String
     *
     * author John Wickberg
     */
    public String toString() {
        return Side.SELL_STRING;
    }

    /**
     * compare two prices and return true if they are tradable
     *
     * @return boolean
     */
    public boolean areTwoPricesTradable(Price ownSidePrice, Price otherSidePrice) {
            if (ownSidePrice.isNoPrice() || otherSidePrice.isNoPrice()){
                return false;
            }
            if (ownSidePrice.isMarketPrice() || otherSidePrice.isMarketPrice()){
                return true;
            }
            return ownSidePrice.lessThanOrEqual(otherSidePrice);
    }

    /**
     * return the value representing SellSide
     */
    public char toSideValue(){
            return Sides.SELL;
    }

    /**
     * Add amount to orignalPrice to create a more competitive price.
     * author Matt Sochacki
     * @return Price
     * @param originalPrice the original price, this must be a valued price
     * @param amount amount to add to the orignalPrice, this must be a valued price
     */
    public Price calculateMoreCompetitivePrice(Price originalPrice, Price amount ){
        return originalPrice.addTicks( -1, // down 1 tick
                                      zeroPrice, //cutoff price
                                      zeroPrice, //ticksize below cutoff
                                      amount ); //ticksize above cutoff

    }

    /**
     * Add amount to orignalPrice to create a more competitive price.
     * author Matt Sochacki
     * @return Price
     * @param originalPrice the original price, this must be a valued price
     * @param amount amount to add to the orignalPrice, this must be a valued price
     */
    public Price calculateLessCompetitivePrice(Price originalPrice, Price amount ){
        return originalPrice.addTicks( 1, // up 1 tick
                                      zeroPrice, //cutoff price
                                      zeroPrice, //ticksize below cutoff
                                      amount ); //ticksize above cutoff

    }
    
    public boolean areTwoPricesLocked(Price ownSidePrice, Price otherSidePrice)
    {
        if (ownSidePrice.isNoPrice() || otherSidePrice.isNoPrice()){
            return false;
        }

        return ownSidePrice.equals(otherSidePrice);
    }
    
    public Price getBestPrice(Price firstPrice, Price secondPrice)
    {
        Price bestPrice;
        if (firstPrice.isValuedPrice() && secondPrice.isValuedPrice())
        {
            bestPrice = (firstPrice.greaterThan(secondPrice))? secondPrice : firstPrice;
        }
        else
        {
            bestPrice = firstPrice.isValuedPrice() ? firstPrice : secondPrice;
        }
        
        return bestPrice;
    }
}
