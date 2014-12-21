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
public interface ExchangeFirmModel extends MutableBusinessModel, ExchangeFirm
{

    /**
     *  Sets the Firm attribute of the ExchangeFirmModel object
     *
     *@param  aFirm  The new Firm value
     */
    public void setFirm(String aFirm);

    /**
     *  Sets the Exchange attribute of the ExchangeFirmModel object
     *
     *@param  anExchange  The new Exchange value
     */
    public void setExchange(String anExchange);

}
