package com.cboe.domain.util;

import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;

import java.util.StringTokenizer;

/**
 * ExchangeVolume
 *
 * @author baranski
 * @date Oct 28, 2002
 */

public class ExchangeVolume implements SqlScalarType {
    /**
     * Exchange name
     */
    private String exchange;
    /**
     * Total volume for the exchange
     */
    private int volume;
    private static final String DEFAULT_EXCHANGE = "CBOE";


    static {
        /*
		 * Register anonymous inner class as generator for type
		 */
        SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF() {
            /*
			 * Creates instance of date from string
			 */
            public SqlScalarType createInstance(String values) {
                return new ExchangeVolume(values);
            }

            /*
			 * Returns this class as generated type.
			 */
            public Class typeGenerated() {
                return ExchangeVolume.class;
            }
        });
    }

    /**
     * ExchangeVolume constructor comment.
     *
     * @param struct exchange volume values
     */
    public ExchangeVolume(ExchangeVolumeStruct struct) {
        super();
        exchange = struct.exchange;
        volume = struct.volume;

    }

    /**
     * Creates instance from formatted string.
     *
     * @param values formatted string containing volume values
     * @see toString
     */
    public ExchangeVolume(String values) {
        super();
        StringTokenizer tokens = new StringTokenizer(values, ":");
        if (tokens.countTokens() == 2) {
            exchange = tokens.nextToken();
            volume = Integer.parseInt(tokens.nextToken());
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
     * Getter for volume.
     *
     * @return total volume
     */
    public int getVolume() {
        return volume;
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
        return exchange + ":" + volume;
    }

    /**
     * Converts this object to a CORBA struct.
     *
     * @return struct containing values
     */
    public ExchangeVolumeStruct toStruct() {
        ExchangeVolumeStruct result = new ExchangeVolumeStruct();
        result.exchange = exchange;
        result.volume = volume;
        return result;
    }
}
