//
// -----------------------------------------------------------------------------------
// Source file: APIWorkerImpl.java
//
// PACKAGE: com.cboe.presentation.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.threading;

import java.lang.reflect.InvocationTargetException;

import com.cboe.interfaces.presentation.threading.GUIWorker;

import com.cboe.presentation.threading.SwingEventThreadWorker;
import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.common.threading.ActionThreaderFactory;

public class APIWorkerImpl extends AbstractAPIWorker
{
    private APIWorkerImpl(GUIWorker guiWorker)
    {
        super(guiWorker);
        Object ref = guiWorker.getSharedLockReference();
        if(ref == null)
        {
            ActionThreaderFactory.getNextAvailableActionThreader().launchAction(this);
        }
        else
        {
            ActionThreaderFactory.getNextAvailableActionThreader(ref).launchAction(this);
        }
    }

    public static void run(GUIWorker guiWorker)
    {
        new APIWorkerImpl(guiWorker);
    }

    public void process()
    {
        if(guiWorker.isInitializeViewRequired())
        {
            SwingEventThreadWorker worker = new SwingEventThreadWorker(true)
            {
                public Object process()
                {
                    getWorker().initializeView();
                    return null;
                }
            };

            try
            {
                worker.doProcess();
            }
            catch(InvocationTargetException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
            catch(InterruptedException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }

        try
        {
            getWorker().execute();

            if (guiWorker.isProcessDataRequired())
            {
                SwingEventThreadWorker worker = new SwingEventThreadWorker(true)
                {
                    public Object process()
                    {
                        getWorker().processData();
                        return null;
                    }
                };

                try
                {
                    worker.doProcess();
                }
                catch(InvocationTargetException e)
                {
                    DefaultExceptionHandlerHome.find().process(e);
                }
                catch(InterruptedException e)
                {
                    DefaultExceptionHandlerHome.find().process(e);
                }
            }
        }
        catch(Exception exception)
        {
            final Exception eLocal = exception;

            SwingEventThreadWorker worker = new SwingEventThreadWorker(true)
            {
                public Object process()
                {
                    getWorker().handleException(eLocal);
                    return null;
                }
            };

            try
            {
                worker.doProcess();
            }
            catch(java.lang.reflect.InvocationTargetException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
            catch(InterruptedException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }

        if(guiWorker.isCleanUpViewRequired())
        {
            SwingEventThreadWorker worker = new SwingEventThreadWorker(true)
            {
                public Object process()
                {
                    getWorker().cleanUpView();
                    return null;
                }
            };

            try
            {
                worker.doProcess();
            }
            catch(java.lang.reflect.InvocationTargetException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
            catch(InterruptedException e)
            {
                DefaultExceptionHandlerHome.find().process(e);
            }
        }
    }
}