
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

public class QuoteCallbackHome extends BOHome {

  public QuoteCallbackHome() {
  }
  public QuoteCallbackInterceptor create()
  {
    QuoteCallback cb = new QuoteCallback();
    addToContainer(cb);
    return new  QuoteCallbackInterceptor(cb);
  }
}