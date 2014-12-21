//
// -----------------------------------------------------------------------------------
// Source file: MessageCollectionListener.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

public interface MessageCollectionListener
{
    void messageElementAdded(MessageElement element);
    void messageElementRemoved(MessageElement element);
    void messageElementUpdated(MessageElement element);
    void messageElementAdded(MessageElement[] elements);
}