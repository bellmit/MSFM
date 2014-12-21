package com.cboe.domain.util;

import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.DataValidationException;

/**
 * Created by IntelliJ IDEA.
 * @author Peng Li
 * Date: Jul 22, 2009
 */
public abstract class LegsNormalizer {
    public abstract int checkEquityLeg(StrategyLegStruct[] legs) throws NotFoundException;
    public abstract void sortLegs(StrategyLegStruct[] legs);

    public void normalizeLegs(StrategyLegStruct[] legs) throws NotFoundException, DataValidationException
    {
        boolean strategyContainsEquityLeg = false;
        int equityLegProductKey = 0;

        try{
            equityLegProductKey = checkEquityLeg(legs);
        }catch(NotFoundException nfe){
            throw nfe;
        }
        strategyContainsEquityLeg = (equityLegProductKey>0? true:false);

        sortLegs(legs);

        if (strategyContainsEquityLeg)
        {
            standardizeStrategyWithEquityLeg(legs,equityLegProductKey);
        }
        else
        {
            standardizeLegs(legs);
        }
    }

    /**
     * Standardizes the order and sizes of the legs.
     *
     * @param legs legs to be standardized.
     */
    private void standardizeLegs(StrategyLegStruct[] legs)  throws DataValidationException

    {
        // sortLegs(legs);
        standardizeQuantities(legs);
    }

    /**
     * Standardizes the order and sizes of the legs with an equity leg.
     *
     * @param legs legs to be standardized.
     */
    private void standardizeStrategyWithEquityLeg(StrategyLegStruct[] legs, int equityLegProductKey)  throws DataValidationException
    {
       // sortLegs(legs);
        standardizeEquityQuantities(legs,equityLegProductKey);
    }
    /**
     * Reduces all quantities so that the Greatest Common Denominator is one.
     *
     * @param legs legs to be reduced
     */
    private void standardizeQuantities(StrategyLegStruct[] legs)
    {
        int gcd = legs[0].ratioQuantity;

        for (int i = 1; gcd > 1 && i < legs.length; i++)
        {
            gcd = calculateGCD(gcd, legs[i].ratioQuantity);
        }

        if (gcd > 1)
        {
            for (int i = 0; i < legs.length; i++)
            {
                legs[i].ratioQuantity /= gcd;
            }
        }
    }

    /**
     * Reduces all quantities so that the Greatest Common Denominator reduces the Equity leg to 100 or
     * if that cannot be accomplished, try to reduce the legs by half until the lowest levels are attained
     * Basically,
     * Check the legs to the adjusted greatest common denominator to where the option leg is not rounded
     * otherwise,
     * Bring the creation to the lowest ratios, on the equity 100's and where the options leg is not rounded
     * For example, if a user is attempting a creation of a 600:40 - at the lowest 100 level
     * this leaves an option remainder that rounds to 7
     * The result should be a BuyWrite creation of 300:20, not 100:7
     * @param legs legs where the ratios are to be reduced, if possible
     * @param equityProduct the product key of the equity leg
     * @author - Mike Hasbrouck
     */
    private void standardizeEquityQuantities(StrategyLegStruct[] legs, int equityProduct)
    {
        // equity leg cannot be adjusted to less than 100
        // creation cannot occur if equity leg is not a multiple of 100
        int equityLegRatioQty = 1;
        int adjustedGCD = 1;
        int floor = 100;
        int tempQty = 1;
        int gcd = 1;
        boolean legHasRemainder = false;

        for (StrategyLegStruct calcLeg : legs)
        {
            if (calcLeg.product == equityProduct)
            {
                equityLegRatioQty = calcLeg.ratioQuantity;
                gcd = equityLegRatioQty;
            }
            else
            {
              gcd = calculateGCD(gcd, calcLeg.ratioQuantity);
            }
        }

        if (equityLegRatioQty == floor)
        {
            Log.information("ProductComponentHomeImpl:normalizeEquityQuantities"
                            + "/n" + "Cannot reduce equity leg with option leg further than original, no reduction to original ratios");
            return;
        }

        if (Log.isDebugOn())
        {
            Log.debug(" GCD = " + gcd);
            Log.debug(" equityLegRatioQty = " + equityLegRatioQty);
        }

        boolean found = false;
        if (gcd > 1)
        {
            for (adjustedGCD=gcd; adjustedGCD > 0; adjustedGCD--)
            {
                found = true; //assume it is true unless it fails one of the checks below
                //Now check the option legs if they are divisible by the adjustedGCD
                for (StrategyLegStruct strategyLeg : legs)
                {
                    if (strategyLeg.ratioQuantity % adjustedGCD != 0)
                    {
                        found = false;
                        break;  // adjustedGCD is not a GCD
                    }
                }

                // Make sure equity leg is a round lot
                if((equityLegRatioQty / adjustedGCD) % floor != 0)
                {
                    found = false;
                    continue;  // The equity leg is not a round lot
                }
                if(found)
                {
                  break;  // found the right combination
                }
            }
            if(!found)
            {
                // second pass ignore the divisibility by floor. Make sure that the new equity
                // leg quantity is greater or equal to floor
                for (adjustedGCD = gcd; adjustedGCD > 0; adjustedGCD--)
                {
                    found = true; //assume it is true unless it fails one of the checks below
                    //Now check the option legs if they are divisible by the adjustedGCD
                    for (StrategyLegStruct strategyLeg : legs)
                    {
                        if (strategyLeg.ratioQuantity % adjustedGCD != 0)
                        {
                            found = false;
                            break;  // adjustedGCD is not a GCD
                        }
                    }

                    // Make sure equity leg ratio is greater or equal to floor
                    if((equityLegRatioQty / adjustedGCD) < floor)
                    {
                        found = false;
                        continue;  // The equity leg is smaller than floor
                    }
                    if(found)
                    {
                      break; //found the right combination
                    }
                }
            }
        }
        if(found)
        {
            for (StrategyLegStruct leg : legs)
            {
                leg.ratioQuantity /= adjustedGCD;
            }
        }
    }

    /**
     * Calculates the Greatest Common Denominator of two integers.  Euclid's algorithm
     * is used.
     *
     * @param value1 first value
     * @param value2 second value
     */
    private int calculateGCD(int value1, int value2)
    {
        int min;
        int max;
        int remainder;
        int gcd = 0;

        // ordering isn't necessary for algorithm to work, but seems cleaner.
        if (value1 < value2)
        {
            min = value1;
            max = value2;
        }
        else
        {
            min = value2;
            max = value1;
        }
        // keep dividing min value into max value until remainder is 0
        // if the remainder isn't 0, max is set to min and min is set to
        // the remainder.
        while (gcd == 0)
        {
            remainder = max % min;
            if (remainder == 0)
            {
                gcd = min;
            }
            else
            {
                max = min;
                min = remainder;
            }
        }
        return gcd;
    }

    protected void swap(Object[] array, int index1, int index2)
    {
        Object temp = array[index1];
        array[index1] = array[index2];
        array[index2] = temp;
    }    
}
