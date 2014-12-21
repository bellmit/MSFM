//
// -----------------------------------------------------------------------------------
// Source file: JmsMsgDecoder.java
//
// PACKAGE: com.cboe.infra.presentation.network
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.infra.presentation.network;

import java.util.*;
import javax.jms.Message;
import javax.jms.BytesMessage;

import org.omg.CORBA.ParameterDescription;

import com.cboe.interfaces.presentation.common.formatters.DateFormatStrategy;

import com.cboe.presentation.common.formatters.CommonFormatFactory;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.cboe.ORBInfra.CDR.CDRInputStream;
import com.cboe.ORBInfra.IR.IRHelper;
import com.cboe.ORBInfra.giopSupport.GiopRequestDecoder;
import com.cboe.ORBInfra.giopSupport.GiopUniversalCodec;
import com.cboe.ORBInfra.giopSupport.GiopVersionDecoder;
import com.cboe.utils.nonrepudiation.AnyReader;

/**
 * 
 */
public class JmsMsgDecoder
{
    protected static DateFormatStrategy dateFormatStrategy;

	public static String decodeMessage( Message message, String subjectName ) {
        StringBuffer messageString = new StringBuffer(500);
        try
        {
            messageString.append( "\nDestination: ");
            messageString.append( message.getJMSDestination() );
            messageString.append( "\nPriority: " );
            messageString.append( message.getJMSPriority() );
            messageString.append( "\nDelivery Mode: " );
            messageString.append( message.getJMSDeliveryMode() );
            messageString.append( "\nMessage ID: " );
            messageString.append( message.getJMSMessageID() );
			/*
            messageString.append( "\nType: " );
            messageString.append( message.getJMSType() );
			*/
            messageString.append( "\nCurrent Time: ");
            messageString.append( getDateFormatStrategy().format(new Date()));
            messageString.append( "\n" );
            String interfaceName = extractIFName(subjectName);
			if ( message instanceof BytesMessage ) {
				BytesMessage bMsg = (BytesMessage)message;
				IRHelper ifr = IRHelper.Instance();
				// get a Giop 1.2 decoder
				GiopVersionDecoder versionDecoder = GiopUniversalCodec.getUniversalDecoder().getVersionDecoder(1,2);
				byte[] giopMsg = new byte[(int)bMsg.getBodyLength()];
				bMsg.readBytes( giopMsg );
				GiopRequestDecoder decoder = (GiopRequestDecoder) versionDecoder.getMessageDecoder(giopMsg);
				String methodName = decoder.getOperation();
				messageString.append( "\nMethod Name: ");
				messageString.append( methodName );
				messageString.append( "\n" );
				CDRInputStream parameterStream = new CDRInputStream(giopMsg,true);
				parameterStream.setStreamPosition( decoder.getParameterPosition() );
				ParameterDescription[] parms = ifr.getParamDescFromId(interfaceName,methodName);
				AnyReader rdr = new AnyReader(parameterStream,parms);
				messageString.append( rdr.getData() );
			} else {
				// Not BytesMessage.
				messageString.append( message.toString() );
			}
        }
        catch (Throwable t)
        {
            messageString.append("!ERROR! ");
            messageString.append( t.getMessage() );
            GUILoggerHome.find().exception("JmsMsgDecoder.decodeMessage() was unable to decode message '"+message.toString()+"'", t);
        }
        return messageString.toString();
	}

    protected static String extractIFName(String subjectName)
    {
        int idlIndex = subjectName.indexOf("IDL");
        int tripleDashIndex = subjectName.indexOf("---", idlIndex );
        return subjectName.substring(idlIndex,tripleDashIndex);
    }


    protected static DateFormatStrategy getDateFormatStrategy()
    {
        if (dateFormatStrategy == null)
        {
            dateFormatStrategy = CommonFormatFactory.getDateFormatStrategy();
        }
        return dateFormatStrategy;
    }
}
