package com.cboe.presentation.product;

import com.cboe.interfaces.presentation.product.SessionStrategy;
import com.cboe.interfaces.presentation.product.SessionStrategyLeg;
import com.cboe.interfaces.presentation.product.StrategyLeg;
import com.cboe.interfaces.presentation.product.Strategy;

import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiSession.SessionStrategyLegStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;


/**
 * SessionStrategy implementation for a SessionStrategyStruct from the API.
 */
class SessionStrategyImpl extends SessionProductImpl implements SessionStrategy
{
    private SessionStrategyLegStruct[] sessionStrategyLegs = null;
    private StrategyImpl delegate = null;

    /**
     * Constructor
     * @param sessionStrategyStruct SessionStrategyStruct
     * @param sessionProduct SessionProductStruct
     */
     protected SessionStrategyImpl(SessionStrategyStruct sessionStrategyStruct)
    {
       super(sessionStrategyStruct.sessionProductStruct);
       StrategyStruct strategyStruct = new StrategyStruct();
       strategyStruct.product = sessionStrategyStruct.sessionProductStruct.productStruct;
       strategyStruct.strategyType = sessionStrategyStruct.strategyType;
       int sessionStrategyLegsLength = sessionStrategyStruct.sessionStrategyLegs.length;
       strategyStruct.strategyLegs = new StrategyLegStruct[sessionStrategyLegsLength];
       for (int j=0; j<sessionStrategyLegsLength; j++) {
           com.cboe.idl.cmiStrategy.StrategyLegStruct strategyLeg = new com.cboe.idl.cmiStrategy.StrategyLegStruct();
           strategyLeg.product = sessionStrategyStruct.sessionStrategyLegs[j].product;
           strategyLeg.ratioQuantity = sessionStrategyStruct.sessionStrategyLegs[j].ratioQuantity;
           strategyLeg.side = sessionStrategyStruct.sessionStrategyLegs[j].side;
           strategyStruct.strategyLegs[j] = strategyLeg;
       }
       delegate = new StrategyImpl(strategyStruct);
       sessionStrategyLegs = sessionStrategyStruct.sessionStrategyLegs;
    }

    /**
     *  Default constructor.
     */
    protected SessionStrategyImpl()
    {
        super();
    }

    /**
     * Gets the legs for this strategy product
     * @return SessionStrategyLeg[]
     */
    public SessionStrategyLeg[] getSessionStrategyLegs()
    {
        int legLength = this.sessionStrategyLegs.length;
        SessionStrategyLeg[] sessionStrategyLegs = new SessionStrategyLegImpl[legLength];
        for (int i=0; i<legLength; i++) {
            sessionStrategyLegs[i] = new SessionStrategyLegImpl(this.sessionStrategyLegs[i]);
        }
        return sessionStrategyLegs;
   }

    public StrategyLeg[] getStrategyLegs()
    {
       return delegate.getStrategyLegs();
    }
    /**
     * Gets the type of this strategy
     * @see com.cboe.idl.cmiConstants.StrategyTypes
     * @return short
     */
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

    public void updateStrategy(Strategy newStrategy) {
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
        for (int i=0; i<legLength; i++){
            for (int j=0; j<sessionLegLength; j++) {
                // find the session strategy leg product based on productKey and update the leg.
                if(legs[i].product == sessionStrategyLegs[j].product) {
                    sessionStrategyLegs[j].ratioQuantity = legs[i].ratioQuantity;
                    sessionStrategyLegs[j].side = legs[i].side;
                    break;
                }
            }
        }
    }
}

