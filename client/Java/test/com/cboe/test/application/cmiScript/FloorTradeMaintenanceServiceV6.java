package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmiV6.FloorTradeMaintenanceService;
import com.cboe.idl.cmiTrade.FloorTradeEntryStruct;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.CboeIdStruct;


public class FloorTradeMaintenanceServiceV6
{
    private EngineAccess engineAccess;
    private FloorTradeMaintenanceService floorTradeMaintenanceServiceV6;
    private QuoteStatusConsumerV2 quoteStatusConsumerV2;

    private static final int INDEX_FIRST_PARAMETER = 2;

    public FloorTradeMaintenanceServiceV6(
            EngineAccess ea, FloorTradeMaintenanceService oq)
    {
        engineAccess = ea;
        floorTradeMaintenanceServiceV6 = oq;

        quoteStatusConsumerV2 = new QuoteStatusConsumerV2();
        engineAccess.associateWithOrb(quoteStatusConsumerV2);        
    }

    /** Execute a command on an FloorTradeMaintenanceService object.
     * @param command Words from command line: FloorTradeMaintenanceServiceV6 function args...
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
            if (cmd.equalsIgnoreCase("acceptFloorTrade"))
            {
                doAcceptFloorTrade(command);
            }
            else if (cmd.equalsIgnoreCase("deleteFloorTrade"))
            {
                doDeleteFloorTrade(command);
            }
            else if (cmd.equalsIgnoreCase("subscribeForFloorTradeReportsByClass"))
            {
                doSubscribeForFloorTradeReportsByClass(command);
            }
            else if (cmd.equalsIgnoreCase("unsubscribeForFloorTradeReportsByClass"))
            {
                doUnsubscribeForFloorTradeReportsByClass(command);
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

    private void doAcceptFloorTrade(String command[]) throws Throwable
    {
        String names[] = { "floorTrade" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing floorTrade");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof FloorTradeEntryStruct))
        {
            Log.message("Not a FloorTradeEntryStruct:" + objName);
            return;
        }
        FloorTradeEntryStruct floorTrade = (FloorTradeEntryStruct) o;

        CboeIdStruct ci =
                floorTradeMaintenanceServiceV6.acceptFloorTrade(floorTrade);
        Log.message(Struct.toString(ci));
    }

    private void doDeleteFloorTrade(String command[]) throws Throwable
    {
        String names[] = { "sessionName", "productKey", "tradeId", "user",
                "firm", "reason" };        
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

        if (values[2] == null)
        {
            Log.message("Missing tradeId");
            return;
        }
        String objName = values[2];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof CboeIdStruct))
        {
            Log.message("Not a CboeIdStruct:" + objName);
            return;
        }
        CboeIdStruct tradeId = (CboeIdStruct) o;

        if (values[3] == null)
        {
            Log.message("Missing user");
            return;
        }
        objName = values[3];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof ExchangeAcronymStruct))
        {
            Log.message("Not an ExchangeAcronymStruct:" + objName);
            return;
        }
        ExchangeAcronymStruct user = (ExchangeAcronymStruct) o;

        if (values[4] == null)
        {
            Log.message("Missing firm");
            return;
        }
        objName = values[4];
        o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof ExchangeFirmStruct))
        {
            Log.message("Not an ExchangeFirmStruct:" + objName);
            return;
        }
        ExchangeFirmStruct firm = (ExchangeFirmStruct) o;

        if (values[5] == null)
        {
            Log.message("Missing reason");
            return;
        }
        String reason = values[5];

        floorTradeMaintenanceServiceV6.deleteFloorTrade(sessionName, productKey,
                tradeId, user, firm, reason);
    }

    private void doSubscribeForFloorTradeReportsByClass(String command[])
            throws Throwable
    {
        String names[] = { "classKey", "gmdCallback" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing gmdCallback");
            return;
        }
        boolean gmdCallback = CommandLine.booleanValue(values[1]);

        floorTradeMaintenanceServiceV6.subscribeForFloorTradeReportsByClass(
                quoteStatusConsumerV2._this(), classKey, gmdCallback);
    }

    private void doUnsubscribeForFloorTradeReportsByClass(String command[])
            throws Throwable
    {
        String names[] = { "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        floorTradeMaintenanceServiceV6.unsubscribeForFloorTradeReportsByClass(
                quoteStatusConsumerV2._this(), classKey);
    }
}
