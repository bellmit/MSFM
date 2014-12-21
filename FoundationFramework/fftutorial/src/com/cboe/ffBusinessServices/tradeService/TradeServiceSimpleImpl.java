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

public class TradeServiceSimpleImpl
    extends TradeServiceImpl
    implements TradeService
{
    public ExecutionReportStruct[] getExecutionReportsForUser(String user)
        throws SystemException
    {
        Log.debug(this, "getExecutionReportsForUser(user=" + user + ")");
        return new ExecutionReportStruct[0];
    }

    public void acceptExecutionReport(ExecutionReportStruct executionReport)
        throws DataValidationException, SystemException, CommunicationException, AuthorizationException
    {
        Log.debug(this, "debug acceptExecutionReport(executionReport)");
        Log.information(this, "info acceptExecutionReport(executionReport)");
        Log.alarm(this, "alarm acceptExecutionReport(executionReport)");
        System.out.println("sysout acceptExecutionReport(executionReport)");
        System.err.println("syserr acceptExecutionReport(executionReport)");

        Log.debug(this, "acceptExecutionReport: validate side");
        if (executionReport.side != Sides.BUY && executionReport.side != Sides.SELL)
        {
            throw ExceptionBuilder.dataValidationException("Invalid side indicator " + executionReport.side, 0);
        }

        String session = getSessionForReport(executionReport);
        Log.debug(this, "acceptExecutionReport: publish execution report to channel for session '" + session + "'");
        getTradeMatchServiceHome().find().acceptExecutionReport(session, executionReport);
        Log.debug(this, "acceptExecutionReport: done.");
    }
}
