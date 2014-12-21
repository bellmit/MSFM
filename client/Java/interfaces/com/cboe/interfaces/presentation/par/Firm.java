//
// -----------------------------------------------------------------------------------
// Source file: Firm.java
//
// PACKAGE: com.cboe.interfaces.presentation.par
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2011 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.par;

/**
 * Using this simple Firm wrapper (contains only firmAcronym and firmNumber data) instead of
 * FirmModel because the PAR client app may save PAR Profiles with invalid firm data (i.e., the
 * firm doesn't exist in the CBOEdirect system), and we still need the SAGUI to be able to handle
 * with that invalid data.
 */
public interface Firm extends Comparable<Firm>
{
    String PREF_PARTS_SEPARATOR = ":";

    String getFirmAcronym();
    String getFirmNumber();

    /**
     * Returns the String representing this Firm to be stored in a PARBrokerProfile preference value.
     */
    String toPrefString();
}