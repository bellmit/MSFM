package com.cboe.domain.instrumentorExtension;

import com.cboe.domain.instrumentorExtension.QueueInstrumentorExtension;

/**
 * @author Jing Chen
 */
public interface QueueInstrumentorExtensionFactoryVisitor
{
    void visit(QueueInstrumentorExtension qiExtension);
}
