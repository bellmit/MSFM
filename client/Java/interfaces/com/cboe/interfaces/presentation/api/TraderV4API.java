//
// -----------------------------------------------------------------------------------
// Source file: TraderV4API.java
//
// PACKAGE: com.cboe.interfaces.presentation.api
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.api;

/**
 * Extends the TraderAPI, and any cmiV4 interfaces.
 */
public interface TraderV4API extends TraderAPI, 
	MarketQueryV4API, MarketQueryV5API
{
}

