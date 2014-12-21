package com.cboe.interfaces.application;

/**
 *
 * @author Jeff Illian
 *
 */
import com.cboe.idl.cmiMarketData.*;
import com.cboe.interfaces.domain.session.SessionBasedCollector;

public interface ExpectedOpeningPriceCollector extends SessionBasedCollector 
{
  public void acceptExpectedOpeningPricesForClass(ExpectedOpeningPriceStruct[] expectedOpeningPrice );
}
