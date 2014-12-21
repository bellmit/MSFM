//
// -----------------------------------------------------------------------------------
// Source file: UserMarketDataWithDsmStruct.java
//
// PACKAGE: com.cboe.interfaces.presentation.marketData;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.marketData;


/*
 * this class was used when UserMarketDataCacheProxy used to calculate DSM and publish it together with UserMarketDataStruct.
 * now DSM is published by DSMCalculator as DSMBidAskStruct, so this class is obsolete and should be deleted. 
 */

@Deprecated
public class UserMarketDataWithDsmStruct // extends UserMarketDataStruct
{
//    private DsmBidAskStruct dsm;
//    private boolean         bidAskFlipped;
//    private boolean         dsmBidAskFlipped;
//
//
//    public UserMarketDataWithDsmStruct(UserMarketDataStruct dataStruct)
//    {
//        super(dataStruct.productKeys, dataStruct.currentMarket, dataStruct.currentMarketPublic,
//              dataStruct.personalBestBook, dataStruct.recap, dataStruct.NBBO);
//    }
//
//    public void setDsm(DsmBidAskStruct d)
//    {
//        dsm = d;
//    }
//
//    public DsmBidAskStruct getDsm()
//    {
//        return dsm;
//    }
//
//    public boolean isBidAskFlipped()
//    {
//	    return bidAskFlipped;
//    }
//
//	public void setBidAskFlipped(boolean flag)
//    {
//		bidAskFlipped = flag;
//	}
//
//    public boolean isDsmBidAskFlipped()
//    {
//	    return dsmBidAskFlipped;
//    }
//
//	public void setDsmBidAskFlipped(boolean flag)
//    {
//		dsmBidAskFlipped = flag;
//	}
}
