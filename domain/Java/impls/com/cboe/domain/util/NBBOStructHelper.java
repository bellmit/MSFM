package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.PriceTypesOperations;
import com.cboe.idl.cmiMarketData.NBBOStruct;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.Order;
import com.cboe.interfaces.domain.Quote;
import com.cboe.interfaces.domain.Side;

/**
 *   Helper class for NBBOStruct use.
 *
 *   @author Matt Sochacki
 */
public class NBBOStructHelper
{
    // PITS75134 - Added this method to detect when the nbbo is locked
    public static boolean isLocked(NBBOStruct nbbo)
    {
        boolean isNBBOLocked = false;
        if( nbbo != null && 
            nbbo.askPrice != null &&
            nbbo.bidPrice.type == PriceTypesOperations.VALUED && 
            nbbo.bidPrice != null && 
            nbbo.bidPrice.type == PriceTypesOperations.VALUED )
        {
            isNBBOLocked = nbbo.askPrice.whole == nbbo.bidPrice.whole && 
                           nbbo.askPrice.fraction == nbbo.bidPrice.fraction;
        }
        return isNBBOLocked; 
    }

    public static boolean isCrossed( NBBOStruct nbbo )
    {
        Price bid = PriceFactory.create( nbbo.bidPrice );
        Price ask = PriceFactory.create( nbbo.askPrice );

        if( bid.isValuedPrice() &&
            ask.isValuedPrice() )
        {
            return bid.greaterThan( ask );
        }
        else
        {
            return false;
        }
    }
    
    public static boolean isWithinNBBO(NBBOStruct nbbo, Price aPrice)
    {
        Price bid = PriceFactory.create( nbbo.bidPrice );
        Price ask = PriceFactory.create( nbbo.askPrice );

        if( bid.isValuedPrice() &&
            ask.isValuedPrice() )
        {
            return aPrice.greaterThanOrEqual( bid )
                   && ask.greaterThanOrEqual( aPrice );
        }
        
        return false; 
    }
    
    public static boolean isCrossedOrLocked( NBBOStruct nbbo )
    {
        Price bid = PriceFactory.create( nbbo.bidPrice );
        Price ask = PriceFactory.create( nbbo.askPrice );

        if( bid.isValuedPrice() &&
            ask.isValuedPrice() )
        {
            return bid.greaterThanOrEqual( ask );
        }
        else
        {
            return false;
        }
    }
    
    // Check if the order will Cross or Lock NBBO, before booking it
    public static boolean willCrossOrLockNBBO( NBBOStruct nbbo, Order anOrder)
    {
        return willCrossOrLockNBBO(nbbo, anOrder.getPrice(), anOrder.getSide());
    }
    
    public static boolean willCrossOrLockNBBO( NBBOStruct nbbo, Quote aQuote)
    {
        if (aQuote.getBid().getQuantityAllowed() > 0)
        {
            if (willCrossOrLockNBBO( nbbo, aQuote.getBid().getPrice(), aQuote.getBid().getSide()))
            {
                return true;
            }
        }
        if (aQuote.getAsk().getQuantityAllowed() > 0)
        {
            if (willCrossOrLockNBBO( nbbo, aQuote.getAsk().getPrice(), aQuote.getAsk().getSide()))
            {
                return true;
            }
        }
        return false;
    }

    public static boolean willCrossOrLockNBBO( NBBOStruct nbbo, Price aPrice, Side aSide)
    {
        Price bid = PriceFactory.create( nbbo.bidPrice );
        Price ask = PriceFactory.create( nbbo.askPrice );
    
        if (aSide.isBuySide())
        {    
            bid = aPrice;
        }
        else
        {
            ask = aPrice;
        }
    
        return (bid.isValuedPrice() &&
            ask.isValuedPrice() &&
            bid.greaterThanOrEqual( ask ));
    }
    
    // Check if the order will Cross NBBO, before booking it
    public static boolean willCrossNBBO( NBBOStruct nbbo, Order anOrder)
    {
        return willCrossNBBO(nbbo, anOrder.getPrice(), anOrder.getSide());
    }
    
    public static boolean willCrossNBBO( NBBOStruct nbbo, Price aPrice, Side aSide)
    {
        Price bid = PriceFactory.create( nbbo.bidPrice );
        Price ask = PriceFactory.create( nbbo.askPrice );
    
        if (aSide.isBuySide())
        {    
            bid = aPrice;
        }
        else
        {
            ask = aPrice;
        }
    
        return (bid.isValuedPrice() &&
            ask.isValuedPrice() &&
            bid.greaterThan( ask ));
    }
}
