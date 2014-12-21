package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.idl.cmiStrategy.*;

    /**
     * A helper that makes it easy to create valid CORBA structs.  The structs created
     * by the methods of this class have default values for all attributes.
     * @author Connie Feng
     */
    public class ProductStrategyStructBuilder
    {
    /**
     * All methods are static, so no instance is needed.
     *
     * @author John Wickberg
     */
    private ProductStrategyStructBuilder()
    {
      super();
    }

    public static StrategyStruct buildStrategyStruct(StrategyRequestStruct strategyRequest)
    {
        StrategyStruct aStruct = new StrategyStruct();

        aStruct.product = ClientProductStructBuilder.buildProductStruct();
        aStruct.strategyLegs = new StrategyLegStruct[0];

        return aStruct;
    }

    public static StrategyStruct buildStrategyStruct(StrategyLegStruct[] strategyLegs)
    {
        StrategyStruct aStruct = new StrategyStruct();

        aStruct.product = ClientProductStructBuilder.buildProductStruct();
        aStruct.strategyLegs = strategyLegs;

        return aStruct;
    }

}
