package com.cboe.interfaces.domain;

import com.cboe.exceptions.SystemException;

/**
 *@author  Cognizant Technology Solutions
 *@created  Aug 7, 2007
 */

public interface AllOptionsOpenInterestDataHome
{
    public final static String HOME_NAME = "AllOptionsOpenInterestDataHome";
    
    public OptionOpenInterestData[] findAll( ) throws SystemException;
}