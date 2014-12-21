package com.cboe.interfaces.domain;

import com.cboe.idl.property.PropertyStruct;
import com.cboe.exceptions.*;

/**
 * Created by IntelliJ IDEA.
 * User: EbrahimR
 * Date: Jun 20, 2003
 * Time: 5:11:33 PM
 * To change this template use Options | File Templates.
 */
public interface Property
{
    String getPropertyName();

    String getPropertyValue();

    long getPropertyGroup();

    public void setProperty(PropertyStruct propertyStruct) throws SystemException, CommunicationException, AuthorizationException, TransactionFailedException, DataValidationException;

    int getPropertyIndex();



}
