package com.cboe.domain.util;

import com.cboe.interfaces.domain.*;
import com.cboe.idl.cmiOrder.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

/**
 * This is a hashable container class for our complete ( cboeId & cmiId ) key struct.
 * @author Keith A. Korecky
 */

public class CompleteOrderIdStructContainer implements BaseOrderIdStructContainer
{
    private ExchangeFirmStruct firmKey;
    private String branchId;
    private int branchSequenceNumber;
    private String correspondentFirmKey;
    private String orderDate;

    private int highCboeId;
    private int lowCboeId;
    private int hashCode;


    /**
      * Sets the internal fields to the passed values ontained by the order
      * identification data structure.
      */
    public CompleteOrderIdStructContainer(OrderIdStruct orderKey)
    {
        branchId                = orderKey.branch;
        branchSequenceNumber    = orderKey.branchSequenceNumber;
        correspondentFirmKey    = orderKey.correspondentFirm;
        firmKey                 = orderKey.executingOrGiveUpFirm;
        orderDate               = orderKey.orderDate;

        highCboeId              = orderKey.highCboeId;
        lowCboeId               = orderKey.lowCboeId;
        hashCode = (  firmKey.exchange.hashCode()
                + firmKey.firmNumber.hashCode()
                + branchId.hashCode()
                + branchSequenceNumber
                + correspondentFirmKey.hashCode()
                + orderDate.hashCode()
                + highCboeId
                + lowCboeId
                ) / 8;
            
    }

    /**
      * The equals for the key.
      * @param obj Object
      * @return boolean
      */
    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof CompleteOrderIdStructContainer))
        {
            CompleteOrderIdStructContainer   completeStruct = (CompleteOrderIdStructContainer)obj;
            return (    ( firmKey.exchange.equals(completeStruct.firmKey.exchange) )
                    &&  ( firmKey.firmNumber.equals(completeStruct.firmKey.firmNumber) )
                    &&  ( branchId.equals( completeStruct.branchId) )
                    &&  ( branchSequenceNumber == completeStruct.branchSequenceNumber )
                    &&  ( correspondentFirmKey.equals( completeStruct.correspondentFirmKey ) )
                    &&  ( orderDate.equals( completeStruct.orderDate ) )
                    &&  ( highCboeId == completeStruct.highCboeId )
                    &&  ( lowCboeId == completeStruct.lowCboeId )
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
        StringBuilder buf = new StringBuilder("");

        buf.append(firmKey.exchange)
           .append(':').append(firmKey.firmNumber)
           .append(':').append(branchId)
           .append(':').append(branchSequenceNumber)
           .append(':').append(correspondentFirmKey)
           .append(':').append(orderDate)
           .append(": h=").append(highCboeId)
           .append(": l=").append(lowCboeId);

        return buf.toString();
    }

    public boolean isValid()
    {
        return true;
        /*
        return (        ( firmKey.length() != 0 )
                    &&  ( branchId.length() != 0 )
                    &&  ( branchSequenceNumber != 0 )
                    &&  ( correspondentFirmKey.length() != 0 )
                    &&  ( orderDate.length() != 0 )
                    &&  ( ( highCboeId != 0 ) || ( lowCboeId != 0 ) )
                );
        */
    }

	public String getBranchId(){
    		return branchId;
    	}

    	public int getBranchSeqNumber(){
    		return branchSequenceNumber;
    	}
	
}
