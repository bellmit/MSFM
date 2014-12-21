package com.cboe.presentation.user;


import com.cboe.interfaces.presentation.user.ExchangeModel;
import com.cboe.presentation.common.businessModels.AbstractMutableBusinessModel;
import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;
//import junit.framework.Test;
//import junit.framework.TestCase;
//import junit.framework.TestSuite;
import org.omg.CORBA.UserException;


public class ExchangeModelImpl extends AbstractMutableBusinessModel implements ExchangeModel
{

    private String exchange = "";
    private String fullname = "";

    protected ExchangeModelImpl()
    {
    }


    protected ExchangeModelImpl(String anExchange, String fullName)
    {
        super();
        setExchange(anExchange);
        setFullName(fullName);

    }

    public String toString()
    {
        return this.exchange;
    }

    public boolean equals(Object o)
    {
        boolean result = false;
        if(this == o)
        {
            result = true;
        }
        else if(o == null)
        {
            result = false;
        }
        else if(o instanceof ExchangeModel)
        {
            ExchangeModel anExchangeModel = (ExchangeModel)o;
            if(this.getExchange().equals(anExchangeModel.getExchange()))
            {
                result = true;

            }

        }
        return result;
    }

    /**
     * Implements Cloneable
     */
    public Object clone() throws CloneNotSupportedException
    {
        ExchangeModelImpl aNewExchangeModel = new ExchangeModelImpl(this.getExchange(), this.getFullName());

        return aNewExchangeModel;

    }

    ///**
    // *  Saves any mods made to the business model
    // */
    //public void saveChanges() throws UserException
    //{
    //    // need to update server here
    //}

    public int hashCode()
    {
        return super.hashCode();
    }

    ///**
    // *  Reloads data from the server
    // */
    //public void refreshData() throws UserException
    //{
    //    // need to get data from server here
    //}

    public String getFullName()
    {
        return fullname;
    }

    public void setFullName(String name)
    {
        this.fullname = name;
    }

    public String getExchange()
    {
        return exchange;
    }

    public void setExchange(String anExchange)
    {
        exchange = anExchange;

    }
    // from original com.cboe.interfaces.internalPresentation.product.Exchange
    public int getExchangeKey()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ExchangeModelImpl has no underlying exchange struct. Returning 0 for exchange key",
                                       GUILoggerBusinessProperty.COMMON);
        }
        return 0;    // @todo: is there an equivalent function?
    }

    public String getAcronym()
    {
        return getExchange();
    }

    public String getName()
    {
        return getFullName();
    }

    public int getMembershipKey()
    {
        if (GUILoggerHome.find().isDebugOn())
        {
            GUILoggerHome.find().debug("ExchangeModelImpl has no underlying struct. Returning 0 for membership key",
                                       GUILoggerBusinessProperty.COMMON);
        }
        return 0;  // @todo: is there an equivalent function?  Also test above 3 methods...
    }

    /**
     *  Remember to add the import import junit.framework.*;
     *
     *  To run - java junit.swingui.TestRunner and
     *   pass [package.SomeClass]$TestImpl as a parameter
     *
     *  You will have access to all privates in this class
     *
     *@author     Nick DePasquale
     *@created    October 25, 2000
     */
//    public static class TestImpl extends TestCase
//    {
//        private ExchangeModelImpl defaultExchangeModelImpl;
//        private String defaultExchange = "CBOE";
//
//        private ExchangeModelImpl otherExchangeModelImpl;
//        private String otherExchange = "CBOT";
//
//        /**
//         *  Constructor for the TestImpl object
//         *
//         *@param  name  Name of the test
//         */
//        public TestImpl(String name)
//        {
//            super(name);
//        }
//
//        /**
//         *  A sample unit test for JUnit
//         */
//        public void testString()
//        {
////               String s = new String ( "ABC" );
////               String t = new String ( "ABC" );
////
////               assertTrue( "Test '=='" , s == s );
////               assertEquals ( "Test equals()" , s , s );
////               assertTrue( "Test equals()" , s.equals ( t ) );
//
//
//            defaultExchangeModelImpl = new ExchangeModelImpl(defaultExchange, defaultExchange);
//
//            assertTrue("Testing Exchange wrapper's exchange", defaultExchangeModelImpl.exchange.equals(defaultExchange));
//
//            try
//            {
//                assertTrue("Testing Exchange wrapper's equals and clone method", defaultExchangeModelImpl.equals(defaultExchangeModelImpl.clone()));
//            }
//            catch(CloneNotSupportedException e)
//            {
//                fail("Clone not supported: " + e.toString());
//            }
//
//            otherExchangeModelImpl = new ExchangeModelImpl("CBOT","CBOT");
//            assertTrue("Testing Exchange wrapper's not equals", !defaultExchangeModelImpl.equals(otherExchangeModelImpl));
//
//
//        }
//
//        /**
//         *  The JUnit setup method
//         */
//        protected void setUp()
//        {
//
//        }
//
//        /**
//         *  A unit test suite for JUnit
//         *
//         * This constructor creates a suite with all the methods starting with "test" that take no arguments.
//         *
//         *@return    The test suite
//         */
//        public static Test suite()
//        {
//            //This constructor creates a suite with all the methods starting with "test" that take no arguments.
//            return new TestSuite(TestImpl.class);
//        }
//
//    }

}
