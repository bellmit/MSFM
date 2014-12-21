package com.cboe.ffBusinessServices.tradeMatchService;

import com.cboe.ffInterfaces.TradeMatchService;
import com.cboe.ffidl.ffExceptions.*;
import com.cboe.ffidl.ffTrade.ExecutionReportStruct;
import com.cboe.ffidl.ffTrade.TradeReportStruct;
import com.cboe.ffUtil.ExceptionBuilder;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;

/**
 *  Very simple trade match service: trades are matched if the complimentary sides' price & volume are identical in the same session.
 */
public class TradeMatchServiceProxy
    extends BObject
    implements TradeMatchService
{
    protected com.cboe.ffidl.ffBusinessServices.TradeMatchService delegate;

	/**
	 * Create a new instance of ths interceptor for the provided BObject.
	 * Create a cache of the instrumentors necessary for instrumentation.
	 */
	public TradeMatchServiceProxy(org.omg.CORBA.Object delegate)
	{
		setDelegate(delegate);
	}


    public synchronized void acceptExecutionReport(String tradingSession, ExecutionReportStruct executionReport)
        throws DataValidationException, CommunicationException, AuthorizationException, SystemException
    {
		String methodID = "acceptExecutionReportMethodId";
		try
		{
			delegate.acceptExecutionReport(tradingSession, executionReport);
		}
		catch(org.omg.CORBA.COMM_FAILURE e)
		{
			Log.exception(this, e);
			throw ExceptionBuilder.communicationException(e.toString(), 0);
		}
		catch(org.omg.CORBA.NO_PERMISSION e)
		{
			Log.exception(this, e);
			throw ExceptionBuilder.authorizationException(e.toString(), 0);
		}
		catch(RuntimeException e)
		{
			Log.exception(this, e);
			throw ExceptionBuilder.systemException(e.toString(), 0);
		}

	}

	private void setDelegate(org.omg.CORBA.Object delegate)
	{
		this.delegate = (com.cboe.ffidl.ffBusinessServices.TradeMatchService)delegate;
	}
}
