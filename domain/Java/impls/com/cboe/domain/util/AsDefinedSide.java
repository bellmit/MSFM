package com.cboe.domain.util;

import com.cboe.interfaces.domain.Side;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiConstants.Sides;

/**
 * AsDefinedSide is an implementation of side AS_DEFINED which is used to describe
 * the side of an spread order. If the order wants to do the same of whatever
 * defined in the spread product, the side of the order is AS_DEFINED.
 *
 */


public class AsDefinedSide extends SideBaseImpl {

    public AsDefinedSide() {
    }

    /**
     * gets the other side of this side
     *
     * @return Side
     */
    public Side getOtherSide(){
            return SideFactory.getOppositeSide();
    }
    /**
     * @return boolean true
     */
    public boolean isAsDefinedSide(){
            return true;
    }

    /**
     * return a boolean to indicate if aSide is AsDefinedSide as myself
     */
    public boolean isSameSide(Side aSide)
    {
        return aSide.isAsDefinedSide();
    }

    /**
     * This method returns the as defined string to represent the side.
     * @return java.lang.String
     *
     */
    public String toString() {
            return Side.AS_DEFINED_STRING;
    }

    /**
     * compare two prices and return true if they are tradable
     *
     * @return boolean
     */
    public boolean areTwoPricesTradable(Price ownSidePrice, Price otherSidePrice) {
            if ( ownSidePrice.isNoPrice() || otherSidePrice.isNoPrice() ){
                return false;
            }
            if (ownSidePrice.isMarketPrice() || otherSidePrice.isMarketPrice()){
                return true;
            }
            return ownSidePrice.toLong() + otherSidePrice.toLong() <= 0;
    }

    /**
     * return the value representing the AsDefinedSide
     */
    public char toSideValue(){
            return Sides.AS_DEFINED;
    }


    /**
     * Add amount to orignalPrice to create a more competitive price.
     * author Matt Sochacki
     * @return Price
     * @param originalPrice the original price, this must be a valued price
     * @param amount amount to add to the orignalPrice, this must be a valued price
     */
    public Price calculateMoreCompetitivePrice(Price originalPrice, Price amount ){
        //Assumption: debit prices are negative, credit prices are positive
        //Decreasing a negative price will increase what the buyer is willing to pay and 
        //is therefore more competitive.
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
        //Assumption: debit prices are negative, credit prices are positive
        //Increasing a negative price will decrease what the buyer is willing to pay and 
        //is therefore less competitive.
        return originalPrice.addTicks( 1, // up 1 tick
                                      zeroPrice, //cutoff price
                                      zeroPrice, //ticksize below cutoff
                                      amount ); //ticksize above cutoff

    }

    public boolean areTwoPricesLocked(Price ownSidePrice, Price otherSidePrice)
    {
        if ( ownSidePrice.isNoPrice() || otherSidePrice.isNoPrice() ){
            return false;
        }
        if (ownSidePrice.isMarketPrice() && otherSidePrice.isMarketPrice()){
            return true;
        }
        return ownSidePrice.toLong() + otherSidePrice.toLong() == 0;
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
