package com.cboe.domain.util.fixUtil;

import java.text.DecimalFormat;
import java.util.regex.Pattern;
import java.util.regex.Matcher;

import com.cboe.idl.cmiConstants.PriceScale;
import com.cboe.idl.cmiConstants.PriceTypes;
import com.cboe.idl.cmiUtil.PriceStruct;

/**
 * Helper class that provides static methods to convert to/from the
 * com.cboe.idl.cmiUtil.PriceStruct class
 * <br><br>
 * Copyright © 1999 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software program is made available to
 * CBOE members and member firms to enable them to develop software applications using
 * the CBOE Market Interface (CMi), and its use is subject to the terms and conditions
 * of a Software License Agreement that governs its use. This document is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 * @author Jim Northey
 *
 */
public class FixUtilPriceHelper {

  private static String MARKET_STRING = "MKT";
  private static ThreadLocal<DecimalFormat> defaultFormatRef = new ThreadLocal<DecimalFormat>() {
	protected DecimalFormat initialValue() {
        	return new DecimalFormat("####0.0000");
	}
  };
  private static ThreadLocal<DecimalFormat> decimalFormatRef = new ThreadLocal<DecimalFormat>() {
	protected DecimalFormat initialValue() {
        	return new DecimalFormat("#######0.00000000");
	}
  };
  
  private static Pattern pricePattern = Pattern.compile( "([+-]?)([0-9]*)(\\.?)([0-9]*)" ); // sign dollar point cents ( all opt in some cases )

  public FixUtilPriceHelper() {
  }

  public static String priceStructToString(PriceStruct aPriceStruct) {

      return priceStructToString(aPriceStruct,defaultFormatRef.get());
  }

  public static double priceStructToDouble(PriceStruct aPriceStruct)
  {
     double d = (double) aPriceStruct.whole + aPriceStruct.fraction / (double) PriceScale.DEFAULT_SCALE;
     if (d < 0) d -= 1.0D / PriceScale.DEFAULT_SCALE;

     return Double.parseDouble(getFormattedPrice(d)) ;
  }

  public static String priceStructToString(PriceStruct aPriceStruct,
                               DecimalFormat formatter){

      if ( aPriceStruct.type == PriceTypes.MARKET ) {

 	      	return MARKET_STRING;
      }
      else {
		      return formatter.format(priceStructToDouble(aPriceStruct));
      }
  }

  public static PriceStruct makeValuedPrice(String strPrice) {

        PriceStruct aPriceStruct = new PriceStruct();

        Matcher priceMatch   = pricePattern.matcher( strPrice );
        boolean isValidPrice = priceMatch.matches();
        boolean isNegativePrice = false ;

        if ( isValidPrice ) {
            //sign
            isNegativePrice       = priceMatch.group(1).equals("-");
            //dollar
            aPriceStruct.whole    = priceMatch.group(2).equals("") ? 0
                : Integer.parseInt(priceMatch.group(2));
            //cents
            aPriceStruct.fraction = priceMatch.group(4).equals("") ? 0
                : (int)(Double.parseDouble("." + priceMatch.group(4)) * PriceScale.DEFAULT_SCALE);
            
            if ( isNegativePrice ) {
                aPriceStruct.whole =    - aPriceStruct.whole;
                aPriceStruct.fraction = - aPriceStruct.fraction;
            }
            aPriceStruct.type = PriceTypes.VALUED;
        } else {
            // Not Valid Price
            aPriceStruct.whole = 0;
            aPriceStruct.fraction = 0;
            aPriceStruct.type = PriceTypes.NO_PRICE ;
        }

        return aPriceStruct;
  }

  /**
   * Convert a floating point number representing a price to the CMi struct for a price
   * @param dblPrice a double representing a price, can be negative
   * @return PriceStruct populated with equivalent numeric values and a type of VALUED
   */	
  public static PriceStruct makeValuedPrice(double dblPrice) {
	PriceStruct aPriceStruct = new PriceStruct();

	double wholeDouble = dblPrice < 0 ? Math.ceil(dblPrice) : Math.floor(dblPrice);
	aPriceStruct.whole = (int)wholeDouble;
	aPriceStruct.fraction = (int)Math.round((dblPrice - wholeDouble) * PriceScale.DEFAULT_SCALE);
	aPriceStruct.type = PriceTypes.VALUED ;

	return aPriceStruct;
  }

  public static String getFormattedPrice(double dblPrice) {
        DecimalFormat convertFormat = decimalFormatRef.get();
        return convertFormat.format(dblPrice);
  }

  public static PriceStruct makeValuedPrice(int whole, int fraction) {

        PriceStruct aPriceStruct = new PriceStruct();

        aPriceStruct.whole = whole;
        aPriceStruct.fraction = fraction;
        aPriceStruct.type = PriceTypes.VALUED ;

        return aPriceStruct;
  }


  public static PriceStruct makeNoPrice(){

        PriceStruct aPriceStruct = new PriceStruct();

        aPriceStruct.whole = 0;
        aPriceStruct.fraction = 0;
        aPriceStruct.type = PriceTypes.NO_PRICE ;

        return aPriceStruct;

  }

  public static PriceStruct makeMarketPrice(){

        PriceStruct aPriceStruct = new PriceStruct();

        aPriceStruct.whole = 0;
        aPriceStruct.fraction = 0;
        aPriceStruct.type = PriceTypes.MARKET;

        return aPriceStruct;
  }


  public static boolean equals(PriceStruct a, PriceStruct b){

     return (a.type == b.type && a.whole == b.whole && a.fraction == b.fraction);

  }

  public static boolean lessThan(PriceStruct a, PriceStruct b){
     boolean result;
     int temp = a.whole - b.whole;
	   result = temp < 0;
	   if (!result && temp == 0) {
	     	result = a.fraction < b.fraction;
	   }
     return result;
  }

  public static int compare(PriceStruct a, PriceStruct b) {

     if(lessThan(a,b)){
	return -1;
     }
     else if (equals(a,b)){
        return 0;
     }
     else{
        return 1;
     }
  }

}
