package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.PriceScale;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiUtil.PriceStruct;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.interfaces.domain.ExpirationDate;

import java.util.StringTokenizer;


/**
 * A wrapper for the <code>ProductNameStruct</code>. This is used to convert the "."
 * delimited product name string into product Name struct. An additional helper method
 * is provided to get the product key based on the product name struct from the derived
 * cache.
 *
 * @author
 */
public class ProductNameHelper
{
    /**
    * ProductNameHelper constructor comment. This will read the delimited string
     * creates the Product Name struct.
    */
    public ProductNameHelper()
    {
    }


    public static ProductNameStruct createProductNameStruct(String productNameStr) throws IllegalArgumentException
    {
        ProductNameStruct prodNameStruct = new ProductNameStruct();

        StringTokenizer tokenizer = new StringTokenizer( productNameStr, "." );
        if(tokenizer.countTokens() < 5)
        {
            throw new IllegalArgumentException("Failed to create the Product Name Struct form the String: " + productNameStr);
        }

        prodNameStruct.reportingClass = tokenizer.nextToken();
        prodNameStruct.expirationDate = new DateStruct();
        prodNameStruct.expirationDate.day = (byte) 1;       // Day does not matter when lookup is done based on Produt Name.
        prodNameStruct.expirationDate.month = (byte) Integer.parseInt(tokenizer.nextToken());
        prodNameStruct.expirationDate.year = Short.parseShort(tokenizer.nextToken());
        DateStruct datePassedIn = prodNameStruct.expirationDate;
        ExpirationDate standardDate = ExpirationDateFactory.createStandardDate(datePassedIn, ExpirationDateFactory.SATURDAY_EXPIRATION);
        prodNameStruct.expirationDate = standardDate.toStruct();
        StringTokenizer strikePriceTokenizer = new StringTokenizer( tokenizer.nextToken(), "-");

        int whole = Integer.parseInt(strikePriceTokenizer.nextToken());
        int fraction = 0;
        
        if(strikePriceTokenizer.hasMoreTokens())
        {
            String fractionString = strikePriceTokenizer.nextToken();
            fraction = Integer.parseInt(fractionString) * (int)(PriceScale.DEFAULT_SCALE/Math.pow((double)10, (double) fractionString.length()));
        }

        else
            fraction = 0;

        PriceStruct strikePriceStruct = PriceFactory.createPriceStruct(PriceTypes.VALUED, whole, fraction);

        prodNameStruct.exercisePrice = strikePriceStruct;
        prodNameStruct.optionType = tokenizer.nextToken().charAt(0);

        return prodNameStruct;
    }
}
