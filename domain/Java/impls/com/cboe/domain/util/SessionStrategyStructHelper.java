package com.cboe.domain.util;

import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;

public class SessionStrategyStructHelper
{
    /**
     * buildSessionStrategyStruct - Method to build a SessionStrategyStruct for a particular session.
     *
     * @param aStrategyStruct
     * @param SessionProductStruct
     * @param sessionName
     */
    public static SessionStrategyStruct buildSessionStrategyStruct(StrategyStruct aStrategyStruct,
        SessionProductStruct sessionProductStruct, String sessionName)
    {
        // Build a SessionStrategyLegStruct array
        StrategyLegStruct[] strategyLegs = aStrategyStruct.strategyLegs;
        SessionStrategyLegStruct[] sessionStrategyLegs =  new SessionStrategyLegStruct[strategyLegs.length];
        for(int i = 0; i < strategyLegs.length; i++)
        {
            sessionStrategyLegs[i] = new SessionStrategyLegStruct(
                sessionName,
                strategyLegs[i].product,
                strategyLegs[i].ratioQuantity,
                strategyLegs[i].side);
        }
    
        SessionStrategyStruct aSessionStrategyStruct =
             new SessionStrategyStruct(aStrategyStruct.strategyType, sessionProductStruct, sessionStrategyLegs);
    
        return aSessionStrategyStruct;
    }
    
    public static StrategyStruct toStrategyStruct(SessionStrategyStruct sessionStrategy)
    {
        // Deconstruct SessionStrategyLegStructs
        SessionStrategyLegStruct[] sessionStrategyLegs = sessionStrategy.sessionStrategyLegs;
        StrategyLegStruct[] strategyLegs = new StrategyLegStruct[sessionStrategy.sessionStrategyLegs.length];
        for(int i = 0; i < strategyLegs.length; i++)
        {
            strategyLegs[i] = new StrategyLegStruct(
                sessionStrategyLegs[i].product,
                sessionStrategyLegs[i].ratioQuantity,
                sessionStrategyLegs[i].side);
        }

        StrategyStruct strategy = new StrategyStruct(
            sessionStrategy.sessionProductStruct.productStruct,
            sessionStrategy.strategyType,
            strategyLegs);
            
        return strategy;
    }
}
