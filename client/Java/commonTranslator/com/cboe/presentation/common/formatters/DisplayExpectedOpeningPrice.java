package com.cboe.presentation.common.formatters;

import com.cboe.idl.cmiMarketData.ExpectedOpeningPriceStruct;
import com.cboe.presentation.common.formatters.ExpectedOpeningPriceTypes;

/**
 * Provides special formatting for an ExpectedOpeningPrice.
 * @see DisplayPrice
 * @author Luis Torres
 */
class DisplayExpectedOpeningPrice extends DisplayPrice
{
    private String displayValue = null;
    private ExpectedOpeningPriceStruct eopStruct = null;
    /**
     * DisplayExpectedOpeningPrice constructor comment.
     * @param initialValue ExpectedOpeningPriceStruct
     */
    protected DisplayExpectedOpeningPrice(ExpectedOpeningPriceStruct initialEOP)
    {
        super(initialEOP.expectedOpeningPrice);
        eopStruct = initialEOP;
    }
    protected DisplayExpectedOpeningPrice(double aPrice)
    {
        super(aPrice);
        eopStruct = null;
    }
    /**
     * Converts a price to a printable string
     * @return formatted string
     */
    public String toString()
    {
        if( displayValue == null )
        {
            if( eopStruct == null )
            {
                displayValue = ExpectedOpeningPriceTypes.getDefault();
            }
            else if( eopStruct.eopType == ExpectedOpeningPriceTypes.OPENING_PRICE )
            {
                displayValue = super.toString();
            }
            else if( eopStruct.eopType == ExpectedOpeningPriceTypes.NEED_MORE_BUYERS ||
                     eopStruct.eopType == ExpectedOpeningPriceTypes.NEED_MORE_SELLERS ||
                     eopStruct.eopType == ExpectedOpeningPriceTypes.PRICE_NOT_IN_BOTR_RANGE)
            {
                displayValue = ExpectedOpeningPriceTypes.toString(eopStruct.eopType)
                               + " @ " + super.toString();
            }
            else
            {
                displayValue = ExpectedOpeningPriceTypes.toString(eopStruct.eopType);
            }
        }
        return displayValue;
    }
}
