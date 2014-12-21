package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Jeff Illian
 */

public class RFQProcessorFactory {
  /**
   * RFQProcessorFactory constructor comment.
   */
  public RFQProcessorFactory() {
    super();
  }

  public static RFQProcessor create(RFQCollector parent) {
    RFQProcessor processor = new RFQProcessor(parent);
    return processor;
  }
}
