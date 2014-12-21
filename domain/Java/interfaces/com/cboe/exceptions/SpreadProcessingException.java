package com.cboe.exceptions;

import com.cboe.util.CboeException;

/**
 * SpreadProcessingException is designed to deal with certain conditions specifically
 * related to spread processing. The following is a list of those conditions
 * 1. In a spread to spread trade, the trade price is not within the range defined by
 *    the derived quote.
 * 2. In a spread to spread trade, leg prices can not be calculated. This is due to the
 *    the fact that leg prices have to be on the tick
 * 3. In a spread to spread trade, all the calculated leg prices touch the best book.
 * 4. In a spread to spread trade, we sometimes have to apply "split trading method". Condition
 *    may exist that the quantity of a leg can not be split.
 */

public class SpreadProcessingException extends CboeException {

        public static final int TradePriceNotWithinDerivedQuote = 100;
        public static final int LegPricesCanNotBeCalculated = 101;
        public static final int AllCalculatedLegPriceTouchBooks = 102;
        public static final int LegQuantityCanNotBeSplited = 103;
        public static final int AtLeastOneCalculatedLegPriceTouchesBook = 104;
        public static final int TradePriceNotWithinTradableRange = 105;
        public static final int TryNextTickInTheRange = 106;
        
        public com.cboe.exceptions.ExceptionDetails details;

        private int errorCode;
        public SpreadProcessingException() {
		details = new com.cboe.exceptions.ExceptionDetails(); 
		details.message = ""; 
		details.dateTime = ""; 
		details.severity = 0; 
		details.error = 0; 
        }
        public SpreadProcessingException(String aMessage, int anErrorCode){
                super(aMessage);
                errorCode = anErrorCode;
		details = new com.cboe.exceptions.ExceptionDetails(); 
		details.message = aMessage;
		details.dateTime = ""; 
		details.severity = 0; 
		details.error = anErrorCode; 
        }
        public SpreadProcessingException(com.cboe.exceptions.ExceptionDetails details) {
		this.details = details; 
        }
        public int getErrorCode(){
                return errorCode;
        }
        public void setErrorCode(int anErrorCode){
                errorCode = anErrorCode;
        }
}
