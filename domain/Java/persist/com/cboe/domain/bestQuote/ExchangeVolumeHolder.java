package com.cboe.domain.bestQuote;

import com.cboe.domain.util.MarketDataStructBuilder;
import com.cboe.idl.cmiMarketData.ExchangeVolumeStruct;
import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;
import java.util.StringTokenizer;

/**
 * Allow ExchangeVolume array to be written to and read from a single database column.
 *
 * @see MarketVolumeHolder
 * 
 * @author Steven Sinclair
 */
public class ExchangeVolumeHolder implements SqlScalarType
{
    private ExchangeVolumeStruct[]  exchangeVolumes;
    
    private String databaseString;
    
    private static final String ARRAY_DELIMITER = ","; // (separate ExchangeVolumeStructs)
    private static final char FIELD_DELIMITER = ':'; // (separate exchange and volume fields)
    private static final ExchangeVolumeStruct[] EMPTY_ARRAY = new ExchangeVolumeStruct[0];

	static
	{
		/*
		 * Register anonymous inner class as generator for type
		 */
		SqlScalarTypeFactory.registerGenerator(new SqlScalarTypeGeneratorIF()
		{
			/*
			 * Creates instance of date from string
			 */
			public SqlScalarType createInstance(String values)
			{
				return new ExchangeVolumeHolder(values);
			}
			/*
			 * Returns this class as generated type.
			 */
			public Class typeGenerated()
			{
				return ExchangeVolumeHolder.class;
			}
		});
  
	}   // end of static block
 
    public ExchangeVolumeHolder(ExchangeVolumeStruct[] exchangeVolumes)
    {
        this.exchangeVolumes = exchangeVolumes;
    }
    
    public ExchangeVolumeHolder(String dataBaseString)
    {
        this.databaseString = dataBaseString;
        StringTokenizer tokenizer = new StringTokenizer(this.databaseString, ARRAY_DELIMITER);
        exchangeVolumes = new ExchangeVolumeStruct[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens())
        {
            exchangeVolumes[i++] = parseStruct(tokenizer.nextToken());
        }
    }
    
    public ExchangeVolumeHolder()
    {
        this(EMPTY_ARRAY);
    }

    /**
     * Get exchange volumes being held.
     */
    public ExchangeVolumeStruct[] getExchangeVolumes()
    {
        return exchangeVolumes;
    }
        
    /**
     * Formats this object for storing in database.  Will use / to separate exchange volume entries.
     *
     * @return formatted string for storing in database
     */
    public String toDatabaseString()
    {
        if (databaseString == null)
        {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < exchangeVolumes.length; i++)
            {
                if (i > 0)
                {
                    buffer.append(ARRAY_DELIMITER);
                }
                appendStruct(buffer, exchangeVolumes[i]);
            }
            databaseString = buffer.toString();
        }
    	return databaseString;
    }
    
    protected ExchangeVolumeStruct parseStruct(String recordString)
    {
        int idx = recordString.indexOf(FIELD_DELIMITER);
        //ExchangeVolumeStruct struct = new ExchangeVolumeStruct();
        ExchangeVolumeStruct struct = MarketDataStructBuilder.getExchangeVolumeStruct(recordString.substring(0, idx), Integer.parseInt(recordString.substring(idx+1, recordString.length()))) ;

        //struct.exchange = recordString.substring(0, idx);
        //struct.volume = Integer.parseInt(recordString.substring(idx+1, recordString.length()));
        return struct;
    }

    protected void appendStruct(StringBuffer buffer, ExchangeVolumeStruct struct)
    {
        buffer.append(struct.exchange);
        buffer.append(FIELD_DELIMITER);
        buffer.append(struct.volume);
    }
}
