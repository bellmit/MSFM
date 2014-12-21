package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.UserSessionConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.idl.infrastructureServices.sessionManagementService.UserLoginStruct;

public class UserSessionEventConsumerInterceptor implements UserSessionConsumer
{
    MethodInstrumentor acceptOpenSessions4;

    MethodInstrumentor acceptSessionOpened3;

    MethodInstrumentor acceptSessionClosed2;

    MethodInstrumentor acceptLogout1;

    MethodInstrumentor acceptLogin0;

    private UserSessionConsumer delegate;

    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public UserSessionEventConsumerInterceptor(Object bo)
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
            StringBuilder name = new StringBuilder(60);
            name.append("UserSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptLogin0");
            acceptLogin0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptLogin0);
            acceptLogin0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("UserSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptLogout1");
            acceptLogout1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptLogout1);
            acceptLogout1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("UserSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptSessionClosed2");
            acceptSessionClosed2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptSessionClosed2);
            acceptSessionClosed2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("UserSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptSessionOpened3");
            acceptSessionOpened3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptSessionOpened3);
            acceptSessionOpened3.setPrivate(privateOnly);
            name.setLength(0);
            name.append("UserSessionEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptOpenSessions4");
            acceptOpenSessions4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptOpenSessions4);
            acceptOpenSessions4.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptLogin0);
        acceptLogin0 = null;
        getMethodInstrumentorFactory().unregister(acceptLogout1);
        acceptLogout1 = null;
        getMethodInstrumentorFactory().unregister(acceptSessionClosed2);
        acceptSessionClosed2 = null;
        getMethodInstrumentorFactory().unregister(acceptSessionOpened3);
        acceptSessionOpened3 = null;
        getMethodInstrumentorFactory().unregister(acceptOpenSessions4);
        acceptOpenSessions4 = null;
    }

    /**
     */
    public void acceptLogin(java.lang.String param0, java.lang.String param1, java.lang.String param2, int param3)
    {
        boolean exception = false;
        if (acceptLogin0 != null)
        {
            acceptLogin0.beforeMethodCall();
        }
        try
        {
            delegate.acceptLogin(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptLogin0 != null)
            {
                acceptLogin0.incCalls(1);
                acceptLogin0.afterMethodCall();
                if (exception)
                {
                    acceptLogin0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (UserSessionConsumer) delegate;
    }

    /**
     */
    public void acceptLogout(java.lang.String param0, java.lang.String param1, boolean param2, int param3, java.lang.String param4)
    {
        boolean exception = false;
        if (acceptLogout1 != null)
        {
            acceptLogout1.beforeMethodCall();
        }
        try
        {
            delegate.acceptLogout(param0, param1, param2, param3, param4);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptLogout1 != null)
            {
                acceptLogout1.incCalls(1);
                acceptLogout1.afterMethodCall();
                if (exception)
                {
                    acceptLogout1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptSessionClosed(int param0, java.lang.String param1, boolean param2, java.lang.String param3)
    {
        boolean exception = false;
        if (acceptSessionClosed2 != null)
        {
            acceptSessionClosed2.beforeMethodCall();
        }
        try
        {
            delegate.acceptSessionClosed(param0, param1, param2, param3);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptSessionClosed2 != null)
            {
                acceptSessionClosed2.incCalls(1);
                acceptSessionClosed2.afterMethodCall();
                if (exception)
                {
                    acceptSessionClosed2.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptSessionOpened(int param0,java.lang.String param1)
    {
        boolean exception = false;
        if (acceptSessionOpened3 != null)
        {
            acceptSessionOpened3.beforeMethodCall();
        }
        try
        {
            delegate.acceptSessionOpened(param0,param1);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptSessionOpened3 != null)
            {
                acceptSessionOpened3.incCalls(1);
                acceptSessionOpened3.afterMethodCall();
                if (exception)
                {
                    acceptSessionOpened3.incExceptions(1);
                }
            }
        }
    }
    /**
     */
    public void acceptOpenSessions(UserLoginStruct[] param0)
    {
        boolean exception = false;
        if (acceptOpenSessions4 != null)
        {
            acceptOpenSessions4.beforeMethodCall();
        }
        try
        {
            delegate.acceptOpenSessions(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptOpenSessions4 != null)
            {
                acceptOpenSessions4.incCalls(1);
                acceptOpenSessions4.afterMethodCall();
                if (exception)
                {
                    acceptOpenSessions4.incExceptions(1);
                }
            }
       }
     }
}