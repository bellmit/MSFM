package com.cboe.presentation.exampleStructs;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.presentation.user.ExchangeFirmFactory;


public class ExampleExchangeFirm
{
    static final String defaultExchange = "CBOE";
    static final String defaultFirm = "123";


    /**
     * ExampleClassStruct constructor comment.
     */
    public ExampleExchangeFirm ()
    {
        super ();
    }

    static public ExchangeFirmStruct getExampleDefaultExchangeFirmStruct ()
    {
        return new ExchangeFirmStruct(defaultExchange, defaultFirm);
    }

    static protected ExchangeFirm getExampleExchangeFirm(ExchangeFirmStruct anExchangeFirmStruct)
    {
        return ExchangeFirmFactory.createExchangeFirm(anExchangeFirmStruct);
    }

    static public ExchangeFirm getExampleDefaultExchangeFirm()
    {
        return  getExampleExchangeFirm(getExampleDefaultExchangeFirmStruct());
    }

    static public ExchangeFirm getExampleCBOTExchangeFirm()
    {
        ExchangeFirmStruct anExchangeFirmStruct;
        anExchangeFirmStruct = getExampleDefaultExchangeFirmStruct();

        anExchangeFirmStruct.exchange = "CBOT";
        anExchangeFirmStruct.firmNumber = "456";

        return  getExampleExchangeFirm(anExchangeFirmStruct);

    }


}
