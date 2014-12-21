/*
 * Created by IntelliJ IDEA.
 * User: huange
 * Date: Oct 9, 2002
 * Time: 3:09:36 PM
 * To change template for new class use 
 * Code Style | Class Templates options (Tools | IDE Options).
 */
package com.cboe.application.shared.consumer;

import com.cboe.interfaces.application.HeldOrderCollector;

/**
 * @author Emily Huang
 */
public class HeldOrderProcessorFactory
{
  /**
   * RFQProcessorFactory constructor comment.
   */
  public HeldOrderProcessorFactory() {
    super();
  }

  public static HeldOrderProcessor create(HeldOrderCollector parent) {
    HeldOrderProcessor processor = new HeldOrderProcessor(parent);
    return processor;
  }
}