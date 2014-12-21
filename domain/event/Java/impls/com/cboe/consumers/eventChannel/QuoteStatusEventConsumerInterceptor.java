package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.QuoteStatusConsumer;
import com.cboe.interfaces.events.QuoteStatusConsumerV2;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;

public class QuoteStatusEventConsumerInterceptor implements QuoteStatusConsumerV2
{

    MethodInstrumentor acceptQuoteBustReportV37;


    MethodInstrumentor acceptQuoteFillReportV36;


    MethodInstrumentor acceptQuoteDeleteReportV35;


    MethodInstrumentor acceptQuoteStatusUpdate4;


    MethodInstrumentor acceptQuoteFillReport3;


    MethodInstrumentor acceptQuoteDeleteReportV22;


    MethodInstrumentor acceptQuoteDeleteReport1;

    private QuoteStatusConsumerV2 delegate;

    MethodInstrumentor acceptQuoteBustReport0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    MethodInstrumentor acceptQuoteStatus;
    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public QuoteStatusEventConsumerInterceptor(Object bo)
    {
        setDelegate(bo);
    }

    private MethodInstrumentorFactory getMethodInstrumentorFactory()
    {
        if (methodInstrumentorFactory == null)
        {
            methodInstrumentorFactory = FoundationFramework.getInstance().getInstrumentationService().getMethodInstrumentorFactory();
        }
        return methodInstrumentorFactory;
    }

