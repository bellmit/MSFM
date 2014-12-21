package com.cboe.domain.util;


import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import java.text.ParseException;


/**
 * This is an implementation of OrderIdGenerator and extends
 * OrderIdGenerator persistent implementation.
 * This class changes the way the branch string is initialized and incremented
 */

public class OrderIdGeneratorAutolinkHomeImpl extends OrderIdGeneratorHomeImpl
{


    //The branch must be unique across all TradeServers in
    //the same session
    //We only have 3 characters to work with, and I assume
    //they must be printable characters.
    //So, the branch will be the BC CLUSTER_NUMBER converted
    //to a two character code.  I want to support three digit
    //BCs just in case one is added soon.
    //Further, the branch must be unique each time the server
    //is restarted since we will not be persisting sequence numbers.
    //To do this, the third character will be incremented each time
    //the server is started, that character will always start at '0'.
    //The two character code will be created by translating the 
    //CLUSTER_NUMBER to base 32 where 0=0,1=1, .... 10=a, 31=v
    protected String getStartingBranch()
    { 
        String clusterNumber = System.getProperty( "clusterNumber" );
        String branch = convert( clusterNumber );
        Log.information( this, "Intial branch for autolink is " + branch);

        return branch;

    }

    private static String convert( String clusterNumber )
    {
        int bc = 0;
        bc=Integer.valueOf(clusterNumber);

        //Convert bc # to a 2 digit, base 32 number
        //This is not a general algorithm, it only
        //works for inputs less than 999 and basically
        //only base 32
        final int base=32;
        int digit1 = bc/base;
        int digit2 = bc%base;

        char chars[] = { (char)('0' + digit1),
                         (char)('0' + digit2),
                         '0'};

        if( digit1 > 9 ) chars[0] += ('a'-'9'-1);
        if( digit2 > 9 ) chars[1] += ('a'-'9'-1);

        return new String( chars );
    }

    protected String incrementBranch( String branch )
    {
        char[] chars = branch.toCharArray();

        //increment the last character by 1.
        //it is assumed that this is done for each failover/restart
        //so there should not be too many in a day.
        //The last char should start out as '0' and increment from
        //there.  If you increment a lot (more than 94), then you
        //will get non printable characters for branch, but it should
        //still work.
        chars[ chars.length - 1 ]++;

        //This is so unlikely it is almost not worth even checking.
        if( chars[ chars.length - 1 ] == '0' )
        {
            Log.alarm( this, "Branch generation is starting over due to excessive increments" );
        }

        return new String( chars );
        
    }


    public static void main( String args[] )
    {
        try
        {
            String output = convert( args[0] );

            System.out.println("BC#: " + args[0] );
            System.out.println("Initial Branch: " + output );
        }
        catch( Exception e )
        {
            e.printStackTrace();
        }
    }

}
