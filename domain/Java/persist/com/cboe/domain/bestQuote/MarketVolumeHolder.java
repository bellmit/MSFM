package com.cboe.domain.bestQuote;

import com.objectwave.persist.SqlScalarType;
import com.objectwave.persist.SqlScalarTypeFactory;
import com.objectwave.persist.SqlScalarTypeGeneratorIF;
import com.cboe.idl.cmiConstants.VolumeTypes;

import java.util.StringTokenizer;

/**
 * A class to hold all of the market volumes for a CurrentMarket instance.  MarketVolumes were being
 * treated directly as arrays, but this caused a problem since even read access was causing the array to
 * be copied into the transaction log.  This would sometimes cause transactions to fail if one thread read
 * the current market in a transaction while another thread updated the current market.
 * 
 * Using a holder for the array will make allow read access without copying the value to the transaction log.
 * 
 * @author John Wickberg
 */
public class MarketVolumeHolder implements SqlScalarType {

    private MarketVolume[]  marketVolumes;
    
    private String databaseString;
    
    private static final String VOLUME_DELIMITER = "/";

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
				return new MarketVolumeHolder(values);
			}
			/*
			 * Returns this class as generated type.
			 */
			public Class typeGenerated()
			{
				return MarketVolumeHolder.class;
			}
		});
  
	}   // end of static block
 
    public MarketVolumeHolder(MarketVolume[] marketVolumes) {
        this.marketVolumes = marketVolumes;
    }
    
    public MarketVolumeHolder(String dataBaseString) {
        this.databaseString = dataBaseString;
        StringTokenizer tokenizer = new StringTokenizer(this.databaseString, VOLUME_DELIMITER);
        marketVolumes = new MarketVolume[tokenizer.countTokens()];
        int i = 0;
        while (tokenizer.hasMoreTokens()) {
            marketVolumes[i++] = new MarketVolume(tokenizer.nextToken());
        }
    }
    
    /**
     * Get market volumes being held.
     */
    public MarketVolume[] getMarketVolumes() {
        return marketVolumes;
    }
        
    /**
     * Formats this object for storing in database.  Will use / to separate market volume entries.
     *
     * @return formatted string for storing in database
     */
    public String toDatabaseString() {
        if (databaseString == null) {
            StringBuffer buffer = new StringBuffer();
            for (int i = 0; i < marketVolumes.length; i++) {
                if (i > 0) {
                    buffer.append(VOLUME_DELIMITER);
                }
                buffer.append(marketVolumes[i].toDatabaseString());
            }
            databaseString = buffer.toString();
        }
    	return databaseString;
    }
    
    /**
     * return the total volumes
     */ 
    public int getTotalVolumes(){
        int total = 0;
        for (int i = 0; i < marketVolumes.length; i++ ){
            total = total + marketVolumes[i].getVolume();
        }
        return total;
    }
    
    /**
     * return the total non volume contingent volumes
     */ 
    public int getNonVolumeContingentVolumes(){
        int total = 0;
        for (int i = 0; i < marketVolumes.length; i++ ){
            if ( isVolumeContingentType(marketVolumes[i].getVolumeType()))
            {
                total = total + marketVolumes[i].getVolume();
            }
        }
        return total;
    } 
    
    /**
     * determine if a volume type is a volume contingenct type
     * Note: a volume type is a volume contingent type when it is
     * 1. AON, or
     * 2. FOK.
     */ 
    private boolean isVolumeContingentType(short volumeType){
        return volumeType == VolumeTypes.AON ||
               volumeType == VolumeTypes.FOK;
    }
    
}