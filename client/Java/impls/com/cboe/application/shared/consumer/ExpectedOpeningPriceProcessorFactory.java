package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Jeff Illian
 */

public class ExpectedOpeningPriceProcessorFactory {
  /**
   * ExpectedOpeningPriceProcessorFactory constructor comment.
   */
  public ExpectedOpeningPriceProcessorFactory() {
    super();
  }
  /**
   * @author Jeff Illian
   */
  public static ExpectedOpeningPriceProcessor create(ExpectedOpeningPriceCollector parent) {
    ExpectedOpeningPriceProcessor processor = new ExpectedOpeningPriceProcessor(parent);
    return processor;
  }
}
