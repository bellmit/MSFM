package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.CurrentMarketCollector;


public class CurrentMarketProcessorFactory {

  public CurrentMarketProcessorFactory() {
    super();
  }

  public static CurrentMarketProcessor create(CurrentMarketCollector parent) {
    CurrentMarketProcessor processor = new CurrentMarketProcessor(parent);
    return processor;
  }
}
