/*
 *  Copyright 2002
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.interfaces.presentation.user;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 *  Description of the Interface
 *
 *@author     Nick DePasquale
 *@created    February 1, 2002
 */
public interface ExchangeFirm extends BusinessModel, Comparable
{
    /**
     *  Gets the Firm attribute of the ExchangeFirm object
     *
     *@return    The Firm value
     */
    public String getFirm();

    /**
     *  Gets the Exchange attribute of the ExchangeFirm object
     *
     *@return    The Exchange value
     */
    public String getExchange();

    /**
     *  Gets the ExchangeFirmStruct attribute of the ExchangeFirm object
     *
     *@return    The ExchangeFirmStruct value
     */
    public ExchangeFirmStruct getExchangeFirmStruct();

}
