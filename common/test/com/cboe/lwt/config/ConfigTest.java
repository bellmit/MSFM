/*
 * ByteVectorTest.java JUnit based test
 * 
 * Created on August 29, 2002, 1:58 PM
 */

package com.cboe.lwt.config;

import java.util.Properties;

import org.junit.Assert;


/**
 * @author dotyl
 */

public class ConfigTest
{


	@org.junit.Test
    public void testSystemOverlay()
    {
        System.out.println( "testSystemOverlay" );

        Properties sys = System.getProperties();
        
        sys.setProperty( "TEST1", "one" );
        sys.setProperty( "TEST2", "two" );
        sys.setProperty( "TEST3", "three" );
        
        
        Properties p1 = new Properties();
        
        p1.setProperty( "TEST3", "3" );
        p1.setProperty( "TEST4", "4" );
        
        {
            Config cfg = new Config();
            
            Assert.assertEquals( "one",   cfg.getProperty( "TEST1", null ) );
            Assert.assertEquals( "two",   cfg.getProperty( "TEST2", null ) );
            Assert.assertEquals( "three", cfg.getProperty( "TEST3", null ) );
            Assert.assertEquals( null,    cfg.getProperty( "TEST4", null ) );
        }

        {
            Config cfg = new Config( p1 );
            
            Assert.assertEquals( "one",   cfg.getProperty( "TEST1", null ) );
            Assert.assertEquals( "two",   cfg.getProperty( "TEST2", null ) );
            Assert.assertEquals( "three", cfg.getProperty( "TEST3", null ) );
            Assert.assertEquals( null,    System.getProperty( "TEST4", null ) );
        }

        {
            Config cfg = new Config( p1 );
            cfg.writeToProperties( System.getProperties() );
            
            Assert.assertEquals( "one",   cfg.getProperty( "TEST1", null ) );
            Assert.assertEquals( "two",   cfg.getProperty( "TEST2", null ) );
            Assert.assertEquals( "three", cfg.getProperty( "TEST3", null ) );
            Assert.assertEquals( "4",     cfg.getProperty( "TEST4", null ) );
            Assert.assertEquals( "4",     System.getProperty( "TEST4", null ) );
        }
        
    }
   

}