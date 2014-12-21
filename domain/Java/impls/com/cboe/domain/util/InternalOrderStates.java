package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.OrderStatesOperations;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class InternalOrderStates implements OrderStatesOperations {

   public static final short ASSIGNED_TO_AGENT = (short) -1;
   /* Constant Declaration */
   public static final short IPP_EXPOSING = (short) -2;

   public static final short ROUTED_AWAY = (short) -3;
    public static final short AUCTION_WAITING = (short) -4;
    public static final short AUCTION_EXPOSING = (short) -5;
    public static final short INACTIVE_CANCEL_RE_REQUEST_PENDING = (short)-6;

    public InternalOrderStates() {
    }
}