package com.cboe.client.util;

import com.cboe.idl.cmiConstants.Sides;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.application.product.cache.ProductCacheKeyFactory;
import com.cboe.application.product.adapter.ProductQueryServiceAdapterImpl;
import com.cboe.application.cache.CacheFactory;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;
import com.cboe.domain.util.LegsNormalizer;
import com.cboe.domain.util.SpreadNormalizationStrategyTypes;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

import java.util.Scanner;

/**
 * Created by IntelliJ IDEA.
 * @author Peng Li
 * Date: Jul 9, 2009
 * Time: 10:49:10 AM
 */
public class StrategyLegsWrapper {

    private StrategyLegStruct[] theStrategyLegs;
    private int hashcode = 0;
    private boolean isValidStrategy = true;
    private char massagedSide;

    public StrategyLegsWrapper(StrategyLegStruct[] aStrategyLegs) throws NotFoundException, DataValidationException{
        theStrategyLegs = new StrategyLegStruct[aStrategyLegs.length];
        for(int i=0; i<aStrategyLegs.length;i++)
        {
           theStrategyLegs[i] = new StrategyLegStruct(aStrategyLegs[i].product,
                   aStrategyLegs[i].ratioQuantity,
                   getValidSideValue(aStrategyLegs[i].side));
            aStrategyLegs[i].side = massagedSide;
        }
        (new CASStrategyLegsNormalizer()).normalizeLegs(theStrategyLegs);
        
        for(StrategyLegStruct myLeg:theStrategyLegs)
        {
            //ratioQuantity * 31*31*31*13 
            hashcode += myLeg.product*7+myLeg.ratioQuantity*387283;
        }
    }

    public StrategyLegsWrapper(String[] aLegs) throws NotFoundException, DataValidationException{
        theStrategyLegs = new StrategyLegStruct[aLegs.length];
        for(int i=0; i<aLegs.length;i++)
        {
            Scanner myScanner = new Scanner(aLegs[i]);
            myScanner.useDelimiter(",");

            int myProduct = myScanner.nextInt();
            char mySide = myScanner.next().trim().toUpperCase().charAt(0);
            int myRatio =  myScanner.nextInt();
            theStrategyLegs[i] = new StrategyLegStruct(myProduct,
                   myRatio,
                   mySide);
        }
        (new CASStrategyLegsNormalizer()).normalizeLegs(theStrategyLegs);
        for(StrategyLegStruct myLeg:theStrategyLegs)
        {
            //ratioQuantity * 31*31*31*13 
            hashcode += myLeg.product*7+myLeg.ratioQuantity*387283;
        }
    }

    public StrategyLegsWrapper(SessionStrategyLegStruct[] aSessionStrategyLegs) throws NotFoundException, DataValidationException{
        theStrategyLegs = new StrategyLegStruct[aSessionStrategyLegs.length];
        for(int i=0; i<aSessionStrategyLegs.length;i++)
        {
           theStrategyLegs[i] = new StrategyLegStruct(aSessionStrategyLegs[i].product,
                   aSessionStrategyLegs[i].ratioQuantity,
                   aSessionStrategyLegs[i].side);
        }
        (new CASStrategyLegsNormalizer()).normalizeLegs(theStrategyLegs);
        
        for(StrategyLegStruct myLeg:theStrategyLegs)
        {
            //ratioQuantity * 31*31*31*13 
            hashcode += myLeg.product*7+myLeg.ratioQuantity*387283;
        }
    }

    @Override
    public int hashCode(){
        return hashcode;
    }


    private final char getValidSideValue(char strategyLegSide)
    {
        // This was put in place for the Short Sell Project - Vivek B
        // This code can be used for converting a Side of Sell_Short and Sell_Short_Exempt to Sell - for strategy lookup
        // For the present - All sides - except B, S, H and X - should be marked as invalid.
        // We will only set the 'isValidStrategy' to false - not to true. The variable is initialized as 'true',
        // and if it is false once - i.e. any of the sides on any of the legs is invalid, the product is invalid.
        if (strategyLegSide == Sides.BUY)
        {
            massagedSide = Sides.BUY;
        } else if(strategyLegSide == Sides.SELL)
        {
            massagedSide = Sides.SELL;
        } else if (strategyLegSide == Sides.SELL_SHORT)
        {
            massagedSide = Sides.SELL;
        } else if(strategyLegSide == Sides.SELL_SHORT_EXEMPT)
        {
            massagedSide = Sides.SELL;
        }
        else
        {
            massagedSide = strategyLegSide;
            isValidStrategy = false;
        }
        return massagedSide;
    }

