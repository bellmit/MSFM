package com.cboe.consumers.eventChannel;

import com.cboe.idl.session.TradingSessionElementStruct;
import com.cboe.idl.session.TradingSessionElementStructV2;
import com.cboe.infrastructureServices.foundationFramework.FoundationFramework;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.interfaces.events.TradingSessionConsumer;

public class TradingSessionEventConsumerInterceptor implements TradingSessionConsumer
{


	MethodInstrumentor acceptTradingSessionElement7;
	MethodInstrumentor acceptTradingSessionElement8;
    MethodInstrumentor updateProductStrategy6;


    MethodInstrumentor updateProductClass5;


    MethodInstrumentor updateProduct4;


    MethodInstrumentor acceptTradingSessionState3;


    MethodInstrumentor acceptBusinessDayEvent2;


    MethodInstrumentor setProductStates1;

    private TradingSessionConsumer delegate;

    MethodInstrumentor setClassState0;
    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public TradingSessionEventConsumerInterceptor(Object bo)
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
            name.append("TradingSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("setClassState0");
            setClassState0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(setClassState0);
            setClassState0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TradingSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("setProductStates1");
            setProductStates1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(setProductStates1);
            setProductStates1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TradingSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptBusinessDayEvent2");
            acceptBusinessDayEvent2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptBusinessDayEvent2);
            acceptBusinessDayEvent2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TradingSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptTradingSessionState3");
            acceptTradingSessionState3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptTradingSessionState3);
            acceptTradingSessionState3.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TradingSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("updateProduct4");
            updateProduct4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(updateProduct4);
            updateProduct4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TradingSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("updateProductClass5");
            updateProductClass5 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(updateProductClass5);
            updateProductClass5.setPrivate(privateOnly);
            name.setLength(0);
            name.append("TradingSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("updateProductStrategy6");
            updateProductStrategy6 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(updateProductStrategy6);
            updateProductStrategy6.setPrivate(privateOnly);

            acceptTradingSessionElement7 = getMethodInstrumentorFactory().create("TradingSessionEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptTradingSessionElementUpdate", null);
            getMethodInstrumentorFactory().register(acceptTradingSessionElement7);
            acceptTradingSessionElement7.setPrivate(privateOnly);

            acceptTradingSessionElement8 = getMethodInstrumentorFactory().create("TradingSessionEventConsumerInterceptor" + Instrumentor.NAME_DELIMITER + "acceptTradingSessionElementUpdateV2", null);
            getMethodInstrumentorFactory().register(acceptTradingSessionElement8);
            acceptTradingSessionElement8.setPrivate(privateOnly);

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
        getMethodInstrumentorFactory().unregister(setClassState0);
        setClassState0 = null;
        getMethodInstrumentorFactory().unregister(setProductStates1);
        setProductStates1 = null;
        getMethodInstrumentorFactory().unregister(acceptBusinessDayEvent2);
        acceptBusinessDayEvent2 = null;
        getMethodInstrumentorFactory().unregister(acceptTradingSessionState3);
        acceptTradingSessionState3 = null;
        getMethodInstrumentorFactory().unregister(updateProduct4);
        updateProduct4 = null;
        getMethodInstrumentorFactory().unregister(updateProductClass5);
        updateProductClass5 = null;
        getMethodInstrumentorFactory().unregister(updateProductStrategy6);
        updateProductStrategy6 = null;
        getMethodInstrumentorFactory().unregister(acceptTradingSessionElement7);
        acceptTradingSessionElement7 = null;
        getMethodInstrumentorFactory().unregister(acceptTradingSessionElement8);
        acceptTradingSessionElement8 = null;
    }

    /**
     */
    public void setClassState(com.cboe.idl.cmiSession.ClassStateStruct param0)
    {
        boolean exception = false;
        if (setClassState0 != null)
        {
            setClassState0.beforeMethodCall();
        }
        try
        {
            delegate.setClassState(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (setClassState0 != null)
            {
                setClassState0.incCalls(1);
                setClassState0.afterMethodCall();
                if (exception)
                {
                    setClassState0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (TradingSessionConsumer) delegate;
    }

    /**
     */
    public void setProductStates(int param0, java.lang.String param1, com.cboe.idl.cmiSession.ProductStateStruct[] param2)
    {
        boolean exception = false;
        if (setProductStates1 != null)
        {
            setProductStates1.beforeMethodCall();
        }
        try
        {
            delegate.setProductStates(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (setProductStates1 != null)
            {
                setProductStates1.incCalls(1);
                setProductStates1.afterMethodCall();
                if (exception)
                {
                    setProductStates1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptBusinessDayEvent(com.cboe.idl.session.BusinessDayStruct param0)
    {
        boolean exception = false;
        if (acceptBusinessDayEvent2 != null)
        {
            acceptBusinessDayEvent2.beforeMethodCall();
        }
        try
        {
            delegate.acceptBusinessDayEvent(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptBusinessDayEvent2 != null)
            {
                acceptBusinessDayEvent2.incCalls(1);
                acceptBusinessDayEvent2.afterMethodCall();
                if (exception)
                {
                    acceptBusinessDayEvent2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptTradingSessionState(com.cboe.idl.cmiSession.TradingSessionStateStruct param0)
    {
        boolean exception = false;
        if (acceptTradingSessionState3 != null)
        {
            acceptTradingSessionState3.beforeMethodCall();
        }
        try
        {
            delegate.acceptTradingSessionState(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptTradingSessionState3 != null)
            {
                acceptTradingSessionState3.incCalls(1);
                acceptTradingSessionState3.afterMethodCall();
                if (exception)
                {
                    acceptTradingSessionState3.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void updateProduct(com.cboe.idl.cmiSession.SessionProductStruct param0)
    {
        boolean exception = false;
        if (updateProduct4 != null)
        {
            updateProduct4.beforeMethodCall();
        }
        try
        {
            delegate.updateProduct(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (updateProduct4 != null)
            {
                updateProduct4.incCalls(1);
                updateProduct4.afterMethodCall();
                if (exception)
                {
                    updateProduct4.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void updateProductClass(com.cboe.idl.cmiSession.SessionClassStruct param0)
    {
        boolean exception = false;
        if (updateProductClass5 != null)
        {
            updateProductClass5.beforeMethodCall();
        }
        try
        {
            delegate.updateProductClass(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (updateProductClass5 != null)
            {
                updateProductClass5.incCalls(1);
                updateProductClass5.afterMethodCall();
                if (exception)
                {
                    updateProductClass5.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void updateProductStrategy(com.cboe.idl.cmiSession.SessionStrategyStruct param0)
    {
        boolean exception = false;
        if (updateProductStrategy6 != null)
        {
            updateProductStrategy6.beforeMethodCall();
        }
        try
        {
            delegate.updateProductStrategy(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (updateProductStrategy6 != null)
            {
                updateProductStrategy6.incCalls(1);
                updateProductStrategy6.afterMethodCall();
                if (exception)
                {
                    updateProductStrategy6.incExceptions(1);
                }
            }
        }
    }

	@Override
	public void acceptTradingSessionElementUpdate(
			TradingSessionElementStruct sessionElement) {
		
	    boolean exception = false;
	    if (acceptTradingSessionElement7 != null)
	    {
	    	acceptTradingSessionElement7.beforeMethodCall();
	    }
	    
	    try
	    {
	    	delegate.acceptTradingSessionElementUpdate(sessionElement);
	    }
	    catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptTradingSessionElement7 != null)
            {
            	acceptTradingSessionElement7.incCalls(1);
            	acceptTradingSessionElement7.afterMethodCall();
                if (exception)
                {
                	acceptTradingSessionElement7.incExceptions(1);
                }
            }
        }
		
	}
    
	@Override
	public void acceptTradingSessionElementUpdateV2(
			TradingSessionElementStructV2 sessionElement) {
		
	    boolean exception = false;
	    if (acceptTradingSessionElement8 != null)
	    {
	    	acceptTradingSessionElement8.beforeMethodCall();
	    }
	    
	    try
	    {
	    	delegate.acceptTradingSessionElementUpdateV2(sessionElement);
	    }
	    catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptTradingSessionElement8 != null)
            {
            	acceptTradingSessionElement8.incCalls(1);
            	acceptTradingSessionElement8.afterMethodCall();
                if (exception)
                {
                	acceptTradingSessionElement8.incExceptions(1);
                }
            }
        }
		
	}
    
    
}
