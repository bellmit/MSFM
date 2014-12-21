package com.cboe.domain.product;

import junit.framework.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiConstants.ListingStates;
import com.cboe.idl.cmiConstants.ProductStates;
import com.cboe.idl.cmiConstants.ClassStates;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiConstants.PriceAdjustmentActions;
import com.cboe.idl.cmiConstants.PriceAdjustmentTypes;
import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.idl.cmiUtil.*;
import com.cboe.domain.util.StructBuilder;
import com.cboe.interfaces.domain.Price;
import com.cboe.domain.util.DateWrapper;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.interfaces.domain.ExpirationDate;
import com.cboe.domain.util.PriceFactory;
import com.cboe.domain.util.ExpirationDateFactory;
import com.cboe.interfaces.domain.product.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.infrastructureServices.foundationFramework.transactionManagement.*;


/**
 * A unit tester for products.
 *
 * @author John Wickberg
 */
public class UnitTest extends TestCase
{
	/**
	 * An indicator so that setup is only done on first test.
	 */
	private static boolean setupComplete;
	/**
	 * Reference to the product home.
	 */
	private static ProductHome productHome;
	/**
	 * Reference to the product class home.
	 */
	private static ProductClassHome productClassHome;
	/**
	 * Reference to the reporting class home.
	 */
	private static ReportingClassHome reportingClassHome;
	/**
	 * Reference to the product type home.
	 */
	private static ProductTypeHome productTypeHome;
	/**
	 * Reference to the price adjustment home.
	 */
	private static PriceAdjustmentHome priceAdjustmentHome;
	/**
	 * Struct created in testProductCreate_equity and used in other tests.
	 */
	private static ProductImpl ibmEquity;
	/**
	 * Struct created in testProductCreate_option and used in price adjustment tests.
	 */
	private static ProductImpl ibmOption;
	/**
	 * Used for transactions in tearDown.
	 */
	private static BOSession session;
	/**
	 * If set, transaction will be committed in tearDown.  Otherwise it will be
	 * rolled back.
	 */
	private boolean testCompleted;
/**
 * Creates a unit test.
 *
 * @param name method name of test
 */
public UnitTest(String name)
{
	super(name);
}
/**
 * Tests two product class structs for equality.
 *
 * @param message description of assertion
 * @param expected struct containing expected values
 * @param result struct containing results of test
 */
public void assertEquals(String message, ClassDefinitionStruct expected, ClassStruct result)
{
	assertTrue(message + ": Class Symbol", expected.classSymbol.equals(result.classSymbol));
	assertEquals(message + ": UnderlyingProduct", expected.underlyingProduct, result.underlyingProduct);
	assertTrue(message + ": Listing State", expected.listingState == result.listingState);
	assertTrue(message + ": Primary Exchange", expected.primaryExchange == null || expected.primaryExchange.equals(result.primaryExchange));
}
/**
 * Tests two product structs for equality.
 *
 * @param message description of assertion
 * @param expected struct containing expected values
 * @param result struct containing results of test
 */
public void assertEquals(String message, ProductStruct expected, ProductStruct result)
{
	assertTrue(message + ": Product Symbol", expected.productName.productSymbol.equals("") || expected.productName.productSymbol.equals(result.productName.productSymbol));
	assertTrue(message + ": Product Type", expected.productKeys.productType == result.productKeys.productType);
	assertTrue(message + ": Company Name", expected.companyName.equals(result.companyName));
	assertTrue(message + ": Reporting Class", expected.productName.reportingClass.equals("") || expected.productName.reportingClass.equals(result.productName.reportingClass));
	assertTrue(message + ": Maturity Date", DateWrapper.convertToMillis(expected.maturityDate) == DateWrapper.convertToMillis(result.maturityDate));
	assertTrue(message + ": Description", expected.description.equals(result.description));
	assertTrue(message + ": Unit of Measure", expected.unitMeasure.equals(result.unitMeasure));
	assertTrue(message + ": Standard Quantity", expected.standardQuantity == expected.standardQuantity);
	assertTrue(message + ": Listing State", (ProductStructBuilder.isDefaultState(expected.listingState) && result.listingState == ListingStates.UNLISTED) || (expected.listingState == result.listingState));
	assertTrue(message + ": Expiration Date", DateWrapper.convertToMillis(expected.productName.expirationDate) == DateWrapper.convertToMillis(expected.productName.expirationDate));
	if (!StructBuilder.isDefault(expected.productName.exercisePrice))
	{
		Price p1 = PriceFactory.create(expected.productName.exercisePrice);
		Price p2 = PriceFactory.create(result.productName.exercisePrice);
		assertTrue(message + ": Exercise Price", p1.equals(p2));
	}
	assertTrue(message + ": Option Type", expected.productName.optionType == 0 || expected.productName.optionType == result.productName.optionType);
	assertTrue(message + ": OPRA Month Code", expected.opraMonthCode == result.opraMonthCode);
	assertTrue(message + ": OPRA Price Code", expected.opraPriceCode == result.opraPriceCode);
}
/**
 * Tests two reporting class structs for equality.
 *
 * @param message description of assertion
 * @param expected struct containing expected values
 * @param result struct containing results of test
 */
public void assertEquals(String message, ReportingClassStruct expected, ReportingClassStruct result)
{
	assertTrue(message + ": Reporting Class Symbol", expected.reportingClassSymbol.equals(result.reportingClassSymbol));
	assertTrue(message + ": Product Class Symbol", expected.productClassSymbol.equals(result.productClassSymbol));
	assertTrue(message + ": Contract Size", expected.contractSize == result.contractSize);
	assertTrue(message + ": Listing State", expected.listingState == result.listingState);
}
/**
 * Creates classes needed for a product.  Should not be used for options and futures.
 *
 * @param product CORBA struct of product
 */
public void createClasses(ProductStruct product, Product underlyingProduct) throws Exception
{
	//
	// Product class
	//
	ClassDefinitionStruct pc = new ClassDefinitionStruct();
	if (product.productName.productSymbol != null && product.productName.productSymbol.length() > 0)
	{
		pc.classSymbol = product.productName.productSymbol;
	}
	else
	{
		pc.classSymbol = product.productName.reportingClass;
	}
	pc.productType = product.productKeys.productType;
	if (underlyingProduct != null)
	{
		pc.underlyingProduct = underlyingProduct.toStruct();
	}
	pc.listingState = ListingStates.ACTIVE;
	productClassHome.create(pc);
	//
	// Reporting class
	//
	ReportingClassStruct rc = new ReportingClassStruct();
	if (product.productName.reportingClass == null || product.productName.reportingClass.length() == 0)
	{
		product.productName.reportingClass = product.productName.productSymbol;
	}
	rc.reportingClassSymbol = product.productName.reportingClass;
	rc.productType = product.productKeys.productType;
	rc.productClassSymbol = pc.classSymbol;
	rc.listingState = ListingStates.ACTIVE;
	reportingClassHome.create(rc);
}
/**
 * Initializes foundation framework.
 */
private static void initialize()
{
	try
	{
		FoundationFramework ff = FoundationFramework.getInstance();
		ConfigurationService configService = new ConfigurationServiceFileImpl();
		String[] args = {"ProductUnitTest.properties"};
		configService.initialize(args, 0);
		ff.initialize("ProductServer", configService);
	}
	catch (Exception e)
	{
		e.printStackTrace();
		System.exit(1);
	}
	catch (Throwable t)
	{
		t.printStackTrace();
		System.exit(1);
	}
}
/**
 * Runs the unit test.
 */
public static void main(String args[])
{
	initialize();
	String[] testArgs = {UnitTest.class.getName()};
	junit.textui.TestRunner.main(testArgs);
}
/**
 * Initializes homes used in testing.
 */
public void setUp()
{
	if (!setupComplete)
	{
		setupComplete = true;
		try
		{
			productHome = (ProductHome) HomeFactory.getInstance().findHome(ProductHome.HOME_NAME);
			productClassHome = (ProductClassHome) HomeFactory.getInstance().findHome(ProductClassHome.HOME_NAME);
			reportingClassHome = (ReportingClassHome) HomeFactory.getInstance().findHome(ReportingClassHome.HOME_NAME);
			productTypeHome = (ProductTypeHome) HomeFactory.getInstance().findHome(ProductTypeHome.HOME_NAME);
			priceAdjustmentHome = (PriceAdjustmentHome) HomeFactory.getInstance().findHome(PriceAdjustmentHome.HOME_NAME);
		}
		catch (Exception e)
		{
			System.out.println("Unable to find homes: " + e);
			System.exit(1);
		}
	}
	session = BOSessionManager.getDefaultManager().getSession();
	if (session == null)
	{
		session = BOSession.createAndJoin("Unit test");
	}
	session.startTransaction();
	testCompleted = false;
}
/**
 * Creates suite of unit tests to be run.
 */
public static Test suite()
{
	TestSuite suite = new TestSuite();
	// equity test must be first - other tests may use result of this test
	suite.addTest(new UnitTest("testProductCreate_equity"));
	suite.addTest(new UnitTest("testProductCreate_debt"));
	suite.addTest(new UnitTest("testProductCreate_commodity"));
	suite.addTest(new UnitTest("testProductCreate_index"));
	suite.addTest(new UnitTest("testProductCreate_volatilityIndex"));
	suite.addTest(new UnitTest("testProductCreate_warrant"));
	suite.addTest(new UnitTest("testProductCreate_linkedNote"));
	suite.addTest(new UnitTest("testProductCreate_unitTrust"));
	suite.addTest(new UnitTest("testProductCreate_option"));
	suite.addTest(new UnitTest("testProductCreate_future"));
	suite.addTest(new UnitTest("testProductClassCreate"));
	suite.addTest(new UnitTest("testReportingClassCreate"));
	suite.addTest(new UnitTest("testPriceAdjustmentCreate"));
	suite.addTest(new UnitTest("testPriceAdjustmentApply"));
	return suite;
}
/**
 * Commit or rollback changes at end of test.
 */
public void tearDown()
{
	if (testCompleted)
	{
		try
		{
			session.commit();
		}
		catch (UpdateException e)
		{
			session.rollback();
		}
	}
	else
	{
		session.rollback();
	}
}
/**
 * Tests applying a price adjustment.
 *
 */
public void testPriceAdjustmentApply() throws Exception
{
	PriceAdjustment adjustment = priceAdjustmentHome.findByProduct(ibmEquity);
	adjustment.apply();
//	assertTrue("Price changed", ibmOption.getExercisePrice().equals(PriceFactory.create(50.0)));
	testCompleted = true;
}
/**
 * Tests creating a price adjustment.
 *
 */
public void testPriceAdjustmentCreate() throws Exception
{
	PriceAdjustmentStruct adjustment = ProductStructBuilder.buildPriceAdjustmentStruct();
	adjustment.productSymbol = "IBM";
	adjustment.type = PriceAdjustmentTypes.SPLIT;
	adjustment.adjustedClasses = new PriceAdjustmentClassStruct[1];
	PriceAdjustmentClassStruct classAdjustment = ProductStructBuilder.buildPriceAdjustmentClassStruct();
	classAdjustment.action = PriceAdjustmentActions.PRICE_ADJUSTMENT_UPDATE;
	classAdjustment.currentClassSymbol = "IBM";
	classAdjustment.afterContractSize = 200;
	adjustment.adjustedClasses[0] = classAdjustment;
	classAdjustment.items = new PriceAdjustmentItemStruct[1];
	PriceAdjustmentItemStruct productAdjustment = ProductStructBuilder.buildPriceAdjustmentItemStruct();
	productAdjustment.action = PriceAdjustmentActions.PRICE_ADJUSTMENT_UPDATE;
	productAdjustment.currentName = ibmOption.getProductName();
	productAdjustment.newName = ibmOption.getProductName();
	productAdjustment.newName.exercisePrice = PriceFactory.create(50.0).toStruct();
	productAdjustment.newOpraPriceCode = 'B';
	classAdjustment.items[0] = productAdjustment;
	PriceAdjustment result = priceAdjustmentHome.create(adjustment);
	PriceAdjustmentStruct resultStruct = result.toStruct(true);
	assertTrue("Reporting Classes", resultStruct.adjustedClasses.length == 1);
	assertTrue("Products", resultStruct.adjustedClasses[0].items.length == 1);
	testCompleted = true;
}
/**
 * Tests creation of a product class.
 */
public void testProductClassCreate() throws Exception
{
	ClassDefinitionStruct pc = ProductStructBuilder.buildClassDefinitionStruct();
	pc.classSymbol = "AOL";
	pc.productType = ProductTypes.OPTION;
	pc.underlyingProduct = ibmEquity.toStruct();	// I know this doesn't make business sense, but ...
	pc.primaryExchange = "CO";
	pc.listingState = ListingStates.ACTIVE;
	ProductClass result = productClassHome.create(pc);
	ProductClassStruct resultStruct = result.toStruct(false, false, false);
	assertEquals("ProductClassStruct", pc, resultStruct.info);
	ReportingClass[] rc = result.getReportingClasses(false);
	assertTrue("Empty Reporting Classes", rc.length == 0);
	testCompleted = true;
}
/**
 * Tests the creation of a commodity.
 */
public void testProductCreate_commodity() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.productSymbol = "SPUDS";
	struct.productKeys.productType = ProductTypes.COMMODITY;
	struct.description = "potatoes";
	struct.unitMeasure = "bushels";
	struct.standardQuantity = 100;
	createClasses(struct, null);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	testCompleted = true;
}
/**
 * Tests the creation of a debt.
 */
