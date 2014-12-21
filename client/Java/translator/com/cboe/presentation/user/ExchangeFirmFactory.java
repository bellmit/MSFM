package com.cboe.presentation.user;

import com.cboe.idl.cmiUser.ExchangeFirmStruct;

import com.cboe.interfaces.presentation.user.ExchangeFirm;

import com.cboe.presentation.exampleStructs.ExampleExchangeFirm;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

public class ExchangeFirmFactory
{
    static public final ExchangeFirm  createExchangeFirm(ExchangeFirmStruct anExchangeFirmStruct)
    {
        return createExchangeFirm(anExchangeFirmStruct.exchange, anExchangeFirmStruct.firmNumber);
    }
    /**
     *  Creates a default (null) implementation of the ExchangeFirm.
     */
    static public final ExchangeFirm createDefaultExchangeFirm()
    {
        return new ExchangeFirmModelNullImpl();
    }

    /**
     * Creates a default (null) implementation of the ExchangeFirm.
     * @param pRenderString The string to be displayed
     */
    static public final ExchangeFirm createDefaultExchangeFirm(String pRenderString)
    {
        return new ExchangeFirmModelNullImpl(pRenderString);
    }

    static public final ExchangeFirm  createExchangeFirm(String anExchange, String aFirm )
    {
        return new ExchangeFirmModelImpl(anExchange, aFirm);
    }

    static public final ExchangeFirm createExchangeFirm(String anExchangeFirm)
    {
        String anExchange = "";
        String aFirm = "";
        int dot = anExchangeFirm.indexOf(".");
        if (dot > 0)
        {
            anExchange = anExchangeFirm.substring(0, dot);
            aFirm = anExchangeFirm.substring(dot + 1);
        }
        return new ExchangeFirmModelImpl(anExchange, aFirm);
    }

    static public final ExchangeFirm [] createExchangeFirms(ExchangeFirmStruct [] exchangeFirmStructs)
    {
        ExchangeFirm [] exchangeFirmList = null;

        if(exchangeFirmStructs != null)
        {
            int length = exchangeFirmStructs.length;
            exchangeFirmList = new ExchangeFirmModelImpl[length];

            for(int x = 0; x < length; x++)
            {
                exchangeFirmList[x] = createExchangeFirm(exchangeFirmStructs[x]);
            }
        }

        return exchangeFirmList;
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
        public static class TestImpl extends TestCase
        {
            /**
             *  Constructor for the TestImpl object
             *
             *@param  name  Name of the test
             */
            public TestImpl ( String name )
            {
                super ( name );
            }

            /**
             *  A sample unit test for JUnit
             */
            public void testString ()
            {
//              String s = new String ( "ABC" );
//              String t = new String ( "ABC" );
//
//              assertTrue( "Test '=='" , s == s );
//              assertEquals ( "Test equals()" , s , s );
//              assertTrue( "Test equals()" , s.equals ( t ) );

                ExchangeFirmStruct defaultExchangeFirmStruct = ExampleExchangeFirm.getExampleDefaultExchangeFirmStruct();



            }

            /**
             *  The JUnit setup method
             */
            protected void setUp ()
            {

            }

            /**
             *  A unit test suite for JUnit
             *
             * This constructor creates a suite with all the methods starting with "test" that take no arguments.
             *
             *@return    The test suite
             */
            public static Test suite ()
            {
                //This constructor creates a suite with all the methods starting with "test" that take no arguments.
                return new TestSuite ( TestImpl.class );
            }

        }


}
