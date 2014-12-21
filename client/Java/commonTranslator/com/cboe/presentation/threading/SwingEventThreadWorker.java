//
// -----------------------------------------------------------------------------------
// Source file: SwingEventThreadWorker.java
//
// PACKAGE: com.cboe.presentation.threading;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2004 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.threading;

import java.lang.reflect.InvocationTargetException;
import javax.swing.SwingUtilities;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;

/**
 * Performs GUI work on Swing Event Dispatch Thread
 */
public abstract class SwingEventThreadWorker implements Runnable
{
    private final boolean waitForCompletion;

    /**
     * These variables need to be set to volatile because they are accessed with multiple
     * threads and are not sychronized. This means each thread can have an inconsistent cached
     * copy. Subsequently we can get null pointer exceptions on results.
     */
    private volatile Object result = null;
    private volatile Throwable exception = null;

    /**
     * Initializes this processes work to be done without waiting for completion. The return
     * of doProcess() will happen immediately regardless of whether the process() method had returned.
     */
    public SwingEventThreadWorker()
    {
        this(true);
    }

    /**
     * Initializes this processes work to be done. The return of doProcess() will happen
     * depending on the waitForCompletion.
     * @param waitForCompletion True if you would like doProcess() to only return
     * upon completion of process(). False if you would like the return of doProcess()
     * to happen immediately regardless of whether the process() method had returned. If
     * you are expecting a return value or an exception you should use true here, as the return value
     * state is unpredictable and an exception will not be thrown if you use false.
     */
    public SwingEventThreadWorker(boolean waitForCompletion)
    {
        super();

        this.waitForCompletion = waitForCompletion;
    }

    /**
     * Performs the requested overriden process() on the Swing event dispatch thread.
     * @return Object return value of your process() method.
     * @exception InvocationTargetException Contains any exception thrown by your process.
     * @exception java.lang.InterruptedException The exception description.
     */
    public Object doProcess() throws InvocationTargetException, InterruptedException
    {
        exception = null;

        if(!SwingUtilities.isEventDispatchThread())
        {
            if(waitForCompletion)
            {
                javax.swing.SwingUtilities.invokeAndWait(this);
            }
            else
            {
                javax.swing.SwingUtilities.invokeLater(this);
            }
        }
        else
        {
            run();
        }

        if(exception != null)
        {
            throw new InvocationTargetException(exception);
        }
        else
        {
            return result;
        }
    }

    /**
     * Your process to be implemented by client code.
     * @return optionally an Object
     */
    public abstract Object process() throws Exception;

    /**
     * Implements processing for Swing event dispatch thread.
     * SHOULD NOT BE CALLED DIRECTLY. FOR SWINGUTILITIES PURPOSE ONLY.
     */
    public void run()
    {
        try
        {
            result = process();
        }
        catch(Throwable e)
        {
            if(waitForCompletion)
            {
                exception = e;
            }
            else
            {
                DefaultExceptionHandlerHome.find()
                        .process(e, "Exception occurred in SwingEventThreadWorker, " +
                                    "that was not returned for handling.");
            }
        }
    }
}