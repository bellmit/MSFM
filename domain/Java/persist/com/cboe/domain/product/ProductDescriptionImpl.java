package com.cboe.domain.product;

import com.cboe.domain.util.*;
import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;
import com.cboe.infrastructureServices.persistenceService.*;
import java.lang.reflect.*;
import java.util.*;


/**
 * Persistent implementation of <code>ProductDescription</code>.  JavaGrinder
 * is used for persistence
 *
 * @author John Wickberg
 * @version
 */

public class ProductDescriptionImpl extends DomainBaseImpl
	implements ProductDescription {

	/**
	 * Name of table for product description data.
	 */
	public static final String TABLE_NAME = "prod_desc";
	/**
	 * Name of this description.
	 */
	private String descriptionName;
	/**
	 * Name of the description that this description is a variation of.
	 */
	private ProductDescriptionImpl baseDescription;
	/**
	 * Minimum tick that can be used in strike prices.
	 */
	private PriceSqlType minimumStrikePriceFraction;
	/**
	 * Maximum strike price that can be used for options.
	 */
	private PriceSqlType maximumStrikePrice;
	/**
	 * Premium price at which minimum tick size changes.
	 */
	private PriceSqlType premiumBreakPoint;
	/**
	 * Minimum tick size for prices that are above the break point.
	 */
	private PriceSqlType minimumAbovePremiumFraction;
	/**
	 * Minimum tick size for prices that are below the break point.
	 */
	private PriceSqlType minimumBelowPremiumFraction;
	/**
	 * Price format code.
	 */
	private short priceDisplayType;
	/**
	 * Price format code for premium prices.
	 */
	private short premiumPriceFormat;
	/**
	 * Price format code for stike prices.
	 */
	private short strikePriceFormat;
	/**
	 * Price format code for prices of the underlying product.
	 */
	private short underlyingPriceFormat;
	/*
	 * Field definitions for JavaGrinder.
	 */
	private static Field _descriptionName;
	private static Field _baseDescription;
	private static Field _minimumStrikePriceFraction;
	private static Field _maximumStrikePrice;
	private static Field _premiumBreakPoint;
	private static Field _minimumAbovePremiumFraction;
	private static Field _minimumBelowPremiumFraction;
	private static Field _priceDisplayType;
	private static Field _premiumPriceFormat;
	private static Field _strikePriceFormat;
	private static Field _underlyingPriceFormat;

	/*
	 * Collection of attribute descriptions for JavaGrinder.
	 */
	private static Vector classDescriptor;

	/*
	 * Static block to initialize fields.
	 */
	static {
		try {
			_descriptionName = ProductDescriptionImpl.class.getDeclaredField("descriptionName");
			_baseDescription = ProductDescriptionImpl.class.getDeclaredField("baseDescription");
			_minimumStrikePriceFraction = ProductDescriptionImpl.class.getDeclaredField("minimumStrikePriceFraction");
			_maximumStrikePrice = ProductDescriptionImpl.class.getDeclaredField("maximumStrikePrice");
			_premiumBreakPoint = ProductDescriptionImpl.class.getDeclaredField("premiumBreakPoint");
			_minimumAbovePremiumFraction = ProductDescriptionImpl.class.getDeclaredField("minimumAbovePremiumFraction");
			_minimumBelowPremiumFraction = ProductDescriptionImpl.class.getDeclaredField("minimumBelowPremiumFraction");
			_priceDisplayType = ProductDescriptionImpl.class.getDeclaredField("priceDisplayType");
			_premiumPriceFormat = ProductDescriptionImpl.class.getDeclaredField("premiumPriceFormat");
			_strikePriceFormat = ProductDescriptionImpl.class.getDeclaredField("strikePriceFormat");
			_underlyingPriceFormat = ProductDescriptionImpl.class.getDeclaredField("underlyingPriceFormat");
		}
		catch (Exception e) {
			Log.exception("Unable to create field defintions for ProductDescription", e);
		}

        SqlScalarTypeInitializer.initTypes();
	}

    public ProductDescriptionImpl()
    {
        super();
        setUsing32bitId(true);
    }

	/**
	 * Creates an initialized description object.
	 *
	 * @see ProductDescription#create
	 */
	public void create(ProductDescriptionStruct description) throws DataValidationException {
		setAllValues(description);
	}

	/**
	 * Gets reference to product description home.
	 */
	private ProductDescriptionHome getProductDescriptionHome() {
		return (ProductDescriptionHome) getBOHome();
	}

	/**
	 * Gets base description given its name.
	 */
	private ProductDescriptionImpl getBaseDescription() {
		return (ProductDescriptionImpl) editor.get(_baseDescription, baseDescription);
	}

	/**
	 * Gets description name.
	 */
	private String getDescriptionName() {
		return (String) editor.get(_descriptionName, descriptionName);
	}

	/**
	 * Gets maximum strike price.
	 */
	private PriceSqlType getMaximumStrikePrice() {
		return (PriceSqlType) editor.get(_maximumStrikePrice, maximumStrikePrice);
	}

	/**
	 * Gets minimum above premium fraction.
	 */
	private PriceSqlType getMinimumAbovePremiumFraction() {
		return (PriceSqlType) editor.get(_minimumAbovePremiumFraction, minimumAbovePremiumFraction);
	}

	/**
	 * Gets minimum below premium fraction.
	 */
	private PriceSqlType getMinimumBelowPremiumFraction() {
		return (PriceSqlType) editor.get(_minimumBelowPremiumFraction, minimumBelowPremiumFraction);
	}

	/**
	 * Gets minimum strike price fraction.
	 */
	private PriceSqlType getMinimumStrikePriceFraction() {
		return (PriceSqlType) editor.get(_minimumStrikePriceFraction, minimumStrikePriceFraction);
	}

	/**
	 * Gets premium break point price.
	 */
	private PriceSqlType getPremiumBreakPoint() {
		return (PriceSqlType) editor.get(_premiumBreakPoint, premiumBreakPoint);
	}

	/**
	 * Gets premium price format.
	 */
	private short getPremiumPriceFormat() {
		return editor.get(_premiumPriceFormat, premiumPriceFormat);
	}

	/**
	 * Gets price display type.
	 */
	private short getPriceDisplayType() {
		return editor.get(_priceDisplayType, priceDisplayType);
	}

	/**
	 * Gets strike price format.
	 */
	private short getStrikePriceFormat() {
		return editor.get(_strikePriceFormat, strikePriceFormat);
	}

	/**
	 * Gets price display type.
	 */
	private short getUnderlyingPriceFormat() {
		return editor.get(_underlyingPriceFormat, underlyingPriceFormat);
	}

	/**
	 * Describe how this class relates to the relational database.
	 */
	public void initDescriptor()
	{
	  synchronized (ProductDescriptionImpl.class)
	  {
		if (classDescriptor != null)
		{
		  return; // already initialized
		}
		Vector tempDescriptor = super.getDescriptor();
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("desc_name", _descriptionName));
		tempDescriptor.addElement(AttributeDefinition.getForeignRelation(ProductDescriptionImpl.class, "base_desc_key", _baseDescription));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("min_strike_frac", _minimumStrikePriceFraction));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("max_strike_price", _maximumStrikePrice));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prem_break_point", _premiumBreakPoint));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("min_above_frac", _minimumAbovePremiumFraction));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("min_below_frac", _minimumBelowPremiumFraction));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("price_disp_type", _priceDisplayType));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prem_price_format", _premiumPriceFormat));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("strike_price_format", _strikePriceFormat));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("undly_price_format", _underlyingPriceFormat));
		classDescriptor = tempDescriptor;
	  }
	}

	/**
	* Needed to define table name and the description of this class.
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
	 * Sets all values from struct.
	 *
	 * @param description struct holding values for description
	 * @exception DataValidationException if validation checks fail
	 */
	private void setAllValues(ProductDescriptionStruct description) throws DataValidationException {
		setDescriptionName(description.name);
		setBaseDescription(description.baseDescriptionName);
		setMinimumStrikePriceFraction(description.minimumStrikePriceFraction);
		setMaximumStrikePrice(description.maxStrikePrice);
		setPremiumBreakPoint(description.premiumBreakPoint);
		setMinimumAbovePremiumFraction(description.minimumAbovePremiumFraction);
		setMinimumBelowPremiumFraction(description.minimumBelowPremiumFraction);
		setPriceDisplayType(description.priceDisplayType);
		setPremiumPriceFormat(description.premiumPriceFormat);
		setStrikePriceFormat(description.strikePriceFormat);
		setUnderlyingPriceFormat(description.underlyingPriceFormat);
	}
	/**
	 * Sets base description given its name.
	 *
	 * @param name name of base description
	 * @exception DataValidationException if base description doesn't exist
	 */
	private void setBaseDescription(String name) throws DataValidationException {
		try {
			// get description for base name if it is not null and not equal to
			// this descriptions name.  Otherwise, use a null base description.
			ProductDescriptionImpl newBase = null;
			if (name != null && !name.equals("") && !name.equals(getDescriptionName())) {
				newBase = (ProductDescriptionImpl) getProductDescriptionHome().findByName(name);
			}
			editor.set(_baseDescription, newBase, baseDescription);
		}
		catch (NotFoundException e) {
			throw new DataValidationException(e.details);
		}
	}

	/**
	 * Sets description name.
	 */
	protected void setDescriptionName(String newName) {
		editor.set(_descriptionName, newName, descriptionName);
	}

	/**
	 * Sets maximum strike price.
	 */
	private void setMaximumStrikePrice(PriceStruct newPrice) {
		PriceSqlType convertedPrice = new PriceSqlType(newPrice);
		editor.set(_maximumStrikePrice, convertedPrice, maximumStrikePrice);
	}

	/**
	 * Sets minimum above premium fraction.
	 */
	private void setMinimumAbovePremiumFraction(PriceStruct newPrice) {
		PriceSqlType convertedPrice = new PriceSqlType(newPrice);
		editor.set(_minimumAbovePremiumFraction, convertedPrice, minimumAbovePremiumFraction);
	}

	/**
	 * Sets minimum below premium fraction.
	 */
	private void setMinimumBelowPremiumFraction(PriceStruct newPrice) {
		PriceSqlType convertedPrice = new PriceSqlType(newPrice);
		editor.set(_minimumBelowPremiumFraction, convertedPrice, minimumBelowPremiumFraction);
	}

	/**
	 * Sets minimum strike price fraction.
	 */
	private void setMinimumStrikePriceFraction(PriceStruct newPrice) {
		PriceSqlType convertedPrice = new PriceSqlType(newPrice);
		editor.set(_minimumStrikePriceFraction, convertedPrice, minimumStrikePriceFraction);
	}

	/**
	 * Sets premium break point price.
	 */
	private void setPremiumBreakPoint(PriceStruct newPrice) {
		PriceSqlType convertedPrice = new PriceSqlType(newPrice);
		editor.set(_premiumBreakPoint, convertedPrice, premiumBreakPoint);
	}

	/**
	 * Sets premium price format.
	 */
	private void setPremiumPriceFormat(short newFormat) {
		editor.set(_premiumPriceFormat, newFormat, premiumPriceFormat);
	}

	/**
	 * Sets price display type.
	 */
	private void setPriceDisplayType(short newType) {
		editor.set(_priceDisplayType, newType, priceDisplayType);
	}

	/**
	 * Sets strike price format.
	 */
	private void setStrikePriceFormat(short newFormat) {
		editor.set(_strikePriceFormat, newFormat, strikePriceFormat);
	}

	/**
	 * Sets price display type.
	 */
	private void setUnderlyingPriceFormat(short newFormat) {
		editor.set(_underlyingPriceFormat, newFormat, underlyingPriceFormat);
	}

	/**
	 * Copies description values to CORBA struct.
	 *
	 * @see ProductDescription#toStruct
	 */
	public ProductDescriptionStruct toStruct() {
		ProductDescriptionStruct result = new ProductDescriptionStruct();
		result.name = getDescriptionName();
		ProductDescriptionImpl base = (ProductDescriptionImpl) getBaseDescription();
		if (base != null) {
			result.baseDescriptionName = base.getDescriptionName();
		}
		else {
			// base not defined, description is its own base
			result.baseDescriptionName = getDescriptionName();
		}
		result.minimumStrikePriceFraction = getMinimumStrikePriceFraction().toStruct();
		result.maxStrikePrice = getMaximumStrikePrice().toStruct();
		result.premiumBreakPoint = getPremiumBreakPoint().toStruct();
		result.minimumAbovePremiumFraction = getMinimumAbovePremiumFraction().toStruct();
		result.minimumBelowPremiumFraction = getMinimumBelowPremiumFraction().toStruct();
		result.priceDisplayType = getPriceDisplayType();
		result.premiumPriceFormat = getPremiumPriceFormat();
		result.strikePriceFormat = getStrikePriceFormat();
		result.underlyingPriceFormat = getUnderlyingPriceFormat();
		return result;
	}

	/**
	 * Updates an existing description.
	 *
	 * @see ProductDescription#update
	 */
	public void update(ProductDescriptionStruct updatedDescription) throws DataValidationException {
		// All fields can be updated
		setAllValues(updatedDescription);
	}

	/**
	 * Accesses attributes of this object for JavaGrinder. This method allows JavaGrinder
	 * to get around security problems with updating an object from a generic framework.
	 */
	public void update(boolean get, Object[] data, Field[] fields) {
		for (int i = 0; i < data.length; i++) {
			try	{
				if (get)
					data[i] = fields[i].get(this);
				else
					fields[i].set(this, data[i]);
			}
			catch (IllegalAccessException ex) {
				System.out.println("Cannot set " + fields[i].getName() + " of " + this.getClass().getName() + ":" + ex);
			}
			catch (IllegalArgumentException ex)	{
				System.out.println("Cannot set " + fields[i].getName() + " of " + this.getClass().getName() + ":" + ex);
			}
		}
	}

    /**
     * Gets the key of this produc descriptiont.
     *
     * @see ProductDescription # getProductDescriptionKey
     */
    public int getProductDescriptionKey()
    {
        return getObjectIdentifierAsInt();
    }
	
} // end of ProductDescriptionImpl
