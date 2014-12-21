package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.CacheUpdateConsumer;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.instrumentationService.instrumentors.MethodInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.factories.MethodInstrumentorFactory;
import com.cboe.instrumentationService.factories.InstrumentorAlreadyCreatedException;
import com.cboe.idl.user.UserFirmAffiliationStruct;
import com.cboe.idl.user.UserSummaryStruct;

public class CacheUpdateEventConsumerInterceptor implements CacheUpdateConsumer
{

    MethodInstrumentor acceptUserDeletion6;
    MethodInstrumentor acceptUserFirmAffiliationDelete5;
    MethodInstrumentor acceptUserFirmAffiliationUpdate4;
    MethodInstrumentor acceptUserUpdate3;
    MethodInstrumentor acceptSessionProfileUserUpdate2;
    MethodInstrumentor acceptFirmUpdate1;
    MethodInstrumentor acceptFirmDeletion0;

    private CacheUpdateConsumer delegate;

    private MethodInstrumentorFactory methodInstrumentorFactory;

    /**
     * Create a new instance of ths interceptor for the provided BObject.
     * Create a cache of the instrumentors necessary for instrumentation.
     */
    public CacheUpdateEventConsumerInterceptor(Object bo)
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
            name.append("CacheUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptFirmDeletion0");
            acceptFirmDeletion0 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptFirmDeletion0);
            acceptFirmDeletion0.setPrivate(privateOnly);
            name.setLength(0);
            name.append("CacheUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptFirmUpdate1");
            acceptFirmUpdate1 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptFirmUpdate1);
            acceptFirmUpdate1.setPrivate(privateOnly);
            name.setLength(0);
            name.append("CacheUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptSessionProfileUserUpdate2");
            acceptSessionProfileUserUpdate2 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptSessionProfileUserUpdate2);
            acceptSessionProfileUserUpdate2.setPrivate(privateOnly);
            name.setLength(0);
            name.append("CacheUpdateEventConsumerInterceptor").append(Instrumentor.NAME_DELIMITER).append("acceptUserUpdate3");
            acceptUserUpdate3 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptUserUpdate3);
            acceptUserUpdate3.setPrivate(privateOnly);
            name.setLength(0);
            name.append("CacheUpdateConsumerProxy").append(Instrumentor.NAME_DELIMITER).append("acceptUserFirmAffiliationUpdate4");
            acceptUserFirmAffiliationUpdate4 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptUserFirmAffiliationUpdate4);
            acceptUserFirmAffiliationUpdate4.setPrivate(privateOnly);
            name.setLength(0);
            name.append("CacheUpdateConsumerProxy").append(Instrumentor.NAME_DELIMITER).append("acceptUserFirmAffiliationDelete5");
            acceptUserFirmAffiliationDelete5 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptUserFirmAffiliationDelete5);
            acceptUserFirmAffiliationDelete5.setPrivate(privateOnly);
            name.setLength(0);
            name.append("CacheUpdateConsumerProxy").append(Instrumentor.NAME_DELIMITER).append("acceptUserDeletion6");
            acceptUserDeletion6 = getMethodInstrumentorFactory().create(name.toString(), null);
            getMethodInstrumentorFactory().register(acceptUserDeletion6);
            acceptUserDeletion6.setPrivate(privateOnly);
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
        getMethodInstrumentorFactory().unregister(acceptFirmDeletion0);
        acceptFirmDeletion0 = null;
        getMethodInstrumentorFactory().unregister(acceptFirmUpdate1);
        acceptFirmUpdate1 = null;
        getMethodInstrumentorFactory().unregister(acceptSessionProfileUserUpdate2);
        acceptSessionProfileUserUpdate2 = null;
        getMethodInstrumentorFactory().unregister(acceptUserUpdate3);
        acceptUserUpdate3 = null;
        getMethodInstrumentorFactory().unregister(acceptUserFirmAffiliationUpdate4);
        acceptUserFirmAffiliationUpdate4 = null;
        getMethodInstrumentorFactory().unregister(acceptUserFirmAffiliationDelete5);
        acceptUserFirmAffiliationDelete5 = null;
        getMethodInstrumentorFactory().unregister(acceptUserDeletion6);
        acceptUserDeletion6 = null;
    }

    /**
     */
    public void acceptFirmDeletion(com.cboe.idl.firm.FirmStruct param0)
    {
        boolean exception = false;
        if (acceptFirmDeletion0 != null)
        {
            acceptFirmDeletion0.beforeMethodCall();
        }
        try
        {
            delegate.acceptFirmDeletion(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptFirmDeletion0 != null)
            {
                acceptFirmDeletion0.incCalls(1);
                acceptFirmDeletion0.afterMethodCall();
                if (exception)
                {
                    acceptFirmDeletion0.incExceptions(1);
                }
            }
        }
    }

    private void setDelegate(Object delegate)
    {
        this.delegate = (CacheUpdateConsumer) delegate;
    }

    /**
     */
    public void acceptFirmUpdate(com.cboe.idl.firm.FirmStruct param0)
    {
        boolean exception = false;
        if (acceptFirmUpdate1 != null)
        {
            acceptFirmUpdate1.beforeMethodCall();
        }
        try
        {
            delegate.acceptFirmUpdate(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptFirmUpdate1 != null)
            {
                acceptFirmUpdate1.incCalls(1);
                acceptFirmUpdate1.afterMethodCall();
                if (exception)
                {
                    acceptFirmUpdate1.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptSessionProfileUserUpdate(com.cboe.idl.cmiUser.SessionProfileUserStruct param0, com.cboe.idl.user.SessionProfileUserDefinitionStruct param1, com.cboe.idl.user.UserEnablementStruct param2)
    {
        boolean exception = false;
        if (acceptSessionProfileUserUpdate2 != null)
        {
            acceptSessionProfileUserUpdate2.beforeMethodCall();
        }
        try
        {
            delegate.acceptSessionProfileUserUpdate(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptSessionProfileUserUpdate2 != null)
            {
                acceptSessionProfileUserUpdate2.incCalls(1);
                acceptSessionProfileUserUpdate2.afterMethodCall();
                if (exception)
                {
                    acceptSessionProfileUserUpdate2.incExceptions(1);
                }
            }
        }
    }

    public void acceptUserFirmAffiliationUpdate(UserFirmAffiliationStruct userFirmAffiliationStruct)
    {
        boolean exception = false;
        if (acceptUserFirmAffiliationUpdate4 != null)
        {
            acceptUserFirmAffiliationUpdate4.beforeMethodCall();
        }
        try
        {
            delegate.acceptUserFirmAffiliationUpdate(userFirmAffiliationStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptUserFirmAffiliationUpdate4 != null)
            {
                acceptUserFirmAffiliationUpdate4.incCalls(1);
                acceptUserFirmAffiliationUpdate4.afterMethodCall();
                if (exception)
                {
                    acceptUserFirmAffiliationUpdate4.incExceptions(1);
                }
            }
        }
    }

    public void acceptUserFirmAffiliationDelete(UserFirmAffiliationStruct userFirmAffiliationStruct)
    {
        boolean exception = false;
        if (acceptUserFirmAffiliationDelete5 != null)
        {
            acceptUserFirmAffiliationDelete5.beforeMethodCall();
        }
        try
        {
            delegate.acceptUserFirmAffiliationDelete(userFirmAffiliationStruct);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptUserFirmAffiliationDelete5 != null)
            {
                acceptUserFirmAffiliationDelete5.incCalls(1);
                acceptUserFirmAffiliationDelete5.afterMethodCall();
                if (exception)
                {
                    acceptUserFirmAffiliationDelete5.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptUserUpdate(com.cboe.idl.cmiUser.UserStruct param0, com.cboe.idl.user.UserDefinitionStruct param1, com.cboe.idl.user.UserEnablementStruct param2)
    {
        boolean exception = false;
        if (acceptUserUpdate3 != null)
        {
            acceptUserUpdate3.beforeMethodCall();
        }
        try
        {
            delegate.acceptUserUpdate(param0, param1, param2);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptUserUpdate3 != null)
            {
                acceptUserUpdate3.incCalls(1);
                acceptUserUpdate3.afterMethodCall();
                if (exception)
                {
                    acceptUserUpdate3.incExceptions(1);
                }
            }
        }
    }

    /**
     */
    public void acceptUserDeletion(UserSummaryStruct param0)
    {
        boolean exception = false;
        if (acceptUserDeletion6 != null)
        {
            acceptUserDeletion6.beforeMethodCall();
        }
        try
        {
            delegate.acceptUserDeletion(param0);
        } catch (RuntimeException ex)
        {
            exception = true;
            throw ex;
        } finally
        {
            if (acceptUserDeletion6 != null)
            {
                acceptUserDeletion6.incCalls(1);
                acceptUserDeletion6.afterMethodCall();
                if (exception)
                {
                    acceptUserDeletion6.incExceptions(1);
                }
            }
        }
    }
}
