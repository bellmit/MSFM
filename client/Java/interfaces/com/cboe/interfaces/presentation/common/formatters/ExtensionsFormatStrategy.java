//
// ------------------------------------------------------------------------
// FILE: ExtensionsFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
//
// ------------------------------------------------------------------------
//
package com.cboe.interfaces.presentation.common.formatters;

public interface ExtensionsFormatStrategy extends CommonFormatStrategyStyles
{
    String format(String extensionsField);
    String format(String extensionsField, String style);
}
