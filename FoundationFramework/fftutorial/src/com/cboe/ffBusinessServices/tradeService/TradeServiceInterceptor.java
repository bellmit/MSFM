package com.cboe.ffBusinessServices.tradeService;

import com.cboe.ffInterfaces.TradeService;
import com.cboe.infrastructureServices.instrumentationService.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.ffUtil.ExceptionBuilder;

public class TradeServiceInterceptor extends BOInterceptor implements TradeService
{


	Instrumentor getExecutionReportsForUser1;

	Instrumentor acceptExecutionReport0;
	TradeService bObjectImpl = (TradeService)getBObject();
	/**
	 * Create a new instance of ths interceptor for the provided BObject.
	 * Create a cache of the instrumentors necessary for instrumentation.
	 */
	public TradeServiceInterceptor(BObject bo)
	{
		super(bo);
        String className = bo.getClass().getName();
		acceptExecutionReport0 = createInstrumentor("acceptExecutionReport0", className + "::acceptExecutionReport(Lcom.cboe.ffidl.ffTrade.ExecutionReportStruct;)V");
		getExecutionReportsForUser1 = createInstrumentor("getExecutionReportsForUser1", className + "::getExecutionReportsForUser(Ljava.lang.String;)Lcom.cboe.ffidl.ffTrade.ExecutionReportStruct;");
	}
	/**
	 */
	public void acceptExecutionReport( com.cboe.ffidl.ffTrade.ExecutionReportStruct param0) throws com.cboe.ffidl.ffExceptions.SystemException, com.cboe.ffidl.ffExceptions.CommunicationException, com.cboe.ffidl.ffExceptions.DataValidationException, com.cboe.ffidl.ffExceptions.AuthorizationException
	{
		long time = 0;
		boolean exception = false;
		String methodID = "acceptExecutionReport0";
		Instrumentor in = acceptExecutionReport0;
		try
		{
			time = System.currentTimeMillis();
			preProcess(in);
			bObjectImpl.acceptExecutionReport(param0);
		}
		catch(RuntimeException ex)
		{
			in.incError(ex);
			systemLog(ex, methodID);
			exception = true;
			throw ExceptionBuilder.systemException(ex.toString(), 0);
		}
		catch(com.cboe.ffidl.ffExceptions.SystemException e)
		{
			in.incError(e);
			debugLog(e, methodID);
			exception = true;
			throw e;
		}

		catch(com.cboe.ffidl.ffExceptions.CommunicationException e)
		{
			in.incError(e);
			debugLog(e, methodID);
			exception = true;
			throw e;
		}

		catch(com.cboe.ffidl.ffExceptions.DataValidationException e)
		{
			in.incError(e);
			debugLog(e, methodID);
			exception = true;
			throw e;
		}

		catch(com.cboe.ffidl.ffExceptions.AuthorizationException e)
		{
			in.incError(e);
			debugLog(e, methodID);
			exception = true;
			throw e;
		}

		finally
		{
			postProcess(time, methodID, acceptExecutionReport0, exception);
		}
	}
	/**
	 */
	public com.cboe.ffidl.ffTrade.ExecutionReportStruct [] getExecutionReportsForUser( java.lang.String param0) throws com.cboe.ffidl.ffExceptions.SystemException, com.cboe.ffidl.ffExceptions.CommunicationException, com.cboe.ffidl.ffExceptions.AuthorizationException
	{
		long time = 0;
		boolean exception = false;
		String methodID = "getExecutionReportsForUser1";
		Instrumentor in = getExecutionReportsForUser1;
		try
		{
			time = System.currentTimeMillis();
			preProcess(in);
			return bObjectImpl.getExecutionReportsForUser(param0);
		}
		catch(RuntimeException ex)
		{
			in.incError(ex);
			systemLog(ex, methodID);
			exception = true;
			throw ExceptionBuilder.systemException(ex.toString(), 0);
		}
		catch(com.cboe.ffidl.ffExceptions.SystemException e)
		{
			in.incError(e);
			debugLog(e, methodID);
			exception = true;
			throw e;
		}

		catch(com.cboe.ffidl.ffExceptions.CommunicationException e)
		{
			in.incError(e);
			debugLog(e, methodID);
			exception = true;
			throw e;
		}

		catch(com.cboe.ffidl.ffExceptions.AuthorizationException e)
		{
			in.incError(e);
			debugLog(e, methodID);
			exception = true;
			throw e;
		}

		finally
		{
			postProcess(time, methodID, getExecutionReportsForUser1, exception);
		}
	}
}
