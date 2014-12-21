package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.TickerConsumer;
import com.cboe.idl.util.RoutingParameterStruct;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiMarketData.TickerStruct;
import com.cboe.idl.marketData.InternalTickerStruct;
import com.cboe.idl.marketData.InternalTickerDetailStruct;
//
// -----------------------------------------------------------------------------------
// Source file: LargeTradeLastSaleEventConsumerImpl.java
//
// PACKAGE: PACKAGE_NAME
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.

// -----------------------------------------------------------------------------------
public class LargeTradeLastSaleEventConsumerImpl extends com.cboe.idl.events.POA_TickerEventConsumer implements TickerConsumer {
        private TickerConsumer delegate;

        /**
         * constructor comment.
         */
        public LargeTradeLastSaleEventConsumerImpl(TickerConsumer tickerConsumer) {
            super();
            delegate = tickerConsumer;
        }

    /*  Empty implementation for acceptTicker and aceptTickerForClass,
        Since we need only LargeTradelastSale events.*/

    public void acceptTickerForClass(RoutingParameterStruct routing, TimeStruct[] tradeTimes, TickerStruct[] tickers) {

        }

        public void acceptTicker(int[] groups, InternalTickerStruct ticker) {

        }

        public void acceptLargeTradeTickerDetailForClass(RoutingParameterStruct routing, InternalTickerDetailStruct[] tickerDetails) {
            delegate.acceptLargeTradeTickerDetailForClass(routing, tickerDetails);
        }

        /**
         * @author Jeff Illian
         */

        public org.omg.CORBA.Object get_typed_consumer() {
            return null;
        }

        public void push(org.omg.CORBA.Any data)
                throws org.omg.CosEventComm.Disconnected {
        }

        public void disconnect_push_consumer() {
        }
    }


