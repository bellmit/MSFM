package com.cboe.domain.marketData;

import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;

import java.util.StringTokenizer;

/**
 * ExchangeIndicatorHolder
 *
 * @author nikhil patel
 * @date june 4, 2004
 */

public class ExchangeIndicatorHolder implements SqlScalarType {

    private ExchangeIndicator[] exchangeIndicators;

    private String databaseString;

    private static final String EXCHANGE_INDCIATOR_DELIMITER = "/";

    static {
        /*
	 * Register anonymous inner class as generator for type
	 */
        SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF() {
        
        /*
	 * Creates instance of date from string
	*/
            public SqlScalarType createInstance(String values) {
                return new ExchangeIndicatorHolder(values);
            }

         /*
          * Returns this class as generated type.
          */
           public Class typeGenerated() {
                return ExchangeIndicatorHolder.class;
            }
        });

    }   // end of static block

    public ExchangeIndicatorHolder(ExchangeIndicator[] exchangeIndicators) {
        this.exchangeIndicators = exchangeIndicators;
    }

    public ExchangeIndicatorHolder(String dataBaseString) {
        this.databaseString = dataBaseString;
        StringTokenizer tokenizer = new StringTokenizer(this.databaseString, EXCHANGE_INDCIATOR_DELIMITER);
        exchangeIndicators = new ExchangeIndicator[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            exchangeIndicators[i++] = new ExchangeIndicator(tokenizer.nextToken());
        }
    }

    /**
     * Get exchange volumes being held.
     */
    public ExchangeIndicator[] getExchangeIndicators() {
        return exchangeIndicators;
    }

    /**
     * Formats this object for storing in database.  Will use / to separate Exchange Indicator entries.
     *
     * @return formatted string for storing in database
     */
    public String toDatabaseString() {
        if (databaseString == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < exchangeIndicators.length; i++) {
                if (i > 0) {
                    buffer.append(EXCHANGE_INDCIATOR_DELIMITER);
                }
                buffer.append(exchangeIndicators[i].toDatabaseString());
            }
            databaseString = buffer.toString();
        }
        return databaseString;
    }
    
    /**
     * Formats ExchangeIndicators arrays.  Will use / to separate Exchange Indicator entries.
     *
     * @return formatted string for storing in database
     */
    public static String toDatabaseString(com.cboe.idl.cmiMarketData.ExchangeIndicatorStruct[] exchangeIndicators) {        
        StringBuffer buffer = new StringBuffer();
        for (int i = 0; i < exchangeIndicators.length; i++) {
            if (i > 0) {
                buffer.append(EXCHANGE_INDCIATOR_DELIMITER);
            }
            buffer.append(exchangeIndicators[i].exchange + ":" + exchangeIndicators[i].marketCondition);
        }
        return buffer.toString();
    }

}