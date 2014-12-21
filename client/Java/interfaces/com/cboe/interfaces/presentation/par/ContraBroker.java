//
// -----------------------------------------------------------------------------------
// Source file: ContraBroker.java
//
// PACKAGE: com.cboe.interfaces.presentation.par
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.par;

/**
 * Represents a Contra Broker as it is stored in a PARBrokerProfile preference.  The Contra Broker
 * is defined as having a broker userID and Firm.
 */
public interface ContraBroker extends Comparable<ContraBroker>
{
    String PREF_PARTS_SEPARATOR = ":";

    String getBrokerUserID();
    Firm getFirm();
    String toPrefString();
}