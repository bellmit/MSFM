package com.cboe.domain.util;

import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;

import java.util.StringTokenizer;

/**
 * ExchangeVolumeHolder
 *
 * @author baranski
 * @date Oct 28, 2002
 */

public class ExchangeVolumeHolder implements SqlScalarType {

    private ExchangeVolume[] exchangeVolumes;

    private String databaseString;

    private static final String VOLUME_DELIMITER = "/";

    static {
        /*
		 * Register anonymous inner class as generator for type
		 */
        SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF() {
            /*
			 * Creates instance of date from string
			 */
            public SqlScalarType createInstance(String values) {
                return new ExchangeVolumeHolder(values);
            }

            /*
			 * Returns this class as generated type.
			 */
            public Class typeGenerated() {
                return ExchangeVolumeHolder.class;
            }
        });

    }   // end of static block

    public ExchangeVolumeHolder(ExchangeVolume[] exchangeVolumes) {
        this.exchangeVolumes = exchangeVolumes;
    }

    public ExchangeVolumeHolder(String dataBaseString) {
        this.databaseString = dataBaseString;
        StringTokenizer tokenizer = new StringTokenizer(this.databaseString, VOLUME_DELIMITER);
        exchangeVolumes = new ExchangeVolume[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            exchangeVolumes[i++] = new ExchangeVolume(tokenizer.nextToken());
        }
    }

    /**
     * Get exchange volumes being held.
     */
    public ExchangeVolume[] getExchangeVolumes() {        
        return exchangeVolumes;
    }

    /**
     * Formats this object for storing in database.  Will use / to separate exchange volume entries.
     *
     * @return formatted string for storing in database
     */
    public String toDatabaseString() {
        if (databaseString == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < exchangeVolumes.length; i++) {
                if (i > 0) {
                    buffer.append(VOLUME_DELIMITER);
                }
                buffer.append(exchangeVolumes[i].toDatabaseString());
            }
            databaseString = buffer.toString();
        }
        return databaseString;
    }

}
