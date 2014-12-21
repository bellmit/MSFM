package com.cboe.testFramework;
/**
 * The interface that is to provide support to the unit tests.
 * 
 */
public interface TestContext
{
    public Thread getNewThread(Runnable r);
    public String getName();
	/**
     * Asserts that a condition is true. If it isn't it throws
     * an AssertionFailedError.
     */
	public void assertTrue(boolean condition);
    /**
     * Asserts that a condition is true. If it isn't it throws
     * an AssertionFailedError with the given message.
     */
	public void assertTrue(String message, boolean condition);
    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown.
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     */
	public void assertEquals(Object expected, Object actual);
    /**
     * Asserts that two longs are equal.
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     */
	public void assertEquals(long expected, long actual);
    /**
     * Asserts that two doubles are equal.
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     * @param delta tolerated delta
     */
	public void assertEquals(double expected, double actual, double delta);
    /**
     * Asserts that two objects are equal. If they are not
     * an AssertionFailedError is thrown.
     * @param message the detail message for this assertion
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     */
    public void assertEquals(String message, Object expected, Object actual);
    /**
     * Asserts that two longs are equal.
     * @param message the detail message for this assertion
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     */
    public void assertEquals(String message, long expected, long actual);
    /**
     * Asserts that two doubles are equal.
     * @param message the detail message for this assertion
     * @param expected the expected value of an object
     * @param actual the actual value of an object
     * @param delta tolerated delta
     */
	public void assertEquals(String message, double expected, double actual, double delta);
}
