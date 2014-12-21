package com.cboe.presentation.common.formatters;

import com.cboe.interfaces.presentation.common.formatters.ExchangeFirmFormatStrategy;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.presentation.common.formatters.Formatter;

public class ExchangeFirmFormatter extends Formatter implements ExchangeFirmFormatStrategy
{

    public ExchangeFirmFormatter()
    {
        super();
        addStyle(FULL, FULL_DESC);
        addStyle(BRIEF, BRIEF_DESC);

        setDefaultStyle(BRIEF);
    }

    public String format(ExchangeFirm exchangeFirm)
    {
        return format(exchangeFirm.getExchangeFirmStruct(), getDefaultStyle());
    }

    public String format(ExchangeFirm exchangeFirm, String styleName)
    {
        return format(exchangeFirm.getExchangeFirmStruct(), styleName);
    }

    public String format(ExchangeFirmStruct exchangeFirmStruct)
    {
        return format(exchangeFirmStruct, getDefaultStyle());
    }

    public String format(ExchangeFirmStruct exchangeFirmStruct, String styleName)
    {
        validateStyle(styleName);
        String retVal = "";

        // only 2 styles -- FULL and BRIEF
        if (styleName.equals(FULL))
        {
            retVal = "Exchange: " + exchangeFirmStruct.exchange + ", Firm: " + exchangeFirmStruct.firmNumber;
        }
        else
        {
            if(exchangeFirmStruct.exchange.trim().length() > 0 || exchangeFirmStruct.firmNumber.trim().length() > 0)
                retVal = exchangeFirmStruct.exchange + "." + exchangeFirmStruct.firmNumber;
            else
                retVal = "";
        }

        return retVal;
    }
}
