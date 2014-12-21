//
// -----------------------------------------------------------------------------------
// Source file: ErrorCodeFormatStrategy.java
//
// PACKAGE: com.cboe.interfaces.presentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.common.formatters;

import com.cboe.idl.cmiConstants.StrategyTypesOperations;

/**
 * Defines a contract for a class that formats a strategy type.
 *
 * @author Thomas Morrow
 */
@SuppressWarnings({"ConstantNamingConvention"})
public interface StrategyTypeFormatStrategy extends FormatStrategy
{
    String UPPER_CASE_FORMAT = "UPPER_CASE";
    String UPPER_CASE_FORMAT_DESC = "Upper Case";
    String CAPITALIZED_FORMAT = "CAPITALIZED";
    String CAPITALIZED_FORMAT_DESC = "Capitalized";

    short UNKNOWN = StrategyTypesOperations.UNKNOWN;
    short STRADDLE = StrategyTypesOperations.STRADDLE;
    short PSEUDO_STRADDLE = StrategyTypesOperations.PSEUDO_STRADDLE;
    short VERTICAL = StrategyTypesOperations.VERTICAL;
    short RATIO = StrategyTypesOperations.RATIO;
    short TIME = StrategyTypesOperations.TIME;
    short DIAGONAL = StrategyTypesOperations.DIAGONAL;
    short COMBO = StrategyTypesOperations.COMBO;
    short BUY_WRITE = StrategyTypesOperations.BUY_WRITE;

    String UNKNOWN_STRING = "Unknown";
    String STRADDLE_STRING = "Straddle";
    String PSEUDO_STRADDLE_STRING = "Pseudo Straddle";
    String VERTICAL_STRING = "Vertical";
    String RATIO_STRING = "Ratio";
    String TIME_STRING = "Time";
    String DIAGONAL_STRING = "Diagonal";
    String COMBO_STRING = "Combo";
    String BUY_WRITE_STRING = "Buy Write";
    String UNDEFINED_STRING = "Undefined";


    String format(Short aShort);

    String format(Short aShort, String styleName);
}