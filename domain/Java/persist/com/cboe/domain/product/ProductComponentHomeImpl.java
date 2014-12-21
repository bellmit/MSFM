package com.cboe.domain.product;

import com.cboe.exceptions.DataValidationException;
import com.cboe.exceptions.NotFoundException;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.infrastructureServices.foundationFramework.BOHome;
import com.cboe.infrastructureServices.foundationFramework.HomeFactory;
import com.cboe.infrastructureServices.foundationFramework.utilities.Log;
import com.cboe.infrastructureServices.persistenceService.ObjectQuery;
import com.cboe.infrastructureServices.persistenceService.PersistenceException;
import com.cboe.interfaces.domain.product.Product;
import com.cboe.interfaces.domain.product.ProductComponent;
import com.cboe.interfaces.domain.product.ProductComponentHome;
import com.cboe.interfaces.domain.product.ProductHome;
import com.cboe.util.ExceptionBuilder;
import com.cboe.domain.util.LegsNormalizer;
import com.cboe.domain.util.SpreadNormalizationStrategyTypes;
import com.objectspace.jgl.HashSet;

import java.util.Enumeration;
import java.util.Vector;


/**
 * An implementation of <code>ProductComponentHome</code> that uses
 * JavaGrinder for persistence.
 *
 * @author John Wickberg
 */
public class ProductComponentHomeImpl extends BOHome implements ProductComponentHome {
    /**
     * Reference to the product home.
     */
    private ProductHome productHome;
    short spreadNormalizationStrategyType;

    /**
     * Creates an instance.
     */
    public ProductComponentHomeImpl() {
        super();
    }

    /**
     * Creates new component for composite and component product.
     *
     * @see ProductComponentHome#create
     */
    public ProductComponent create(Product composite, Product component, double quantity, char side) throws DataValidationException {
        ProductComponentImpl newComponent = new ProductComponentImpl();
        addToContainer(newComponent);
        newComponent.create(composite, component, quantity, side);
        return newComponent;
    }
    
    public void setSpreadNormalizationStrategyType(short type)
    {
        this.spreadNormalizationStrategyType = type;
    }  
    
    public short getSpreadNormalizationStrategyType()
    {
        return spreadNormalizationStrategyType;
    }
    
    /**
     * Creates components for all legs of a strategy.
     *
     * @see ProductComponentHome#createLegs
     */
    public ProductComponent[] createLegs(Product composite, StrategyLegStruct[] legs) throws DataValidationException {
        Product componentProduct = null;
        ProductComponent[] result = new ProductComponentImpl[legs.length];

        try {
            for (int i = 0; i < legs.length; i++) {
                componentProduct = getProductHome().findByKey(legs[i].product);
                result[i] = create(composite, componentProduct, (double) legs[i].ratioQuantity, legs[i].side);
            }

            return result;
        }
        catch (NotFoundException e) {
            Log.exception(e.details.message, e);
            throw new DataValidationException(e.details);
        }
    }

    /**
     * Finds all composites that contain this component.
     *
     * @see ProductComponentHome#findByComponent
     */
    public ProductComponent[] findByComponent(Product component) {
        ProductComponentImpl example = new ProductComponentImpl();
        ProductComponentImpl[] result;
        addToContainer(example);
        ObjectQuery query = new ObjectQuery(example);
        example.setComponent(component);
        try {
            Vector queryResult = query.find();
            result = new ProductComponentImpl[queryResult.size()];
            queryResult.copyInto(result);
        }
        catch (PersistenceException e) {
            result = new ProductComponentImpl[0];
        }
        return result;
    }

    /**
     * Finds all composites that contain all of the given components.
     *
     * @see ProductComponentHome#findByComponents
     */
    public Product[] findByComponents(Product[] components, boolean exactMatch) {
        HashSet composites = getComposites(components[0]);
        HashSet tempSet;
        for (int i = 1; composites.size() > 0 && i < components.length; i++) {
            tempSet = getComposites(components[i]);
            composites = composites.intersection(tempSet);
        }

        // Convert set to array for result
        Product[] result;
        Enumeration productsEnum = composites.elements();
        if (exactMatch) {
            // restrict result to composites with exactly the requested components
            Vector tempResult = new Vector();
            Product composite;
            ProductComponent[] componentsForComposite;
            while (productsEnum.hasMoreElements()) {
                composite = (Product) productsEnum.nextElement();
                componentsForComposite = findByProduct(composite);
                if (componentsForComposite.length == components.length) {
                    tempResult.addElement(composite);
                }
            }
            result = new ProductImpl[tempResult.size()];
            tempResult.copyInto(result);
        } else {
            result = new ProductImpl[composites.size()];
            for (int i = 0; productsEnum.hasMoreElements(); i++) {
                result[i] = (Product) productsEnum.nextElement();
            }
        }
        return result;
    }

    /**
     * Find composite having matching legs.
     *
     * @see ProductComponentHome#findByLegs
     */
    public Product findByLegs(StrategyLegStruct[] legs) throws DataValidationException, NotFoundException {
        Product[] components = new ProductImpl[legs.length];

        try {
            for (int i = 0; i < legs.length; i++) {
                StrategyLegStruct leg = legs[i];
                Product componentProduct = getProductHome().findByKey(leg.product);
                components[i] = componentProduct;
            }
            ServerStrategyLegsNormalizer myNormalizer = new ServerStrategyLegsNormalizer();
            myNormalizer.normalizeLegs(legs);
        }
        catch (NotFoundException e) {
            Log.exception(e.details.message, e);
            throw new DataValidationException(e.details);
        }

        Product[] composites = findByComponents(components, true);
        return selectMatch(composites, legs);
    }

