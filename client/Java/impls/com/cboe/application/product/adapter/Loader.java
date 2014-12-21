package com.cboe.application.product.adapter;

import com.cboe.exceptions.AuthorizationException;
import com.cboe.exceptions.CommunicationException;
import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.SystemException;

public interface Loader
{
    void load(Integer key) throws SystemException, CommunicationException, AuthorizationException, DataValidationException;

}
