package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.SessionBasedCollector;

/**
 *
 * @author Jeff Illian
 *
 */
public interface RFQCollector extends SessionBasedCollector {
  public void acceptRFQ(com.cboe.idl.cmiQuote.RFQStruct rfq) ;
}
