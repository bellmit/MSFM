package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ReportingClassStruct;
import com.cboe.idl.cmiSession.ProductStateStruct;
import com.cboe.idl.product.ClassDefinitionStruct;
import com.cboe.idl.product.ClassSettlementStructV3;
import com.cboe.idl.product.ClassStructV2;
import com.cboe.idl.product.PriceAdjustmentClassStruct;
import com.cboe.idl.product.PriceAdjustmentItemStruct;
import com.cboe.idl.product.PriceAdjustmentStruct;
import com.cboe.idl.product.ProductClassStruct;
import com.cboe.idl.product.ProductClassStructV4;
import com.cboe.idl.product.ProductClassStructV5;
import com.cboe.idl.product.ProductInformationStruct;
import com.cboe.idl.product.ProductLocationStruct;
import com.cboe.idl.product.ProductOpenInterestStruct;
import com.cboe.idl.product.ProductSettlementStruct;
import com.cboe.idl.product.ProductSettlementStructV2;
import com.cboe.idl.product.ProductStructV4;
import com.cboe.idl.product.ReportingClassStructV2;
import com.cboe.idl.product.TransactionFeeCodeStruct;

/**
 * A helper that makes it easy to create valid CORBA structs.  The structs created
 * by the methods of this class have default values for all attributes.  There are
 * also some test methods that can be used to check if a struct is a default struct.
 *
 * This class is spitted into ClientProductStructBuilder and this.  ClientProductStructBuilder handles
 * all methods for product structs defined in cmiProduct only.  And this class extends the client version
 * with additionla functionality to handle structs defined product.idl.
 *
 * ***Any maintenance on structs defined in cmiProduct.IDL should go into ClientProductStructBuilder instead
 * @author John Wickberg
 */
