package com.cboe.interfaces.application;

import com.cboe.interfaces.domain.session.SessionBasedCollector;

/**
 *
 * @author Jeff Illian
 *
 */
public interface RecapCollector extends SessionBasedCollector
{
  public void acceptRecapsForClass(com.cboe.idl.cmiMarketData.RecapStruct[] recaps) ;
}
