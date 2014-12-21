package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ExchangeAcronymFormatStrategy;
import com.cboe.interfaces.presentation.user.ExchangeAcronym;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.presentation.common.formatters.Formatter;

public class ExchangeAcronymFormatter extends Formatter implements ExchangeAcronymFormatStrategy
{

    public ExchangeAcronymFormatter()
    {
        super();
        addStyle(FULL, FULL_DESC);
        addStyle(BRIEF, BRIEF_DESC);

        setDefaultStyle(BRIEF);
    }

    public String format(ExchangeAcronym exchangeFirm)
    {
        return format(exchangeFirm.getExchangeAcronymStruct(), getDefaultStyle());
    }

    public String format(ExchangeAcronym exchangeFirm, String styleName)
    {
        return format(exchangeFirm.getExchangeAcronymStruct(), styleName);
    }

    public String format(ExchangeAcronymStruct exchangeFirmStruct)
    {
        return format(exchangeFirmStruct, getDefaultStyle());
    }

    public String format(ExchangeAcronymStruct exchangeAcronymStruct, String styleName)
    {
        validateStyle(styleName);
        String retVal = "";

        // only 2 styles -- FULL and BRIEF
        if (styleName.equals(FULL))
        {
            retVal = "Exchange: " + exchangeAcronymStruct.exchange + ", User Acronym: " + exchangeAcronymStruct.acronym;
        }
        else
        {
            if(exchangeAcronymStruct.exchange.trim().length() > 0 || exchangeAcronymStruct.acronym.trim().length() > 0)
                retVal = exchangeAcronymStruct.exchange + "." + exchangeAcronymStruct.acronym;
            else
                retVal = "";
        }

        return retVal;
    }
}
