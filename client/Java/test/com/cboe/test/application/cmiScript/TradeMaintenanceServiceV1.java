package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiTrade.ExternalBustTradeStruct;
import com.cboe.idl.cmiTrade.ExternalTradeEntryStruct;
import com.cboe.idl.cmiTrade.ExternalTradeReportStruct;
import com.cboe.idl.cmiTradeMaintenanceService.TradeMaintenanceService;
import com.cboe.idl.cmiUtil.CboeIdStruct;


public class TradeMaintenanceServiceV1
{
    private EngineAccess engineAccess;
    private TradeMaintenanceService tradeMaintenanceService;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public TradeMaintenanceServiceV1(EngineAccess ea,
        TradeMaintenanceService tms)
    {
        engineAccess = ea;
        tradeMaintenanceService = tms;
    }

    /** Execute a command on a TradeMaintenanceServiceV1 object.
     * @param command Words from command line: TradeMaintenanceServiceV1 function args...
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
            if (cmd.equalsIgnoreCase("acceptExternalTrade"))
            {
                doAcceptExternalTrade(command);
            }
            else if (cmd.equalsIgnoreCase("acceptExternalTradeBust"))
            {
                doAcceptExternalTradeBust(command);
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

    private void doAcceptExternalTrade(String command[]) throws Throwable
    {
        String names[] = { "epfOrBlockTrade" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing epfOrBlockTrade");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof ExternalTradeEntryStruct))
        {
            Log.message("Not an ExternalTradeEntryStruct:" + objName);
            return;
        }
        ExternalTradeEntryStruct ete = (ExternalTradeEntryStruct) o;

        ExternalTradeReportStruct etr =
                tradeMaintenanceService.acceptExternalTrade(ete);
        Log.message(Struct.toString(etr));
    }

    private void doAcceptExternalTradeBust(String command[]) throws Throwable
    {
        String names[] = { "tradingSessionName", "productKey", "tradeId",
                "bustedTrades", "reason" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing tradingSessionName");
            return;
        }
        String tradingSessionName = values[0];

        if (values[1] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[1]);

        if (values[2] == null)
        {
            Log.message("Missing tradeId");
            return;
        }
        CboeIdStruct tradeId = Struct.makeCboeIdStruct(values[2]);

        if (values[3] == null)
        {
            Log.message("Missing bustedTrades");
            return;
        }
        String objName = values[3];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof ExternalBustTradeStruct[]))
        {
            Log.message("Not an ExternalBustTradeStructSequence:" + objName);
            return;
        }
        ExternalBustTradeStruct bustedTrades[] = (ExternalBustTradeStruct[]) o;

        if (values[4] == null)
        {
            Log.message("Missing reason");
            return;
        }
        String reason = values[4];

        tradeMaintenanceService.acceptExternalTradeBust(tradingSessionName,
                productKey, tradeId, bustedTrades, reason);
    }
}
