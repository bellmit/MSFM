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
public class TradeServiceClient2
{
    /**
    * The main driver program
    *
    * @fyi use args[0] to pass IOR file for TradeService reference
    */
    public static void main(String[] args)
    {
        if ( args.length != 2)
        {
            System.err.println("Usage: TradeServiceClient <userAcronym>");
            System.err.println("args.length=" + args.length);
            System.exit(-1);
        }

        Random rnd = new Random(System.currentTimeMillis());

        try
        {
            // Initialize the ORB.
            System.out.println("Initializing the ORB...");
            org.omg.CORBA.ORB orb = com.cboe.ORBInfra.ORB.Orb.init(new String[0],null);
            System.out.println("Initialized the ORB.");

            // Locate a TradeService through the IOR file.
            System.out.println("Resolving TradeService...");
            TradeService tradeService =  TradeServiceClient.getTradeService(orb);
            System.out.println("Resolved TradeService: " + tradeService);

            // Initialize struct to send: set fields that the iteration will not be modifying
            //
			System.out.println("Query for user '"+args[1] + "'");
            ExecutionReportStruct[] reports = tradeService.getExecutionReportsForUser(args[1]);
			System.out.println();
			System.out.println("Query results:");
			com.cboe.util.ReflectiveObjectWriter.writeObject(reports, "  executionReport");

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
        System.out.println("EXITING MAIN()");
        System.exit(0); // there are CORBA threads floating around out there...
    }
}
