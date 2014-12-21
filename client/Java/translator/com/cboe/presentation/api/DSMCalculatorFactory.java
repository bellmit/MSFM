//
// -----------------------------------------------------------------------------------
// Source file: DsmCalculatorFactory.java
//
// PACKAGE: com.cboe.presentation.api;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2010 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.api;


public class DSMCalculatorFactory
{
    private static DSMCalculator dsmCalculator = null;

    public DSMCalculatorFactory()
    {
    }

    synchronized public static DSMCalculator find()
    {
        if(dsmCalculator == null)
        {
            dsmCalculator = new DSMCalculator();
        }
        return dsmCalculator ;
    }
}
