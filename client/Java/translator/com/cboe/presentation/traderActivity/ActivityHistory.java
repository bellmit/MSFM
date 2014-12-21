//
// -----------------------------------------------------------------------------------
// Source file: ActivityHistory.java
//
// PACKAGE: com.cboe.presentation.traderActivity;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2001 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.traderActivity;

import java.util.*;

import com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct;

import com.cboe.interfaces.presentation.common.formatters.Formattable;
import com.cboe.interfaces.domain.dateTime.DateTime;

import com.cboe.presentation.common.dateTime.DateTimeImpl;

/********************************************************************************
 * Contains Order, Quote, and RFQ activity history
 *
 * @see com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct
 */
public class ActivityHistory implements Formattable
{

//*** Public Attributes

    // Format constants
    public static final String NAME_VALUE_FORMAT = new String( "NAME_VALUE_FORMAT" );


//*** Private Attributes

    private ActivityHistoryStruct m_activityHistoryStruct = null;
    private List                  m_activityRecords       = null;
    private DateTime              m_endTime               = null;
    private Map                   m_formattedStrings      = null;
    private DateTime              m_startTime             = null;


//*** Public Methods

    /*****************************************************************************
     * Memberwise constructor
     *
     * @see com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct
     */
    public ActivityHistory( ActivityHistoryStruct activityHistoryStruct )
    {
        m_activityHistoryStruct = activityHistoryStruct;
        m_activityRecords       = null;
        m_endTime               = null;
        m_formattedStrings      = new HashMap( );
        m_startTime             = null;
    }


    /*****************************************************************************
     * Returns the ActivityHistoryStruct that this object represents
     *
     * Note: This method exists primarily for backwards compatability reasons.
     *       Please use the wrapper objects whenever possible
     *
     * @see com.cboe.idl.cmiTraderActivity.ActivityHistoryStruct
     */
    public ActivityHistoryStruct getActivityHistoryStruct( )
    {
        return m_activityHistoryStruct;
    }


    /*****************************************************************************
     * Returns the activity records
     *
     * @return a List of ActivityRecords
     */
    public List getActivityRecords( )
    {
        if( m_activityRecords == null )
    {
            m_activityRecords = new ArrayList( );
            for( int index = 0; index < m_activityHistoryStruct.activityRecords.length; index++ )
        {
               m_activityRecords.add( new ActivityRecord( m_activityHistoryStruct.activityRecords[ index ]));
            }
        }
        return m_activityRecords;
    }


    /*****************************************************************************
     * Returns the ClassKey that these activity records represent
     */
    public int getClassKey( )
    {
        return m_activityHistoryStruct.classKey;
    }


    /*****************************************************************************
     * Returns the end time for this group of activity records
     */
    public DateTime getEndTime( )
    {
        if( m_endTime == null )
    {
            m_endTime = new DateTimeImpl( m_activityHistoryStruct.endTime );
        }
        return m_endTime;
    }


    /*****************************************************************************
     * Returns the start time for this group of activity records
     */
    public DateTime getStartTime( )
    {
        if( m_startTime == null )
    {
            m_startTime = new DateTimeImpl( m_activityHistoryStruct.startTime );
        }
        return m_startTime;
    }


    /*****************************************************************************
     * Returns a string representation of the object in NAME_VALUE_FORMAT format
     *
     * @return a string representation of the object
     */
    public String toString( )
    {
        return toString( NAME_VALUE_FORMAT );
    }


    /*****************************************************************************
     * Returns a string representation of the object in the given format
     *
     * @param formatSpecifier - a string that specifies how the object should
     *                          format itself.
     * @return a string representation of the object
     * @see java.text.SimpleDateFormat
     */
    public String toString( String formatSpecifier )
    {
        // Check to see if we've already rendered this string
        String formattedString = ( String )m_formattedStrings.get( formatSpecifier );
        if( formattedString == null )
        {
            if( formatSpecifier.equals( NAME_VALUE_FORMAT ))
        {
                formattedString = new String( "Class Key: " + String.valueOf( getClassKey( )) +
                                              ", Start Time: " + getStartTime( ).toString() +
                                              ", End Time: " + getEndTime( ).toString() +
                                              ", Activity Records: \n" + getActivityRecords( ).toString( ));
            }
            else
            {
                formattedString = new String( "ERROR: Format not supported" );
            }
            m_formattedStrings.put( formatSpecifier, formattedString );
        }
        return formattedString;
    }


//*** Private Methods

    /*****************************************************************************
     * Hide the default constructor from the public interface
     */
    private ActivityHistory( )
    {
    }

}