public void testProductCreate_debt() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.productSymbol = "IBM_debt";
	struct.productKeys.productType = ProductTypes.DEBT;
	struct.companyName = "International Business Machines";
	struct.maturityDate = new DateStruct((byte) 1, (byte) 1, (short) 2000);
	createClasses(struct, null);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	testCompleted = true;
}
/**
 * Tests the creation of an equity.
 */
public void testProductCreate_equity() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.productSymbol = "IBM";
	struct.productKeys.productType = ProductTypes.EQUITY;
	struct.companyName = "International Business Machines";
	createClasses(struct, null);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	ibmEquity = (ProductImpl) result;
	testCompleted = true;
}
/**
 * Tests the creation of a future.
 */
public void testProductCreate_future() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.reportingClass = "IBM";
	struct.productKeys.productType = ProductTypes.FUTURE;
	ExpirationDate exDate = ExpirationDateFactory.createStandardDate(System.currentTimeMillis(), ExpirationDateFactory.FRIDAY_EXPIRATION);
	struct.productName.expirationDate = exDate.toStruct();
	createClasses(struct, ibmEquity);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	testCompleted = true;
}
/**
 * Tests the creation of an index.
 */
public void testProductCreate_index() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.productSymbol = "OEX";
	struct.productKeys.productType = ProductTypes.INDEX;
	struct.description = "S&P 100";
	createClasses(struct, null);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	testCompleted = true;
}
/**
 * Tests the creation of a linked note.
 */
