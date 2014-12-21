//
// -----------------------------------------------------------------------------------
// Source file: TransactionIdFactory.java
//
// PACKAGE: com.cboe.presentation.userSession
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.util;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

import javax.swing.*;

import com.cboe.presentation.userSession.UserSessionFactory;

public class TransactionIdFactory
{
    /**
     * Returns transationID.
     * @return transationID
     */
    public static String createTransactionID()
    {
        return getWorkStationInfo(getUserID()) + ' ' + createTimeStamp();
    }

    /**
     * Returns current loggedIn user workstation Information byt he user ID.
     * @return workStationInfo
     */
    public static String getWorkStationInfo(String userID)
    {
        String workStationInfo;
        try
        {
            InetAddress addr = InetAddress.getLocalHost();
            workStationInfo = addr.getHostAddress();
        }
        catch(UnknownHostException uhe)
        {
            workStationInfo = userID;
            JOptionPane.showMessageDialog(null, "Work station IP not found !", "IP Not Found",
                                          JOptionPane.INFORMATION_MESSAGE);
        }
        return workStationInfo;
    }

    /**
     * Returns timestamp.
     * @return timestamp.
     */
    public static String createTimeStamp()
    {
        StringBuilder timeStamp = new StringBuilder(35);
        Calendar cal = new GregorianCalendar();
        timeStamp.append(cal.get(Calendar.DATE)).append(':')
                .append(cal.get(Calendar.MONTH)).append(':')
                .append(cal.get(Calendar.YEAR)).append(':')

                .append(cal.get(Calendar.HOUR)).append(':')
                .append(cal.get(Calendar.MINUTE)).append(':')
                .append(cal.get(Calendar.SECOND));
        return timeStamp.toString();
    }

    /**
     * Returns the logged in userID.
     * @return userID
     */
    public static String getUserID()
    {
        return UserSessionFactory.findUserSession().getUserModel().getUserId();
    }
}
