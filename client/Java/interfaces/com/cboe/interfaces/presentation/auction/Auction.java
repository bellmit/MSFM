// -----------------------------------------------------------------------------------
// Source file: Auction
//
// PACKAGE: com.cboe.interfaces.presentation.auction
// 
// Created: Sep 9, 2004 2:28:39 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.auction;

import com.cboe.interfaces.presentation.common.businessModels.BusinessModel;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.domain.Price;
import com.cboe.idl.cmiUtil.TimeStruct;
import com.cboe.idl.cmiOrder.AuctionStruct;

public interface Auction extends BusinessModel, Comparable
{
    public String getSessionName();
    public Integer getClassKey();
    public short getProductType();
    public Integer getProductKey();
    public CBOEId getAuctionId();
    public short getAuctionType();
    public short getState();
    public char getSide();
    public int getAuctionQuantity();
    public Price getStartingPrice();
    public short getAuctionedOrderContingencyType();
    public Time getEntryTime();
    public TimeStruct getEntryTimeStruct();
    public String getExtensions();
    public void setStruct(AuctionStruct struct);
}
