package com.cboe.domain.marketData;

import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUser.*;

import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;

import java.util.StringTokenizer;

/**
 * ExchangeAcronym
 *
 * @author nikhil patel
 * @date June 07, 2004
 */

public class ExchangeAcronym implements SqlScalarType {
    /**
     * Exchange name
     */
    private String exchange;

    /**
     * Acronym 
     */
    private String acronym;
    private static final String DEFAULT_EXCHANGE = "CBOE";
    private static final String DEFAULT_ACRONYM = "UNKNOWN";


    static {
        /*
	 * Register anonymous inner class as generator for type
        */
        SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF() {
        
         /*
	 * Creates instance from string
	 */
            public SqlScalarType createInstance(String values) {
                return new ExchangeAcronym(values);
            }

            /*
    	     * Returns this class as generated type.
	    */
            public Class typeGenerated() {
                return ExchangeAcronym.class;
            }
        });
    }

    /**
     * ExchangeAcronym constructor comment.
     *
     * @param struct exchange acronym values
     */
    public ExchangeAcronym(ExchangeAcronymStruct struct) {
        super();
        exchange = struct.exchange;
        acronym = struct.acronym;
    }

    /**
     * Creates instance from formatted string.
     *
     * @param values formatted string containing volume values
     * @see toString
     */
    public ExchangeAcronym(String values) {
        super();
        StringTokenizer tokens = new StringTokenizer(values, ":");
        if (tokens.countTokens() == 2) {
            exchange = tokens.nextToken();
            acronym = tokens.nextToken();
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
     * Getter for Acronym.
     *
     * @return Acronym
     */
    public String getAcronym() {
        return acronym;
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
        if(exchange.trim().length() == 0){
            exchange = DEFAULT_EXCHANGE;
        }
        if(acronym.trim().length() == 0){
            acronym = DEFAULT_ACRONYM;
        }
        return exchange + ":" + acronym;
    }

    /**
     * Converts this object to a CORBA struct.
     *
     * @return struct containing values
     */
    public ExchangeAcronymStruct toStruct() {
        ExchangeAcronymStruct result = new ExchangeAcronymStruct();
        result.exchange = exchange;
        result.acronym = acronym;
        return result;
    }
}
