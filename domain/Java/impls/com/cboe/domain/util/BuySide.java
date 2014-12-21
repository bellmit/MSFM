package com.cboe.domain.util;

import com.cboe.interfaces.domain.*;
import com.cboe.idl.cmiConstants.Sides;

/**
 * @author Tom Lynch
 */
public class BuySide extends SideBaseImpl {

    private static final Price zeroPrice = PriceFactory.create(0.0);

    /**
     * Compares for equality.
     */
    public boolean equals(Object otherSide) {
        return otherSide instanceof BuySide;
    }
    /**
     * author Tom Lynch
     * @return com.cboe.utils.Side
     */
    public Side getOtherSide() {
        return SideFactory.getSellSide();
    }
    /**
     * author Tom Lynch
     * @return boolean
     */
    public boolean isBuySide() {
        return true;
    }

    /**
     * return a boolean to indicate if aSide is a buySide as myself
     */
    public boolean isSameSide(Side aSide)
    {
        return aSide.isBuySide();    
    }

    /**
     * test condition if firstPrice is greater than the secondPrice.
     * @return boolean
     */
    protected boolean compareFirstWithSecond(Price firstPrice, Price secondPrice) {
        return firstPrice.greaterThan(secondPrice);
    }
    
    /**
     * This method returns the buy string to represent the side.
     * @return java.lang.String
     *
     * author Mark Novak
     */
    public String toString() {
        return Side.BUY_STRING;
    }

        /**
         * override the super class implememtation to return 1 which means that a tick ahead
         * for the buy side is to add one tick to the price
         *
         * @return int
         */
        public int getMinTickAhead(){
                return 1;
        }

        /**
         * Compare two prices and return true if they are tradable
         */
        public boolean areTwoPricesTradable(Price ownSidePrice, Price otherSidePrice){
                if (ownSidePrice.isNoPrice() || otherSidePrice.isNoPrice()){
                    return false;
                }
                if ( ownSidePrice.isMarketPrice() || otherSidePrice.isMarketPrice()){
                    return true;
                }
                return ownSidePrice.greaterThanOrEqual(otherSidePrice);
        }

        /**
         * return the value representing BuySide
         */
        public char toSideValue(){
                return Sides.BUY;
        }

    /**
     * Add amount to orignalPrice to create a more competitive price. 
     * author Matt Sochacki
     * @return Price
     * @param originalPrice the original price, this must be a valued price
     * @param amount amount to add to the orignalPrice, this must be a valued price
     */
    public Price calculateMoreCompetitivePrice(Price originalPrice, Price amount ){
        return originalPrice.addTicks( 1, // up 1 tick
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
        return originalPrice.addTicks( -1, // down 1 tick
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
            bestPrice = (firstPrice.greaterThan(secondPrice))? firstPrice : secondPrice;
        }
        else
        {
            bestPrice = firstPrice.isValuedPrice() ? firstPrice : secondPrice;
        }
        
        return bestPrice;
    }
}
