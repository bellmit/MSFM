package com.cboe.interfaces.application;

/**
 *
 * @author Jeff Illian
 *
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.domain.session.SessionBasedCollector;
import com.cboe.interfaces.domain.CurrentMarketContainer;

public interface CurrentMarketCollector extends SessionBasedCollector
{
  //public void acceptCurrentMarketsForClass(CurrentMarketStruct[] marketBest) ;
  public void acceptCurrentMarketsForClass(CurrentMarketContainer marketBest) ;
  public void acceptNBBOsForClass(NBBOStruct[] NBBO);
}