    public void startInstrumentation(boolean privateOnly)
    {
        try
        {
            StringBuilder name = new StringBuilder(70);
            name.append("QuoteStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptQuoteBustReport0");
            String nameString = name.toString();
            acceptQuoteBustReport0 = getMethodInstrumentorFactory().find(nameString);
            if (acceptQuoteBustReport0 == null)
            {
	            acceptQuoteBustReport0 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptQuoteBustReport0);
	            acceptQuoteBustReport0.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("QuoteStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptQuoteDeleteReport1");
            nameString = name.toString();
            acceptQuoteDeleteReport1 = getMethodInstrumentorFactory().find(nameString);
            if (acceptQuoteDeleteReport1 == null)
            {
	            acceptQuoteDeleteReport1 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptQuoteDeleteReport1);
	            acceptQuoteDeleteReport1.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("QuoteStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptQuoteDeleteReportV22");
            nameString = name.toString();
            acceptQuoteDeleteReportV22 = getMethodInstrumentorFactory().find(nameString);
            if (acceptQuoteDeleteReportV22 == null)
            {
	            acceptQuoteDeleteReportV22 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptQuoteDeleteReportV22);
	            acceptQuoteDeleteReportV22.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("QuoteStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptQuoteFillReport3");
            nameString = name.toString();
            acceptQuoteFillReport3 = getMethodInstrumentorFactory().find(nameString);
            if (acceptQuoteFillReport3 == null)
            {
	            acceptQuoteFillReport3 = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptQuoteFillReport3);
	            acceptQuoteFillReport3.setPrivate(privateOnly);
            }
            name.setLength(0);
            name.append("QuoteStatusEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptQuoteStatus");
            nameString = name.toString();
            acceptQuoteStatus = getMethodInstrumentorFactory().find(nameString);
            if (acceptQuoteStatus == null)
            {
	            acceptQuoteStatus = getMethodInstrumentorFactory().create(nameString, null);
	            getMethodInstrumentorFactory().register(acceptQuoteStatus);
	            acceptQuoteStatus.setPrivate(privateOnly);
            }
        } catch (InstrumentorAlreadyCreatedException ex)
        {
            Log.exception(ex);
        }
    }

    /**
     *
     */
    public void removeInstrumentation()
    {
        getMethodInstrumentorFactory().unregister(acceptQuoteBustReport0);
        acceptQuoteBustReport0 = null;
        getMethodInstrumentorFactory().unregister(acceptQuoteDeleteReport1);
        acceptQuoteDeleteReport1 = null;
        getMethodInstrumentorFactory().unregister(acceptQuoteDeleteReportV22);
        acceptQuoteDeleteReportV22 = null;
        getMethodInstrumentorFactory().unregister(acceptQuoteFillReport3);
        acceptQuoteFillReport3 = null;
        getMethodInstrumentorFactory().unregister(acceptQuoteStatus);
        acceptQuoteStatus = null;
    }

    /**
     */
    public void acceptQuoteBustReport(int[] param0, com.cboe.idl.quote.QuoteInfoStruct param1, short param2, com.cboe.idl.cmiOrder.BustReportStruct[] param3,String param4)
    {
        boolean exception = false;
        if (acceptQuoteBustReport0 != null)
        {
            acceptQuoteBustReport0.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteBustReport(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteBustReport0 != null)
            {
                acceptQuoteBustReport0.incCalls(1);
                acceptQuoteBustReport0.afterMethodCall();
                if (exception)
                {
                    acceptQuoteBustReport0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (QuoteStatusConsumerV2) delegate;
    }

    /**
     */
    public void acceptQuoteDeleteReport(int[] param0, java.lang.String param1, int[] param2, short param3,String param4)
    {
        boolean exception = false;
        if (acceptQuoteDeleteReport1 != null)
        {
            acceptQuoteDeleteReport1.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteDeleteReport(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteDeleteReport1 != null)
            {
                acceptQuoteDeleteReport1.incCalls(1);
                acceptQuoteDeleteReport1.afterMethodCall();
                if (exception)
                {
                    acceptQuoteDeleteReport1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptQuoteDeleteReportV2(int[] param0, java.lang.String param1, com.cboe.idl.cmiQuote.QuoteStruct[] param2, short param3,String param4)
    {
        boolean exception = false;
        if (acceptQuoteDeleteReportV22 != null)
        {
            acceptQuoteDeleteReportV22.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteDeleteReportV2(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteDeleteReportV22 != null)
            {
                acceptQuoteDeleteReportV22.incCalls(1);
                acceptQuoteDeleteReportV22.afterMethodCall();
                if (exception)
                {
                    acceptQuoteDeleteReportV22.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptQuoteFillReport(int[] param0, com.cboe.idl.quote.QuoteInfoStruct param1, short param2, com.cboe.idl.cmiOrder.FilledReportStruct[] param3,String param4)
    {
        boolean exception = false;
        if (acceptQuoteFillReport3 != null)
        {
            acceptQuoteFillReport3.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteFillReport(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteFillReport3 != null)
            {
                acceptQuoteFillReport3.incCalls(1);
                acceptQuoteFillReport3.afterMethodCall();
                if (exception)
                {
                    acceptQuoteFillReport3.incExceptions(1);
                }
            }
        }
    }



    /**
     */
    public void acceptQuoteStatusUpdate(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.cmiQuote.QuoteStruct param1, short param2)
    {
        boolean exception = false;
        if (acceptQuoteStatusUpdate4 != null)
        {
            acceptQuoteStatusUpdate4 .beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteStatusUpdate(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteStatusUpdate4 != null)
            {
                acceptQuoteStatusUpdate4.incCalls(1);
                acceptQuoteStatusUpdate4.afterMethodCall();
                if (exception)
                {
                    acceptQuoteStatusUpdate4.incExceptions(1);
                }
            }
        }
    }



    /**
     */
    public void acceptQuoteDeleteReportV3(com.cboe.idl.util.RoutingParameterStruct param0, String param1, com.cboe.idl.cmiQuote.QuoteStruct[] param2, short param3,String param4)
    {
        boolean exception = false;
        if (acceptQuoteDeleteReportV35 != null)
        {
            acceptQuoteDeleteReportV35.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteDeleteReportV3(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteDeleteReportV35 != null)
            {
                acceptQuoteDeleteReportV35.incCalls(1);
                acceptQuoteDeleteReportV35.afterMethodCall();
                if (exception)
                {
                    acceptQuoteDeleteReportV35.incExceptions(1);
                }
            }
        }
    }



    /**
     */
    public void acceptQuoteFillReportV3(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.quote.QuoteInfoStruct param1, short param2, com.cboe.idl.cmiOrder.FilledReportStruct[] param3, String param4)
    {
        boolean exception = false;
        if (acceptQuoteFillReportV36 != null)
        {
            acceptQuoteFillReportV36.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteFillReportV3(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteFillReportV36 != null)
            {
                acceptQuoteFillReportV36.incCalls(1);
                acceptQuoteFillReportV36.afterMethodCall();
                if (exception)
                {
                    acceptQuoteFillReportV36.incExceptions(1);
                }
            }
        }
    }



    /**
     */
    public void acceptQuoteBustReportV3(com.cboe.idl.util.RoutingParameterStruct param0, com.cboe.idl.quote.QuoteInfoStruct param1, short param2, com.cboe.idl.cmiOrder.BustReportStruct[] param3,String param4)
    {
        boolean exception = false;
        if (acceptQuoteBustReportV37 != null)
        {
            acceptQuoteBustReportV37.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteBustReportV3(param0, param1, param2, param3,param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteBustReportV37 != null)
            {
                acceptQuoteBustReportV37.incCalls(1);
                acceptQuoteBustReportV37.afterMethodCall();
                if (exception)
                {
                    acceptQuoteBustReportV37.incExceptions(1);
                }
            }
        }
    }

    public void acceptQuoteStatus(short[] seqmap, com.cboe.idl.quote.GroupQuoteFillReportStruct[] fillReports, com.cboe.idl.quote.GroupQuoteFillReportV3Struct[] fillReportsV3, 
    			com.cboe.idl.quote.GroupQuoteDeleteReportStruct[] deleteReports, com.cboe.idl.quote.GroupQuoteDeleteReportV2Struct[] deleteReportsV2, 
    			com.cboe.idl.quote.GroupQuoteDeleteReportV3Struct[] deleteReportsV3, com.cboe.idl.quote.GroupQuoteBustReportStruct[] bustReports, 
    			com.cboe.idl.quote.GroupQuoteBustReportV3Struct[] bustReportsV3, com.cboe.idl.quote.GroupQuoteStatusUpdateStruct[] statusUpdates)
    {
        boolean exception = false;
        if (acceptQuoteStatus != null)
        {
        	acceptQuoteStatus.beforeMethodCall();
        }
        try
        {
            delegate.acceptQuoteStatus(seqmap, fillReports, fillReportsV3, deleteReports, 
            							deleteReportsV2, deleteReportsV3, bustReports, bustReportsV3, statusUpdates);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptQuoteStatus != null)
            {
            	acceptQuoteStatus.incCalls(1);
            	acceptQuoteStatus.afterMethodCall();
                if (exception)
                {
                	acceptQuoteStatus.incExceptions(1);
                }
            }
        }
    	
    }


}
