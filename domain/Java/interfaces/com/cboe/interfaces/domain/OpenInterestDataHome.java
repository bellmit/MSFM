package com.cboe.interfaces.domain;

import com.cboe.exceptions.NotFoundException;
import com.cboe.exceptions.SystemException;
/**
 *@author  David Hoag
 *@created  October 11, 2001
 */
public interface OpenInterestDataHome
{
	public final static String HOME_NAME = "OpenInterestDataHome";
	/**
	 *@param  sessionName
	 *@return
	 *@exception  SystemException
	 */
	public OpenInterestData[] findAll( ) throws SystemException;
}