    /**
     * Finds all components for a composite.
     *
     * @see ProductComponentHome#findByProduct
     */
    public ProductComponent[] findByProduct(Product composite) {
        ProductComponentImpl example = new ProductComponentImpl();
        ProductComponentImpl[] result;
        addToContainer(example);
        ObjectQuery query = new ObjectQuery(example);
        example.setComposite(composite);
        // want to return products in order by OID.  This should return the components in
        // the standard order.
        query.addOrderByField("objectIdentifier");
        try {
            Vector queryResult = query.find();
            result = new ProductComponentImpl[queryResult.size()];
            queryResult.copyInto(result);
        }
        catch (PersistenceException e) {
            result = new ProductComponentImpl[0];
        }
        return result;
    }

    /**
     * Gets all composites that have component as a member.
     *
     * @param component member component
     * @return set of composites
     */
    public HashSet getComposites(Product component) {
        ProductComponent[] composites = findByComponent(component);
        HashSet result = new HashSet();
        for (int i = 0; i < composites.length; i++) {
            result.put(composites[i].getComposite());
        }
        return result;
    }

    /**
     * Gets reference to the product home.
     */
    private ProductHome getProductHome() {
        if (productHome == null) {
            try {
                productHome = (ProductHome) HomeFactory.getInstance().findHome(ProductHome.HOME_NAME);
            }
            catch (Exception e) {
                throw new NullPointerException("Unable to find product home");
            }
        }
        return productHome;
    }

    /**
     * Selects composite that matches all legs of the strategy.
     *
     * @param composites strategies being searched
     * @param legs       desired strategy legs
     * @return first strategy that matches all legs
     * @throws NotFoundException if no match is found
     */
    private Product selectMatch(Product[] composites, StrategyLegStruct[] legs) throws NotFoundException {
        ProductComponent[] components;
        for (int i = 0; i < composites.length; i++) {
            components = findByProduct(composites[i]);
            if (legs.length == components.length) {
                // Arrange the elements of the ProductComponent[] using the 
                // standard sorting criteria, then compare the ProductComponent[]
                // elements with the StrategyLegStruct[] elements.
                ServerStrategyLegsNormalizer myNormalizer = new ServerStrategyLegsNormalizer();
                myNormalizer.sortLegs(components);                
                boolean match = true;
                boolean reverseSides = components[0].toLegStruct().side != legs[0].side;
                for (int j = 0; match && j < legs.length; j++) {
                    match &= components[j].matchesLeg(legs[j], reverseSides);
                }
                if (match) {
                    return composites[i];
                }
            }
        }
        throw ExceptionBuilder.notFoundException("No strategy found matching all legs", 0);
    }

    class ServerStrategyLegsNormalizer extends LegsNormalizer {
        public int checkEquityLeg(StrategyLegStruct[] legs) throws NotFoundException {
            
            switch(ProductComponentHomeImpl.this.getSpreadNormalizationStrategyType())
            {
                case SpreadNormalizationStrategyTypes.NO_STOCK_LEG_FULL_LOT_NORMALIZATION:
                    return -1;
                case SpreadNormalizationStrategyTypes.STOCK_LEG_FULL_LOT_NORMALIZATION:
                    for (int i = 0; i < legs.length; i++) {
                        StrategyLegStruct leg = legs[i];
                        Product componentProduct = getProductHome().findByKey(leg.product);
                        if (leg.product == componentProduct.getProductKey() &&
                                (componentProduct.getProductEditor().isProductType(ProductTypes.EQUITY)
                                        || componentProduct.getProductEditor().isProductType(ProductTypes.INDEX))) {
                            return leg.product;
                        }
                    }
                    return -1;   
                default:
                    return -1;
            }    
        }

        /**
         * Sorts the legs into the standard order.
         *
         * @param legs the legs to be sorted.
         * @throws DataValidationException if a leg is not an option
         */
        public void sortLegs(StrategyLegStruct[] legs) {
            String[] names = new String[legs.length];
            Product currentProduct;
            for (int i = 0; i < legs.length; i++) {
                try {
                    currentProduct = getProductHome().findByKey(legs[i].product);
                }
                catch (NotFoundException e) {
                    Log.exception(e.details.message, e);
                    return;
                }

                names[i] = com.cboe.domain.util.ClientProductStructBuilder.toString(currentProduct.getProductName());

            }
            // should only be a small number of legs, so simple bubble sort should be fine.
            for (int i = 0; i < legs.length - 1; i++) {
                for (int j = i + 1; j < legs.length; j++) {
                    if (names[i].compareTo(names[j]) > 0) {
                        swap(names, i, j);
                        swap(legs, i, j);
                    }
                }
            }
        }
        
        public void sortLegs(ProductComponent[] legs) 
        {
            String[] names = new String[legs.length];
            Product currentProduct;
            for (int i = 0; i < legs.length; i++)  {
                currentProduct = legs[i].getComponent();
                names[i] = com.cboe.domain.util.ClientProductStructBuilder.toString(currentProduct.getProductName());
            }
            for (int i = 0; i < legs.length - 1; i++) {
                for (int j = i + 1; j < legs.length; j++) {
                    if (names[i].compareTo(names[j]) > 0) {
                        swap(names, i, j);
                        swap(legs, i, j);
                    }
                }
            }
        }
    }
}



