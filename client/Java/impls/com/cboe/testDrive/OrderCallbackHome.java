
/**
 * Title:        <p>
 * Description:  <p>
 * Copyright:    Copyright (c) <p>
 * Company:      <p>
 * @author
 * @version 1.0
 */
package com.cboe.testDrive;
import com.cboe.infrastructureServices.foundationFramework.*;

public class OrderCallbackHome extends BOHome {

  public OrderCallbackHome() {
  }
  public OrderCallbackInterceptor create()
  {
    OrderCallback cb = new OrderCallback();
    addToContainer(cb);
    return new  OrderCallbackInterceptor(cb);
  }
}