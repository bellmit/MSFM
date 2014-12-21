package com.cboe.interfaces.domain;

/**
 *@author  David Hoag
 *@created  October 11, 2001
 */
public interface OpenInterestData
{
	public com.cboe.idl.cmiUtil.DateTimeStruct getExpirationDate();
	/**
	 *  Gets the openInterest attribute of the OpenInterestData object
	 *
	 *@return  The openInterest value
	 */
	public int getOpenInterest();
	public String getSymbol();
}
