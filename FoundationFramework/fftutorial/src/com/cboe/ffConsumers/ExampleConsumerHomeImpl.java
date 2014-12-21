
package com.cboe.ffConsumers;

import com.cboe.ffidl.ffTrade.TradeReportStruct;
import com.cboe.ffInterfaces.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.util.*;
import java.util.*;

public class ExampleConsumerHomeImpl extends BOHome implements TradeReportConsumer
{
	public void goMaster(boolean failover)
	{
		try
		{
			TradeReportConsumerHome home = (TradeReportConsumerHome)HomeFactory.getInstance().findHome(TradeReportConsumerHome.HOME_NAME);
			home.addConsumer(this);
			Log.information(this, "Registered as an unfiltered TradeReportConsumer");
		}
		catch (Exception ex)
		{
			Log.exception(this, "Failed to go master", ex);
		}
	}

	public void acceptTradeReport(String session, TradeReportStruct report)
	{
		System.out.println();
		System.out.println("Received trade report:");
		System.out.println("  session = \"" + session + "\"");
		ReflectiveObjectWriter.writeObject(report, "  tradeReport");
	}
}
