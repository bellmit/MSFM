package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.RecapCollector;


public class RecapProcessorFactory {

  public RecapProcessorFactory() {
    super();
  }

  public static RecapProcessor create(RecapCollector parent) {
    RecapProcessor processor = new RecapProcessor(parent);
    return processor;
  }
}
