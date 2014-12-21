package com.cboe.presentation.threading;

/**
 * 
 * @version 
 * @see 
 * @author
 */
public class TestSwingWorker {
/**
 * TestSwingWorker constructor comment.
 */
public TestSwingWorker() {
    super();
}
/**
 * 
 * @param 
 * @return 
 * @exception
 * @param args java.lang.String[]
 */
public static void main(String args[])
{
    String result = null;

    SwingEventThreadWorker worker = new SwingEventThreadWorker(true)
    {
        public Object process()
        {
            throw new IllegalArgumentException("illegal argument");
            //return "hello";
        }
    };

    try
    {
        result = (String)worker.doProcess();
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
        System.out.println("Invocation: " + e);
    }
    catch(InterruptedException e)
    {
        System.out.println("Interrupted: " + e);
    }

    System.out.println(result);

    TestSwingWorkerThread worker2 = new TestSwingWorkerThread();

    try
    {
        result = (String)worker2.doProcess();
    }
    catch(java.lang.reflect.InvocationTargetException e)
    {
        System.out.println("Invocation: " + e);
    }
    catch(InterruptedException e)
    {
        System.out.println("Interrupted: " + e);
    }

    System.out.println(result);
    
    TestSwingWorkerThread2 worker3 = new TestSwingWorkerThread2();

    try
    {
        result = (String)worker3.doProcess();
    }
    catch(IllegalArgumentException e)
    {
        System.out.println("Illegal: " + e);
    }
    catch(InterruptedException e)
    {
        System.out.println("Interrupted: " + e);
    }

    System.out.println(result);
    
    System.exit(0);
}
}
