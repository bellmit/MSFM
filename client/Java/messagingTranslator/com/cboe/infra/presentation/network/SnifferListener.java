//
// -----------------------------------------------------------------------------------
// Source file: SnifferListener.java
//
// PACKAGE: com.cboe.infra.presentation.nodelist
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

/**
 * 
 */
public interface SnifferListener
{
    /**
     * This method is invoked on the listener when a message is received by a subject sniffer.
     * @param subjectName The name of the subject we are sniffing.
     * @param message The plain-text (unmarshalled) contents of the message.
     */
    public void messageSniffed(String subjectName, String message);

    /**
     * This method is invoked on the listener when a subject sniffer starts sniffing.
     */
    public void sniffingStarted();

    /**
     * This method is invoked on the listener when the sniffer stops sniffing.  Sniffers stop
     * sniffing after preset time periods, the longest being 60 seconds (as of this writing).
     */
    public void sniffingEnded();
}