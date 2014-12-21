package com.cboe.testFramework;

import java.lang.reflect.Method;
import junit.framework.*;
/**
 * Hide the details of the JUnit junit.framework. 
 * This level of indirection will allow the development of and integration with new
 * junit.frameworks without changing the system under test.
 */
public class TestRunner
{
    /**
     */
    public static void run(UnitTest unitTestFactory, String [] args)
    {
        try
        {
            junit.framework.TestSuite suite = new junit.framework.TestSuite();
            if(args.length == 0)
            {
                Method [] methods = unitTestFactory.getClass().getMethods();
                for(int i = 0; i < methods.length; i++)
                {
                    Method meth = methods[i];
                    if(meth.getName().startsWith("test"))
                    {
                        TestImpl testImpl =  new TestImpl(meth.getName());
                        UnitTest test = unitTestFactory.createTest(meth.getName(), testImpl);
                        testImpl.setUnitTest(test);
                        throw new RuntimeException("TestRunner class is not longer runnable: this class is a member of a framework that will be deleted.");
                        //suite.addTest(testImpl);
                    }
                }
            }
            else
            {
                for(int i = 0; i < args.length; i++)
                {
                    TestImpl testImpl =  new TestImpl(args[i]);
                    UnitTest test = unitTestFactory.createTest(args[i], testImpl);
                    testImpl.setUnitTest(test);
                    throw new RuntimeException("TestRunner class is not longer runnable: this class is a member of a framework that will be deleted.");
                    //suite.addTest(testImpl);
                }
            }
            junit.textui.TestRunner.run(suite);
        }
        catch(Throwable t)
        {
            t.printStackTrace();
            System.exit(1);
        }
        System.exit(0);
    }
}
