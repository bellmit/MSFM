/*
 * Created on Dec 7, 2005
 * -----------------------------------------------------------------------------------
 * Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
 *-----------------------------------------------------------------------------------
 */
package com.cboe.consumers.eventChannel;

import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CosEventComm.Disconnected;

import com.cboe.exceptions.ExceptionDetails;
import com.cboe.idl.icsGroupElementEvents.ICSGroupElementEventConsumerPOA;
import com.cboe.idl.groupElement.ElementStruct;
import com.cboe.idl.groupElement.ElementErrorResultStruct;
import com.cboe.interfaces.events.ICSGroupElementConsumer;

public class ICSGroupElementConsumerProxyImpl extends ICSGroupElementEventConsumerPOA implements ICSGroupElementConsumer
{

    private ICSGroupElementConsumer  delegate;
    public ICSGroupElementConsumerProxyImpl(ICSGroupElementConsumer  delegate)
    {
        super();
        this.delegate = delegate;
    }

    public void acceptGroupElements(long id, ElementStruct[] elementStructs)
    {
        delegate.acceptGroupElements(id, elementStructs);
    }

    public void acceptUpdateElement(long id, ElementStruct elementStruct)
    {
        delegate.acceptUpdateElement(id, elementStruct);
    }

    public void acceptAddElement(long id, long parentKey, ElementStruct elementStruct)
    {
        delegate.acceptAddElement(id, parentKey, elementStruct);
    }

    public void acceptRemoveElement(long id, long parentKey, ElementStruct elementStruct, boolean isRemove)
    {
        delegate.acceptRemoveElement(id, parentKey, elementStruct, isRemove);
    }

    public void acceptGroupElementResults(long id, ElementErrorResultStruct[] elementErrorResultStructs)
    {
        delegate.acceptGroupElementResults(id, elementErrorResultStructs);
    }


    public void acceptAlreadyExistsException(long id, ExceptionDetails exception)
    {
        delegate.acceptAlreadyExistsException(id, exception);
    }

    public void acceptDataValidationException(long id, ExceptionDetails exception)
    {
        delegate.acceptDataValidationException(id, exception);

    }

    public void acceptNotFoundException(long id, ExceptionDetails exception)
    {
        delegate.acceptNotFoundException(id, exception);
    }

    public void acceptNotAcceptedException(long id, ExceptionDetails exception)
    {
        delegate.acceptNotAcceptedException(id, exception);
    }

    public void acceptSystemException(long id, ExceptionDetails exception)
    {
        delegate.acceptSystemException(id, exception);
    }

    public void acceptTransactionFailedException(long id, ExceptionDetails exception)
    {
        delegate.acceptTransactionFailedException(id, exception);
    }

    public Object get_typed_consumer()
    {
        return null;
    }

    public void push(Any arg0) throws Disconnected
    {
    }

    public void disconnect_push_consumer()
    {
    }

}