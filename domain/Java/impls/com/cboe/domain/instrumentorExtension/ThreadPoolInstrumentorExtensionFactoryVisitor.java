package com.cboe.domain.instrumentorExtension;

import com.cboe.domain.instrumentorExtension.ThreadPoolInstrumentorExtension;

/**
 * @author Jing Chen
 */
public interface ThreadPoolInstrumentorExtensionFactoryVisitor
{
    void visit(ThreadPoolInstrumentorExtension tiExtension);
}
