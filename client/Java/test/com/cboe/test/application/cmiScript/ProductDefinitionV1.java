package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.ProductDefinition;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;
import com.cboe.idl.cmiStrategy.StrategyRequestStruct;
import com.cboe.idl.cmiUtil.PriceStruct;

public class ProductDefinitionV1
{
    private EngineAccess engineAccess;
    private ProductDefinition productDefinitionV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public ProductDefinitionV1(EngineAccess ea, ProductDefinition pd)
    {
        engineAccess = ea;
        productDefinitionV1 = pd;
    }

    /** Execute a command on a ProductDefinition object.
     * @param command Words from command line: ProductDefinitionV1 function args...
     **/
    public void doCommand(String command[])
    {
        if (command.length < 2)
        {
            Log.message("Command line must have at least object, function");
            return;
        }

        try
        {
            String cmd = command[1];
            if (cmd.equalsIgnoreCase("acceptStrategy"))
            {
                doAcceptStrategy(command);
            }
            else if (cmd.equalsIgnoreCase("buildStrategyRequestByName"))
            {
                doBuildStrategyRequestByName(command);
            }
            else if (cmd.equalsIgnoreCase("buildStrategyRequestByProductKey"))
            {
                doBuildStrategyRequestByProductKey(command);
            }
            else
            {
                Log.message("Unknown function:" + cmd + "  for " + command[0]);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doAcceptStrategy(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "strategyRequest" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing sessionName");
            return;
        }
        String sessionName = values[0];
        
        if (values[1] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        String objName = values[1];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof StrategyRequestStruct))
        {
            Log.message("Not a StrategyRequestStruct:" + objName);
            return;
        }
        StrategyRequestStruct sr = (StrategyRequestStruct) o;

        SessionStrategyStruct ss =
                productDefinitionV1.acceptStrategy(sessionName, sr);
        Log.message(Struct.toString(ss));
    }

    private void doBuildStrategyRequestByName(String command[]) throws Throwable
    {
        String names[] = { "strategyType", "anchorProduct", "priceIncrement",
                "monthIncrement" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing strategyType");
            return;
        }
        short strategyType = Short.parseShort(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing anchorProduct");
            return;
        }
        String objName = values[1];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof ProductNameStruct))
        {
            Log.message("Not a ProductNameStruct:" + objName);
            return;
        }
        ProductNameStruct anchorProduct = (ProductNameStruct) o;

        if (values[2] == null)
        {
            Log.message("Missing priceIncrement");
            return;
        }
        PriceStruct priceIncrement = Struct.makePriceStruct(values[2]);
        if (priceIncrement == null)
        {
            // Error message already produced, just leave
            return;
        }

        if (values[3] == null)
        {
            Log.message("Missing monthIncrement");
            return;
        }
        short monthIncrement = Short.parseShort(values[3]);

        StrategyRequestStruct sr = productDefinitionV1
                .buildStrategyRequestByName(strategyType, anchorProduct,
                        priceIncrement, monthIncrement);
        Log.message(Struct.toString(sr));
    }

    private void doBuildStrategyRequestByProductKey(String command[]) throws Throwable
    {
        String names[] = { "strategyType", "anchorProductKey", "priceIncrement",
                           "monthIncrement"};
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing strategyType");
            return;
        }
        short strategyType = Short.parseShort(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing anchorProductKey");
            return;
        }
        int anchorProductKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing priceIncrement");
            return;
        }
        PriceStruct priceIncrement = Struct.makePriceStruct(values[2]);
        if (priceIncrement == null)
        {
            // Error message already produced, just leave
            return;
        }

        if (values[3] == null)
        {
            Log.message("Missing monthIncrement");
            return;
        }
        short monthIncrement = Short.parseShort(values[3]);

        StrategyRequestStruct sr =
                productDefinitionV1.buildStrategyRequestByProductKey(
                strategyType, anchorProductKey, priceIncrement, monthIncrement);
        Log.message(Struct.toString(sr));
    }
}
