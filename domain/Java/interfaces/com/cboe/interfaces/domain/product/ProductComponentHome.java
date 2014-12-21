package com.cboe.interfaces.domain.product;

import com.cboe.exceptions.*;
import com.cboe.idl.cmiStrategy.*;

/**
 * A home for components of composite products.  Currently components will
 * only be used for strategies, but they could also be used for indices, so
 * the methods are more general.
 *
 * @author John Wickberg
 */
public interface ProductComponentHome
{
/**
 * Name of home used in HomeFactory.
 */
public static final String HOME_NAME = "ProductComponentHome";

/**
 * Creates a new component for a composite product.
 *
 * @param composite reference to the composite product
 * @param component reference to the component product
 * @param quantity amount of component used in composite
 * @param side side of trade for component when composite is bought
 */
public ProductComponent create(Product composite, Product component, double quantity, char side) throws DataValidationException;
/**
 * Creates a new component for a composite product.
 *
 * @param composite reference to the composite product
 * @param legs structs containing definitions of components for composite
 */
public ProductComponent[] createLegs(Product composite, StrategyLegStruct[] legs) throws DataValidationException;

/**
 * Finds all components that use a product as a component of a composite product.
 *
 * @param component reference to the component product
 */
public ProductComponent[] findByComponent(Product component);

/**
 * Finds all composite products that use a set of products as a components.
 *
 * @param components references to the components a composite should contain
 * @param exactMatch indicates whether or not result should only contain composites
 *                   with exactly the given components.
 */
public Product[] findByComponents(Product[] components, boolean exactMatch);

public void setSpreadNormalizationStrategyType(short type);

/**
 * Finds a composite product that has the given legs.
 *
 * @param legs legs of the strategy
 */
public Product findByLegs(StrategyLegStruct[] legs) throws DataValidationException, NotFoundException;

/**
 * Finds all components for a composite product.
 *
 * @param composite reference to the composite product
 */
public ProductComponent[] findByProduct(Product composite);
}