public class ProductStructBuilder extends ClientProductStructBuilder
{
/**
 * All methods are static, so no instance is needed.
 */
private ProductStructBuilder()
{
    super();
}

/**
 * Creates a default instance of a ClassDefinitionStruct.
 *
 * @return default instance of struct
 */
public static ClassDefinitionStruct buildClassDefinitionStruct()
{
  ClassDefinitionStruct aStruct = new ClassDefinitionStruct();
  aStruct.classSymbol = "";
  aStruct.underlyingProduct = buildProductStruct();
  aStruct.primaryExchange = "";
  aStruct.activationDate = StructBuilder.buildDateStruct();
  aStruct.inactivationDate = StructBuilder.buildDateStruct();
  aStruct.classKey = 0;
  aStruct.defaultTransactionFeeCode = "";
  aStruct.descriptionName = "";
  aStruct.listingState = 0;
  aStruct.productType = 0;
  aStruct.settlementType = 0;
  aStruct.testClass = false;

  return aStruct;
}

/**
 * Creates a price adjustment class struct with default values.
 *
 * @return CORBA struct with default values
 */
public static PriceAdjustmentClassStruct buildPriceAdjustmentClassStruct()
{
        PriceAdjustmentClassStruct result = new PriceAdjustmentClassStruct();
        result.currentClassSymbol = "";
        result.action = 0;
        result.afterContractSize = 0;
        result.beforeContractSize = 0;
        result.classKey = 0;
        result.currentClassSymbol = "";
        result.productType = 0;
        result.newClassSymbol = "";
        result.items = new PriceAdjustmentItemStruct[0];
        return result;
}
/**
 * Creates a default valued price adjustment item struct.
 *
 * @return CORBA struct with default values
 */
public static PriceAdjustmentItemStruct buildPriceAdjustmentItemStruct()
{
        PriceAdjustmentItemStruct result = new PriceAdjustmentItemStruct();
        result.currentName = buildProductNameStruct();
        result.newName = buildProductNameStruct();
        result.action = 0;
        result.newOpraMonthCode = ' ';
        result.newOpraPriceCode = ' ';
        return result;
}
/**
 * Creates a default valued price adjustment struct.
 *
 * @return CORBA struct with default values
 */
public static PriceAdjustmentStruct buildPriceAdjustmentStruct()
{
        PriceAdjustmentStruct result = new PriceAdjustmentStruct();
        result.adjustmentNumber = 0;
        result.type = 0;
        result.productKey = 0;
        result.cashDividend = StructBuilder.buildPriceStruct();
        result.createdTime = StructBuilder.buildDateTimeStruct();
        result.effectiveDate = StructBuilder.buildDateStruct();
        result.highRange = StructBuilder.buildPriceStruct();
        result.lowRange = StructBuilder.buildPriceStruct();
        result.lastModifiedTime = StructBuilder.buildDateTimeStruct();
        result.newProductSymbol = "";
        result.productSymbol = "";
        result.runDate = StructBuilder.buildDateStruct();
        result.stockDividend = StructBuilder.buildPriceStruct();
        result.adjustedClasses = new PriceAdjustmentClassStruct[0];
        result.orderAction = 0;
        return result;
}
/**
 * Creates a default instance of a <code>ProductClassStruct</code>.
 *
 * @return default instance of struct
 */
public static ProductClassStruct buildProductClassStruct()
{
  ProductClassStruct aStruct = new ProductClassStruct();
  aStruct.sessionCode = "";
  aStruct.defaultTransactionFeeCode = "";
  aStruct.info = buildClassStruct();
  aStruct.products = new ProductStruct[0];
  aStruct.settlementType = 0;
  return aStruct;
}

/**
 * Creates a default instance of a <code>ProductOpenInterestStruct</code>.
 *
 * @return default instance of struct
 */
public static ProductOpenInterestStruct buildProductOpenInterestStruct()
{
  ProductOpenInterestStruct aStruct = new ProductOpenInterestStruct();
  aStruct.productKeys = buildProductKeysStruct();
  aStruct.openInterest = 0;
  return aStruct;
}
/**
 * Creates a default instance of a <code>ProductSettlementStruct</code>.
 *
 * @return default instance of struct
 */
public static ProductSettlementStruct buildProductSettlementStruct()
{
  ProductSettlementStruct aStruct = new ProductSettlementStruct();
  aStruct.productKeys = buildProductKeysStruct();
  aStruct.settlementPrice = StructBuilder.buildPriceStruct();
  return aStruct;
}

/**
 * Creates a default instance of a <code>ProductClassStruct</code>.
 *
 * @return default instance of struct
 */
public static ReportingClassStruct buildReportingClassStruct()
{
  ReportingClassStruct aStruct = new ReportingClassStruct();
  aStruct.classKey = 0;
  aStruct.productType = 0;
  aStruct.reportingClassSymbol = "";
  aStruct.productClassSymbol = "";
  aStruct.productClassKey = 0;
  aStruct.listingState = 0;
  aStruct.contractSize = 0;
  aStruct.transactionFeeCode = "";
  aStruct.activationDate = StructBuilder.buildDateStruct();
  aStruct.inactivationDate = StructBuilder.buildDateStruct();
  aStruct.createdTime = StructBuilder.buildDateTimeStruct();
  aStruct.lastModifiedTime = StructBuilder.buildDateTimeStruct();
  return aStruct;
}
/**
 * Creates a TransactionFeeCodeStruct with default values.
 *
 * @return CORBA struct with default values
 */
public static TransactionFeeCodeStruct buildTransactionFeeCodeStruct()
{
    TransactionFeeCodeStruct result = new TransactionFeeCodeStruct();
    result.transactionFeeCode = "";
    result.description = "";
    return result;
}

/**
 * Clones class defininition struct.
 *
 * @param productClass struct to be cloned
 * @return cloned struct
 */
public static ClassDefinitionStruct cloneClassDefinitionStruct(ClassDefinitionStruct productClass)
{
        ClassDefinitionStruct result = null;
        if (productClass != null)
        {
                result = new ClassDefinitionStruct();
                result.activationDate = StructBuilder.cloneDate(productClass.activationDate);
                result.classKey = productClass.classKey;
                result.classSymbol = productClass.classSymbol;
                result.inactivationDate = StructBuilder.cloneDate(productClass.inactivationDate);
                result.listingState = productClass.listingState;
                result.primaryExchange = productClass.primaryExchange;
                result.productType = productClass.productType;
                result.underlyingProduct = cloneProduct(productClass.underlyingProduct);
                result.defaultTransactionFeeCode = productClass.defaultTransactionFeeCode;
                result.descriptionName = productClass.descriptionName;
                result.settlementType = productClass.settlementType;
                result.testClass = productClass.testClass;
        }
        return result;
}

/**
 * Clones PriceAdjustmentStruct.
 *
 * @param priceAdjustmentStruct to be cloned
 * @return cloned struct
 */
public static PriceAdjustmentStruct clonePriceAdjustmentStruct(PriceAdjustmentStruct priceAdjustmentStruct)
{
    PriceAdjustmentStruct result = null;
    if(priceAdjustmentStruct != null)
    {
        result = new PriceAdjustmentStruct();

        PriceAdjustmentClassStruct[] paClassStructs = priceAdjustmentStruct.adjustedClasses;
        result.adjustedClasses = new PriceAdjustmentClassStruct[paClassStructs.length];
        for(int i=0; i<paClassStructs.length; i++)
        {
            result.adjustedClasses[i] = clonePriceAdjustmentClassStruct(paClassStructs[i]);
        }

        result.adjustmentNumber = priceAdjustmentStruct.adjustmentNumber;
        result.cashDividend = StructBuilder.clonePrice(priceAdjustmentStruct.cashDividend);
        result.createdTime = StructBuilder.cloneDateTime(priceAdjustmentStruct.createdTime);
        result.effectiveDate = StructBuilder.cloneDate(priceAdjustmentStruct.effectiveDate);
        result.highRange = StructBuilder.clonePrice(priceAdjustmentStruct.highRange);
        result.lastModifiedTime = StructBuilder.cloneDateTime(priceAdjustmentStruct.lastModifiedTime);
        result.lowRange = StructBuilder.clonePrice(priceAdjustmentStruct.lowRange);
        result.newProductSymbol =  priceAdjustmentStruct.newProductSymbol;
        result.productKey = priceAdjustmentStruct.productKey;
        result.productSymbol = priceAdjustmentStruct.productSymbol;
        result.runDate = StructBuilder.cloneDate(priceAdjustmentStruct.runDate);
        result.source = priceAdjustmentStruct.source;
        result.splitDenominator = priceAdjustmentStruct.splitDenominator;
        result.splitNumerator = priceAdjustmentStruct.splitNumerator;
        result.stockDividend = StructBuilder.clonePrice(priceAdjustmentStruct.stockDividend);
        result.type = priceAdjustmentStruct.type;
        result.orderAction = priceAdjustmentStruct.orderAction;
    }
    return result;
}

public static PriceAdjustmentClassStruct clonePriceAdjustmentClassStruct(PriceAdjustmentClassStruct priceAdjustmentClassStruct)
{
    PriceAdjustmentClassStruct result = null;
    if(priceAdjustmentClassStruct != null)
    {
        result = new PriceAdjustmentClassStruct();
        result.action = priceAdjustmentClassStruct.action;
        result.afterContractSize = priceAdjustmentClassStruct.afterContractSize;
        result.beforeContractSize = priceAdjustmentClassStruct.beforeContractSize;
        result.classKey = priceAdjustmentClassStruct.classKey;
        result.currentClassSymbol = priceAdjustmentClassStruct.currentClassSymbol;

        PriceAdjustmentItemStruct[] itemStructs = priceAdjustmentClassStruct.items;
        result.items = new PriceAdjustmentItemStruct[itemStructs.length];
        for(int i=0; i<itemStructs.length; i++)
        {
            result.items[i] = clonePriceAdjustmentItemStruct(itemStructs[i]);
        }

        result.newClassSymbol = priceAdjustmentClassStruct.newClassSymbol;
        result.productType = priceAdjustmentClassStruct.productType;
    }
    return result;
}

public static PriceAdjustmentItemStruct clonePriceAdjustmentItemStruct(PriceAdjustmentItemStruct priceAdjustmentItemStruct)
{
    PriceAdjustmentItemStruct result = null;
    if(priceAdjustmentItemStruct != null)
    {
        result = new PriceAdjustmentItemStruct();
        result.action = priceAdjustmentItemStruct.action;
        result.currentName = cloneProductName(priceAdjustmentItemStruct.currentName);
        result.newName = cloneProductName(priceAdjustmentItemStruct.newName);
        result.newOpraMonthCode = priceAdjustmentItemStruct.newOpraMonthCode;
        result.newOpraPriceCode = priceAdjustmentItemStruct.newOpraPriceCode;
    }
    return result;
}

/**
 * Clones ProductOpenInterestStruct.
 *
 * @param productOpenInterestStruct to be cloned
 * @return cloned struct
 */
public static ProductOpenInterestStruct cloneProductOpenInterestStruct(ProductOpenInterestStruct productOpenInterestStruct)
{
        ProductOpenInterestStruct result = null;
        if (productOpenInterestStruct != null)
        {
                result = new ProductOpenInterestStruct();
                result.productKeys = cloneProductKeys(productOpenInterestStruct.productKeys);
                result.openInterest = productOpenInterestStruct.openInterest;
        }
        return result;
}
/**
 * Clones ProductSettlementStruct.
 *
 * @param productSettlementStruct to be cloned
 * @return cloned struct
 */
public static ProductSettlementStruct cloneProductSettlementStruct(ProductSettlementStruct productSettlementStruct)
{
        ProductSettlementStruct result = null;
        if (productSettlementStruct != null)
        {
                result = new ProductSettlementStruct();
                result.productKeys = cloneProductKeys(productSettlementStruct.productKeys);
                result.settlementPrice = StructBuilder.clonePrice(productSettlementStruct.settlementPrice);
        }
        return result;
}

/**
 * Clones product class struct.
 *
 * @param productClass struct to be cloned
 * @return cloned struct
 */
public static ProductClassStruct cloneProductClass(ProductClassStruct productClass)
{
        ProductClassStruct result = null;
        if (productClass != null)
        {
                result = new ProductClassStruct();
                result.sessionCode = productClass.sessionCode;
                result.defaultTransactionFeeCode = new String(productClass.defaultTransactionFeeCode);
                result.info = cloneClassStruct(productClass.info);
                result.products = new ProductStruct[productClass.products.length];
                for (int i = 0; i < productClass.products.length; i++)
                {
                        result.products[i] = cloneProduct(productClass.products[i]);
                }
                result.settlementType = productClass.settlementType;
        }
        return result;
}
/**
 * Clones reporting class struct.
 *
 * @param reportingClass struct to be cloned
 * @return cloned struct
 */
public static ReportingClassStruct cloneReportingClass(ReportingClassStruct reportingClass)
{
        ReportingClassStruct result = null;
        if (reportingClass != null)
        {
                result = new ReportingClassStruct();
                result.activationDate = StructBuilder.cloneDate(reportingClass.activationDate);
                result.classKey = reportingClass.classKey;
                result.contractSize = reportingClass.contractSize;
                result.createdTime = StructBuilder.cloneDateTime(reportingClass.createdTime);
                result.inactivationDate = StructBuilder.cloneDate(reportingClass.inactivationDate);
                result.lastModifiedTime = StructBuilder.cloneDateTime(reportingClass.lastModifiedTime);
                result.listingState = reportingClass.listingState;
                result.transactionFeeCode = reportingClass.transactionFeeCode;
                result.productClassKey = reportingClass.productClassKey;
                result.productType = reportingClass.productType;
                result.productClassSymbol = reportingClass.productClassSymbol;
                result.reportingClassSymbol = reportingClass.reportingClassSymbol;
        }
        return result;
}

/**
 * Clones TransactionFeeCodeStruct.
 *
 * @param transactionFeeCodeStruct to be cloned
 * @return cloned struct
 */
public static TransactionFeeCodeStruct cloneTransactionFeeCodeStruct(TransactionFeeCodeStruct transactionFeeCodeStruct)
{
    TransactionFeeCodeStruct result = null;
    if (transactionFeeCodeStruct != null)
    {
        result = new TransactionFeeCodeStruct();
        result.transactionFeeCode = transactionFeeCodeStruct.transactionFeeCode;
        result.description = transactionFeeCodeStruct.description;
    }
    return result;
}

/**
 * Checks product class for default value.
 *
 * @param productClass struct to be checked
 * @return <code>true</code> if key has default value
 */
public static boolean isDefault(ProductClassStruct productClass)
{
  // assume it is a default struct if product type hasn't been set
  return productClass.info.productType == 0;
}
/**
 * Checks reporting class for default value.
 *
 * @param reportingClass struct to be checked
 * @return <code>true</code> if key has default value
 */
public static boolean isDefault(ReportingClassStruct reportingClass)
{
  // assume it is a default struct if product type hasn't been set
  return reportingClass.productType == 0;
}

public static ProductStructV4 buildProductStructV4()
{
    ProductStructV4 productV4 = new ProductStructV4();
    productV4.product = new ProductStruct();
    productV4.closingPrice = StructBuilder.buildPriceStruct();
    productV4.closingSuffix = "";
    productV4.cusip = "";
    return productV4;
}

public static ProductInformationStruct buildProductInformationStruct()
{
    ProductInformationStruct productInformation = new ProductInformationStruct();
    productInformation.productKey = 0;
    productInformation.closingPrice = StructBuilder.buildPriceStruct();
    productInformation.closingSuffix = "";
    productInformation.cusip = "";
    productInformation.extensions = "";
    return productInformation;
}

