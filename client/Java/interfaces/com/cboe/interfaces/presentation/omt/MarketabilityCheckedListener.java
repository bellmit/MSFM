//
// -----------------------------------------------------------------------------------
// Source file: MessageCollectionListener.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;


/**
 *
 * Listener that is being trigger when a marketability checked has been completed.
 *
 * @author Eric Maheo
 * @since 01/16/2009
 *
 */
public interface MarketabilityCheckedListener
{
    /**
     * Method being called once the event occurs.
     * @param element that was Marketable checked.
     */
    void messageElementMarketabilityUpdated(MessageElement element);
}