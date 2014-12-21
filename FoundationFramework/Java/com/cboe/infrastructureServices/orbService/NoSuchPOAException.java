package com.cboe.infrastructureServices.orbService;
/**
 */
public class NoSuchPOAException extends java.lang.Exception
{
    public NoSuchPOAException(String name)
    {
        super("No POA with the name of " + name + " could be found.");
    }
}