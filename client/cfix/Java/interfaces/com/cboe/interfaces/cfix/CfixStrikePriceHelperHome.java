package com.cboe.interfaces.cfix;

/**
 * CfixStrikePriceHelperHome.java
 *
 * @author Dmitry Volpyansky
 *
 */

public interface CfixStrikePriceHelperHome
{
    public final static String HOME_NAME = "CfixStrikePriceHelperHome";

    public CfixStrikePriceHelper find();
    public void loadStrikePriceFile();
}
