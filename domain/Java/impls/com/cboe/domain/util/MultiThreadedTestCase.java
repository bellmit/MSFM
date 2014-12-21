package com.cboe.domain.util;

import junit.framework.TestCase;
import junit.framework.TestResult;
import junit.framework.AssertionFailedError;

public class MultiThreadedTestCase extends TestCase
{
    private Thread threads[] = null;
    private TestResult testResult = null;

    public MultiThreadedTestCase()
    {
        super(null);
    }

    public MultiThreadedTestCase(final String s)
    {
        super(s);
    }

    public void run(final TestResult result)
    {
        testResult = result;
        super.run(result);
        testResult = null;
    }

    protected void runTestCaseRunnables(final TestCaseRunnable[] runnables)
    {
        if (runnables == null)
        {
            throw new IllegalArgumentException("runnables is null");
        }
        threads = new Thread[runnables.length];
        for (int i = 0; i < threads.length; i++)
        {
            threads[i] = new Thread(runnables[i]);
        }
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].start();
        }
        try
        {
            for (int i = 0; i < threads.length; i++)
            {
                threads[i].join();
            }
        } catch (InterruptedException ignore)
        {
            System.out.println("Thread join interrupted.");
        }
        threads = null;
    }

    private void interruptThreads()
    {
        for (int i = 0; i < threads.length; i++)
        {
            threads[i].interrupt();
        }
    }

    private void handleException(final Throwable t)
    {
        synchronized (testResult)
        {
            if (t instanceof AssertionFailedError)
            {
                testResult.addFailure(this, (AssertionFailedError) t);
            } else
            {
                testResult.addError(this, t);
            }
        }
    }

    protected abstract class TestCaseRunnable implements Runnable
    {
        public abstract void runTestCase()
                throws Throwable;

        public void run()
        {
            try
            {
                runTestCase();
            } catch (Throwable t)
            {
                handleException(t);
                interruptThreads();
            }
        }
    }
}
