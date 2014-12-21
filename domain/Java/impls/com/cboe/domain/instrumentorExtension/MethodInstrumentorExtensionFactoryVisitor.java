package com.cboe.domain.instrumentorExtension;

import com.cboe.domain.instrumentorExtension.MethodInstrumentorExtension;

/**
 * @author Jing Chen
 */
public interface MethodInstrumentorExtensionFactoryVisitor
{
    void visit(MethodInstrumentorExtension miExtension);
}
