package com.cboe.presentation.api;

import java.util.*;

import com.cboe.exceptions.*;


public abstract class TradingSessionFinderFactory {
    private static TradingSessionFinder finder = null;

    public TradingSessionFinderFactory()
    {
        super();
    }

    public static TradingSessionFinder create()
    {
        return find();
    }

    public static synchronized TradingSessionFinder find()
    {
        if (finder == null)
        {
            finder = new TradingSessionFinder();
        }

        return finder;
    }

}