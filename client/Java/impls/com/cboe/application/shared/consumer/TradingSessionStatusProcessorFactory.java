package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Connie Feng
 */

public class TradingSessionStatusProcessorFactory {
  /**
   * NBBOProcessorFactory constructor comment.
   */
  public TradingSessionStatusProcessorFactory() {
    super();
  }

  public static TradingSessionStatusProcessor create(TradingSessionStatusCollector parent) {
    TradingSessionStatusProcessor processor = new TradingSessionStatusProcessor(parent);
    return processor;
  }
}
