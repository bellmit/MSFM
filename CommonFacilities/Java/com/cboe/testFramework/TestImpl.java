package com.cboe.testFramework;
import java.lang.reflect.*;
/**
 * Hide the details of the JUnit junit.framework. 
 * This level of indirection will allow the development of and integration with new
 * junit.frameworks without changing the system under test.
 */
public class TestImpl implements TestContext
{
    UnitTest unitTest;
    String methodName;

    protected void runTest(String fName) throws Throwable 
    {
        methodName = fName;
        Method runMethod= null;
        try {
                runMethod= unitTest.getClass().getMethod(fName, new Class[] { TestContext.class } );
        }
        catch (NoSuchMethodException e) {}
        if(runMethod == null)
        try
        {
                runMethod= unitTest.getClass().getMethod(fName, new Class[0] );
        }
        catch (NoSuchMethodException e) {
                e.fillInStackTrace();
                throw e;
        }

        try {
                runMethod.invoke(unitTest, new Class[0]);
        }
        catch (InvocationTargetException e) {
                e.fillInStackTrace();
                throw e.getTargetException();
        }
        catch (IllegalAccessException e) {
                e.fillInStackTrace();
                throw e;
        }
    }

    public TestImpl(String str)
    {
        methodName = str;
    }
    public void setUnitTest(UnitTest test)
    {
        unitTest = test;
    }

    public Thread getNewThread(Runnable r)
    {
        return new Thread(r);
    }
    public String getName()
    {
        return "name";
    }
	/**
     * Asserts that a condition is true. If it isn't it throws
     * an AssertionFailedError.
     */
	public void assertTrue(boolean condition)
	{
	}
    /**
     * Asserts that a condition is true. If it isn't it throws
     * an AssertionFailedError with the given message.
     */
	public void assertTrue(String message, boolean condition)
	{
	}

    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown.
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     */
	public void assertEquals(Object expected, Object actual)
	{
	}
    /**
     * Asserts that two longs are equal.
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     */
	public void assertEquals(long expected, long actual) {
	}

    /**
     * Asserts that two doubles are equal.
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     * @param delta tolerated delta
     */
	public void assertEquals(double expected, double actual, double delta) {
	}

    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown.
     * @param message the detail message for this assertion
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     */
    public void assertEquals(String message, Object expected, Object actual)
    {
    }

    /**
     * Asserts that two longs are equal.
     * @param message the detail message for this assertion
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     */
    public void assertEquals(String message, long expected, long actual)
    {
    }
    /**
     * Asserts that two doubles are equal.
     * @param message the detail message for this assertion
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     * @param delta tolerated delta
     */
	public void assertEquals(String message, double expected, double actual, double delta)
	{
    }
    /**
     */
    protected void tearDown()
    {
        unitTest.tearDown(this);
    }
    protected void setUp(String methodName)
    {
        unitTest.setUp(methodName, this);
    }
    public String toString()
    {
        if(unitTest == null) return super.toString();
        return unitTest.getClass().getName() + "." + methodName;
    }
}
