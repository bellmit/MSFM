package com.cboe.interfaces.domain;

import com.cboe.interfaces.domain.product.OptionType;

/**
 * User: Uma Diddi
 * Date: Oct 5, 2005
 */
public interface OptionOpenInterestData
 {
    public Price getExercisePrice();
    public com.cboe.idl.cmiUtil.DateTimeStruct getExpirationDate();
    public OptionType getOptionType();

	/**
	 *  Gets the openInterest attribute of the OpenInterestData object
	 *
	 *@return  The openInterest value
	 */
	public int getOpenInterest();
	public String getSymbol();
}
