package com.cboe.interfaces.application;

/**
 *
 * @author William Wei
 *
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.domain.session.SessionBasedCollector;

public interface BookDepthCollector extends SessionBasedCollector
{
  public void acceptBookDepthsForClass(BookDepthStruct[] marketBests) ;
}
