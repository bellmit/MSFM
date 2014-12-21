package com.cboe.consumers.eventChannel;


import com.cboe.interfaces.events.*;


public class AlertEventConsumerImpl extends com.cboe.idl.internalEvents.POA_AlertEventConsumer
                                            implements AlertConsumer
{
    private AlertConsumer delegate;

    public AlertEventConsumerImpl(AlertConsumer alertConsumer) {
        super();
        delegate = alertConsumer;
    }
    /**
     * This method is called by the CORBA event channel when alert event is
     * generated.
     */
    public void acceptAlert(com.cboe.idl.cmiIntermarketMessages.AlertStruct alert)
    {
        delegate.acceptAlert(alert);
    }

    public void acceptAlertUpdate(com.cboe.idl.cmiIntermarketMessages.AlertStruct alertUpdated)
    {
        delegate.acceptAlertUpdate(alertUpdated);
    }

    public void acceptSatisfactionAlert(com.cboe.idl.cmiIntermarketMessages.SatisfactionAlertStruct alert)
    {
        delegate.acceptSatisfactionAlert(alert);
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
