package com.cboe.testFramework;
/**
 * Provide all of the default implementation code.
 * All a subclass needs to do is extend this and provide a main method.
 */
public class UnitTestBaseImpl implements UnitTest
{
    public  TestContext testContext;
    public void tearDown(TestContext context)
    {
        //do noting
    }
    public UnitTest createTest(String methodName, TestContext context)
    {
        UnitTestBaseImpl test = null;
        try
        {
            Class c = this.getClass();
            test = (UnitTestBaseImpl)c.newInstance();
            test.testContext = context; //in case someone overrides setup
        }
        catch(Throwable t)
        {
            t.printStackTrace();
        }
        return test;
    }
    public void setUp(String methodName, TestContext context)
    {
        testContext = context;
        //do nothing
    }
    /**
     * Read from the datainput stream and print the output with the provided prefix.
     */
    public static Runnable printStream(final java.io.DataInputStream din, final String prefix )
    {
        return new Runnable()
        {
            public void run()
            {
                try
                {
                    for(String line = din.readLine(); line != null; line = din.readLine())
                    {
                        System.out.println(prefix + line);
                    }
                }
                catch(Exception ex)
                {
                    System.out.println(prefix + ex);
                    ex.printStackTrace();
                }
            }
        };
    } 
}
