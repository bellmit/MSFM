package com.cboe.interfaces.domain;

import com.cboe.interfaces.domain.product.OptionType;

/**
 *@author  Cognizant Technology Solutions
 *@created  Aug 7, 2007
 */

public interface AllOptionsOpenInterestData {
		
	    /**
	     * @return price
	     */
	    public Price getExercisePrice();
	    /**
	     * @return ExpirationDate
	     */
	    public com.cboe.idl.cmiUtil.DateTimeStruct getExpirationDate();
	    /**
	     * @return OptionType
	     */
	    public OptionType getOptionType();
	    /**
	     * @return OpenInterest value
	     */
	    public int getOpenInterest();
	    /**
	     * @return OpenInterestUpdateTime
	     */
		public com.cboe.idl.cmiUtil.DateTimeStruct getOpenInterestUpdateTime();
		/**
	     * @return String
	     */
		public String getSymbol();
	}


