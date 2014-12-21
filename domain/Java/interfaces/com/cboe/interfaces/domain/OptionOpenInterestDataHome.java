package com.cboe.interfaces.domain;

import com.cboe.exceptions.SystemException;

/**
 * User: Uma Diddi
 * Date: Oct 5, 2005
 */
public interface OptionOpenInterestDataHome
 {

    public final static String HOME_NAME = "OptionOpenInterestDataHome";

	/**
	 *@return
	 *@exception  com.cboe.exceptions.SystemException
	 */
	public OptionOpenInterestData[] findAll( ) throws SystemException;
}
