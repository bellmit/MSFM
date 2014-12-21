package com.cboe.domain.product;

import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.interfaces.domain.product.*;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import java.lang.reflect.*;
import java.util.Vector;

/**
 * A persistent implementation of <code>ProductComponent</code>.
 *
 * @author John Wickberg
 */
public class ProductComponentImpl extends PersistentBObject implements ProductComponent
{
    /**
     * The name of the database table.
     */
    public static final String TABLE_NAME = "prod_comp";
    /**
     * The owning product of all parts of a composite.
     */
    private ProductImpl composite;

    /**
     * A product that is used as a component of a composite.
     */
    private ProductImpl component;

    /**
     * The amount of the component product used in the composite.
     */
    private double quantity;

    /**
     * The side of the component in a trade when the composite is bought.
     */
    private char sideValue;

    /**
     * Field definitions used by JavaGrinder
     */
	private static Vector classDescriptor;
    private static Field _composite;
    private static Field _component;
    private static Field _quantity;
    private static Field _sideValue;

    // Initialization code for JavaGrinder fields.
    static {
        try
        {
            _composite = ProductComponentImpl.class.getDeclaredField("composite");
            _component = ProductComponentImpl.class.getDeclaredField("component");
            _quantity = ProductComponentImpl.class.getDeclaredField("quantity");
            _sideValue = ProductComponentImpl.class.getDeclaredField("sideValue");
        }
		catch (NoSuchFieldException ex)
		{
			System.out.println(ex);
		}
    }
    /**
     * Constructor for unitialized component.
     */
    public ProductComponentImpl()
    {
        super();
        setUsing32bitId(true);
    }

    /**
     * Create an initialized component.
     *
     * @see ProductComponent#create
     */
    public void create(Product aComposite, Product aComponent, double aQuantity, char aSide)
    {
        setComposite(aComposite);
        setComponent(aComponent);
        setQuantity(aQuantity);
        setSideValue(aSide);
    }

    /**
     * Gets component product.
	 *
	 * @see ProductComponent#getComponent
     */
    public Product getComponent()
    {
        return (ProductImpl) editor.get(_component, component);
    }

    /**
     * Get composite product.
	 *
	 * @see ProductComponent#getComposite
     */
    public Product getComposite()
    {
        return (ProductImpl) editor.get(_composite, composite);
    }

    /**
     * Gets quantity.
     */
    private double getQuantity()
    {
        return editor.get(_quantity, quantity);
    }

    /**
     * Gets side value.
     */
    private char getSideValue()
    {
        return editor.get(_sideValue, sideValue);
    }
    /**
     * Creates JavaGrinder descriptor for the database record of this object.
     */
    private void initDescriptor()
    {
        synchronized (ProductImpl.class)
	    {
             if (classDescriptor != null)
             {
			    return; // already initialized
             }
             Vector tempDescriptor = getSuperDescriptor();
             tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductImpl.class, "composite_prod_key", _composite));
             tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductImpl.class, "component_prod_key", _component));
             tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("quantity", _quantity));
             tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("side", _sideValue));
             classDescriptor = tempDescriptor;
        }
    }

    /**
     * Creates JavaGrinder editor for this object.
     */
    public ObjectChangesIF initializeObjectEditor()
    {
     	final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
       	if (classDescriptor == null)
           initDescriptor();
	    result.setTableName(TABLE_NAME);
	    result.setClassDescription(classDescriptor);
	    return result;
    }

	/**
	 * Compares this component to a given strategy leg.
	 *
	 * @see ProductComponent#matchesLeg
	 */
	public boolean matchesLeg(StrategyLegStruct leg, boolean reverseSides)
	{
		boolean result = getComponent().getProductKey() == leg.product &&
						 Math.round(getQuantity()) == leg.ratioQuantity;
        if (!reverseSides) {
		    result &= getSideValue() == leg.side;
        }
        else {
            // side has been validated to be buy or sell, so inequality check is sufficient
            result &= getSideValue() != leg.side;
        }
		return result;
	}
    /**
     * Sets component value.
     */
    protected void setComponent(Product newComponent)
    {
        editor.set(_component, (ProductImpl) newComponent, component);
    }

    /**
     * Sets component value.
     */
    protected void setComposite(Product newComposite)
    {
        editor.set(_composite, (ProductImpl) newComposite, composite);
    }

    /**
     * Sets quantity.
     */
    private void setQuantity(double newQuantity)
    {
        editor.set(_quantity, newQuantity, quantity);
    }
    /**
     * Sets side.
     */
    private void setSideValue(char newValue)
    {
        editor.set(_sideValue, newValue, sideValue);
    }

    /**
     * Converts this component to a leg struct used for strategies.
	 *
	 * @see ProductComponent#toLegStruct
     */
    public StrategyLegStruct toLegStruct()
    {
        StrategyLegStruct result = new StrategyLegStruct();
        result.product = getComponent().getProductKey();
        result.ratioQuantity = (int) getQuantity();
        result.side = getSideValue();
        return result;
    }

	/** Accesses attributes of this object for JavaGrinder. This method allows JavaGrinder
	  * to get around security problems with updating an object from a generic framework.
	  */
	public void update(boolean get, Object[] data, Field[] fields)
	{
		for (int i = 0; i < data.length; i++)
		{
			try
			{
				if (get)
					data[i] = fields[i].get(this);
				else
					fields[i].set(this, data[i]);
			}
			catch (IllegalAccessException ex)
			{
				System.out.println("Cannot set " + fields[i].getName() + " of " + this.getClass().getName() + ":" + ex);
			}
			catch (IllegalArgumentException ex)
			{
				System.out.println("Cannot set " + fields[i].getName() + " of " + this.getClass().getName() + ":" + ex);
			}
		}
	}
}
