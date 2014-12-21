package com.cboe.domain.util;

import com.cboe.idl.cmiConstants.ExtensionFieldsOperations;

/**
 * Title:
 * Description:
 * Copyright:    Copyright (c) 2001
 * Company:
 * @author
 * @version 1.0
 */

public class InternalExtensionFields implements ExtensionFieldsOperations
{

   /* Constant Declaration */
   public static final String   INTERNAL_SOURCE = "InternalSource";
   
   /* New cross product leg trading extension tags */
   public static final String  MIN_CONTINGENT_QTY   = "MinCQty";
   public static final String  MAX_CONTINGENT_QTY   = "MaxCQty";
   public static final String  NON_CONTINGENT_QTY   = "NonCQty";   
   public static final String  EQUITY_LEG_RATIO_QTY = "EquityLegRatioQty"; 
   public static final String  CPS_LEGGING_ORDER    = "StrategyProductKey";

   /* Send new from OrderCache extension tag */
   public static final String SEND_NEW = "SendNew";

    public InternalExtensionFields()
    {
    }

}