public void testProductCreate_linkedNote() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.productSymbol = "IBM_LN";
	struct.productKeys.productType = ProductTypes.LINKED_NOTE;
	struct.companyName = "International Business Machines";
	struct.description = "a linked note";
	struct.productName.expirationDate = new DateStruct((byte) 1, (byte) 1, (short) 2000);
	createClasses(struct, ibmEquity);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	testCompleted = true;
}
/**
 * Tests the creation of an option.
 */
public void testProductCreate_option() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.reportingClass = "IBM";
	struct.productKeys.productType = ProductTypes.OPTION;
	ExpirationDate exDate = ExpirationDateFactory.createStandardDate(System.currentTimeMillis(), ExpirationDateFactory.SATURDAY_EXPIRATION);
	struct.productName.expirationDate = exDate.toStruct();
	Price exPrice = PriceFactory.create(100.0);
	struct.productName.exercisePrice =  exPrice.toStruct();
	struct.productName.optionType = OptionTypes.CALL;
	struct.opraMonthCode = 'A';
	struct.opraPriceCode = 'A';
	createClasses(struct, ibmEquity);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	ibmOption = (ProductImpl) result;
	testCompleted = true;
}
/**
 * Tests the creation of a unit investment trust.
 */
public void testProductCreate_unitTrust() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.productSymbol = "IBM_UT";
	struct.productKeys.productType = ProductTypes.UNIT_INVESTMENT_TRUST;
	struct.companyName = "International Business Machines";
	struct.description = "a unit trust";
	struct.productName.expirationDate = new DateStruct((byte) 1, (byte) 1, (short) 2000);
	createClasses(struct, ibmEquity);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	testCompleted = true;
}
/**
 * Tests the creation of an volatility index.
 */
