package com.cboe.presentation.common.formatters;

import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.CabinetPrice;
import com.cboe.domain.util.NoPrice;
import com.cboe.domain.util.MarketPrice;
import com.cboe.domain.util.ValuedPrice;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.interfaces.domain.Price;

import java.text.DecimalFormat;

/**
 * Provides special formatting for a Price.
 * @version (4/26/00 2:18:53 PM)
 * @see ValuedPrice
 * @author Nick DePasquale
 * @author Troy Wehrle
 */
class DisplayPrice implements Price
{
    private Price price = null;
    private String displayValue = null;
    private static final DecimalFormat displayDecimalFormat = new DecimalFormat("###0.00####");


    /**
     * DisplayValuePriced constructor comment.
     * @param aPrice double
     */
    protected DisplayPrice(double aPrice)
    {
        super();
        this.price = PriceFactory.create(aPrice);
    }

    /**
     * DisplayValuePriced constructor comment.
     * @param aPrice long
     */
    protected DisplayPrice(long aPrice)
    {
        super();
        this.price = PriceFactory.create(aPrice);
    }

    /**
     * DisplayValuePriced constructor comment.
     * @param initialValue PriceStruct
     */
    protected DisplayPrice(PriceStruct initialValue)
    {
        super();
        this.price = PriceFactory.create(initialValue);
    }

    /**
     * DisplayValuePriced constructor comment.
     * @param initialValue CabinetPrice
     */
    protected DisplayPrice(CabinetPrice initialValue)
    {
        super();
        this.price = initialValue;
    }

    /**
     * DisplayValuePriced constructor comment.
     * @param initialValue NoPrice
     */
    protected DisplayPrice(NoPrice initialValue)
    {
        super();
        this.price = initialValue;
    }

    /**
     * DisplayValuePriced constructor comment.
     * @param initialValue MarketPrice
     */
    protected DisplayPrice(MarketPrice initialValue)
    {
        super();
        this.price = initialValue;
    }

    /**
     * DisplayValuePriced constructor comment.
     * @param initialValue ValuedPrice
     */
    protected DisplayPrice(ValuedPrice initialValue)
    {
        super();
        this.price = initialValue;
    }

    /**
     * DisplayValuePriced constructor comment.
     * @param aPrice java.lang.String
     */
    protected DisplayPrice(String aPrice)
    {
        super();
        this.price = PriceFactory.create(aPrice);
    }

    /**
     * Converts a price to a printable string
     * @return formatted string
     */
    public String toString()
    {
        if (displayValue == null)
        {
            if (isValuedPrice())
            {
                displayValue = displayDecimalFormat.format(toDouble());
            }
            else if(isNoPrice())
            {
                displayValue = displayDecimalFormat.format(0.0);
            }
            else
            {
                displayValue = this.price.toString();
            }
        }
        return displayValue;
    }

    /**
     *  Implement Price interface
     */

    /**
     * @deprecated
     */
    public Price addTicks(int i)
    {
        return this.price.addTicks(i);
    }

    public Price addTicks(int i, Price price, Price price1, Price price2)
    {
        return this.price.addTicks(i, price, price1, price2);
    }

    public Price addPrice(Price priceToAdd)
    {
        return this.price.addPrice(priceToAdd);
    }

    public Price subtractPrice(Price priceToSubtract)
    {
        return this.price.subtractPrice(priceToSubtract);
    }

    public int getFraction()
    {
        return this.price.getFraction();
    }

    public int getFraction(int i)
    {
        return this.price.getFraction(i);
    }

    public int getWhole()
    {
        return this.price.getWhole();
    }

    public int getWhole(int i)
    {
        return this.price.getWhole(i);
    }

    public boolean greaterThan(Price aPrice)
    {
        return this.price.greaterThan(aPrice);
    }

    public boolean greaterThanOrEqual(Price aPrice)
    {
        return this.price.greaterThanOrEqual(aPrice);
    }

    public boolean isCabinetPrice()
    {
        return this.price.isCabinetPrice();
    }

    public boolean isMarketPrice()
    {
        return this.price.isMarketPrice();
    }

    public boolean isNoPrice()
    {
        return this.price.isNoPrice();
    }

    public boolean isValuedPrice()
    {
        return this.price.isValuedPrice();
    }

    public boolean lessThan(Price aPrice)
    {
        return this.price.lessThan(aPrice);
    }

    public boolean lessThanOrEqual(Price aPrice)
    {
        return this.price.lessThanOrEqual(aPrice);
    }

    public double toDouble()
    {
        return this.price.toDouble();
    }

    public long toLong()
    {
        return this.price.toLong();
    }

    public PriceStruct toStruct()
    {
        return this.price.toStruct();
    }

    public boolean equals(Object anObject)
    {
        boolean result = false;
        if (anObject instanceof Price)
        {
            result = this.price.equals(anObject);
        }
        return result;
    }

    public int getTickDifference(Price price1, Price breakPoint, Price tickSizeBelow, Price tickSizeAbove)
    {
        return this.price.getTickDifference(price1, breakPoint, tickSizeBelow, tickSizeAbove);
    }

    public Price subtractAndKeepSign(Price priceToSubtract)
    {
        return this.price.subtractAndKeepSign(priceToSubtract);
    }

//    public Object clone() throws CloneNotSupportedException
//    {
//         return super.clone();
//    }
}
