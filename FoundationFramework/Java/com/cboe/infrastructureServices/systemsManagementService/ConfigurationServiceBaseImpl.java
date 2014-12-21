package com.cboe.infrastructureServices.systemsManagementService;

import com.cboe.infrastructureServices.foundationFramework.FrameworkComponent;
import java.util.Properties;

/**
 * The base class for ConfigurationService implementations.
 */
public abstract class ConfigurationServiceBaseImpl  implements ConfigurationService
{
	private String name;
	private static ConfigurationService instance;
	private static String serviceImplClassName;

    protected String configValPrefix = System.getProperty("configValPrefix", "config_");

    /**
     *  All tag-substitutions must start with this prefix, otherwise substituteSpecialTags() will throw an IllegalArgumentException on 'bad tags'.
     *  Initially specified via "-DconfigValPrefix", and defaults to "config_".  The 'old' prefix, "prefix", will be supported regardless.
     */
    public void setConfigValPrefix(String val)
    {
        this.configValPrefix = val;
    }
	
    /**
     *  All tag-substitutions must start with this prefix, otherwise substituteSpecialTags() will throw an IllegalArgumentException on 'bad tags'.
     *  Initially specified via "-DconfigValPrefix", and defaults to "config_".  The 'old' prefix, "prefix", will be supported regardless.
     */
    public String getConfigValPrefix()
    {
        return this.configValPrefix;
    }
	
	/**
	 * This method will load the specified class and initialize it.
	 *
	 * @return 	an instantiation of the implementation of the Configuration service
	 *
	 */
	public static ConfigurationService getInstance( )
	{
		if ( null == instance ) {
			try {
				Class configurationServiceClass = Class.forName( serviceImplClassName );
				instance = ( ConfigurationService ) configurationServiceClass.newInstance();
			}
			catch ( ClassNotFoundException cnfe ) {
				System.err.println( "Unable to find ConfigurationService class " + serviceImplClassName );
			}
			catch ( InstantiationException ie ) {
				System.err.println( "Unable to create ConfigurationService " + serviceImplClassName + ": " + ie );
			}
			catch ( IllegalAccessException iae ) {
				System.err.println( "No permission to create ConfigurationService " + serviceImplClassName + ": " + iae );
			}
		}
		return instance;
	}
	/**
	 * This method will load the specified class and initialize it.
	 *
	 * @param parameters	String[]	This is configuration parameters, probably command line arguments
	 * @param firstConfigParamater	int	This is the offset into parameters to the first argument pertinent to Configuration Service
	 * @return 	true means initialization went OK; false = initialization failure
	 *
	 * NOTE: parameters[ firstConfigParameter ] should be the name of the class which actually provides the configuration service.
	 * This class will be loaded and instantiated and its initialize method will be called with (firstConfigParameter + 1 );
	 */
	public static boolean getInstance(String [] parameters, int firstConfigParameter ) {
		String className = parameters[ firstConfigParameter ];
		boolean returnValue = false; // Prepare for the worst
		try {
			Class configurationServiceClass = Class.forName( className );
			instance = ( ConfigurationService ) configurationServiceClass.newInstance();
			returnValue = instance.initialize( parameters, firstConfigParameter + 1 );
		}
		catch ( ClassNotFoundException cnfe ) {
			System.err.println( "Unable to find ConfigurationService class " + className );
		}
		catch ( InstantiationException ie ) {
			System.err.println( "Unable to create ConfigurationService " + className + ": " + ie );
		}
		catch ( IllegalAccessException iae ) {
			System.err.println( "No permission to create ConfigurationService " + className + ": " + iae );
		}
		return returnValue;
	}
	/**
	   Return a String of the name of the ProcessDescriptor
	   @roseuid 3658CE6F03D0
	 */
	public String getName() {
		return name;
	}
/**
 * This method was created in VisualAge.
 * @return java.lang.String
 */
public static String getServiceImplClassName( ) {
	return serviceImplClassName;
}
	/**
	 * This method will initialize the particular implimentation of ConfigurationService.
	 *
	 * @param parameters	String[]	This is configuration parameters, probably command line arguments
	 * @param firstConfigParamater	int	This is the offset into parameters to the first argument pertinent to Configuration Service
	 * @return 	true means initialization went OK; false = initialization failure
	 *
	 */
	public abstract boolean initialize(String [] parameters, int firstConfigParameter );
/**
 * This method was created in VisualAge.
 * @param args java.lang.String[]
 */
public static void main(String args[]) {
	ConfigurationServiceBaseImpl.setServiceImplClassName( args[ 1 ] );
	ConfigurationService configurationService = ConfigurationServiceBaseImpl.getInstance();
	configurationService.initialize( args, 2 );
	System.out.println( configurationService.getProperty( args[ 0 ], "Property " + args[ 0 ] + " not found!" ) );
	String[] list = configurationService.getPropertyList( args[ 0 ], ",", "Property " + args[ 0 ] + " not found!" );
	for ( int i = 0; i < list.length; i++ ) 	{
		System.out.println( "     " + list[ i ] );
	}
	Property[] propertySet = configurationService.getPropertySet( "LoggingService.*.FileAgent1.*" );
	for ( int i = 0; i < propertySet.length; i++ )
	{
		System.out.println( propertySet[ i ].name + " = " + propertySet[ i ].value );
	}
	propertySet = configurationService.getPropertySet( "LoggingService.*.FileAgent1.type" );
	for ( int i = 0; i < propertySet.length; i++ )
	{
		System.out.println( propertySet[ i ].name + " = " + propertySet[ i ].value );
	}
	((ConfigurationServiceFileImpl)configurationService).setProperty( "test.of.saving.property", "red,green,blue" );
}
	/**
	   Set the name of the ProcessDescriptor
	   @roseuid 3658CE6F033A
	 */
	public void setName(String aName) {
	    name = aName;
	}
/**
 * This method was created in VisualAge.
 * @param className java.lang.String
 */
public static void setServiceImplClassName( String className ) {
	serviceImplClassName = className;
}
	/**
	 */
	public String getFullName(FrameworkComponent comp)
	{
		if(comp == null) return getName();
		String [] values = new String [ 10 ];
		int idx = 0;
		while(comp.getParentComponent() != null)
		{
			values[idx++ ] = comp.getSmaName();
			comp = comp.getParentComponent();
		}
		values[idx] = comp.getSmaName();

		StringBuffer result = new StringBuffer();
		result.append(values[idx]);
		for(int i = idx - 1; i > -1 ; i--)
		{
			result.append('.');
			result.append(values[i]);
		} 
		return result.toString(); 
	}
/**
 *  Subclasses can use this method to perform tag-substitution in strings.
 */