public void testProductCreate_volatilityIndex() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.productSymbol = "VIC";
	struct.productKeys.productType = ProductTypes.VOLATILITY_INDEX;
	struct.description = "S&P 100 volatility";
	createClasses(struct, null);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	testCompleted = true;
}
/**
 * Tests the creation of an warrant.
 */
public void testProductCreate_warrant() throws Exception
{
	ProductStruct struct = ProductStructBuilder.buildProductStruct();
	struct.productName.productSymbol = "IBM_W";
	struct.productKeys.productType = ProductTypes.WARRANT;
	struct.companyName = "International Business Machines";
	struct.description = "a warrant";
	struct.productName.expirationDate = new DateStruct((byte) 1, (byte) 1, (short) 2000);
	createClasses(struct, ibmEquity);
	Product result = productHome.create(struct);
	ProductStruct resultStruct = result.toStruct();
	assertEquals("ProductStructs", struct, resultStruct);
	testCompleted = true;
}
/**
 * Tests creation of a reporting class.
 */
public void testReportingClassCreate() throws Exception
{
	ReportingClassStruct rc = ProductStructBuilder.buildReportingClassStruct();
	rc.reportingClassSymbol = "AOL";
	rc.productType = ProductTypes.OPTION;
	rc.productClassSymbol = "AOL";
	rc.contractSize = 100;
	rc.listingState = ListingStates.ACTIVE;
	ReportingClass result = reportingClassHome.create(rc);
	ReportingClassStruct resultStruct = result.toStruct();
	assertEquals("Reporting Class Struct", rc, resultStruct);
	Product[] prods = result.getProducts(false);
	assertTrue("Empty Product Array", prods.length == 0);
	testCompleted = true;
}
}
