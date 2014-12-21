// TradeServiceClient.java
package com.cboe.ffClient;

import com.cboe.ffidl.ffExceptions.*;
import com.cboe.ffidl.ffBusinessServices.*;
import com.cboe.ffidl.ffTrade.ExecutionReportStruct;
import com.cboe.ffidl.ffUtil.Sides;
import com.cboe.ffidl.ffUtil.TimeStruct;
import com.cboe.ffUtil.TimeHelper;
import com.cboe.infrastructureUtility.CBOETradingBinder;
import java.util.*;

/**
*
* TradeServiceClient is used as a driver program to test the TradeService.
*
* @author Mark Woyna
* @since 11/26/2001
* @version 1.0
*/
public class TradeServiceClient
{
    static org.omg.CORBA.ORB orb;

    public static TradeService getTradeService(org.omg.CORBA.ORB orb)
        throws Exception
    {
        // Find Trader IOR from initrefs, query for the TradeService service type, return
        // the CORBA proxy objects.
        //
        CBOETradingBinder binder = new CBOETradingBinder(orb);
        org.omg.CORBA.Object[] objects = binder.resolveFromString("ffBusinessServices/TradeService", "routeName == " + System.getProperty("prefixRemoteRouteName"));

        // Validate results and return the narrowed reference.
        //
        if (objects == null || objects.length == 0)
        {
            throw new RuntimeException("Service query returned no references!");
        }
        if (objects.length > 1)
        {
            System.err.println("Found " + objects.length + " matches for service query.  Using first one.");
        }
        return TradeServiceHelper.narrow(objects[0]);
    }

    /**
    * The main driver program
    *
    * @fyi use args[0] to pass IOR file for TradeService reference
    */
    public static void main(String[] args)
    {
        if ( args.length != 3  || (!args[2].equals("B") && !args[2].equals("S")))
        {
            System.err.println("Usage: TradeServiceClient <IORfile> <NumberOfMessages> <Side {B|S}>");
            System.err.println("args.length=" + args.length);
            System.exit(-1);
        }

        Random rnd = new Random(System.currentTimeMillis());

        try
        {
            // Initialize the ORB.
            System.out.println("Initializing the ORB...");
            orb = com.cboe.ORBInfra.ORB.Orb.init(new String[0],null);
            System.out.println("Initialized the ORB.");

            // Locate a TradeService through the IOR file.
            System.out.println("Resolving TradeService...");
            TradeService tradeService =  getTradeService(orb);
            System.out.println("Resolved TradeService: " + tradeService);

            // Initialize struct to send: set fields that the iteration will not be modifying
            //
            ExecutionReportStruct executionReport = new ExecutionReportStruct();
            executionReport.user = "ABC";
            executionReport.side = args[2].equals("B") ? Sides.BUY : Sides.SELL;
            executionReport.quantity = 10;

            final String[] symbols = { "IBM", "AOL", "DJX", "GM" };
            final float[] prices = { 5.0f, 7.5f, 12f };

            int count = Integer.parseInt(args[1]);
            System.out.println("Sending " + count + " message(s)...");
            for (int i = 0 ; i < count ; i++ )
            {
                executionReport.symbol = symbols[rnd.nextInt(symbols.length)];
                executionReport.price = prices[rnd.nextInt(prices.length)];
                executionReport.sentTime = TimeHelper.createTimeStruct();

                System.out.println();
                System.out.println("Sending report " + (i+1) + "/" + count + ":");
                com.cboe.util.ReflectiveObjectWriter.writeObject(executionReport, "  executionReport.");

                tradeService.acceptExecutionReport(executionReport);

                System.out.println("Done sending report.");
            }

            System.out.println("Client waiting 3 seconds before disconnecting...");
            Thread.sleep(3000);
        }
        catch (DataValidationException ex)
        {
            System.err.println("Data validation error: " + ex.details.message);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        System.out.println("SHUTTING DOWN ORB...");
	orb.shutdown(false/*!waitforcompletion*/);
        System.out.println("EXITING MAIN()");
		
        //System.exit(0); // there are CORBA threads floating around out there...
    }
}
