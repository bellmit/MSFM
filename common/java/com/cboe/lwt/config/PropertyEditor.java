/*
 * Created on Jan 26, 2004
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package com.cboe.lwt.config;

import java.io.File;
import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.cboe.lwt.string.BufferFile;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class PropertyEditor
{
    private String expMatchPrefix = "(";
    private String expMatchSuffix = ")=(\\S*)\\s*";
    private String expReplacePrefix = "$1=";
    private String expReplaceSuffix = "\n";
    
    StringBuffer properties = new StringBuffer();
    
    public PropertyEditor()
    {
    }
    
    
    public void setMatchExp( String p_newPrefix, String p_newSuffix )
    {
        expMatchPrefix = p_newPrefix;
        expMatchSuffix = p_newSuffix;
    }
    
    
    public void setReplaceExp( String p_newPrefix, String p_newSuffix )
    {
        expReplacePrefix = p_newPrefix;
        expReplaceSuffix = p_newSuffix;
    }

    
    public PropertyEditor( StringBuffer p_sb )
    {
        load( p_sb );
    }
    
    
    public void load( StringBuffer p_sb )
    {
        properties = p_sb;
    }
    
    
    public void loadFromEditor( PropertyEditor p_editor )
    {
        properties = p_editor.properties;
    }
    
    
    public void loadFromFile( File p_file ) 
        throws IOException
    {
        properties = BufferFile.loadFromFile( p_file );
    }


    public void writeToFile( File p_file ) 
        throws IOException
    {
        BufferFile.writeToFile( p_file, properties );
    }


    public CharSequence getBuffer()
    {
        return properties;
    }
    
    
    public String getProperty( String p_key )
    {
        Matcher matcher = prepareMatcher( p_key );
     
        if ( matcher.find() )
        {
            return matcher.group( 2 );
        };        
        
        return null;
    }


    public String getProperty( String p_key, String p_default )
    {
        String result = getProperty( p_key );
        
        if ( result == null )
        {
            result = p_default;
        }
        
        return result;
    }
    
    
    public int setProperty( String p_key, String p_newValue )
    {
        int numReplaced = 0;

        Matcher matcher = prepareMatcher( p_key );
        
        StringBuffer dest = new StringBuffer();
        String replacement = expReplacePrefix + p_newValue + expReplaceSuffix;
        
        while ( matcher.find() )
        {
            matcher.appendReplacement( dest, replacement );        
            ++numReplaced;
        }
        matcher.appendTail( dest );
        
        if ( numReplaced > 0 )
        {
            properties = dest;
        }
        
        return numReplaced;
    }
    
    
    private Matcher prepareMatcher( String p_key )
    {
        StringBuffer regex = new StringBuffer();
        
        regex.append( expMatchPrefix )
             .append( p_key )
             .append( expMatchSuffix );
                    
        Pattern pattern = Pattern.compile( regex.toString() );
        Matcher matcher = pattern.matcher( properties );
        return matcher;
    }

}