    /**
     * This method initializes the ProductClassStructV4.
     * @return ProductClassStructV4
     * @author Cognizant Technology Solutions.
     */
    public static ProductClassStructV4 buildProductClassStructV4()
    {
        ProductClassStructV4 result = new ProductClassStructV4();
        
        result.productClass = buildProductClassStruct();
        result.classSettlement = buildClassSettlementStructV3();
        
        return result;
    }
    
    /**
     * This method initializes ClassSettlementStructV3
     * @return ClassSettlementStructV3
     * @author Cognizant Technology Solutions.
     */
    public static ClassSettlementStructV3 buildClassSettlementStructV3()
    {
       ClassSettlementStructV3 classSettlement = new ClassSettlementStructV3();
       classSettlement.classKey = 0;
       classSettlement.extension = "";
       classSettlement.multilist = false;
       classSettlement.productLocation = buildProductLocationStruct(); 
       classSettlement.productSettlements = new ProductSettlementStructV2[0];
       
       return classSettlement;
        
    }
 
   public static ProductLocationStruct buildProductLocationStruct()
   {
       ProductLocationStruct productLocation =new ProductLocationStruct(); 
       productLocation.postNumber = "";
       productLocation.stationNumber = "";
       
       return productLocation;
   }

    public static ProductClassStructV5 buildProductClassStructV5()
    {
        ProductClassStructV5 aStruct = new ProductClassStructV5();
        aStruct.sessionCode = "";
        aStruct.defaultTransactionFeeCode = "";
        aStruct.info = buildClassStructV2();
        aStruct.products = new ProductStructV4[0];
        aStruct.extensions = "";
        aStruct.multiList = false;
        aStruct.productLocation = buildProductLocationStruct(); 
        aStruct.settlementType = 0;
        return aStruct;
    }

