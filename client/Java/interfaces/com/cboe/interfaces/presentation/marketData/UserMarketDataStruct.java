//
// -----------------------------------------------------------------------------------
// Source file: UserMarketDataStruct.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;

import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiMarketData.CurrentMarketStruct;
import com.cboe.idl.cmiMarketData.RecapStruct;
import com.cboe.idl.cmiMarketData.NBBOStruct;

public class UserMarketDataStruct
{
    public ProductKeysStruct productKeys;
    public CurrentMarketStruct currentMarket;
    public CurrentMarketStruct currentMarketPublic;
    public PersonalBestBook personalBestBook;
    public RecapStruct recap;
    public NBBOStruct NBBO;

    public UserMarketDataStruct()
    {}

    public UserMarketDataStruct(ProductKeysStruct productKeys,
                                 CurrentMarketStruct currentMarket,
                                 CurrentMarketStruct currentMarketPublic,
                                 PersonalBestBook personalBestBook,
                                 RecapStruct recap,
                                 NBBOStruct NBBO)
    {
        this.productKeys = productKeys;
        this.currentMarket = currentMarket;
        this.currentMarketPublic = currentMarketPublic;
        this.personalBestBook = personalBestBook;
        this.recap = recap;
        this.NBBO = NBBO;
    }
}
