package com.cboe.testFramework;
/**
 */
public interface UnitTest
{
    public void tearDown(TestContext context);
    public UnitTest createTest(String methodName, TestContext context);
    public void setUp(String methodName, TestContext context);
}