    public boolean isValidStrategy()
    {
        return isValidStrategy;
    }

    @Override
    public boolean equals(Object anObject) {

      try{
        if(!(anObject instanceof StrategyLegsWrapper))
            return false;
        StrategyLegsWrapper aStrategyLegsWrapper = (StrategyLegsWrapper)anObject;

        if (theStrategyLegs.length == aStrategyLegsWrapper.getStrategyLegs().length)
        {
            boolean myMatch = true;
            boolean myReverseSide = aStrategyLegsWrapper.getStrategyLegs()[0].side != theStrategyLegs[0].side;
            for (int j = 0; myMatch && j < theStrategyLegs.length; j++)
            {
                myMatch = aStrategyLegsWrapper.getStrategyLegs()[j].product == theStrategyLegs[j].product &&
                                 aStrategyLegsWrapper.getStrategyLegs()[j].ratioQuantity == theStrategyLegs[j].ratioQuantity;
                if (!myReverseSide) {
                    myMatch &= aStrategyLegsWrapper.getStrategyLegs()[j].side == theStrategyLegs[j].side;
                }
                else {
                    myMatch &= aStrategyLegsWrapper.getStrategyLegs()[j].side != theStrategyLegs[j].side;
                }
            }
            return myMatch;
        }
        else{
            return false;
        }
      }catch(NullPointerException e){
          Log.exception(e);
          return false;
      }
    }

    @Override
    public String toString() {
        StringBuilder myBuffer = new StringBuilder(60);
        for(StrategyLegStruct myLeg:theStrategyLegs)
        {
            myBuffer.append(myLeg.product);
            myBuffer.append(myLeg.side);
            myBuffer.append(myLeg.ratioQuantity);
            myBuffer.append(';');
        }
        return myBuffer.toString();
    }

    public StrategyLegStruct[] getStrategyLegs(){
        return theStrategyLegs;
    }

    /**
     * Revisit this class after rollout is complete for stock no full lot normalization is rolled out.
     * All this code will go away.
     */
    class CASStrategyLegsNormalizer extends LegsNormalizer{
        public int checkEquityLeg(StrategyLegStruct[] legs) throws NotFoundException
        {
            switch(ProductQueryServiceAdapterImpl.getStrategyLegNormalizationType())
            {
               case SpreadNormalizationStrategyTypes.NO_STOCK_LEG_FULL_LOT_NORMALIZATION:
                    return -1;
                case SpreadNormalizationStrategyTypes.STOCK_LEG_FULL_LOT_NORMALIZATION:
            for(StrategyLegStruct myLeg:legs)
            {
                ProductStruct myProductStruct = (ProductStruct) CacheFactory.getProductCache().
                        find(ProductCacheKeyFactory.getPrimaryProductKey(), Integer.valueOf(myLeg.product));
                if (myProductStruct== null){
                    throw ExceptionBuilder.notFoundException("No product found for a leg", 0);
                }
                if(myProductStruct.productKeys.productType== ProductTypes.EQUITY){
                    return myProductStruct.productKeys.productKey;
                }
            }
            return -1;
                default:
                        return -1;
            }
        }
        public void sortLegs(StrategyLegStruct[] legs)
        {
            for (int i = 0; i < legs.length - 1; i++)
            {
                for (int j = i + 1; j < legs.length; j++)
                {
                    if (legs[i].product > legs[j].product)
                    {
                        swap(legs, i, j);
                    } 
                }
            }
        }
            /** This method is over-written in this class to handle NotFoundException.
              * @param legs
              * @throws NotFoundException   Ignore it as it means normalized leg only.
              * @throws DataValidationException
              */
             public void normalizeLegs(StrategyLegStruct[] legs)  throws NotFoundException, DataValidationException
             {
                 try
                 {
                      super.normalizeLegs(legs);
                 }
                 catch (NotFoundException e)
                 {
                     sortLegs(legs);
                     return;
                 }
             }

    }
}
