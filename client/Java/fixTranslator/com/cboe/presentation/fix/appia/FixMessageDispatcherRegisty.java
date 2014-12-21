/*
 * Created on Jul 14, 2004
 *
 */
package com.cboe.presentation.fix.appia;

import java.util.HashMap;

import com.cboe.presentation.common.logging.GUILoggerBusinessProperty;
import com.cboe.presentation.common.logging.GUILoggerHome;

import com.javtech.appia.Heartbeat;
import com.javtech.appia.MessageObject;

/**
 * Dispatches FIX messages to handlers by message type 
 * 
 * @author Don Mendelson
 *
 */
public class FixMessageDispatcherRegisty {
    
    // Registry of message handlers
    private HashMap dispatcherMap = new HashMap();
    
    /**
     * FIX session handler calls this method to handle received messages
     * @param message to handle
     * @param session the FIX session that received the message
     */
    public void dispatch(MessageObject message, FixSessionImpl session) {
        FixMessageDispatcher dispatcher = 
            (FixMessageDispatcher) dispatcherMap.get(message.getMsgTypeStr());
        if (dispatcher != null) {
            dispatcher.dispatch(message, session);
        } else if (GUILoggerHome.find().isDebugOn()) {
        	// Report message type without a handler, except for Heartbeat
        	String msgTypeStr = message.getMsgTypeStr();
        	if ( !Heartbeat.msg_type.equals(msgTypeStr) ) {
        		GUILoggerHome.find().debug("No FIX message dispatcher registered for MsgType "
                    + msgTypeStr, 
					GUILoggerBusinessProperty.COMMON);
        	}
        }
    }
    
    /**
     * Call to register a  handler for a FIX message type. A handler
     * can be registered to handle multiple message types, if desired. If a different
     * handler was previously registered for a type, it is replaced. 
     * @param msgType a value of MsgType (tag 35)
     * @param dispatcher a handler for the message type
     */
    public void register(String msgType, FixMessageDispatcher dispatcher) {
        dispatcherMap.put(msgType, dispatcher);
    }

}