    private static ClassStructV2 buildClassStructV2()
    {
        ClassStructV2 classStruct = new ClassStructV2();
        classStruct.classSymbol = "";
        classStruct.classKey = 0;
        classStruct.productType = 0;
        classStruct.listingState = 0;
        classStruct.underlyingProduct = buildProductStruct();
        classStruct.primaryExchange = "";
        classStruct.activationDate = StructBuilder.buildDateStruct();
        classStruct.inactivationDate = StructBuilder.buildDateStruct();        
        classStruct.createdTime = StructBuilder.buildDateTimeStruct();
        classStruct.lastModifiedTime = StructBuilder.buildDateTimeStruct();        
        classStruct.epwValues = new EPWStruct[0];            
        classStruct.epwFastMarketMultiplier = 1;
        classStruct.productDescription = buildProductDescriptionStruct();
        classStruct.testClass = false;
        classStruct.reportingClasses = new ReportingClassStructV2[0]; 
        return classStruct;
    }
    
    public static ReportingClassStructV2 buildReportingClassStructV2()
    {
        ReportingClassStructV2 aStruct = new ReportingClassStructV2();
        aStruct.reportingClass = buildReportingClassStruct();
        aStruct.extensions = "";
        return aStruct;
    }
    
    
    public static ProductStateStruct buildProductStateStruct()
    {
        ProductStateStruct aStruct = new ProductStateStruct();
        aStruct.productKeys = buildProductKeysStruct();
        aStruct.sessionName = "";
        aStruct.productState = 0;
        aStruct.productStateTransactionSequenceNumber = 0;
        return aStruct;
    }
}
