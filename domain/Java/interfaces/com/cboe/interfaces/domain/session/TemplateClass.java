package com.cboe.interfaces.domain.session;

import com.cboe.idl.cmiStrategy.StrategyStruct;
import com.cboe.interfaces.domain.product.Product;

/**
 * A template class represents an assignment of a class to a trading session
 * element template.
 *
 * @author John Wickberg
 */
public interface TemplateClass {

    /**
     * Gets class key for this template class.
     *
     * @return class key
     */
    int getClassKey();

    /**
     * Gets the session name of underlying product
     */
    String getUnderlyingSessionName();
    /**
     * Gets template of this class.
     *
     * @return owning template
     */
    TradingSessionElementTemplate getTemplate();

    /**
     * Gets products selected by this class.
     *
     * @return selected products
     */
    Product[] getSelectedProducts();

    /**
     * Tests a product to see if it is selected by this template class.
     *
     * @param productKey key of product to be checked
     * @return true if product is selected
     */
    boolean isSelected(int productKey);
    
    public boolean isSelected(StrategyStruct strategyStruct);
}
