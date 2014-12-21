/*
 *  Copyright 2002
 *
 *  CBOE
 *  All rights reserved
 */
package com.cboe.interfaces.presentation.user;

import com.cboe.interfaces.presentation.common.businessModels.MutableBusinessModel;

/**
 *  Description of the Interface
 *
 *@author     Nick DePasquale
 *@created    February 1, 2002
 */
public interface AccountModel extends MutableBusinessModel, Account
{
    /**
     *  Sets the Account attribute of the AccountModel object
     *
     *@param  account  The new Account value
     */
    void setAccount(String account);

    /**
     *  Sets the ExchangeFirm attribute of the AccountModel object
     *
     *@param  exchangeFirm  The new ExchangeFirm value
     */
    void setExchangeFirm(ExchangeFirm exchangeFirm);
}
