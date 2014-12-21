// -----------------------------------------------------------------------------------
// Source file: AuctionImpl
//
// PACKAGE: com.cboe.presentation.auction
// 
// Created: Sep 9, 2004 2:37:19 PM
// -----------------------------------------------------------------------------------
// Copyright (c) 2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.auction;

import com.cboe.interfaces.presentation.auction.Auction;
import com.cboe.interfaces.presentation.util.CBOEId;
import com.cboe.interfaces.domain.dateTime.Time;
import com.cboe.interfaces.domain.Price;
import com.cboe.presentation.common.businessModels.AbstractBusinessModel;
import com.cboe.presentation.common.formatters.DisplayPriceFactory;
import com.cboe.presentation.common.dateTime.TimeImpl;
import com.cboe.presentation.util.CBOEIdImpl;
import com.cboe.idl.cmiOrder.AuctionStruct;
import com.cboe.idl.cmiUtil.TimeStruct;

public class AuctionImpl extends AbstractBusinessModel implements Auction
{
    private AuctionStruct auctionStruct;
    private CBOEId auctionId;
    private Price startingPrice;
    private Time entryTime;
    private Integer classKey;
    private Integer productKey;

    public AuctionImpl(AuctionStruct auctionStruct)
    {
        setStruct(auctionStruct);
    }

    public String getSessionName()
    {
        return auctionStruct.sessionName;
    }

    public Integer getClassKey()
    {
        return classKey;
    }

    public short getProductType()
    {
        return auctionStruct.productType;
    }

    public Integer getProductKey()
    {
        return productKey;
    }

    public CBOEId getAuctionId()
    {
        return auctionId;
    }

    public short getAuctionType()
    {
        return auctionStruct.auctionType;
    }

    public char getSide()
    {
        return auctionStruct.side;
    }

    public int getAuctionQuantity()
    {
        return auctionStruct.auctionQuantity;
    }

    public Price getStartingPrice()
    {
        return startingPrice;
    }

    public short getAuctionedOrderContingencyType()
    {
        return auctionStruct.auctionedOrderContingencyType;
    }

    public Time getEntryTime()
    {
        return entryTime;
    }

    public TimeStruct getEntryTimeStruct()
    {
        return auctionStruct.entryTime;
    }

    public String getExtensions()
    {
        return auctionStruct.extensions;
    }

    public short getState()
    {
        return auctionStruct.auctionState;
    }

    public void setStruct(AuctionStruct struct)
    {
        checkState(struct);

        auctionId     = new CBOEIdImpl(struct.auctionId);
        startingPrice = DisplayPriceFactory.create(struct.startingPrice);
        entryTime     = new TimeImpl(struct.entryTime);
        classKey      = new Integer(struct.classKey);
        productKey    = new Integer(struct.productKey);

        this.auctionStruct = struct;
    }

    public int compareTo(Object obj)
    {
        if (obj != null && (obj instanceof Auction))
        {
            Auction castedObject = (Auction)obj;
            return auctionId.compareTo(castedObject.getAuctionId());
        }

        return -1;
    }

    public Object getKey()
    {
        return auctionId;
    }

    public int hashCode()
    {
        return auctionId.hashCode();
    }

    public boolean equals(Object obj)
    {
        if (obj != null && (obj instanceof Auction))
        {
            Auction castedObject = (Auction)obj;
            return auctionId.equals(castedObject.getAuctionId());
        }

        return false;
    }

    public Object clone() throws CloneNotSupportedException
    {
        AuctionStruct newStruct = new AuctionStruct(
            auctionStruct.sessionName,
            auctionStruct.classKey,
            auctionStruct.productType,
            auctionStruct.productKey,
            auctionStruct.auctionId,
            auctionStruct.auctionType,
            auctionStruct.auctionState,
            auctionStruct.side,
            auctionStruct.auctionQuantity,
            auctionStruct.startingPrice,
            auctionStruct.auctionedOrderContingencyType,
            auctionStruct.entryTime,
            auctionStruct.extensions
        );

        return AuctionFactory.create(newStruct);
    }

} // -- end of class AuctionImpl
