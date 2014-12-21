package com.cboe.interfaces.events;

import com.cboe.util.*;
import com.cboe.util.event.*;

/**
 * This is the common interface for the QuoteBustReportConsumer
 * @author Connie Feng
 */
public interface QuoteBustReportConsumerHome extends EventChannelConsumerManager {
	/**
	 * Name that will be used for this home.
	 */
	public final static String HOME_NAME = "QuoteBustReportConsumerHome";
  /**
   * Returns a reference to the QuoteBustReportConsumer.
   *
   * @return reference to QuoteBustReportConsumer
   */
  public QuoteBustReportConsumer find();
  /**
   * Creates an instance of the QuoteBustReportConsumer.
   *
   * @return reference to QuoteBustReportConsumer
   */
  public QuoteBustReportConsumer create();

}

