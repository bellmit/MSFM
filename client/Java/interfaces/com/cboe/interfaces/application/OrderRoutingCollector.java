//
// -----------------------------------------------------------------------------------
// Source file: OrderRoutingCollector.java
//
// PACKAGE: com.cboe.interfaces.application
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.SessionBasedCollector;
import com.cboe.interfaces.ohsEvents.OrderRoutingConsumer;

public interface OrderRoutingCollector extends SessionBasedCollector, OrderRoutingConsumer
{
}