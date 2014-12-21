/*
 *  Copyright 2005
 *
 *  CBOE
 *  All rights reserved
 */

package com.cboe.presentation.common.exchange;

import com.cboe.interfaces.presentation.common.exchange.ExchangeFactory;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

/**
 * Used for building ExchangeFactorys for diffent app bases.
 *
 * @author Joy Kyriakopulos
 * @version Jun 15, 2005
 */
public class GUIExchangeHome
{
    private static com.cboe.interfaces.presentation.common.exchange.ExchangeFactory exchangeFactory = null;

    /**
     *  Creates the appropriate API Factory based on class name passed in.
     *
     *@param  className  fully qualified class name that implements
     *      OrderBookAPIFactory.
     */
    public static void create(String className)
    {
        try
        {
            Class theClass = Class.forName(className);
            create(theClass);
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    /**
     *  Creates the appropriate API Factory based on class passed in.
     *
     *@param  theClass  Description of Parameter
     */
    public static void create(Class theClass)
    {
        try
        {

            Class interfaceClass = ExchangeFactory.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                exchangeFactory = (ExchangeFactory) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("Does not support interface com.cboe.interfaces.presentation.common.exchange.ExchangeFactory. className = " + theClass);
            }
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    /**
     *  Call the instantiated classes find method to obtain the
     *  OrderBookAPIFactory.
     *
     *@return    Description of the Returned Value
     */
    public static ExchangeFactory find()
    {
        if (exchangeFactory != null)
        {
            return exchangeFactory;
        }
        else
        {
            throw new IllegalStateException("GUIExchangeHome: Create has not been called yet.");
        }
    }

}
