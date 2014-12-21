/*
 * Created by IntelliJ IDEA.
 * User: torresl
 * Date: Oct 2, 2002
 * Time: 5:21:05 PM
 */
package com.cboe.interfaces.intermarketPresentation.api;

public interface IntermarketAPI extends IntermarketQueryAPI, NBBOAgentAPI
{
    public static final String INTERMARKET_TRANSLATOR_NAME = "INTERMARKETTRANSLATOR";
    public static final String ALLOW_INTERMARKET_ACCESS_PROPERTY_NAME = "AllowIntermarketAPIAccess";
}
