package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.TradingSession;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiSession.SessionClassStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiSession.SessionStrategyStruct;


public class TradingSessionV1
{
    private ClassStatusConsumer classStatusConsumer;
    private ProductStatusConsumer productStatusConsumer;
    private StrategyStatusConsumer strategyStatusConsumer;
    private TradingSessionStatusConsumer tradingSessionStatusConsumer;

    private EngineAccess engineAccess;
    private TradingSession tradingSessionV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public TradingSessionV1(EngineAccess ea, TradingSession ts)
    {
        engineAccess = ea;
        tradingSessionV1 = ts;

        classStatusConsumer = new ClassStatusConsumer();
        engineAccess.associateWithOrb(classStatusConsumer);

        productStatusConsumer = new ProductStatusConsumer();
        engineAccess.associateWithOrb(productStatusConsumer);

        strategyStatusConsumer = new StrategyStatusConsumer();
        engineAccess.associateWithOrb(strategyStatusConsumer);

        tradingSessionStatusConsumer = new TradingSessionStatusConsumer();
        engineAccess.associateWithOrb(tradingSessionStatusConsumer);
    }

    /** Execute a command on a TradingSession object.
     * @param command Words from command line: TradingSessionV1 function args...
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
            if (cmd.equalsIgnoreCase("getCurrentTradingSessions"))
            {
                doGetCurrentTradingSessions();
            }
            else if (cmd.equalsIgnoreCase("unsubscribeTradingSessionStatus"))
            {
                doUnsubscribeTradingSessionStatus();
            }
            else if (cmd.equalsIgnoreCase("getProductTypesForSession"))
            {
                doGetProductTypesForSession(command);
            }
            else if (cmd.equalsIgnoreCase("getClassesForSession"))
            {
                doGetClassesForSession(command);
            }
            else if (cmd.equalsIgnoreCase("getProductsForSession"))
            {
                doGetProductsForSession(command);
            }
            else if (cmd.equalsIgnoreCase("getStrategiesByClassForSession"))
            {
                doGetStrategiesByClassForSession(command);
            }
            else if (cmd.equalsIgnoreCase("getProductBySessionForKey"))
            {
                doGetProductBySessionForKey(command);
            }
            else if (cmd.equalsIgnoreCase("getStrategyBySessionForKey"))
            {
                doGetStrategyBySessionForKey(command);
            }
            else if (cmd.equalsIgnoreCase("getClassBySessionForKey"))
            {
                doGetClassBySessionForKey(command);
            }
            else if (cmd.equalsIgnoreCase("getProductBySessionForName"))
            {
                doGetProductBySessionForName(command);
            }
            else if (cmd.equalsIgnoreCase("getClassBySessionForSymbol"))
            {
                doGetClassBySessionForSymbol(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeClassesByTypeForSession"))
            {
                doUnsubscribeClassesByTypeForSession(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeProductsByClassForSession"))
            {
                doUnsubscribeProductsByClassForSession(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeStrategiesByClassForSession"))
            {
                doUnsubscribeStrategiesByClassForSession(command);
            }
            else if (cmd.equalsIgnoreCase("getStrategiesByComponent"))
            {
                doGetStrategiesByComponent(command);
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

    private void doGetCurrentTradingSessions() throws Throwable
    {
        tradingSessionV1.getCurrentTradingSessions(
                tradingSessionStatusConsumer._this());
    }

    private void doUnsubscribeTradingSessionStatus() throws Throwable
    {
        tradingSessionV1.unsubscribeTradingSessionStatus(
                tradingSessionStatusConsumer._this());
    }    

    private void doGetProductTypesForSession(String command[]) throws Throwable
    {
        String names[] = { "sessionName" };
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

        ProductTypeStruct ptseq[] =
                tradingSessionV1.getProductTypesForSession(sessionName);
        Log.message(Struct.toString(ptseq));
    }

    private void doGetClassesForSession(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productType" };
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
            Log.message("Missing productType");
            return;
        }
        short productType = Short.parseShort(values[1]);

        SessionClassStruct scseq[] = tradingSessionV1.getClassesForSession(
                sessionName, productType, classStatusConsumer._this());
        Log.message(Struct.toString(scseq));
    }

    private void doGetProductsForSession(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "classKey" };
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
        int classKey = Integer.parseInt(values[1]);

        SessionProductStruct spseq[] = tradingSessionV1.getProductsForSession(
                sessionName, classKey, productStatusConsumer._this());
        Log.message(Struct.toString(spseq));
    }

    private void doGetStrategiesByClassForSession(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "classKey" };
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
        int classKey = Integer.parseInt(values[1]);

        SessionStrategyStruct ssseq[] =
                tradingSessionV1.getStrategiesByClassForSession(
                        sessionName, classKey, strategyStatusConsumer._this());
        Log.message(Struct.toString(ssseq));
    }

    private void doGetProductBySessionForKey(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productKey" };
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
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        SessionProductStruct sp = tradingSessionV1.getProductBySessionForKey(
                sessionName, productKey);
        Log.message(Struct.toString(sp));
    }

    private void doGetStrategyBySessionForKey(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productKey" };
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
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        SessionStrategyStruct ss = tradingSessionV1.getStrategyBySessionForKey(
                sessionName, productKey);
        Log.message(Struct.toString(ss));
    }

    private void doGetClassBySessionForKey(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "classKey" };
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
        int classKey = Integer.parseInt(values[1]);

        SessionClassStruct sc = tradingSessionV1.getClassBySessionForKey(
                        sessionName, classKey);
        Log.message(Struct.toString(sc));
    }

    private void doGetProductBySessionForName(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productName" };
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
            Log.message("Missing productName");
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
            Log.message("Not a ProductNameyStruct:" + objName);
            return;
        }
        ProductNameStruct productName = (ProductNameStruct) o;

        SessionProductStruct sp = tradingSessionV1.getProductBySessionForName(
                sessionName, productName);
        Log.message(Struct.toString(sp));
    }

    private void doGetClassBySessionForSymbol(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productType", "className" };
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
            Log.message("Missing productType");
            return;
        }
        short productType = Short.parseShort(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing className");
            return;
        }
        String className = values[2];

        SessionClassStruct sc = tradingSessionV1.getClassBySessionForSymbol(
                sessionName, productType, className);
        Log.message(Struct.toString(sc));
    }

    private void doUnsubscribeClassesByTypeForSession(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "productType" };
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
            Log.message("Missing productType");
            return;
        }
        short productType = Short.parseShort(values[1]);
        
        tradingSessionV1.unsubscribeClassesByTypeForSession(
                sessionName, productType, classStatusConsumer._this());
    }

    private void doUnsubscribeProductsByClassForSession(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "classKey" };
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
        int classKey = Integer.parseInt(values[1]);

        tradingSessionV1.unsubscribeProductsByClassForSession(
                sessionName, classKey, productStatusConsumer._this());
    }

    private void doUnsubscribeStrategiesByClassForSession(String command[])
            throws Throwable
    {
        String names[] = { "sessionName", "classKey" };
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
        int classKey = Integer.parseInt(values[1]);

        tradingSessionV1.unsubscribeStrategiesByClassForSession(
                sessionName, classKey, strategyStatusConsumer._this());
    }

    private void doGetStrategiesByComponent(String command[]) throws Throwable
    {
        String names[] = { "componentKey", "sessionName" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing componentKey");
            return;
        }
        int componentKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing sessionName");
            return;
        }
        String sessionName = values[1];

        SessionStrategyStruct ssseq[] = tradingSessionV1
                .getStrategiesByComponent(componentKey, sessionName);
        Log.message(Struct.toString(ssseq));
    }
}
