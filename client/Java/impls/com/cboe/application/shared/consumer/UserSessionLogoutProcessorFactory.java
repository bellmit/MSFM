package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Jeff Illian
 */

public class UserSessionLogoutProcessorFactory {
  /**
   * RFQProcessorFactory constructor comment.
   */
  public UserSessionLogoutProcessorFactory() {
    super();
  }
  /**
   * @author Jeff Illian
   */
  public static UserSessionLogoutProcessor create(UserSessionLogoutCollector parent) {
    UserSessionLogoutProcessor processor = new UserSessionLogoutProcessor();
    processor.setParent(parent);
    return processor;
  }
}
