
package com.cboe.presentation.product;

import com.cboe.idl.cmiSession.SessionProductStruct;

import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.interfaces.presentation.product.*;
import com.cboe.idl.cmiProduct.ProductStruct;

class InactiveSessionStrategyImpl extends InactiveSessionProductImpl implements SessionStrategy
{
    protected SessionStrategyLegStruct[] sessionStrategyLegs ;
    private   StrategyImpl delegate;
    protected StrategyStruct strategyStruct;

    public InactiveSessionStrategyImpl(String sessionName, String inactiveSessionName, StrategyStruct strategyStruct)
    {
        super(sessionName, inactiveSessionName, strategyStruct.product);
        this.strategyStruct = strategyStruct;
        initialize();
    }
    public boolean isInactiveInTradingSession()
    {
        return true;
    }

    protected void initialize()
    {
       delegate = new StrategyImpl(strategyStruct);
       // initialize session strategy legs
       int strategyLegsLength = strategyStruct.strategyLegs.length;
       sessionStrategyLegs = new SessionStrategyLegStruct[strategyLegsLength];
       String sessionName = getTradingSessionName();
       for(int i=0; i<strategyLegsLength; i++)
       {
            StrategyLegStruct strategyLegStruct = strategyStruct.strategyLegs[i];
            sessionStrategyLegs[i] = new SessionStrategyLegStruct(sessionName, strategyLegStruct.product, strategyLegStruct.ratioQuantity, strategyLegStruct.side);
       }
    }

    /**
     * Gets the legs for this strategy product
     * @return SessionStrategyLeg[]
     */
    public SessionStrategyLeg[] getSessionStrategyLegs()
    {
        int legLength = this.sessionStrategyLegs.length;
        SessionStrategyLeg[] sessionStrategyLegs = new SessionStrategyLegImpl[legLength];
        for (int i=0; i<legLength; i++)
        {
            sessionStrategyLegs[i] = new SessionStrategyLegImpl(this.sessionStrategyLegs[i]);
        }
        return sessionStrategyLegs;
   }

    public StrategyLeg[] getStrategyLegs()
    {
       return delegate.getStrategyLegs();
    }

    public short getStrategyType()
    {
       return delegate.getStrategyType();
    }

    public StrategyLegStruct[] getStrategyLegStructs()
    {
       return delegate.getStrategyLegStructs();
    }

    public SessionStrategyLegStruct[] getSessionStrategyLegStructs()
    {
       return sessionStrategyLegs;
    }

    public void updateStrategy(Strategy newStrategy)
    {
        ProductStruct productTemp = newStrategy.getProductStruct();
        super.updateProduct(ProductFactoryHome.find().create(productTemp));
        StrategyStruct strategyTemp = new StrategyStruct();
        strategyTemp.product = productTemp;
        strategyTemp.strategyLegs = newStrategy.getStrategyLegStructs();
        strategyTemp.strategyType = newStrategy.getStrategyType();
        delegate = new StrategyImpl(strategyTemp);
        StrategyLegStruct[] legs = newStrategy.getStrategyLegStructs();
        int legLength = legs.length;
        int sessionLegLength = sessionStrategyLegs.length;
        for (int i=0; i<legLength; i++)
        {
            for (int j=0; j<sessionLegLength; j++)
            {
                // find the session strategy leg product based on productKey and update the leg.
                if(legs[i].product == sessionStrategyLegs[j].product)
                {
                    sessionStrategyLegs[j].ratioQuantity = legs[i].ratioQuantity;
                    sessionStrategyLegs[j].side = legs[i].side;
                    break;
                }
            }
        }
    }

}
