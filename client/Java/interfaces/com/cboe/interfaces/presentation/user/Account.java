/*
 *  Copyright 2002
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.interfaces.presentation.user;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;

/**
 *  Description of the Interface
 *
 *@author     Nick DePasquale
 *@created    February 1, 2002
 */
public interface Account extends BusinessModel
{
    /**
     *  Gets the Account attribute of the Account object
     *
     *@return    The Account value
     */
    String getAccount();

    /**
     *  Gets the ExchangeFirm attribute of the Account object
     *
     *@return    The ExchangeFirm value
     */
    ExchangeFirm getExchangeFirm();
}
