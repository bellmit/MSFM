package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Jeff Illian
 */

public class UserTimeoutWarningProcessorFactory {
  /**
   * UserTimeoutWarningProcessorFactory constructor comment.
   */
  public UserTimeoutWarningProcessorFactory() {
    super();
  }
  /**
   * @author Jeff Illian
   */
  public static UserTimeoutWarningProcessor create(UserTimeoutWarningCollector parent) {
    UserTimeoutWarningProcessor processor = new UserTimeoutWarningProcessor();
    processor.setParent(parent);
    return processor;
  }
}
