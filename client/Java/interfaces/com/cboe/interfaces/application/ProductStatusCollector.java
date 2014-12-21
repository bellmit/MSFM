package com.cboe.interfaces.application;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.product.LinkageIndicatorResultStruct;

/**
 *
 * @author Jeff Illian
 *
 */
public interface ProductStatusCollector {
  public void setProductState(ProductStateStruct[] productState );
  public void setClassState(ClassStateStruct newState);
  public void updateProduct(SessionProductStruct updatedProduct);
  public void updateProductClass(SessionClassStruct updatedClass);
  public void updateProductStrategy(SessionStrategyStruct updatedStrategy);

}
