//
// -----------------------------------------------------------------------------------
// Source file: DropCopyMessageElementImpl.java
//
// PACKAGE: com.cboe.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2008 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import com.cboe.idl.util.RoutingParameterV2Struct;

import com.cboe.interfaces.presentation.omt.DropCopyMessageElement;

/**
 * An implementation of the <code>DropCopyMessageElement</code> interface,
 * which adds a pc-local message number that gets reset with every app restart, as
 * is meaningful only for the pc on which it runs.
 */
public abstract class DropCopyMessageElementImpl
        extends InfoMessageElementImpl
        implements DropCopyMessageElement
{
    private static int localMessageNumber;

    static
    {
        localMessageNumber = 0;
    }

    private static synchronized int getNextSeqNumber()
    {
        return ++localMessageNumber;
    }

    protected DropCopyMessageElementImpl(MessageType msgType, RoutingParameterV2Struct routingParameterV2Struct)
    {
        super(msgType, routingParameterV2Struct);
        super.setMessageNumber(getNextSeqNumber());
    }

    protected DropCopyMessageElementImpl(String text)
    {
        super(text);
    }
}
