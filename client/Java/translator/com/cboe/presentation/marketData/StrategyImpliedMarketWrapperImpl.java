//
// -----------------------------------------------------------------------------------
// Source file: StrategyImpliedMarketWrapperImpl.java
//
// PACKAGE: com.cboe.presentation.marketData
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.marketData;

import com.cboe.interfaces.presentation.marketData.StrategyImpliedMarketWrapper;

public class StrategyImpliedMarketWrapperImpl implements StrategyImpliedMarketWrapper
{
    private double impliedOpposite;
    private double impliedSame;

    public StrategyImpliedMarketWrapperImpl(double impliedOpposite, double impliedSame)
    {
        this.impliedOpposite = impliedOpposite;
        this.impliedSame = impliedSame;
    }

    public double getImpliedOpposite()
    {
        return impliedOpposite;
    }

    public double getImpliedSame()
    {
        return impliedSame;
    }
}
