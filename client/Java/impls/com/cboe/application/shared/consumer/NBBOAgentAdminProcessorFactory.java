/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 11, 2002
 * Time: 1:57:46 PM
 * To change template for new class use
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.*;

/**
 * @author Emily Huang
 */

public class NBBOAgentAdminProcessorFactory {
  /**
   * NBBOAgentAdminProcessorFactory constructor comment.
   */
  public NBBOAgentAdminProcessorFactory() {
    super();
  }

  public static NBBOAgentAdminProcessor create(NBBOAgentAdminCollector parent) {
    NBBOAgentAdminProcessor processor = new NBBOAgentAdminProcessor(parent);
    return processor;
  }
}