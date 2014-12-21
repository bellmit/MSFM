//
// -----------------------------------------------------------------------------------
// Source file: ExchangeFactoryImpl.java
//
// PACKAGE: com.cboe.presentation.user;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.user;

import com.cboe.idl.cmiConstants.ExchangeStrings;

import com.cboe.interfaces.presentation.common.exchange.Exchange;
import com.cboe.interfaces.presentation.user.ExchangeModel;
import com.cboe.presentation.common.exchange.AbstractExchangeFactory;

public class ExchangeFactoryImpl extends AbstractExchangeFactory
{
    private static final ExchangeModel AMEXExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.AMEX, "American Stock Exchange");
    private static final ExchangeModel BSEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.BSE, "Boston Stock Exchange");
    private static final ExchangeModel CBOEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.CBOE, "Chicago Board Options Exchange");
    private static final ExchangeModel CBOE2ExchangeModelImpl =
            new ExchangeModelImpl(ExchangeStrings.CBOE2, "Chicago Board Options Exchange 2");
    private static final ExchangeModel EDGAExchangeModelImpl =
            new ExchangeModelImpl("EDGA", "EDGA Exchange");
    private static final ExchangeModel EDGXExchangeModelImpl =
            new ExchangeModelImpl("EDGX", "EDGX Exchange");
    private static final ExchangeModel BATSExchangeModelImpl =
            new ExchangeModelImpl(ExchangeStrings.BATS, "BATS Exchange");
    private static final ExchangeModel CBOTExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.CBOT, "Chicago Board of Trade");
    private static final ExchangeModel CFEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.CFE, "CBOE Futures Exchange");
    private static final ExchangeModel CHXExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.CHX, "Chicago Stock Exchange");
    private static final ExchangeModel CMEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.CME, "Chicago Mercantile Exchange");
    private static final ExchangeModel CSEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.CSE, "Cincinnati Stock Exchange");
    private static final ExchangeModel ISEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.ISE, "International Stock Exchange");
    private static final ExchangeModel LIFFEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.LIFFE, "International Financial Futures and Options Exchange");
    private static final ExchangeModel NASDExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.NASD, "National Association of Securities Dealers");
    private static final ExchangeModel NYMEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.NYME, "New York Mercantile Exchange");
    private static final ExchangeModel NYSEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.NYSE, "New York Stock Exchange");
    private static final ExchangeModel ONEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.ONE, "OneChicago Exchange");
    private static final ExchangeModel PHLXExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.PHLX, "Philadelphia Stock Exchange");
    private static final ExchangeModel PSEExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.PSE, "Pacific Stock Exchange");
    private static final ExchangeModel NQLXExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.NQLX, "Nasdaq Liffe Markets");
    private static final ExchangeModel BOXExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.BOX, "Boston Options Exchange");
    private static final ExchangeModel NSXExchangeModelImpl =
            new ExchangeModelImpl( ExchangeStrings.NSX, "National Stock Exchange");
    private static final ExchangeModel NASDAQExchangeModelImpl=
            new ExchangeModelImpl( ExchangeStrings.NASDAQ, "National Association of Securities Dealers Automated Quotation");

    private static final ExchangeModel unspecifiedExchangeModelImpl =
            new ExchangeModelImpl("", "Unspecified Exchange for no Exchange");

    public static final Exchange UNSPECIFIED_EXCHANGE = unspecifiedExchangeModelImpl;

    public static final Exchange AMEXExchangeImpl = AMEXExchangeModelImpl;
    public static final Exchange BSEExchangeImpl = BSEExchangeModelImpl;
    public static final Exchange CBOEExchangeImpl = CBOEExchangeModelImpl;
    public static final Exchange CBOE2ExchangeImpl = CBOE2ExchangeModelImpl;
    public static final Exchange EDGAExchangeImpl = EDGAExchangeModelImpl;
    public static final Exchange EDGXExchangeImpl = EDGXExchangeModelImpl;
    public static final Exchange BATSExchangeImpl = BATSExchangeModelImpl;
    public static final Exchange CBOTExchangeImpl = CBOTExchangeModelImpl;
    public static final Exchange CFEExchangeImpl = CFEExchangeModelImpl;
    public static final Exchange CHXExchangeImpl = CHXExchangeModelImpl;
    public static final Exchange CMEExchangeImpl = CMEExchangeModelImpl;
    public static final Exchange CSEExchangeImpl = CSEExchangeModelImpl;
    public static final Exchange ISEExchangeImpl = ISEExchangeModelImpl;
    public static final Exchange LIFFEExchangeImpl = LIFFEExchangeModelImpl;
    public static final Exchange NASDExchangeImpl = NASDExchangeModelImpl;
    public static final Exchange NQLXExchangeImpl = NQLXExchangeModelImpl;
    public static final Exchange NYMEExchangeImpl = NYMEExchangeModelImpl;
    public static final Exchange NYSEExchangeImpl = NYSEExchangeModelImpl;
    public static final Exchange ONEExchangeImpl = ONEExchangeModelImpl;
    public static final Exchange PHLXExchangeImpl = PHLXExchangeModelImpl;
    public static final Exchange PSEExchangeImpl = PSEExchangeModelImpl;
    public static final Exchange BOXExchangeImpl = BOXExchangeModelImpl;
    public static final Exchange NSXExchangeImpl = NSXExchangeModelImpl;
    public static final Exchange NASDAQExchangeImpl = NASDAQExchangeModelImpl;

    public static final Exchange[] EXCHANGE_LIST = {AMEXExchangeModelImpl,
                                                    BSEExchangeModelImpl,
                                                    CBOEExchangeModelImpl,
                                                    CBOE2ExchangeModelImpl,
                                                    EDGAExchangeModelImpl,
                                                    EDGXExchangeModelImpl,
                                                    BATSExchangeModelImpl,
                                                    CBOTExchangeModelImpl,
                                                    CFEExchangeModelImpl,
                                                    CHXExchangeModelImpl,
                                                    CMEExchangeModelImpl,
                                                    CSEExchangeModelImpl,
                                                    ISEExchangeModelImpl,
                                                    LIFFEExchangeModelImpl,
                                                    NASDExchangeModelImpl,
                                                    NYMEExchangeModelImpl,
                                                    NYSEExchangeModelImpl,
                                                    ONEExchangeModelImpl,
                                                    PHLXExchangeModelImpl,
                                                    PSEExchangeModelImpl,
                                                    NQLXExchangeModelImpl,
                                                    BOXExchangeModelImpl,
                                                    NSXExchangeModelImpl,
                                                    NASDAQExchangeModelImpl,
                                                    };

    /**
     *  Public constructor used by HomeBuilder to create the Exchange Home for Trader GUI's
     */
    public ExchangeFactoryImpl(){}

    /**
     * Gets the unspecified Exchange for specifying non exchange
     */
    public Exchange getUnspecifiedExchange()
    {
        return UNSPECIFIED_EXCHANGE;
    }

    /**
     *  Gets the ExchangeList attribute of the ExchangeFactory class
     *
     *@return    The ExchangeList value
     */
    public Exchange[] getExchangeList()
    {
        return EXCHANGE_LIST;
    }

    public Exchange createExchange(String anExchange, String fullName)
    {
        return new ExchangeModelImpl(anExchange, fullName);
    }

}
