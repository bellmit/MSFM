//
// -----------------------------------------------------------------------------------
// Source file: MessageCollectionHelper.java
//
// PACKAGE: com.cboe.presentation.omt
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.omt;

import java.util.*;

import com.cboe.interfaces.presentation.omt.MessageElement;
import com.cboe.interfaces.presentation.order.Order;
import com.cboe.interfaces.presentation.util.CBOEId;


public final class MessageCollectionHelper
{
    private MessageCollectionHelper()
    {
    }

    /**
     * Finds any fill reject messages that match a given order's ID
     */
    public static List<MessageElement> getFillRejectMessages(Order order)
    {
        MessageElement.MessageType[] types = new MessageElement.MessageType[] {MessageElement.MessageType.ORDER_FILL_REPORT_REJECT};
        return MessageCollectionFactory.getInfoMessagesCollection().findElements(types, order.getOrderId().getCboeId());
    }

    /**
     * Finds any order timeout messages that match a given order's ID
     */
    public static List<MessageElement> getOrderTimeoutMessages(Order order)
    {
        MessageElement.MessageType[] types =
                new MessageElement.MessageType[]{MessageElement.MessageType.MANUAL_ORDER_TIMEOUT};
        return MessageCollectionFactory.getInfoMessagesCollection().findElements(types, order.getOrderId().getCboeId());
    }


    /**
     * Finds any fill timeout messages that match a given order's ID
     */
    public static List<MessageElement> getFillTimeoutMessages(Order order)
    {
        MessageElement.MessageType[] types =
                new MessageElement.MessageType[]{MessageElement.MessageType.MANUAL_FILL_TIMEOUT};
        return MessageCollectionFactory.getInfoMessagesCollection().findElements(types, order.getOrderId().getCboeId());
    }

    /**
     * Finds any info messages, of any type, that match a given order's ID
     */
    public static List<MessageElement> getInfoMessages(CBOEId id)
    {
        return MessageCollectionFactory.getInfoMessagesCollection()
                .findElements(id);
    }

    /**
     * Returns boolean indicator of whether there are info messages, of any type, matching a given order's ID
     */
    public static boolean hasInfoMessages(CBOEId id)
    {
        return !(getInfoMessages(id).isEmpty());
    }

    /**
     * Finds an order in the order messages collection that has same CBOEId as message element. It is assumed that the
     * message element is a non-order element, e.g. info message.
     */
    public static List<MessageElement> getOrderMessages(MessageElement messageElement)
    {
        MessageElement.MessageType[] types =
                new MessageElement.MessageType[] {MessageElement.MessageType.ORDER_ACCEPTED,
                                                  MessageElement.MessageType.ORDER_CANCEL_REPLACED,
                                                  MessageElement.MessageType.ORDER_CANCELED};
        return MessageCollectionFactory.getOrderMessagesCollection().findElements(types, messageElement.getCboeId());
    }

}
