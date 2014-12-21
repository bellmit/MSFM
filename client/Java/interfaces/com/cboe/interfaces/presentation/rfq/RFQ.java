package com.cboe.interfaces.presentation.rfq;

import com.cboe.exceptions.DataValidationException;
import com.cboe.idl.cmiQuote.RFQStruct;
import com.cboe.idl.cmiQuote.QuoteDetailStruct;
import com.cboe.interfaces.presentation.common.instruction.InstructionTarget;

/**
  Interface used to describe a "request for quote"
  @author Will McNabb
*/
public interface RFQ extends InstructionTarget, Comparable
{
///////////////////////////////////////////////////////////////////////////////
// CONSTANTS

    public static final int DELETED = 0;
    public static final int GREEN_STATE = 1;
    public static final int YELLOW_STATE = 2;
    public static final int RED_STATE = 3;

///////////////////////////////////////////////////////////////////////////////
// PUBLIC INTERFACE

    /**
      Gets the product key.
      @return Integer
    */
    public Integer getProductKey();
    /**
      Gets the class key.
      @return Integer
    */
    public Integer getClassKey();
    /**
      Gets the product type.
      @return Integer
    */
    public Integer getProductType();
    /**
      Gets the session name.
      @return int
    */
    public String getSessionName();
    /**
      Gets the quantity value for this RFQ
      @return int
     */
    public Integer getQuantity();
    /**
      Gets the time to live value for this RFQ
      @return long
     */
    public long getTimeToLive();
    /**
      Gets the state of this this RFQ
      @return int
      @see RFQImpl.STATE_1
      @see RFQImpl.STATE_2
      @see RFQImpl.DELETED
    */
    public int getState();
    /**
      Get the type of this RFQ.
      @return short
      @see com.cboe.idl.cmiConstants.RFQTypes
    */
    public short getType();
    /**
      Get the entry time of this RFQ
      @return long
     */
    public long getEntryTime();
    /**
      Get the unadjusted expiration time of this RFQ
      @return long
    */
    public long getExpireTime();
    /**
      Sets the given RFQStruct as the backing data for this interface.
      @param RFQStruct rfqStruct
      @throws DataValidationException
    */
    public void update(RFQStruct rfqStruct) throws DataValidationException;
    /**
      Force the deletion of this RFQ.
    */
    public void forceDelete();

    public RFQStruct getRFQStruct();
}
