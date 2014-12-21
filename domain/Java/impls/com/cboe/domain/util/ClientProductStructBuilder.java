package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmiSession.*;
import com.cboe.idl.cmiConstants.*;


/**
 * A helper that makes it easy to create valid Product CORBA structs.  The structs created
 * by the methods of this class have default values for all attributes.  There are
 * also some test methods that can be used to check if a struct is a default struct.
 * This class handles the client product structs only.  For server side product structs see
 * ProductStructBuilder which extends this one.
 *
 * @author John Wickberg
 */
public class ClientProductStructBuilder
{
/**
 * All methods are static, so no instance is needed.
 */
protected ClientProductStructBuilder()
{
  super();
}


/**
 * Creates a default instance of a ClassStruct.
 *
 * @return default instance of struct
 */
public static ClassStruct buildClassStruct()
{
  ClassStruct aStruct = new ClassStruct();
  aStruct.classSymbol = "";
  aStruct.classKey = 0;
  aStruct.productType = 0;
  aStruct.listingState = 0;
  aStruct.underlyingProduct = buildProductStruct();
  aStruct.primaryExchange = "";
  aStruct.activationDate = StructBuilder.buildDateStruct();
  aStruct.inactivationDate = StructBuilder.buildDateStruct();
  aStruct.createdTime = StructBuilder.buildDateTimeStruct();
  aStruct.lastModifiedTime = StructBuilder.buildDateTimeStruct();
  aStruct.epwValues = new EPWStruct[0];
  aStruct.epwFastMarketMultiplier = 1;
  aStruct.productDescription = buildProductDescriptionStruct();
  aStruct.testClass = false;
  aStruct.reportingClasses = new ReportingClassStruct[0];
  return aStruct;
}

public static ProductDescriptionStruct buildProductDescriptionStruct()
{
    ProductDescriptionStruct cloned = new ProductDescriptionStruct();

    cloned.baseDescriptionName = "";
    cloned.maxStrikePrice = StructBuilder.buildPriceStruct();
    cloned.minimumAbovePremiumFraction = StructBuilder.buildPriceStruct();
    cloned.minimumBelowPremiumFraction = StructBuilder.buildPriceStruct();
    cloned.minimumStrikePriceFraction = StructBuilder.buildPriceStruct();
    cloned.name = "";
    cloned.premiumBreakPoint = StructBuilder.buildPriceStruct();
    cloned.premiumPriceFormat = 0;
    cloned.priceDisplayType = 0;
    cloned.strikePriceFormat = 0;
    cloned.underlyingPriceFormat = 0;

    return cloned;
}

/**
 * Creates a default instance of a ProductNameStruct.
 *
 * @return default instance of struct
 */
public static ProductNameStruct buildProductNameStruct()
{
  ProductNameStruct aStruct = new ProductNameStruct();
  aStruct.productSymbol = "";
  aStruct.reportingClass = "";
  aStruct.exercisePrice = StructBuilder.buildPriceStruct();
  aStruct.expirationDate = StructBuilder.buildDateStruct();
  aStruct.optionType = ' ';
  return aStruct;
}
/**
 * Creates a default instance of a <code>ProductStruct</code>.
 *
 * @return default instance of struct
 */
public static ProductStruct buildProductStruct()
{
  ProductStruct aStruct = new ProductStruct();
  // need to assign values to all structs and strings
  aStruct.productKeys = buildProductKeysStruct();
  aStruct.listingState = 0;
  aStruct.productName = buildProductNameStruct();
  aStruct.description = "";
  aStruct.companyName = "";
  aStruct.unitMeasure = "";
  aStruct.standardQuantity = 0;
  aStruct.maturityDate = StructBuilder.buildDateStruct();
  aStruct.activationDate = StructBuilder.buildDateStruct();
  aStruct.inactivationDate = StructBuilder.buildDateStruct();
  aStruct.createdTime = StructBuilder.buildDateTimeStruct();
  aStruct.lastModifiedTime = StructBuilder.buildDateTimeStruct();
  aStruct.opraMonthCode = ' ';
  aStruct.opraPriceCode = ' ';

  return aStruct;
}

/**
 * Creates a default instance of a <code>ProductTypeStruct</code>.
 *
 * @return default instance of struct
 */
public static ProductTypeStruct buildProductTypeStruct()
{
  ProductTypeStruct aStruct = new ProductTypeStruct();
  // need to assign values to all structs and strings
  aStruct.type = 0;
  aStruct.name = "";
  aStruct.description = "";;
  aStruct.createdTime = StructBuilder.buildDateTimeStruct();
  aStruct.lastModifiedTime = StructBuilder.buildDateTimeStruct();

  return aStruct;
}

public static ProductKeysStruct buildProductKeysStruct()
{
    ProductKeysStruct keyStruct = new ProductKeysStruct();
    keyStruct.classKey = 0;
    keyStruct.productKey = 0;
    keyStruct.productType = 0;
    keyStruct.reportingClass = 0;

    return keyStruct;
}

public static SessionProductStruct buildSessionProductStruct()
{
    SessionProductStruct sessionProduct = new SessionProductStruct();

    sessionProduct.productState = 0;
    sessionProduct.productStateTransactionSequenceNumber = 0;
    sessionProduct.sessionName = "";
    sessionProduct.productStruct = buildProductStruct();

    return sessionProduct;
}

/**
 * Clones a session class.
 *
 * @param productClass struct to be cloned
 * @return cloned struct
 */
public static SessionClassStruct cloneSessionClassStruct(SessionClassStruct productClass)
{
    SessionClassStruct result = null;
    if (productClass != null) {
        result = new SessionClassStruct();
        result.classState = productClass.classState;
        result.classStateTransactionSequenceNumber = productClass.classState;
        result.eligibleSessions = productClass.eligibleSessions;
        result.classStruct = cloneClassStruct(productClass.classStruct);
        result.sessionName = productClass.sessionName;
        result.underlyingSessionName = productClass.underlyingSessionName;
    }
    return result;
}

/**
 * Clones a session class, but replaces the embedded class structure with one passed in.
 *
 * @param sessionClass struct to be cloned
 * @param classStruct replaces the session's embedded ClassStruct
 * @return cloned struct
 */
public static SessionClassStruct cloneSessionClassStruct(SessionClassStruct sessionClass, ClassStruct classStruct)
{
    SessionClassStruct result = null;
    if ((sessionClass != null) && (classStruct != null))
    {
        result = new SessionClassStruct();
        result.classState = sessionClass.classState;
        result.classStateTransactionSequenceNumber = sessionClass.classState;
        result.underlyingSessionName = sessionClass.underlyingSessionName;
        result.eligibleSessions = sessionClass.eligibleSessions;
        result.classStruct = classStruct;
        result.sessionName = sessionClass.sessionName;
    }
    return result;
}

/**
 * Clones class struct.
 *
 * @param productClass struct to be cloned
 * @return cloned struct
 */
public static ClassStruct cloneClassStruct(ClassStruct productClass)
{
    ClassStruct result = null;
    if (productClass != null)
    {
        result = new ClassStruct();
        result.activationDate = StructBuilder.cloneDate(productClass.activationDate);
        result.classKey = productClass.classKey;
        result.classSymbol = productClass.classSymbol;
        result.createdTime = StructBuilder.cloneDateTime(productClass.createdTime);
        result.inactivationDate = StructBuilder.cloneDate(productClass.inactivationDate);
        result.lastModifiedTime = StructBuilder.cloneDateTime(productClass.lastModifiedTime);
        result.listingState = productClass.listingState;
        result.primaryExchange = productClass.primaryExchange;
        result.productType = productClass.productType;
        result.underlyingProduct = cloneProduct(productClass.underlyingProduct);

        result.epwValues = new EPWStruct[productClass.epwValues.length];
        for (int i = 0; i < productClass.epwValues.length; i++)
        {
                result.epwValues[i] = cloneEPWStruct(productClass.epwValues[i]);
        }

        result.epwFastMarketMultiplier = productClass.epwFastMarketMultiplier;
        result.productDescription = cloneProductDescriptionStruct(productClass.productDescription);

        result.testClass = productClass.testClass;

        result.reportingClasses = new ReportingClassStruct[productClass.reportingClasses.length];
        for (int i = 0; i < productClass.reportingClasses.length; i++)
        {
                result.reportingClasses[i] = ProductStructBuilder.cloneReportingClass(productClass.reportingClasses[i]);
        }
    }
    return result;
}

public static ProductDescriptionStruct cloneProductDescriptionStruct(ProductDescriptionStruct productDesc)
{
    ProductDescriptionStruct cloned = null;

    if ( productDesc != null )
    {
		cloned = new ProductDescriptionStruct();
        cloned.baseDescriptionName = productDesc.baseDescriptionName;
        cloned.maxStrikePrice = StructBuilder.clonePrice(productDesc.maxStrikePrice);
        cloned.minimumAbovePremiumFraction = StructBuilder.clonePrice(productDesc.minimumAbovePremiumFraction);
        cloned.minimumBelowPremiumFraction = StructBuilder.clonePrice(productDesc.minimumBelowPremiumFraction);
        cloned.minimumStrikePriceFraction = StructBuilder.clonePrice(productDesc.minimumStrikePriceFraction);
        cloned.name = productDesc.name;
        cloned.premiumBreakPoint = StructBuilder.clonePrice(productDesc.premiumBreakPoint);
        cloned.premiumPriceFormat = productDesc.premiumPriceFormat;
        cloned.priceDisplayType = productDesc.priceDisplayType;
        cloned.strikePriceFormat = productDesc.strikePriceFormat;
        cloned.underlyingPriceFormat = productDesc.underlyingPriceFormat;
    }
    return cloned;
}
/**
 * Clone EPW struct.
 *
 * @param EPW struct to be cloned
 * @return cloned struct
 */
public static EPWStruct cloneEPWStruct(EPWStruct epwStruct)
{
    EPWStruct result = null;

    if (epwStruct != null )
    {
        result = new EPWStruct();
        result.maximumAllowableSpread = epwStruct.maximumAllowableSpread;
        result.maximumBidRange = epwStruct.maximumBidRange;
        result.minimumBidRange = epwStruct.minimumBidRange;
    }

    return result;
}

/**
 * Clones a session product
 *
 * @param sessionProduct SessionProductStruct to be cloned
 * @return cloned struct
 */
public static SessionProductStruct cloneSessionProduct(SessionProductStruct sessionProduct)
{
    SessionProductStruct result = null;
    if (sessionProduct != null) {
        result = new SessionProductStruct();
        result.productState = sessionProduct.productState;
        result.productStateTransactionSequenceNumber = sessionProduct.productStateTransactionSequenceNumber;
        result.sessionName = sessionProduct.sessionName;
        result.productStruct = cloneProduct(sessionProduct.productStruct);
    }
    return result;
}

/**
 * Clones a session product, but replaces the embedded product structure with one passed in.
 *
 * @param sessionProduct SessionProductStruct to be cloned
 * @param product replaces the embedded ProductStruct
 * @return cloned struct
 */
public static SessionProductStruct cloneSessionProduct(SessionProductStruct sessionProduct, ProductStruct product)
{
    SessionProductStruct result = null;
    if ((sessionProduct != null) && (product != null))
    {
        result = new SessionProductStruct();
        result.productState = sessionProduct.productState;
        result.productStateTransactionSequenceNumber = sessionProduct.productStateTransactionSequenceNumber;
        result.sessionName = sessionProduct.sessionName;
        result.productStruct = product;
    }
    return result;
}

/**
 * Clone product struct.
 *
 * @param product struct to be cloned
 * @return cloned struct
 */
public static ProductStruct cloneProduct(ProductStruct product)
{
	ProductStruct result = null;
	if (product != null)
	{
		result = new ProductStruct();
		result.activationDate = StructBuilder.cloneDate(product.activationDate);
		result.companyName = product.companyName;
		result.createdTime = StructBuilder.cloneDateTime(product.createdTime);
		result.description = product.description;
		result.inactivationDate = StructBuilder.cloneDate(product.inactivationDate);
		result.lastModifiedTime = StructBuilder.cloneDateTime(product.lastModifiedTime);
		result.listingState = product.listingState;
		result.maturityDate = StructBuilder.cloneDate(product.maturityDate);
		result.opraMonthCode = product.opraMonthCode;
		result.opraPriceCode = product.opraPriceCode;
		result.productKeys = cloneProductKeys(product.productKeys);
		result.productName = cloneProductName(product.productName);
		result.standardQuantity = product.standardQuantity;
		result.unitMeasure = product.unitMeasure;
	}
	return result;
}

/**
 * Clones product keys struct.
 *
 * @param keys struct to be cloned
 * @return cloned struct
 */
public static ProductKeysStruct cloneProductKeys(ProductKeysStruct keys)
{
	ProductKeysStruct result = null;
	if (keys != null)
	{
		result = new ProductKeysStruct();
		result.classKey = keys.classKey;
		result.reportingClass = keys.reportingClass;
		result.productKey = keys.productKey;
		result.productType = keys.productType;
	}
	return result;
}
/**
 * Clones product name struct.
 *
 * @param name struct to be cloned
 * @return cloned struct
 */
public static ProductNameStruct cloneProductName(ProductNameStruct name)
{
	ProductNameStruct result = null;
	if (name != null)
	{
		result = new ProductNameStruct();
		result.reportingClass = name.reportingClass;
		result.exercisePrice = StructBuilder.clonePrice(name.exercisePrice);
		result.expirationDate = StructBuilder.cloneDate(name.expirationDate);
		result.optionType = name.optionType;
		result.productSymbol = name.productSymbol;
	}
	return result;
}

/**
 * Clones product type struct.
 *
 * @param product type struct to be cloned
 * @return cloned struct
 */
public static ProductTypeStruct cloneProductType(ProductTypeStruct productType)
{
	ProductTypeStruct result = null;
	if (productType != null)
	{
		result = new ProductTypeStruct();
		result.type = productType.type;
		result.name = productType.name;
		result.description = productType.description;
		result.createdTime = StructBuilder.cloneDateTime(productType.createdTime);
		result.lastModifiedTime = StructBuilder.cloneDateTime(productType.lastModifiedTime);
	}
	return result;
}

/**
 * Clones session strategy struct.
 *
 * @param sessionStrategy struct to be cloned
 * @return cloned struct
 */
public static SessionStrategyStruct cloneSessionStrategy(SessionStrategyStruct sessionStrategy)
{
    SessionStrategyStruct result = null;
    if (sessionStrategy != null)
    {
        result = new SessionStrategyStruct();
        result.sessionProductStruct = cloneSessionProduct(sessionStrategy.sessionProductStruct);
        result.strategyType = sessionStrategy.strategyType;

        result.sessionStrategyLegs = new SessionStrategyLegStruct[sessionStrategy.sessionStrategyLegs.length];
        for (int i = 0; i < sessionStrategy.sessionStrategyLegs.length; i++)
        {
            result.sessionStrategyLegs[i] = cloneSessionStrategyLeg(sessionStrategy.sessionStrategyLegs[i]);
        }
    }
    return result;
}

/**
 * Clones a session strategy, but replaces the embedded session product structure with one passed in.
 *
 * @param sessionStrategy SessionStrategyStruct to be cloned
 * @param session product replaces the embedded SessionProductStruct
 * @return cloned struct
 */

public static SessionStrategyStruct cloneSessionStrategy(SessionStrategyStruct sessionStrategy, SessionProductStruct sessionProduct)
{
    SessionStrategyStruct result = null;
    if ((sessionStrategy != null) && (sessionProduct != null))
    {
        result = new SessionStrategyStruct();
        result.sessionProductStruct = sessionProduct;
        result.strategyType = sessionStrategy.strategyType;

        result.sessionStrategyLegs = new SessionStrategyLegStruct[sessionStrategy.sessionStrategyLegs.length];
        for (int i = 0; i < sessionStrategy.sessionStrategyLegs.length; i++)
        {
            result.sessionStrategyLegs[i] = cloneSessionStrategyLeg(sessionStrategy.sessionStrategyLegs[i]);
        }
    }
    return result;
}

/**
 * Clones strategy struct.
 *
 * @param strategy struct to be cloned
 * @return cloned struct
 */
public static StrategyStruct cloneStrategy(StrategyStruct strategy)
{
	StrategyStruct result = null;
	if (strategy != null)
	{
		result = new StrategyStruct();
        result.product = cloneProduct(strategy.product);
        result.strategyLegs = new StrategyLegStruct[strategy.strategyLegs.length];
        for (int i = 0; i <strategy.strategyLegs.length; i++)
        {
            result.strategyLegs[i] = cloneStrategyLeg(strategy.strategyLegs[i]);
        }
	}
	result.strategyType = strategy.strategyType;
	return result;
}

/**
 * Clones strategy struct but uses passed-in product instead,.
 *
 * @param strategy struct to be cloned
 * @param product replaces the pre-existing product
 * @return cloned struct
 */
public static StrategyStruct cloneStrategy(StrategyStruct strategy, ProductStruct product)
{
	StrategyStruct result = null;
	if ((strategy != null) && (product != null))
	{
		result = new StrategyStruct();
        result.product = product;
        result.strategyLegs = new StrategyLegStruct[strategy.strategyLegs.length];
        for (int i = 0; i <strategy.strategyLegs.length; i++)
        {
            result.strategyLegs[i] = cloneStrategyLeg(strategy.strategyLegs[i]);
        }
	}
	result.strategyType = strategy.strategyType;
	return result;
}

/**
 * Clones strategy leg struct.
 *
 * @param leg struct to be cloned
 * @return cloned struct
 */
public static StrategyLegStruct cloneStrategyLeg(StrategyLegStruct leg)
{
	StrategyLegStruct result = null;
	if (leg != null)
	{
		result = new StrategyLegStruct();
        result.product = leg.product;
        result.ratioQuantity = leg.ratioQuantity;
        result.side = leg.side;
	}
	return result;
}

public static SessionStrategyLegStruct cloneSessionStrategyLeg(SessionStrategyLegStruct leg)
{
    SessionStrategyLegStruct result = null;
    if (leg != null)
    {
        result = new SessionStrategyLegStruct();
        result.product = leg.product;
        result.ratioQuantity = leg.ratioQuantity;
        result.sessionName = leg.sessionName;
        result.side = leg.side;
    }
    return result;
}

/**
 * Checks product class for default value.
 *
 * @param aClass struct to be checked
 * @return <code>true</code> if key has default value
 */
public static boolean isDefault(ClassStruct aClass)
{
  // assume it is a default struct if product type hasn't been set
  return aClass.productType == 0;
}
/**
 * Checks product for default value.
 *
 * @param product struct to be checked
 * @return <code>true</code> if key has default value
 */
public static boolean isDefault(ProductStruct product)
{
  // assume it is a default struct if product type hasn't been set
  return product.productKeys.productType == 0;
}

/**
 * Checks key for default value.
 *
 * @param key key to be checked
 * @return <code>true</code> if key has default value
 */
public static boolean isDefaultKey(long key)
{
  return key == 0;
}
/**
 * Checks any state for default value.
 *
 * @param state state to be checked
 * @return <code>true</code> if state has default value
 */
public static boolean isDefaultState(short state)
{
  return state == 0;
}
/**
 * Checks symbol for default value.
 *
 * @param symbol product symbol to be checked
 * @return <code>true</code> if symbol is default value
 */
public static boolean isDefaultSymbol(String symbol)
{
  return symbol == null || symbol.length() == 0;
}
/**
 * Checks product type for default value.
 *
 * @param ProductTypeStruct product type to be checked
 * @return <code>true</code> if type has default value
 */
public static boolean isDefault(ProductTypeStruct productType)
{
  return isDefaultType(productType.type);
}
/**
 * Checks product type for default value.
 *
 * @param type product type to be checked
 * @return <code>true</code> if type has default value
 */
public static boolean isDefaultType(short type)
{
  return type == 0;
}
/**
 * Checks state against valid class state values.
 *
 * @param state class state to be validated
 * @return true if state is a valid class state
 */
public static boolean isValidClassState(short state)
{
	switch (state)
	{
        case ClassStates.CLOSED :
		case ClassStates.HALTED :
		case ClassStates.PRE_OPEN :
		case ClassStates.OPENING_ROTATION :
		case ClassStates.OPEN :
        case ClassStates.FAST_MARKET :
        case ClassStates.ENDING_HOLD :
        case ClassStates.NO_SESSION :
        case ClassStates.ON_HOLD :
        case ClassStates.SUSPENDED :
        case ClassStates.NOT_IMPLEMENTED :
			return true;
		default :
			return false;
	}
}
/**
 * Checks listing state agaist valid listing state values.
 *
 * @param state listing state to be validated
 * @return true if state is a valid listing state
 */
public static boolean isValidListingState(short state)
{
	switch (state)
	{
		case ListingStates.ACTIVE :
		case ListingStates.INACTIVE :
		case ListingStates.OBSOLETE :
		case ListingStates.UNLISTED :
			return true;
		default :
			return false;
	}
}
/**
 * Checks state against valid product state values.
 *
 * @param state product state to be validated
 * @return true if state is a valid product state value
 */
public static boolean isValidProductState(short state)
{
	switch (state)
	{
		case ProductStates.CLOSED :
		case ProductStates.HALTED :
		case ProductStates.PRE_OPEN :
		case ProductStates.OPENING_ROTATION :
		case ProductStates.OPEN :
        case ProductStates.FAST_MARKET :
        case ProductStates.ENDING_HOLD :
        case ProductStates.NO_SESSION :
        case ProductStates.ON_HOLD :
        case ProductStates.SUSPENDED :
			return true;
		default :
			return false;
	}
}
/**
 * Converts class state to string.
 *
 * @param state class state to be converted
 * @return name of class state
 */
public static String toClassStateString(short state)
{
  switch (state)
  {
  case 0:
  return "No Class State";
  case ClassStates.CLOSED:
  return "Closed";
  case ClassStates.HALTED:
  return "Halted";
  case ClassStates.PRE_OPEN:
  return "Pre-Open";
  case ClassStates.OPENING_ROTATION:
  return "Opening Rotation";
  case ClassStates.OPEN:
  return "Open";
  case ClassStates.ENDING_HOLD:
  return "Ending Hold";
  case ClassStates.FAST_MARKET:
  return "Fast Market";
  case ClassStates.ON_HOLD:
  return "On Hold";
  case ClassStates.NO_SESSION:
  return "No Session";
  case ClassStates.NOT_IMPLEMENTED:
  return "Not Implemented";
  case ClassStates.SUSPENDED:
  return "Suspended";
  default:
  return "Unknown";
  }
}
/**
 * Converts listing state to string.
 *
 * @param state listing state to be converted
 * @return name of listing state
 */
public static String toListingStateString(short state)
{
  switch (state)
  {
  case 0:
  return "No Listing State";
  case ListingStates.ACTIVE:
  return "Active";
  case ListingStates.INACTIVE:
  return "Inactive";
  case ListingStates.OBSOLETE:
  return "Obsolete";
  case ListingStates.UNLISTED:
  return "Unlisted";
  default:
  return "Unknown";
  }
}
/**
 * Converts option type to string.  Could not use conversion to option type object, because
 * value may be "no_option_type".
 *
 * @param type option type to be converted
 * @return name of option type
 */
public static String toOptionString(char type)
{
   switch (type)
   {
      case OptionTypes.CALL:
         return "Call";
      case OptionTypes.PUT:
         return "Put";
      default:
         return "Unknown";
   }
}
/**
 * Converts product state to string.  Could not use conversion to product state object, because
 * value may be "no_product_state".
 *
 * @param state product state to be converted
 * @return name of product state
 */
public static String toProductStateString(short state)
{
  switch (state)
  {
  case 0:
  return "No Product State";
  case ProductStates.CLOSED:
  return "Closed";
  case ProductStates.HALTED:
  return "Halted";
  case ProductStates.PRE_OPEN:
  return "Pre-Open";
  case ProductStates.OPENING_ROTATION:
  return "Opening Rotation";
  case ProductStates.OPEN:
  return "Open";
  case ProductStates.ENDING_HOLD:
  return "Ending Hold";
  case ProductStates.FAST_MARKET:
  return "Fast Market";
  case ProductStates.ON_HOLD:
  return "On Hold";
  case ProductStates.NO_SESSION:
  return "No Session";
  case ProductStates.SUSPENDED:
  return "Suspended";
  default:
  return "Unknown";
  }
}

/**
 * Creates a string from a <code>ProductNameStruct</code>.
 *
 * @param productName struct to be converted
 * @return formatted string representing name struct
 */
public static String toString(ProductNameStruct productName)
{
     if (!isDefaultSymbol(productName.productSymbol))
      {
    	return productName.productSymbol;
      }
      else
      {
    	StringBuilder productNameString = new StringBuilder(50);   
        //If its a no price, then it's likely not an option
        if( PriceFactory.create(productName.exercisePrice).isNoPrice() )
        {
        	productNameString.append(productName.reportingClass)
        	.append(" ");
        	if(productName.expirationDate.month <=9){
        		productNameString.append("0")
        		.append(StructBuilder.toString(productName.expirationDate));
        	}else{
        		productNameString.append(StructBuilder.toString(productName.expirationDate));
        	}
            return productNameString.toString();
        }
        productNameString.append(productName.reportingClass)
        .append(" ")
        .append(StructBuilder.toString(productName.expirationDate))
        .append(" ")
        .append(StructBuilder.toString(productName.exercisePrice))
        .append(" ")
        .append(toOptionString(productName.optionType));
    	return productNameString.toString();
      }
 }

/**
 * Creates a string from a <code>ProductNameStruct</code> without the day of the month.
 *
 * @param productName struct to be converted
 * @return formatted string representing name struct
 */
public static String toStringNoDayOfMonth(ProductNameStruct productName)
{
     if (!isDefaultSymbol(productName.productSymbol))
      {
    	return productName.productSymbol;
      }
      else
      {
    	  StringBuilder productNameString = new StringBuilder(50);   
        //If its a no price, then it's likely not an option
        if( PriceFactory.create(productName.exercisePrice).isNoPrice() )
        {
        	productNameString.append(productName.reportingClass)
        	.append(" ")
        	.append(productName.expirationDate.month)
        	.append("/")
        	.append(productName.expirationDate.year);
            return productNameString.toString();
            
        }
        productNameString.append(productName.reportingClass)
        .append(" ")
        .append(productName.expirationDate.month)
        .append("/")
        .append(productName.expirationDate.year)
        .append(" ")
        .append(StructBuilder.toString(productName.exercisePrice))
        .append(" ")
        .append(toOptionString(productName.optionType));
        return productNameString.toString();
      }
 }


/**
 * Converts product type to string.  Could not use conversion to product type object, because
 * value may be "no_product_type".
 *
 * @param type product type to be converted
 * @return name of product type
 */
public static String toTypeString(short type)
{
  switch (type)
  {
  case 0:
  return "No Product Type";
  case ProductTypes.COMMODITY:
  return "Commodity";
  case ProductTypes.DEBT:
  return "Debt";
  case ProductTypes.EQUITY:
  return "Equity";
  case ProductTypes.FUTURE:
  return "Future";
  case ProductTypes.INDEX:
  return "Index";
  case ProductTypes.LINKED_NOTE:
  return "Linkded Note";
  case ProductTypes.OPTION:
  return "Option";
  case ProductTypes.UNIT_INVESTMENT_TRUST:
  return "Unit Investment Trust";
  case ProductTypes.VOLATILITY_INDEX:
  return "Volatility Index";
  case ProductTypes.WARRANT:
  return "Warrant";
  case ProductTypes.STRATEGY:
  return "Strategy";
  default:
  return "Unknown";
  }
}
}
