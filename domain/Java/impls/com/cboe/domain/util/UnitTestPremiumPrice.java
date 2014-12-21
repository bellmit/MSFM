package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.ProductDescriptionStruct;
import com.cboe.idl.cmiConstants.*;
import com.cboe.interfaces.domain.Price;
import junit.framework.*;
import java.lang.reflect.*;

/**
 * @author Kevin Park
 */
public class UnitTestPremiumPrice extends TestCase {
	private static Class myClass = UnitTestPremiumPrice.class;
    private ProductDescriptionStruct myProductDescription;
    private PremiumPrice myPremiumPrice;
    private short myProductType;

public UnitTestPremiumPrice(String name) {
	super(name);
}

public static void main(String args[]) {
	junit.textui.TestRunner.main( new String[] {myClass.getName()} );
    //System.exit(0);
}
public void setUp() {
    System.out.println("Starting test " + getName());
    
    myProductDescription = new ProductDescriptionStruct();
    myProductDescription.premiumBreakPoint = PriceFactory.create(3.0).toStruct();
    myProductDescription.minimumAbovePremiumFraction = PriceFactory.create(0.125).toStruct();
    myProductDescription.minimumBelowPremiumFraction = PriceFactory.create(0.0625).toStruct(); 
    myProductType = ProductTypes.OPTION;
    
    myPremiumPrice = new PremiumPrice(myProductDescription);
}
public static Test suite() {
	return suite(myClass);
}
public static Test suite(Class aClass) {
	TestSuite suite= new TestSuite();
	Method[] myMethods = aClass.getDeclaredMethods();
	Class[] constructorSignature = { String.class };
	Constructor currentConstructor;
	String[] constructorArguments = new String[1];
	
	Method currentMethod;
	String methodName;
	Class[] methodParameters;
	Class methodReturnType;
	
	for (int i = 0; i < myMethods.length; i++) {
		currentMethod = myMethods[i];
		methodName = currentMethod.getName();
		
		// use only methods that start with "test", has no arguments
		// and returns void.
		if ( methodName.startsWith("test") ) {
			methodParameters = currentMethod.getParameterTypes();
			if ( methodParameters.length == 0 ) {
				methodReturnType = currentMethod.getReturnType();
				if ( methodReturnType.equals(Void.TYPE) ) {
					try {
						currentConstructor = aClass.getDeclaredConstructor(constructorSignature);
						constructorArguments[0] = methodName;
						suite.addTest((Test)currentConstructor.newInstance(constructorArguments));
					} catch (Exception e) {
						System.out.println("Failure adding " + aClass.getName() + e);
					} 
				}
			}
		}
	} 
	return suite;
}
public void testCeilToTickPositive() throws Exception {
    Price aPrice;
	aPrice = PremiumPrice.ceilToTick( PriceFactory.create(3.626), myProductDescription );
    assertEquals( PriceFactory.create(3.75), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(3.125), myProductDescription );
    assertEquals( PriceFactory.create(3.125), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(300.126), myProductDescription );
    assertEquals( PriceFactory.create(300.25), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(3.0), myProductDescription );
    assertEquals( PriceFactory.create(3.0), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(2.99), myProductDescription );
    assertEquals( PriceFactory.create(3.0), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(2.9375), myProductDescription );
    assertEquals( PriceFactory.create(2.9375), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(2.9374), myProductDescription );
    assertEquals( PriceFactory.create(2.9375), aPrice );
}

public void testCeilToTickNegative() throws Exception {
    Price aPrice;
	aPrice = PremiumPrice.ceilToTick( PriceFactory.create(-3.626), myProductDescription );
    assertEquals( PriceFactory.create(-3.625), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(-3.125), myProductDescription );
    assertEquals( PriceFactory.create(-3.125), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(-300.126), myProductDescription );
    assertEquals( PriceFactory.create(-300.125), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(-3.0), myProductDescription );
    assertEquals( PriceFactory.create(-3.0), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(-2.99), myProductDescription );
    assertEquals( PriceFactory.create(-2.9375), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(-2.9375), myProductDescription );
    assertEquals( PriceFactory.create(-2.9375), aPrice );
    
    aPrice = PremiumPrice.ceilToTick( PriceFactory.create(-2.9374), myProductDescription );
    assertEquals( PriceFactory.create(-2.875), aPrice );
}

public void testFloorToTickPositive() throws Exception {
    Price aPrice;
	aPrice = PremiumPrice.floorToTick( PriceFactory.create(3.626), myProductDescription );
    assertEquals( PriceFactory.create(3.625), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(3.125), myProductDescription );
    assertEquals( PriceFactory.create(3.125), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(300.126), myProductDescription );
    assertEquals( PriceFactory.create(300.125), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(3.0), myProductDescription );
    assertEquals( PriceFactory.create(3.0), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(2.99), myProductDescription );
    assertEquals( PriceFactory.create(2.9375), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(2.9375), myProductDescription );
    assertEquals( PriceFactory.create(2.9375), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(2.9374), myProductDescription );
    assertEquals( PriceFactory.create(2.875), aPrice );
}
public void testFloorToTickNegative() throws Exception {
    Price aPrice;
	aPrice = PremiumPrice.floorToTick( PriceFactory.create(-3.626), myProductDescription );
    assertEquals( PriceFactory.create(-3.75), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(-3.125), myProductDescription );
    assertEquals( PriceFactory.create(-3.125), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(-300.126), myProductDescription );
    assertEquals( PriceFactory.create(-300.25), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(-3.0), myProductDescription );
    assertEquals( PriceFactory.create(-3.0), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(-2.99), myProductDescription );
    assertEquals( PriceFactory.create(-3.0), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(-2.9375), myProductDescription );
    assertEquals( PriceFactory.create(-2.9375), aPrice );
    
    aPrice = PremiumPrice.floorToTick( PriceFactory.create(-2.9374), myProductDescription );
    assertEquals( PriceFactory.create(-2.9375), aPrice );
}
public void testIsValidTickMarketAndNoPrice() throws Exception {
    Price aPrice;
	aPrice = PriceFactory.create(Price.MARKET_STRING);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = new NoPrice();
    assertTrue( !PremiumPrice.isValidTick(aPrice, myProductDescription) );
}
public void testIsValidTickMarketAndNoPrice2() throws Exception {
    Price aPrice;
	aPrice = PriceFactory.create(Price.MARKET_STRING);
    assertTrue( myPremiumPrice.isValidTick(aPrice) );
    
    aPrice = new NoPrice();
    assertTrue( !PremiumPrice.isValidTick(aPrice, myProductDescription) );
    assertTrue( !myPremiumPrice.isValidTick(aPrice) );
}
public void testIsValidTickValuedPrice() throws Exception {
    Price aPrice;
	aPrice = PriceFactory.create(0.0);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(0.03125);
    assertTrue( !PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(0.0625);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(2.875);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(2.8750001);
    assertTrue( !PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(3.0);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(3.1875);
    assertTrue( !PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(3.25);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
}
public void testIsValidTickValuedPrice2() throws Exception {
    Price aPrice;
	aPrice = PriceFactory.create(0.0);
    assertTrue( myPremiumPrice.isValidTick(aPrice) );
    assertTrue( myPremiumPrice.isValidTick(aPrice.toDouble()) );
    
    aPrice = PriceFactory.create(0.03125);
    assertTrue( !myPremiumPrice.isValidTick(aPrice) );
    assertTrue( !myPremiumPrice.isValidTick(aPrice.toDouble()) );
    
    aPrice = PriceFactory.create(0.0625);
    assertTrue( myPremiumPrice.isValidTick(aPrice) );
    assertTrue( myPremiumPrice.isValidTick(aPrice.toDouble()) );
    
    aPrice = PriceFactory.create(2.875);
    assertTrue( myPremiumPrice.isValidTick(aPrice) );
    assertTrue( myPremiumPrice.isValidTick(aPrice.toDouble()) );
    
    aPrice = PriceFactory.create(2.8750001);
    assertTrue( !myPremiumPrice.isValidTick(aPrice) );
    assertTrue( !myPremiumPrice.isValidTick(aPrice.toDouble()) );
    
    aPrice = PriceFactory.create(3.0);
    assertTrue( myPremiumPrice.isValidTick(aPrice) );
    assertTrue( myPremiumPrice.isValidTick(aPrice.toDouble()) );
    
    aPrice = PriceFactory.create(3.1875);
    assertTrue( !myPremiumPrice.isValidTick(aPrice) );
    assertTrue( !myPremiumPrice.isValidTick(aPrice.toDouble()) );    
    
    aPrice = PriceFactory.create(3.25);
    assertTrue( myPremiumPrice.isValidTick(aPrice) );
    assertTrue( myPremiumPrice.isValidTick(aPrice.toDouble()) );
}
public void testIsValidTickNegativeValuedPrice() throws Exception {
    Price aPrice;
	aPrice = PriceFactory.create(-0.0);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(-0.03125);
    assertTrue( !PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(-0.0625);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(-2.875);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(-2.8750001);
    assertTrue( !PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(-3.0);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(-3.1875);
    assertTrue( !PremiumPrice.isValidTick(aPrice, myProductDescription) );
    
    aPrice = PriceFactory.create(-3.25);
    assertTrue( PremiumPrice.isValidTick(aPrice, myProductDescription) );
}
public void testTickUp() throws Exception {
    assertEquals( PriceFactory.create(0.0625), PremiumPrice.tickUp(PriceFactory.create(0.045), 1, myProductDescription) );
    assertEquals( PriceFactory.create(0.125), PremiumPrice.tickUp(PriceFactory.create(0.0625), 1, myProductDescription) );
    assertEquals( PriceFactory.create(3.125), PremiumPrice.tickUp(PriceFactory.create(2.875), 3, myProductDescription) );
}
public void testTickUp2() throws Exception {
    assertEquals( PriceFactory.create(0.0625), myPremiumPrice.tickUp(PriceFactory.create(0.045), 1) );
    assertEquals( PriceFactory.create(0.125), myPremiumPrice.tickUp(PriceFactory.create(0.0625), 1) );
    assertEquals( PriceFactory.create(3.125), myPremiumPrice.tickUp(PriceFactory.create(2.875), 3) );
}
public void testTickDown() throws Exception {
    assertEquals( PriceFactory.create(3.875), PremiumPrice.tickDown(PriceFactory.create(3.9375), 1, myProductDescription) );
    assertEquals( PriceFactory.create(3.75), PremiumPrice.tickDown(PriceFactory.create(3.875), 1, myProductDescription) );
    assertEquals( PriceFactory.create(2.9375), PremiumPrice.tickDown(PriceFactory.create(3.126), 3, myProductDescription) );
    assertEquals( PriceFactory.create(2.75), PremiumPrice.tickDown(PriceFactory.create(2.9375), 3, myProductDescription) );
}
public void testTickDown2() throws Exception {
    assertEquals( PriceFactory.create(3.875), myPremiumPrice.tickDown(PriceFactory.create(3.9375), 1) );
    assertEquals( PriceFactory.create(3.75), myPremiumPrice.tickDown(PriceFactory.create(3.875), 1) );
    assertEquals( PriceFactory.create(2.9375), myPremiumPrice.tickDown(PriceFactory.create(3.126), 3) );
    assertEquals( PriceFactory.create(2.75), myPremiumPrice.tickDown(PriceFactory.create(2.9375), 3) );
}
public void testNearestPrice() throws Exception {
    assertEquals( PriceFactory.create(3.875), PremiumPrice.nearestPrice(PriceFactory.create(3.875), myProductDescription) );
    // hmmm... if tie, takes higher price...
    assertEquals( PriceFactory.create(3.125), PremiumPrice.nearestPrice(PriceFactory.create(3.0625), myProductDescription) );
    assertEquals( PriceFactory.create(3.125), PremiumPrice.nearestPrice(PriceFactory.create(3.0626).toDouble(), myProductDescription) );
    assertEquals( PriceFactory.create(3.0), PremiumPrice.nearestPrice(PriceFactory.create(3.0624), myProductDescription) );
    assertEquals( PriceFactory.create(2.9375), PremiumPrice.nearestPrice(PriceFactory.create(2.945).toDouble(), myProductDescription) );
    assertEquals( PriceFactory.create(2.75), PremiumPrice.nearestPrice(PriceFactory.create(2.734375), myProductDescription) );
}

public void testIsValidForOptionOrder() throws Exception {
    assertTrue( PremiumPrice.isValidForOrder(PriceFactory.create(Price.MARKET_STRING), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForOrder(new NoPrice(), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForOrder(PriceFactory.create(0.0), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForOrder(PriceFactory.create(-0.5), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForOrder(PriceFactory.create(2.875), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForOrder(PriceFactory.create(3.1875), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForOrder(PriceFactory.create(3.25), myProductDescription, myProductType) );
}

public void testIsValidForSpreadOrder() throws Exception {
    myProductType = ProductTypes.STRATEGY;
    assertTrue( PremiumPrice.isValidForOrder(PriceFactory.create(Price.MARKET_STRING), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForOrder(new NoPrice(), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForOrder(PriceFactory.create(0.0), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForOrder(PriceFactory.create(-0.5), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForOrder(PriceFactory.create(2.875), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForOrder(PriceFactory.create(3.1875), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForOrder(PriceFactory.create(3.25), myProductDescription, myProductType) );
}

public void testIsValidForOptionQuote() throws Exception {
    assertTrue( !PremiumPrice.isValidForQuote(PriceFactory.create(Price.MARKET_STRING), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForQuote(new NoPrice(), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForQuote(PriceFactory.create(0.0), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForQuote(PriceFactory.create(-0.5), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForQuote(PriceFactory.create(2.875), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForQuote(PriceFactory.create(3.1875), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForQuote(PriceFactory.create(3.25), myProductDescription, myProductType) );
}

public void testIsValidForSpreadQuote() throws Exception {
    myProductType = ProductTypes.STRATEGY;  
    assertTrue( !PremiumPrice.isValidForQuote(PriceFactory.create(Price.MARKET_STRING), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForQuote(new NoPrice(), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForQuote(PriceFactory.create(0.0), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForQuote(PriceFactory.create(-0.5), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForQuote(PriceFactory.create(2.875), myProductDescription, myProductType) );
    assertTrue( !PremiumPrice.isValidForQuote(PriceFactory.create(3.1875), myProductDescription, myProductType) );
    assertTrue( PremiumPrice.isValidForQuote(PriceFactory.create(3.25), myProductDescription, myProductType) );
}

}
