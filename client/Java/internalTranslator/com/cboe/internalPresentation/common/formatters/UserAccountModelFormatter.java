//
// -----------------------------------------------------------------------------------
// Source file: UserAccountModelFormatter.java
//
// PACKAGE: com.cboe.internalPresentation.common.formatters
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.internalPresentation.common.formatters;

import com.cboe.interfaces.internalPresentation.common.formatters.UserAccountModelFormatStrategy;
import com.cboe.interfaces.internalPresentation.user.UserAccountModel;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;

import com.cboe.presentation.common.formatters.Formatter;

public class UserAccountModelFormatter extends Formatter implements UserAccountModelFormatStrategy
{
    UserAccountModelFormatter()
    {
        addStyle(ACRONYM_EXCHANGE_FULL_NAME_STYLE, ACRONYM_EXCHANGE_FULL_NAME_DESCRIPTION);
        addStyle(ACRONYM_EXCHANGE_STYLE, ACRONYM_EXCHANGE_DESCRIPTION);
        addStyle(USER_ID_FULL_NAME_STYLE, USER_ID_FULL_NAME_DESCRIPTION);
        addStyle(USER_ID_ACRONYM_EXCHANGE_STYLE, USER_ID_ACRONYM_EXCHANGE_DESCRIPTION);
        addStyle(FULL_NAME_USER_ID_STYLE, FULL_NAME_USER_ID_DESCRIPTION);
        addStyle(FULL_NAME_ACRONYM_EXCHANGE_STYLE, FULL_NAME_ACRONYM_EXCHANGE_DESCRIPTION);

        setDefaultStyle(USER_ID_FULL_NAME_STYLE);
    }

    public String format(UserAccountModel user)
    {
        return format(user, getDefaultStyle());
    }

    public String format(UserAccountModel user, String styleName)
    {
        StringBuffer buffer = new StringBuffer(50);

        if(ACRONYM_EXCHANGE_FULL_NAME_STYLE.equals(styleName))
        {
            buffer.append(format(user, ACRONYM_EXCHANGE_STYLE));
            buffer.append(" (").append(user.getFullName()).append(')');
        }
        else if(ACRONYM_EXCHANGE_STYLE.equals(styleName))
        {
            ExchangeAcronym exchAcro = user.getExchangeAcronym();
            buffer.append(exchAcro.getAcronym()).append('-').append(exchAcro.getExchange());
        }
        else if(USER_ID_FULL_NAME_STYLE.equals(styleName))
        {
            buffer.append(user.getUserId());
            buffer.append(" (").append(user.getFullName()).append(')');
        }
        else if(USER_ID_ACRONYM_EXCHANGE_STYLE.equals(styleName))
        {
            buffer.append(user.getUserId());
            buffer.append(" (").append(format(user, ACRONYM_EXCHANGE_STYLE)).append(')');
        }
        else if(FULL_NAME_USER_ID_STYLE.equals(styleName))
        {
            buffer.append(user.getFullName());
            buffer.append(" (").append(user.getUserId()).append(')');
        }
        else if(FULL_NAME_ACRONYM_EXCHANGE_STYLE.equals(styleName))
        {
            buffer.append(user.getFullName());
            buffer.append(" (").append(format(user, ACRONYM_EXCHANGE_STYLE)).append(')');
        }
        else if(USER_ID_ACRONYM_EXCHANGE_FULL_NAME_STYLE.equals(styleName))
        {
            buffer.append(user.getUserId());
            // buffer.append(" (").append(format(user, ACRONYM_EXCHANGE_FULL_NAME_STYLE)).append(')');
            ExchangeAcronym exchAcro = user.getExchangeAcronym();
            buffer.append('-').append(exchAcro.getAcronym()).append('-').append(exchAcro.getExchange()).append(" (").append(user.getFullName()).append(')');
        }
        else if(USER_ID_EXCHANGE_FULL_NAME_STYLE.equals(styleName))
        {
            buffer.append(user.getUserId());
            ExchangeAcronym exchAcro = user.getExchangeAcronym();
            buffer.append('-').append(exchAcro.getExchange()).append(" (").append(user.getFullName()).append(") ");
        }
        else
        {
            buffer.append(INVALID_STYLE_STRING).append(':').append(user.getUserId());
        }

        return buffer.toString();
    }
}
