package com.cboe.interfaces.domain.product;

import com.cboe.interfaces.domain.*;
import com.cboe.idl.cmiStrategy.*;

/**
 * A part of a composite product.  Strategies are the only currently planned
 * use, but a component could also be part of an index.
 *
 * @author John Wickberg
 */
public interface ProductComponent
{
	/**
 	 * Creates an initialized instance.
	 *
	 * @param composite parent of the component
	 * @param component product used as a component of this composite
	 * @param quantity amount of component used in the composite
	 * @param side how component will be traded in composite
	 */
	public void create(Product composite, Product component, double quantity, char side);

	/**
	 * Gets component product.
	 */
	public Product getComponent();

	/**
	 * Get composite product.
	 */
	public Product getComposite();

	/**
	 * Tests to values in leg against values in this component.
	 *
	 * @param leg strategy leg to be tested
     * @param reverseSides if true, sides must be opposite
	 * @return true if leg matches this component
	 */
	public boolean matchesLeg(StrategyLegStruct leg, boolean reverseSides);

	/**
	 * Converts component to a strategy leg struct.
	 */
	public StrategyLegStruct toLegStruct();

}
