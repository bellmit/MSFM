package com.cboe.presentation.product;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

/**
 * Date: June 23, 2010
 */
public class ProductClassFactoryHome
{
    private static ProductClassFactoryInterface factory;

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

    public static void create(Class theClass)
    {
        try
        {
            Class interfaceClass = com.cboe.presentation.product.ProductClassFactoryInterface.class;

            Object newOBJ = theClass.newInstance();

            if (interfaceClass.isInstance(newOBJ))
            {
                factory = (ProductClassFactoryInterface) newOBJ;
            }
            else
            {
                throw new IllegalArgumentException("ProductClassFactoryHome: class '" + theClass +
                        "' does not support interface '"+interfaceClass+"'");
            }
        }
        catch (Exception e)
        {
            DefaultExceptionHandlerHome.find().process(e);
        }
    }

    public static ProductClassFactoryInterface find()
    {
        if(factory != null)
        {
            return factory;
        }
        else
        {
            throw new IllegalStateException("ProductClassFactoryHome: Create has not been called yet.");
        }
    }
}
