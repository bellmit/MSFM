package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.RemoteCASCallbackRemovalCollector;


public class RemoteCASCallbackRemovalProcessorFactory {

  public RemoteCASCallbackRemovalProcessorFactory() {
    super();
  }

  public static RemoteCASCallbackRemovalProcessor create(RemoteCASCallbackRemovalCollector parent) {
    RemoteCASCallbackRemovalProcessor processor = new RemoteCASCallbackRemovalProcessor();
    processor.setParent(parent);
    return processor;
  }
}
