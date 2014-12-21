package com.cboe.interfaces.domain;

import com.cboe.idl.cmiConstants.VolumeTypes;


public enum MarketUpdateVolType
{
    Best_limit(0, VolumeTypes.LIMIT),
    Best_AON(1, VolumeTypes.AON),
    Best_OddLot(2, VolumeTypes.ODD_LOT),
    BestLimit_limit(3, VolumeTypes.LIMIT),
    BestLimit_AON(4, VolumeTypes.AON),
    BestLimit_OddLot(5, VolumeTypes.ODD_LOT),

    BestPublic_customer(6, VolumeTypes.CUSTOMER_ORDER),
    BestPublic_professional(7, VolumeTypes.PROFESSIONAL_ORDER),
    BestLimit_limitAndReserve(8, (short)(VolumeTypes.ODD_LOT+1)),
    BestLimit_RoundLot_Limit(9, VolumeTypes.LIMIT),
    ;                                                                      
                                                                    
    /*  REMINDER !!!
       BestLimit_limitAndReserve is added using the next value from VolumeTypes for reserve quantity needed by STS,
       we don't have time to change the CmiConstant at this moment
       so please be very carefull and remember to update this value if you introduce a new volume type
       */
    
    private final int idx;
    private final int maskIdx;
    private final short volType;

    MarketUpdateVolType(int p_idx, short p_volType)
    {
        idx = p_idx;
        maskIdx = 1<<idx;
        volType = p_volType;
    }

    public short getVolType() 
    {
        return volType;
    }

    public boolean isBitSet(int p_mask)
    {
        return (p_mask & maskIdx) != 0;
    }
    public int setBit(int p_mask)
    {
        return p_mask | maskIdx;
    }
    public int clearBit(int p_mask)
    {
        return p_mask & (~maskIdx);
    }
    public int getVol(int[] p_vols)
    {
        return p_vols[idx];
    }
    public void setVol(int[] p_vols, int p_vol)
    {
        p_vols[idx] = p_vol;
    }
}
