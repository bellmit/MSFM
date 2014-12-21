package com.cboe.domain.supplier.proxy;

import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;

public abstract class CallbackInterceptor
{
    public abstract void startInstrumentation(String prefix, boolean privateOnly);
    public abstract void removeInstrumentation();
    public abstract void addQueueInstrumentorRelation(QueueInstrumentorExtension queueInstrumentorExtension);
}
