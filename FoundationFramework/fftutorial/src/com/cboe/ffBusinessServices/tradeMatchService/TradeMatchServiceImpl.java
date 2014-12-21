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

/**
 *  Very simple trade match service: trades are matched if the complimentary sides' price & volume are identical in the same session.
 */
public class TradeMatchServiceImpl
    extends BObject
    implements TradeMatchService
{
    protected static final DecimalFormat PRICE_FORMATTER = new DecimalFormat("######.##");

    protected Map buyReports  = new HashMap(); // map: ( {sess:prodSym:volume:price} -> ExecutionReportStruct )
    protected Map sellReports = new HashMap(); // map: ( {sess:prodSym:volume:price} -> ExecutionReportStruct )

    protected TradeReportConsumer publisher;

    public synchronized void acceptExecutionReport(String tradingSession, ExecutionReportStruct executionReport)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
        Log.debug(this, "acceptExecutionReport(tradingSession="+tradingSession+", executionReport)");
        Object hashKey = createKey(tradingSession, executionReport);
        Log.debug(this, "acceptExecutionReport - hashKey=" + hashKey + ", side=" + executionReport.side);


        Map reportSide, matchSide;
        switch (executionReport.side)
        {
            case Sides.BUY:
                reportSide = buyReports;
                matchSide = sellReports;
                break;
            case Sides.SELL:
                reportSide = sellReports;
                matchSide = buyReports;
                break;
            default:
                throw ExceptionBuilder.dataValidationException("Invalid side in report: '" + executionReport.side + "'", 0);
        }

        Object match = matchSide.get(hashKey);
        if (match != null)
        {
            Log.debug(this, "acceptExecutionReport - found match for " + hashKey);
            matchSide.remove(hashKey);
            processMatch(tradingSession, executionReport, (ExecutionReportStruct)match);
        }
        else
        {
            Log.debug(this, "acceptExecutionReport - no match for " + hashKey + ", adding to book");
            reportSide.put(hashKey, executionReport);
        }
        Log.debug(this, "acceptExecutionReport done.");
    }

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

        Log.debug(this, "Created tradeReport");

        getPublisher().acceptTradeReport(tradingSession, tradeReport);
        Log.debug(this, "Published tradeReport to event channel.");
    }

    protected TradeReportConsumer getPublisher()
        throws SystemException
    {
        if (publisher == null)
        {
            BOHome home;
            try
            {
                home = HomeFactory.getInstance().findHome(TradeReportConsumerHome.HOME_NAME);
            }
            catch (CBOELoggableException ex)
            {
                throw ExceptionBuilder.systemException("Home not found: " + TradeReportConsumerHome.HOME_NAME, 0);
            }
            publisher = ((TradeReportConsumerHome)home).find();
        }
        return publisher;
    }

    protected TradeReportStruct createTradeReport(ExecutionReportStruct buy, ExecutionReportStruct sell)
    {
        TradeReportStruct tradeReport = new TradeReportStruct();
        tradeReport.symbol = buy.symbol;
        tradeReport.buyer = buy.user;
        tradeReport.seller = sell.user;
        tradeReport.price = buy.price;
        tradeReport.quantity = buy.quantity;
        tradeReport.sentTime = TimeHelper.createTimeStruct();
        return tradeReport;
    }

    protected Object createKey(String tradingSession, ExecutionReportStruct executionReport)
    {
        StringBuffer buf = new StringBuffer();
        buf.append(tradingSession);
        buf.append(':');
        buf.append(executionReport.symbol);
        buf.append(':');
        buf.append(executionReport.quantity);
        buf.append(':');
        buf.append(PRICE_FORMATTER.format(executionReport.price));
        return buf.toString();
    }

    public void registerCallbacks()
    {
        try
        {
            getBOHome().registerCommand(this, "dumpBook", "adminDumpBook",
                "print all execution reports in the book", new String[0], new String[0]);
        }
        catch (CBOELoggableException ex)
        {
            Log.exception(this, "Failed to register callback dumpBook", ex);
        }
    }

    public String adminDumpBook()
    {
        Log.debug("Admin request adminDumpBook invoked");
        try
        {
            StringWriter strWriter = new StringWriter();
            PrintWriter writer = new PrintWriter(strWriter);
            writer.println();
            writer.println("---BUY SIDE---");
            writeReports("  ", writer, buyReports.values());
            writer.println();
            writer.println("---SELL SIDE---");
            writeReports("  ", writer, sellReports.values());
            writer.println();
            writer.flush();
            strWriter.flush();
            String result = strWriter.toString();
            writer.close();
            strWriter.close();
            Log.debug("Admin request adminDumpBook complete.");
            return result;
        }
        catch (IOException ex)
        {
            // (should never happen when writing to a string)
            throw new RuntimeException("IOException writing to a string: " + ex);
        }
    }

    protected void writeReports(String prefix, PrintWriter writer, Collection reportStructs) throws IOException
    {
        Iterator iter = reportStructs.iterator();

                     // (10)       (8)      (4)  (4.2)   (8)      (2.2.2.2)
        writer.print(prefix);
        writer.println(" Symbol     User     Side Price   Quantity SentTime   ");
        writer.print(prefix);
        writer.println("---------- -------- ---- ------- -------- -----------");
        while (iter.hasNext())
        {
            ExecutionReportStruct report = (ExecutionReportStruct)iter.next();
            String side     = (report.side==Sides.BUY) ? "BUY" : "SELL";
            String price    = PRICE_FORMATTER.format(report.price);
            String quantity = Integer.toString(report.quantity);
            String time     = TimeHelper.toString(report.sentTime);

            writer.print(prefix);
            Util.writePaddedString(writer, report.symbol, 10+1, false);
            Util.writePaddedString(writer, report.user,   8+1,  false);
            Util.writePaddedString(writer, side,          4+1,  false);
            Util.writePaddedString(writer, price,         7+1,  false);
            Util.writePaddedString(writer, quantity,      8+1,  false);
            Util.writePaddedString(writer, time,          11,   false);
            writer.println();
        }
    }
}
