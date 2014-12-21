package com.cboe.domain.marketData;

import com.cboe.idl.cmiMarketData.*;

import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;

import java.util.StringTokenizer;

/**
 * ExchangeIndicator
 *
 * @author nikhil patel
 * @date June 04, 2004
 */

public class ExchangeIndicator implements SqlScalarType {
    /**
     * Exchange name
     */
    private String exchange;

    /**
     * Market Condition 
     */
    private short marketCondition;


    static {
        /*
	 * Register anonymous inner class as generator for type
        */
        SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF() {
        
         /*
	 * Creates instance from string
	 */
            public SqlScalarType createInstance(String values) {
                return new ExchangeIndicator(values);
            }

            /*
    	     * Returns this class as generated type.
	    */
            public Class typeGenerated() {
                return ExchangeIndicator.class;
            }
        });
    }

    /**
     * ExchangeIndicator constructor comment.
     *
     * @param struct exchange indicator values
     */
    public ExchangeIndicator(ExchangeIndicatorStruct struct) {
        super();
        exchange = struct.exchange;
        marketCondition = struct.marketCondition;
    }

    /**
     * Creates instance from formatted string.
     *
     * @param values formatted string containing Exchange Indicator values
     * @see toString
     */
    public ExchangeIndicator(String values) {
        super();
        StringTokenizer tokens = new StringTokenizer(values, ":");
        if (tokens.countTokens() == 2) {
            exchange = tokens.nextToken();
            marketCondition = Short.parseShort(tokens.nextToken());
        } else {
            throw new IllegalArgumentException("Improper string passed: " + values);
        }
    }

    /**
     * Getter for exchange.
     *
     * @return exchange
     */
    public String getExchange() {
        return exchange;
    }

    /**
     * Getter for Market Indicator.
     *
     * @return market Indicator
     */
    public int getIndicator() {
        return marketCondition;
    }


    /**
     * Formats this object for storing in database.
     *
     * @return formatted string for storing in database
     */
    public String toDatabaseString() {
        return toString();
    }

    /**
     * Formats this object into standard form.
     *
     * @return formatted string
     */
    public String toString() {
        return exchange + ":" + marketCondition;
    }

    /**
     * Converts this object to a CORBA struct.
     *
     * @return struct containing values
     */
    public ExchangeIndicatorStruct toStruct() {
        ExchangeIndicatorStruct result = new ExchangeIndicatorStruct();
        result.exchange = exchange;
        result.marketCondition = marketCondition;
        return result;
    }
}
