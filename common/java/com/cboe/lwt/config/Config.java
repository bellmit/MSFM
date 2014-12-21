package com.cboe.lwt.config;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Properties;

import com.cboe.lwt.eventLog.Logger;


public class Config
{
    private static final String DEFAULT_DELIMITERS = ",";
    
    
    ///////////////////////////////////////////////////////////////////////////
    // static
    
    private static boolean logConfigAccess = true;

    
    public static void enableConfigLog()
    {
        logConfigAccess = true;
    }

    
    public static void disableConfigLog()
    {
        logConfigAccess = false;
    }
    
    // static
    ///////////////////////////////////////////////////////////////////////////

    
    private Properties props;
    

    /**
     * Default constructor, initializes the property list from system properties
     * values
     */
    public Config()
    {
        props = System.getProperties();
    }


    /**
     * Reads the property list from the file, then overlays that list from the 
     * system properties.  This causes all entries on the command line (system props)
     * to override the entries in the file.
     * 
     * If it is desired to override the system properties, the loadFromFile method 
     * should be called
     * 
     * If configPathName is not valid or empty, no action is taken after the sytem
     * props are loaded
     * 
     * @param p_configPathName -
     *            the configuration file pathname
     */
    public Config( String p_configPathName )
        throws IOException
    {
        try
        {
            props = new Properties();
            File file = new File( p_configPathName );
            FileInputStream in = new FileInputStream( file );
            
            props.load( in );
            props.putAll( System.getProperties() );
            
            if ( logConfigAccess )
            {
                StringBuffer msg = new StringBuffer( "CONFIG ---- System Properties updated with props in file : " );
                msg.append( p_configPathName )
                   .append( ", which evaluates to : " )
                   .append( file.getAbsolutePath() );
                
                Logger.trace( msg.toString() );
            }
        }
        catch ( IOException ioe )
        {
            Logger.error( "Can't load configuration", ioe );
            throw ioe;
        }
    }


    /**
     * Reads the property list from the file, then overlays that list from the 
     * system properties.  This causes all entries on the command line (system props)
     * to override the entries in the file.
     * 
     * If it is desired to override the system properties, the loadFromFile method 
     * should be called
     * 
     * If configPathName is not valid or empty, no action is taken after the sytem
     * props are loaded
     * 
     * @param p_configPathName -
     *            the configuration file pathname
     */
    public Config( Properties p_overrideConfig )
    {
        props = new Properties();
        props.putAll( p_overrideConfig );
        props.putAll( System.getProperties() );
    }


    public void writeToFile( String p_configPathName ) 
        throws IOException
    {
        FileOutputStream out = new FileOutputStream( new File( p_configPathName ) );
        props.store( out, "Written by Config.java" );
    }


    public void writeToProperties( Properties p_dest ) 
    {
        p_dest.putAll( props );
    }


    public void addFromProps( Properties p_src ) 
    {
        props.putAll( p_src );
    }


    /**
     * Gets boolean value.
     * 
     * @return boolean
     * @param propertyName
     *            java.lang.String
     * @param defaultValue
     *            boolean
     */
    public boolean getBoolean( String p_propertyName, boolean p_defaultValue )
    {
        String value = internalGetProperty( p_propertyName,
                                            Boolean.toString( p_defaultValue ),
                                            "Boolean" );
        
        return Boolean.valueOf( value ).booleanValue();
    }


    /**
     * Gets a property as an integer.
     * 
     * @param propertyName
     *            name of property
     */
    public int getInt( String p_propertyName ) 
        throws BadKeyException,
               BadValueException
    {
        String value = internalGetProperty( p_propertyName,
                                            null,
                                            "Integer" );
        
        if ( value == null ) 
        {
            throw new BadKeyException( "Cannot find int property: " + p_propertyName );
        }

        try
        {
            return Integer.parseInt( value );
        }
        catch ( NumberFormatException ex )
        {
            throw new BadValueException( "Cannot covert value to int: " + value, ex );
        }
    }


    /**
     * Gets a property as an integer.
     * 
     * @param propertyName
     *            name of property
     * @param defaultValue
     *            value returned if property not defined
     */
    public int getInt( String p_propertyName, 
                       int p_defaultValue )
    {
        int result = p_defaultValue;
        
        String value = internalGetProperty( p_propertyName,
                                            Integer.toString( p_defaultValue ),
                                            "Integer" );
        
        try
        {
            result = Integer.parseInt( value);
        }
        catch ( NumberFormatException e )
        {
            // ignore error and use default value
        }
        return result;
    }


    /**
     * Gets property.
     */
    public String getProperty( String p_propertyName ) 
        throws BadKeyException
    {
        String value = internalGetProperty( p_propertyName, null, "String" );

        if ( value == null ) 
        {
            throw new BadKeyException( "Cannot find property: " + p_propertyName );
        }

        return value;
    }


    /**
     * Gets property with a default value.
     */
    public String getProperty( String p_propertyName, String p_defaultValue )
    {
        return internalGetProperty( p_propertyName, p_defaultValue, "String" );
    }


    /**
     * Gets list of tokens using default values.
     * 
     * @return java.lang.String[]
     * @param propertyName
     *            java.lang.String
     */
    public String[] getPropertyList( String p_propertyName )
    {
        return getPropertyList( p_propertyName, null, DEFAULT_DELIMITERS );
    }


    /**
     * Gets list of tokens using default values.
     * 
     * @return java.lang.String[]
     * @param propertyName
     *            property name
     * @param delimiters
     *            characters used to delimit tokens
     */
    public String[] getPropertyList( String p_propertyName, 
                                     String p_defaultValue )
    {
        return getPropertyList( p_propertyName, p_defaultValue, DEFAULT_DELIMITERS );
    }


    /**
     * Gets list of tokens from property value.
     * 
     * @return java.lang.String[]
     * @param propertyName
     *            java.lang.String
     * @param defaultValue
     *            java.lang.String
     */
    public String[] getPropertyList( String p_propertyName,
                                     String p_defaultValue,
                                     String p_delimiters )
    {
        String list = internalGetProperty( p_propertyName, p_defaultValue, "List" );
        
        String[] results = null;
        if ( list != null )
        {
            results = list.split( p_delimiters);
        }
        return results;
    }


    ////////////////////////////////////////////////////////////////////////////
    // implementation
    


    /**
     * @param p_propertyName
     * @param p_defaultValue
     * @return
     */
    private String internalGetProperty( String p_propertyName,
                                        String p_defaultValue,
                                        String p_type )
    {
        String value = System.getProperty( p_propertyName );
        
        if ( value == null )
        {
            value = props.getProperty( p_propertyName,
                                           p_defaultValue );
        }
        
        if ( logConfigAccess )
        {
            StringBuffer msg = new StringBuffer( "CONFIG ---- " );
            msg.append( p_type )
               .append( " : " )
               .append( p_propertyName )
               .append( " = " )
               .append( value );
            
            Logger.trace( msg.toString() );
        }
        
        return value;
    }

}
