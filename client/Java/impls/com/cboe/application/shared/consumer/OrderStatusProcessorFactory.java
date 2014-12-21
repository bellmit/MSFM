package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Jeff Illian
 */

public class OrderStatusProcessorFactory {
  /**
   * OrderStatusProcessorFactory constructor comment.
   */
  public OrderStatusProcessorFactory() {
    super();
  }
  /**
   * @author Jeff Illian
   */
  public static OrderStatusProcessor create(OrderStatusCollector parent) {
    OrderStatusProcessor processor = new OrderStatusProcessor();
    processor.setParent(parent);
    return processor;
  }
}
