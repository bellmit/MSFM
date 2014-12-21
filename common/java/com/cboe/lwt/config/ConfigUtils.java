/*
 * Created on Sep 9, 2004
 *
 */
package com.cboe.lwt.config;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.util.Vector;

/**
 * @author dotyl
 *
 */
public abstract class ConfigUtils
{
    public static int[] getNumberListFromFile( String p_path )
        throws FileNotFoundException,
               IOException
    {
        File usersFile = new File( p_path );
        FileInputStream inStream = new FileInputStream( usersFile );
        Reader reader = new BufferedReader( new InputStreamReader( inStream ) );
        LineNumberReader userSrc = new LineNumberReader( reader );

        Vector userIdStrings = new Vector( 128 );
        
        for ( String userId = userSrc.readLine(); userId != null; userId = userSrc.readLine() )
        {
            if ( userId.length() > 0 )
            {
                userIdStrings.add( userId.trim() );
            }
        }

        int numUsers = userIdStrings.size();
        int[] userIds = new int[ numUsers];
        for ( int i = 0; i < numUsers; ++i )
        {
            userIds[ i ] = Integer.parseInt( (String)userIdStrings.get( i ) );
        }
        return userIds;
    }
}
