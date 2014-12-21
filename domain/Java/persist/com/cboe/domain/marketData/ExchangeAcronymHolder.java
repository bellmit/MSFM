package com.cboe.domain.marketData;

import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;

import java.util.StringTokenizer;

/**
 * ExchangeAcronymHolder
 *
 * @author nikhil patel
 * @date june 7, 2004
 */

public class ExchangeAcronymHolder implements SqlScalarType {

    private ExchangeAcronym[] exchangeAcronyms;

    private String databaseString;

    private static final String EXCHANGE_ACRONYM_DELIMITER = "/";

    static {
        /*
	 * Register anonymous inner class as generator for type
	 */
        SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF() {
        
        /*
	 * Creates instance of date from string
	*/
            public SqlScalarType createInstance(String values) {
                return new ExchangeAcronymHolder(values);
            }

         /*
          * Returns this class as generated type.
          */
           public Class typeGenerated() {
                return ExchangeAcronymHolder.class;
            }
        });

    }   // end of static block

    public ExchangeAcronymHolder(ExchangeAcronym[] exchangeAcronyms) {
        this.exchangeAcronyms = exchangeAcronyms;
    }

    public ExchangeAcronymHolder(String dataBaseString) {
        this.databaseString = dataBaseString;
        StringTokenizer tokenizer = new StringTokenizer(this.databaseString,EXCHANGE_ACRONYM_DELIMITER);
        exchangeAcronyms = new ExchangeAcronym[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            exchangeAcronyms[i++] = new ExchangeAcronym(tokenizer.nextToken());
        }
    }

    /**
     * Get exchange acronyms being held.
     */
    public ExchangeAcronym[] getExchangeAcronyms() {
        return exchangeAcronyms;
    }

    /**
     * Formats this object for storing in database.  Will use / to separate Exchange Acronym entries.
     *
     * @return formatted string for storing in database
     */
    public String toDatabaseString() {
        if (databaseString == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < exchangeAcronyms.length; i++) {
                if (i > 0) {
                    buffer.append(EXCHANGE_ACRONYM_DELIMITER);
                }
                buffer.append(exchangeAcronyms[i].toDatabaseString());
            }
            databaseString = buffer.toString();
        }
        return databaseString;
    }

}