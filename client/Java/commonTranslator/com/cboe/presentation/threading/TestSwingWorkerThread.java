package com.cboe.presentation.threading;

/**
 * 
 * @version 
 * @see 
 * @author
 */
public class TestSwingWorkerThread extends SwingEventThreadWorker {
/**
 * TestSwingWorkerThread constructor comment.
 */
public TestSwingWorkerThread() {
    super(true);
}
/**
 * process method comment.
 */
public Object process() throws Exception
{
    return "hello";
}
}
