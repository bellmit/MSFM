package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Jeff Illian
 */

public class QuoteNotificationProcessorFactory {
  /**
   * QuoteNotificationProcessorFactory constructor comment.
   */
  public QuoteNotificationProcessorFactory() {
    super();
  }

  public static QuoteNotificationProcessor create(QuoteNotificationCollector parent) {
    QuoteNotificationProcessor processor = new QuoteNotificationProcessor(parent);
    return processor;
  }
}
