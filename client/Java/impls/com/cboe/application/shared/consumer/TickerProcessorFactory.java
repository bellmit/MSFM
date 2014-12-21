package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.TickerCollector;


public class TickerProcessorFactory {

  public TickerProcessorFactory() {
    super();
  }

  public static TickerProcessor create(TickerCollector parent) {
    TickerProcessor processor = new TickerProcessor(parent);
    return processor;
  }
}
