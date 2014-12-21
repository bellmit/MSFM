package com.cboe.ffBusinessServices.tradeMatchService;

import com.cboe.ffidl.ffExceptions.*;
import com.cboe.ffidl.ffTrade.ExecutionReportStruct;
import com.cboe.ffidl.ffTrade.TradeReportStruct;
import com.cboe.ffidl.ffUtil.Sides;
import com.cboe.ffidl.ffUtil.TimeStruct;
import com.cboe.ffInterfaces.TradeMatchService;
import com.cboe.ffInterfaces.TradeReportConsumer;
import com.cboe.ffInterfaces.TradeReportConsumerHome;
import com.cboe.ffUtil.ExceptionBuilder;
import com.cboe.ffUtil.TimeHelper;
import com.cboe.ffUtil.Util;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.text.DecimalFormat;
import java.util.Calendar;
import java.util.Collection;
import java.util.Iterator;
import java.util.Map;
import java.util.HashMap;

public class TradeMatchServiceSimpleImpl
    extends TradeMatchServiceImpl
    implements TradeMatchService
{
    protected void processMatch(String tradingSession, ExecutionReportStruct side1, ExecutionReportStruct side2)
        throws SystemException
    {
        ExecutionReportStruct buy, sell;
        if (side1.side == Sides.BUY)
        {
            buy = side1;
            sell = side2;
        }
        else // (must be SELL, we're past input validation at this point)
        {
            buy = side2;
            sell = side1;
        }

        TradeReportStruct tradeReport = createTradeReport(buy, sell);

        Log.information(this, "Created tradeReport");
        com.cboe.util.ReflectiveObjectWriter.writeObject(tradeReport, "  tradeReport");
    }
}