    /**
     *  Substitute tags using System properties for the tag values.
     *  @see specialTagSubstitution(String, Properties)
     */
    protected String specialTagSubstitution(String str)
    {
        return specialTagSubstitution(str, System.getProperties());
    }

    /**
     * Perform tag substitution for XML-like tag substrings.
     * All tag-like structures (matching the pattern "&lt;[a-zA-Z0-9]_-$]+&gt;")
     * that are missing an 'end tag' will be tolerated.  For tags found, their chars
     * will be replaced with their corresponding property from tagValues.
     * Whitespace within a tag is not tolerated.
     * All substrings that do not look like tags are copied byte-for-byte.
     * If the tag is not found in tagValues, it is substituted with a blank string and a warning is printed to System.err.
     *
     * <p><b> NOTE:
     *   All tags must begin with the value returned by <code>getConfigValPrefix()</code> or with "prefix"
     *   (for reverse compatibility), otherwise an IllegalArgumentException will be thrown!
     *   (The default prefix is "config_".)
     * </b></p>
     *
     *  <pre>
     *     For example,  the string "abc<def>ghi" will be replaced with "abcDEFghi" where the cmd line included "-Ddef=DEF".
     *     For example,  the string "abc<def no end tag" will be unaffected (since there's no end tag).
     *     For example,  the string "abc<prefix no end tag" will cause an IllegalArgumentException (for backward compat.).
     *     For example,  the string "abc<prefixABC>" will become "abcwow" where the cmd line included "-DprefixABC=wow".
     *     For example,  the string "abc<xxx>def<yyy>" will become "abcdef" where the cmd line has neither "-Dxxx=..." nor "-Dyyy=...".
     *  </pre>
     *
     */
    protected String specialTagSubstitution(String str, Properties tagValues) throws IllegalArgumentException
    {
        final int len = str.length();
        StringBuffer strBuf = new StringBuffer(len);
        StringBuffer tagBuf = null;
        boolean inTag = false;
        char c;
        for (int i=0; i < len; i++)
        {
            c = str.charAt(i);
            if (c=='<' && !inTag) // start a tag
            {
                inTag = true;
                tagBuf = new StringBuffer(32);
                continue;
            }
            if (c=='>' && inTag) // end a tag
            {
                String tagStr = tagBuf.toString();
                if (tagStr.length() == 0)
                {
                    strBuf.append("<>"); // it's not a tag, it's actually "<>": just append the bytes.
                }
                else if (!tagStr.startsWith(getConfigValPrefix()) && !tagStr.startsWith("prefix"))
                {
                    throw new IllegalArgumentException("Tags must start with '" + getConfigValPrefix() + "' or 'prefix'!  The offending tag is <"+tagStr+"> in '" + str + "'.");
                }
                else
                {
                    // process tag
                    String subWith = tagValues.getProperty(tagStr);
                    if (subWith == null)
                    {
                        // (Use System.err since the logging service may not be initialized yet.)
                        System.err.println(new java.util.Date() + " ConfigurationService Warning: for string '" + str + "', tag <" + tagStr
                            + "> is not found in the properties.  Empty string will be used.");
                    }
                    else
                    {
                        strBuf.append(subWith);
                    }
                }

                inTag = false;
                tagBuf = null;
                continue;
            }
            if (inTag)
            {
                if ((Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c)) || c == '-')  // matches [0-9a-zA-Z_\$\.\-] 
                {
                    // tagBuf will contain the tag chars.
                    //
                    tagBuf.append(c);
                }
                else
                {
                    // Well, it looked like a tag, but turns out not to be (found invalid char before '>').
                    // Append the '<' and any tag chars.
                    //
                    strBuf.append('<').append(tagBuf); 
                    inTag = false;
                    tagBuf = null;
                }
            }
            else
            {
                strBuf.append(c); // not "inTag", so just append the current char.
            }
        }

        if (tagBuf != null)
        {
            strBuf.append('<').append(tagBuf); // don't forget any leftovers
        }
        return strBuf.toString();
    }
}
