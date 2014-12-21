package com.cboe.domain.util;

import com.cboe.idl.trade.AtomicCmtaAllocationStruct;

/**
 * A helper that makes it easy to create valid Atomic CMTA Allocation Struct.
 *
 * @author Anil Kalra
 */
public class AtomicCmtaAllocationStructBuilder
{
    /**
     * All methods are static, so no instance is needed.
     */
    private AtomicCmtaAllocationStructBuilder()
    {
        super();
    }

    /**
     * Creates a default instance of a AtomicCmtaAllocationStruct.
     *
     * @return default instance of struct
     */
    public static AtomicCmtaAllocationStruct buildAtomicCmtaAllocationStructBuilder()
    {
        AtomicCmtaAllocationStruct struct = new AtomicCmtaAllocationStruct();
        struct.atomicTradeId = null;
        struct.buyerAwayExchangeAcronym = StructBuilder.buildExchangeAcronymStruct("", "");
        struct.buyerOrderDate = "";
        struct.buyerOrsid = "";
        struct.buyerSupressionReason = 0;
        struct.sellerAwayExchangeAcronym = StructBuilder.buildExchangeAcronymStruct("", "");
        struct.sellerOrderDate = "";
        struct.sellerOrsid = "";
        struct.sellerSupressionReason = 0;

        return struct;
    }


}
