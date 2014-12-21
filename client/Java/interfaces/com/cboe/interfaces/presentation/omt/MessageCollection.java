//
// -----------------------------------------------------------------------------------
// Source file: MessageCollection.java
//
// PACKAGE: com.cboe.interfaces.presentation.omt;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.omt;

import java.util.*;

import com.cboe.interfaces.presentation.util.CBOEId;


public interface MessageCollection
{
    MessageElement[] getAllMessageElements();
    int getCount();
    MessageElement getMessageElement(int index);
    boolean removeMessageElement(MessageElement messageElement);
    List<MessageElement> findElements(MessageElement.MessageType[] types, CBOEId id);
    List<MessageElement> findElements(CBOEId id);

    void addListener(MessageCollectionListener listener);
    void addListener(MessageCollectionListener listener, boolean republishMessages);
    void removeListener(MessageCollectionListener listener);
}