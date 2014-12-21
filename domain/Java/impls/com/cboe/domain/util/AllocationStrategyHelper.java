package com.cboe.domain.util;

import com.cboe.idl.tradingProperty.AllocationStrategyStruct;
import com.cboe.idl.tradingProperty.AllocationStrategyStructV2;
import com.cboe.idl.constants.AllocationTradeTypes;

import java.util.ArrayList;

/**
 * Utility class to help the conversion between the original allocation strategy struct
 * and the new V2 struct that contains the allocation trade type.
 * User: liangc
 * Date: Oct 20, 2003
 * Time: 11:30:44 AM
 *
 */
public class AllocationStrategyHelper {

    public static AllocationStrategyStruct toDefaultStruct(AllocationStrategyStructV2[] allStrategies)
    {
        if (allStrategies == null || allStrategies.length == 0)
        {
            return null;
        }

        for(int i = 0; i < allStrategies.length; i++)
        {
            if(allStrategies[i].allocationTradeType == AllocationTradeTypes.REGULAR)
            {
                return toOriginalStruct(allStrategies[i]);
            }
        }

        return null;
    }

    public static AllocationStrategyStructV2[] consolidateWithDefaultStrategy(AllocationStrategyStructV2[] allStrategies, AllocationStrategyStruct defaultStrategy)
    {
        AllocationStrategyStruct existingDefault = toDefaultStruct(allStrategies);
        if(existingDefault == null)
        {
            ArrayList newList = new ArrayList();
            newList.add(toV2Struct(defaultStrategy));
            for(int i = 0; i < allStrategies.length; i++)
            {
                newList.add(allStrategies[i]);
            }

            allStrategies = new AllocationStrategyStructV2[newList.size()];
            newList.toArray(allStrategies);
        }
        else
        {
            for(int i = 0; i < allStrategies.length; i++)
            {
                if(allStrategies[i].allocationTradeType == AllocationTradeTypes.REGULAR)
                {
                    allStrategies[i] = toV2Struct(defaultStrategy);
                    break;
                }
            }
        }

        return allStrategies;
    }
    public static  AllocationStrategyStructV2 toV2Struct(AllocationStrategyStruct struct)
    {
        AllocationStrategyStructV2 v2 = new AllocationStrategyStructV2();
        v2.allocationTradeType =AllocationTradeTypes.REGULAR;
        v2.defaultStrategyCode = struct.defaultStrategyCode;
        v2.prioritizedStrategyCodes = struct.prioritizedStrategyCodes;

        return v2;
    }

    public static AllocationStrategyStructV2[] toV2StructSequence(AllocationStrategyStruct struct)
    {
        AllocationStrategyStructV2[] list = new AllocationStrategyStructV2[1];
        list[0] = toV2Struct(struct);
        return list;
    }
    public static  AllocationStrategyStruct toOriginalStruct(AllocationStrategyStructV2 v2Struct)
    {
        if(v2Struct == null)
        {
            return null;
        }
        AllocationStrategyStruct v1 = new AllocationStrategyStruct();
        v1.defaultStrategyCode = v2Struct.defaultStrategyCode;
        v1.prioritizedStrategyCodes = v2Struct.prioritizedStrategyCodes;
        return v1;
    }

}

