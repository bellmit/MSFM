package com.cboe.consumers.eventChannel;

import com.cboe.interfaces.events.*;
import com.cboe.idl.property.PropertyGroupStruct;


public class PropertyConsumerImpl extends com.cboe.idl.events.POA_PropertyEventConsumer implements PropertyConsumer {
    private PropertyConsumer delegate;
    /**
     * constructor comment.
     */
    public PropertyConsumerImpl(PropertyConsumer propertyEventConsumer) {
        super();
        delegate = propertyEventConsumer;
    }

   public void acceptPropertyUpdate(PropertyGroupStruct property)
   {
       delegate.acceptPropertyUpdate(property);
   }


   public void acceptPropertyRemove(String category, String propertyKey)
   {
       delegate.acceptPropertyRemove(category, propertyKey);
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
