package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.FutureProduct;

import com.cboe.idl.cmiSession.SessionProductStruct;


/**
 * SessionFutureProduct implementation.
 */
class SessionFutureProductImpl extends SessionProductImpl implements FutureProduct
{
    /**
     * Constructor
     * @param sessionProductStruct SessionProductStruct
     */
     protected SessionFutureProductImpl(SessionProductStruct sessionProductStruct)
    {
       super(sessionProductStruct);
    }

    /**
     *  Default constructor.
     */
    protected SessionFutureProductImpl()
    {
        super();
    }

}

