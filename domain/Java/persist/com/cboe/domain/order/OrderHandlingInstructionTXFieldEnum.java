package com.cboe.domain.order;

import java.util.HashMap;

public enum OrderHandlingInstructionTXFieldEnum
{
    OPPOSITESIDEBOTR,
    EXECUTIONPRICE,
    ORIGINALEXECUTIONPRICE,
    MAXIMUMEXECUTIONVOLUME,
    MAXIMUMEXECUTIONVOLUMEREASON,
    TRADINGRESTRICTION,
    REMAINDERHANDLINGMODE,
    IGNORECONTINGENCY,
    ORDERQUANTITYATRECEIVETIME,
    TRADEDVOLUME,
    MAXTRADABLEQTYFORINDEXHYBRID,
    OVERRIDEREASONFORINDEXHYBRID,
    EXPRESSORDER,
    BOOKABLEORDERMARKETLIMIT,
    UNKNOWN;
    
    private static final OrderHandlingInstructionTXFieldEnum[] vals = OrderHandlingInstructionTXFieldEnum.values();
    private static final HashMap<String, OrderHandlingInstructionTXFieldEnum> valsByName;
    
    long bitMask = 1L<< ordinal();

    static
    {
        valsByName = new HashMap<String, OrderHandlingInstructionTXFieldEnum>(vals.length*2);
        for (int i=0; i < vals.length; i++)
        {
            valsByName.put(vals[i].name(), vals[i]);
        }
    }
    
    public static final int NUM_FIELDS = vals.length;
        
    public long setBit(long p_bits)
    {
        return p_bits | (bitMask);
    }
    
    public long clearBit(long p_bits)
    {
        return p_bits & ~(bitMask);
    }

    public boolean isBitSet(long p_bits)
    {
       return (p_bits & bitMask) != 0;
    }
    
    public static OrderHandlingInstructionTXFieldEnum getValueOf(String name) 
    {
        OrderHandlingInstructionTXFieldEnum retVal = valsByName.get(name);
        if(retVal == null)
        {
            retVal = OrderHandlingInstructionTXFieldEnum.UNKNOWN;
        }
        
        return retVal;
      }

}
