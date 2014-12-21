package com.cboe.consumers.eventChannel;


import com.cboe.interfaces.events.*;
import com.cboe.idl.groupElement.ElementStruct;

public class GroupElementConsumerImpl extends com.cboe.idl.events.POA_GroupElementEventConsumer implements GroupElementConsumer {
    private GroupElementConsumer delegate;
    /**
     * constructor comment.
     */
    public GroupElementConsumerImpl(GroupElementConsumer groupElementEventConsumer) {
        super();
        delegate = groupElementEventConsumer;
    }

    public void acceptUpdateElement(ElementStruct updatedElementStruct)
    {
		delegate.acceptUpdateElement(updatedElementStruct);
    }
    public void acceptAddElement(long parentGroupElementKey,ElementStruct newElementStruct)
    {
		delegate.acceptAddElement(parentGroupElementKey,newElementStruct);
	}
    public void acceptRemoveElement(long parentGroupElementKey,ElementStruct elementStruct,boolean isRemoveElement)
    {
		delegate.acceptRemoveElement(parentGroupElementKey,elementStruct,isRemoveElement);
    }

    public org.omg.CORBA.Object get_typed_consumer() {
        return null;
    }

    public void push(org.omg.CORBA.Any data)
    throws org.omg.CosEventComm.Disconnected {
    }

    public void disconnect_push_consumer() {
    }
}
