package com.cboe.interfaces.application;

import com.cboe.idl.cmiQuote.*;
import com.cboe.interfaces.domain.session.SessionBasedCollector;

/**
 *
 * @author William Wei
 *
 */
public interface QuoteNotificationCollector extends SessionBasedCollector {
  public void acceptQuoteNotification(LockNotificationStruct[] quoteLocks) ;
}
