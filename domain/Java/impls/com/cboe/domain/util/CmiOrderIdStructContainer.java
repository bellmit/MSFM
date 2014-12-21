package com.cboe.domain.util;

import com.cboe.interfaces.domain.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

/**
 * This is a hashable container class for our cmiId key struct.
 * @author Keith A. Korecky
 */

public class CmiOrderIdStructContainer implements BaseOrderIdStructContainer
{
    private ExchangeFirmStruct firmKey;
    private String branchId;
    private int branchSequenceNumber;
    private String correspondentFirmKey;
    private String orderDate;
    private int hashCode;

    /**
      * Sets the internal fields to the passed values ontained by the order
      * identification data structure.
      */
    public CmiOrderIdStructContainer(OrderIdStruct orderKey)
    {
        branchId                = orderKey.branch;
        branchSequenceNumber    = orderKey.branchSequenceNumber;
        correspondentFirmKey    = orderKey.correspondentFirm;
        firmKey                 = orderKey.executingOrGiveUpFirm;
        orderDate               = orderKey.orderDate;
        hashCode = (firmKey.firmNumber.hashCode()
                + firmKey.exchange.hashCode()
                + branchId.hashCode()
                + branchSequenceNumber
                + correspondentFirmKey.hashCode()
                + orderDate.hashCode()
                ) / 6;
    }

    public CmiOrderIdStructContainer(ExchangeFirmStruct firmKey, String branchId, int branchSequenceNumber,
    										String correspondentFirmKey, String orderDate)
    {
        this.branchId                = branchId;
        this.branchSequenceNumber    = branchSequenceNumber;
        this.correspondentFirmKey    = correspondentFirmKey;
        this.firmKey                 = firmKey;
        this.orderDate               = orderDate;
        this.hashCode = (firmKey.firmNumber.hashCode()
                + firmKey.exchange.hashCode()
                + branchId.hashCode()
                + branchSequenceNumber
                + correspondentFirmKey.hashCode()
                + orderDate.hashCode()
                ) / 6;
    }

    
    /**
      * The equals for the key.
      * @param obj Object
      * @return boolean
      */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof CmiOrderIdStructContainer))
        {
            CmiOrderIdStructContainer   cmiStruct = (CmiOrderIdStructContainer)obj;
            return (    ( firmKey.exchange.equals(cmiStruct.firmKey.exchange) )
                    &&  ( firmKey.firmNumber.equals(cmiStruct.firmKey.firmNumber) )
                    &&  ( branchId.equals( cmiStruct.branchId) )
                    &&  ( branchSequenceNumber == cmiStruct.branchSequenceNumber )
                    &&  ( correspondentFirmKey.equals( cmiStruct.correspondentFirmKey ) )
                    &&  ( orderDate.equals( cmiStruct.orderDate ) )
                    );
         }
        return false;
    }

    /**
      * The hashCode for the key.
      * @return int
      */
    public int hashCode()
    {
        return hashCode;
    }

    /**
      * The toString() for the key.
      * @return String
      */
    public String toString()
    {
        StringBuilder buf = new StringBuilder(50);

        buf.append(firmKey.exchange)
        .append(":")
        .append(firmKey.firmNumber)
        .append(":")
        .append(branchId)
        .append(":")
        .append(branchSequenceNumber)
        .append(":")
        .append(correspondentFirmKey)
        .append(":")
        .append(orderDate);

        return buf.toString();
    }

    public boolean isValid()
    {
        return ( true );
    }
    public String getBranchId(){
    	return branchId;
    }

    public int getBranchSeqNumber(){
    	return branchSequenceNumber;
    }
}
