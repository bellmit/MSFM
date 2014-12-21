package com.cboe.presentation.threading;

import java.lang.reflect.InvocationTargetException;

/**
 * 
 * @version 
 * @see 
 * @author
 */
public class TestSwingWorkerThread2 extends SwingEventThreadWorker {
/**
 * TestSwingWorkerThread constructor comment.
 */
public TestSwingWorkerThread2() {
    super(true);
}
/**
 * 
 * @param 
 * @return 
 * @exception
 * @return java.lang.Object
 * @exception java.lang.IllegalArgumentException The exception description.
 */
public Object doProcess() throws IllegalArgumentException, InterruptedException
{
    
    try
    {
        return super.doProcess();
    }
    catch(InvocationTargetException e)
    {
        throw (IllegalArgumentException) e.getTargetException();
    }
}
/**
 * process method comment.
 */
public Object process() throws Exception
{
    throw new IllegalArgumentException("illegal argument");
    //return "hello";
}
}
