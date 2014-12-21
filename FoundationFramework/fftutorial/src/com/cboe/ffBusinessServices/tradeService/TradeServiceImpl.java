package com.cboe.ffBusinessServices.tradeService;

import com.cboe.ffidl.ffExceptions.*;
import com.cboe.ffidl.ffTrade.ExecutionReportStruct;
import com.cboe.ffidl.ffTrade.TradeReportStruct;
import com.cboe.ffidl.ffUtil.Sides;
import com.cboe.ffidl.ffUtil.TimeStruct;
import com.cboe.ffInterfaces.*;
import com.cboe.ffUtil.ExceptionBuilder;
import com.cboe.ffUtil.TimeHelper;
import com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.foundationFramework.utilities.Transaction;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import java.util.Collection;
import java.util.Iterator;

public class TradeServiceImpl
    extends BObject
    implements TradeService
{
    protected TradeMatchServiceHome tradeMatchServiceHome;
    protected ExecutionReportHome executionReportHome;
    protected ProductHome productHome;

    public ExecutionReportStruct[] getExecutionReportsForUser(String user)
        throws SystemException
    {
        Log.debug(this, "getExecutionReportsForUser(user=" + user + ")");
        Collection reports;
        try
        {
            reports = getExecutionReportHome().findForAcronym(user);
            Log.debug(this, "getExecutionReportsForUser: found " + reports.size() + " report(s).");
        }
        catch (PersistenceException ex)
        {
            throw ExceptionBuilder.systemException("Error querying for user '" + user + "': " + ex, 0);
        }
        ExecutionReportStruct[] result = new ExecutionReportStruct[reports.size()];
        Iterator iter = reports.iterator();
        for (int i=0; iter.hasNext(); i++)
        {
            ExecutionReport report = (ExecutionReport)iter.next();
            result[i] = report.toStruct();
        }
        Log.debug(this, "getExecutionReportsForUser: returning results.");
        return result;
    }

    public void acceptExecutionReport(ExecutionReportStruct executionReport)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        Log.debug(this, "acceptExecutionReport(executionReport)");

        Log.debug(this, "acceptExecutionReport: validate side");
        if (executionReport.side != Sides.BUY && executionReport.side != Sides.SELL)
        {
            throw ExceptionBuilder.dataValidationException("Invalid side indicator " + executionReport.side, 0);
        }

        Log.debug(this, "acceptExecutionReport: validate product");
        try
        {
            Product product = getProductHome().find(executionReport.symbol);
        }
        catch (NotFoundException ex)
        {
            throw ExceptionBuilder.dataValidationException("Unknown product " + executionReport.symbol, 0);
        }
        catch (PersistenceException ex)
        {
            throw ExceptionBuilder.systemException("Failure querying for product " + executionReport.symbol, 0);
        }

        Log.debug(this, "acceptExecutionReport: create persistent report");
        Transaction.startTransaction();
        boolean committed = false;
        try
        {
            getExecutionReportHome().create(executionReport);
            committed = Transaction.commit();
        }
        finally
        {
            if (!committed)
            {
                Transaction.rollback();
            }
        }


        String session = getSessionForReport(executionReport);
        Log.debug(this, "acceptExecutionReport: publish execution report to channel for session '" + session + "'");
        getTradeMatchServiceHome().find().acceptExecutionReport(session, executionReport);
        Log.debug(this, "acceptExecutionReport: done.");
    }

    protected static final String[] ETH_SYMBOLS = { "IBM", "AOL", "YHOO" };

    /**
     *  Fake a way to route reports to sessions based on product.
     */
    protected String getSessionForReport(ExecutionReportStruct report)
    {
        for (int i=0; i < ETH_SYMBOLS.length; i++)
        {
            if (report.symbol.startsWith(ETH_SYMBOLS[i]))
            {
                return "ETH";
            }
        }
        return "RTH";
    }

    protected TradeMatchServiceHome getTradeMatchServiceHome() throws SystemException
    {
        if (tradeMatchServiceHome == null)
        {
            tradeMatchServiceHome = (TradeMatchServiceHome)findHome(TradeMatchServiceHome.HOME_NAME);
        }
        return tradeMatchServiceHome;
    }

    protected ExecutionReportHome getExecutionReportHome() throws SystemException
    {
        if (executionReportHome == null)
        {
            executionReportHome = (ExecutionReportHome)findHome(ExecutionReportHome.HOME_NAME);
        }
        return executionReportHome;
    }

    protected ProductHome getProductHome() throws SystemException
    {
        if (productHome == null)
        {
            productHome = (ProductHome)findHome(ProductHome.HOME_NAME);
        }
        return productHome;
    }

    protected BOHome findHome(String homeName) throws SystemException
    {
        try
        {
            return HomeFactory.getInstance().findHome(homeName);
        }
        catch (CBOELoggableException ex)
        {
            throw ExceptionBuilder.systemException("Failed to find home '" + homeName + "'", 0);
        }
    }

}
