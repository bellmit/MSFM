//
// -----------------------------------------------------------------------------------
// Source file: ContingencyFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiOrder.OrderContingencyStruct;
import com.cboe.interfaces.domain.routingProperty.common.ContingencyType;

/**
 * Title:        ContingencyFormatStrategy
 * Description:  Interface for a contingency formatter
 * Copyright:    Copyright (c) 2001
 * Company:      Chicago Board Options Exchange
 * @author Luis Torres
 * @version 1.0
 */

public interface ContingencyFormatStrategy extends FormatStrategy
{
    final public static String BRIEF="BRIEF";
    final public static String FULL="FULL";
    final public static String BRIEF_DESC="Do not display a label for NONE";
    final public static String FULL_DESC="Display a label for all contingencies (including NONE)";

    public String format(OrderContingencyStruct orderContingency);
    public String format(OrderContingencyStruct orderContingency,String styleName);
    
    public String format(ContingencyType contingency);
}