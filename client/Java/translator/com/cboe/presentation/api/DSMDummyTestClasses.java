//
// -----------------------------------------------------------------------------------
// Source file: com/cboe/presentation/api/DSMDummyTestClasses.java
//
// PACKAGE: com.cboe.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2009 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------

package com.cboe.presentation.api;

import com.cboe.interfaces.presentation.product.*;
import com.cboe.interfaces.domain.Price;
import com.cboe.interfaces.domain.SessionKeyWrapper;
import com.cboe.interfaces.domain.dateTime.Date;
import com.cboe.presentation.common.dateTime.DateImpl;
import com.cboe.idl.cmiMarketData.*;
import com.cboe.idl.cmiUtil.DateStruct;
import com.cboe.idl.cmiUtil.DateTimeStruct;
import com.cboe.idl.cmiStrategy.StrategyLegStruct;
import com.cboe.idl.cmiSession.SessionProductStruct;
import com.cboe.idl.cmiProduct.ProductKeysStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiConstants.OptionTypes;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.domain.util.PriceFactory;

import java.util.Calendar;


public class DSMDummyTestClasses
{
    private static int todayMonth = Calendar.getInstance().get(Calendar.MONTH);
    private static int todayYear  = Calendar.getInstance().get(Calendar.YEAR);


    // inner class //////////////////////////////////////////
    public static class DummyMarketVolumeStruct
    {
		private MarketVolumeStruct mVolumeStruct;
		
		public DummyMarketVolumeStruct(int qty)
        {
			short volType = 1;
			boolean multipleParties = false;
			mVolumeStruct = new MarketVolumeStruct(volType, qty, multipleParties);
		}
		
		public MarketVolumeStruct[] getMarketVolumeStruct()
        {
			return new MarketVolumeStruct[]{mVolumeStruct};//so far it appears that 1 is enought..
		}
	} // end inner class DummyMarketVolumeStruct

    // inner class //////////////////////////////////////////
	public static class DummySessionProduct implements SessionProduct, Strategy {

		private String description;
		private StrategyLeg[] strategyLegs;

        public void setStrategyLegs(StrategyLeg[] legs)
        {
			strategyLegs = legs;
        }
		
		public void setDescription(String desc){
			description = desc;
		}
		
        public int getProductStateTransactionSequenceNumber()
        {
	        return 0;
        }
		
		public Object clone(){
			return null;
		}
		
        public SessionKeyWrapper getSessionKeyWrapper()
        {
	        return null;
        }

        public boolean getLeapIndicator() {
            return false;
        }

        public ExpirationType getExpirationType() {
            return ExpirationType.STANDARD;
        }

        public SessionProductStruct getSessionProductStruct()
        {
	        return null;
        }

        public short getState()
        {
	        return 0;
        }

        public String getTradingSessionName()
        {
	        return null;
        }

        public boolean isDefaultSession()
        {
	        return false;
        }

        public boolean isInactiveInTradingSession()
        {
	        return false;
        }

        public void setProductStateTransactionSequenceNumber(int sequenceNumber)
        {
        }

        public void setState(short state)
        {
        }

        public void updateProduct(Product newProduct)
        {
        }

        public DateStruct getActivationDate()
        {
	        return null;
        }

        public String getCompanyName()
        {
	        return null;
        }

        public DateTimeStruct getCreatedTime()
        {
	        return null;
        }

        public String getDescription()
        {
	        return description;
        }

        public Price getExercisePrice()
        {
	        return null;
        }

        public Date getExpirationDate()
        {
	        return null;
        }

        public DateStruct getInactivationDate()
        {
	        return null;
        }

        public DateTimeStruct getLastModifiedTime()
        {
	        return null;
        }

        public short getListingState()
        {
	        return 0;
        }

        public DateStruct getMaturityDate()
        {
	        return null;
        }

        public char getOpraMonthCode()
        {
	        return 0;
        }

        public char getOpraPriceCode()
        {
	        return 0;
        }

        public int getProductKey()
        {
	        return 0;
        }

        public ProductKeysStruct getProductKeysStruct()
        {
	        return null;
        }

        public ProductNameStruct getProductNameStruct()
        {
            return null;
        }

        public ProductStruct getProductStruct()
        {
	        return null;
        }

        public short getProductType()
        {
	        return 0;
        }

        public double getStandardQuantity()
        {
	        return 0;
        }

        public String getUnitMeasure()
        {
	        return null;
        }

        public boolean isAllSelectedProduct()
        {
	        return false;
        }

        public boolean isDefaultProduct()
        {
	        return false;
        }

        public Object getKey()
        {
	        return null;
        }

        public StrategyLegStruct[] getStrategyLegStructs()
        {
	        return null;
        }

        public StrategyLeg[] getStrategyLegs()
        {
	        return strategyLegs;
        }

        public short getStrategyType()
        {
	        return 0;
        }
	} // end inner class DummySessionProduct

    // inner class //////////////////////////////////////////
    public static class DummyStrategyLeg implements StrategyLeg
    {
		
		private int key;
		private int ratio;
		private char side;
		private Product product;
		
        public DummyStrategyLeg(char side, Product product)
        {
            this(1, side, product);
        }
        public DummyStrategyLeg(int ratio, char side, Product product)
        {
            this(0, ratio, side, product);
        }
		public DummyStrategyLeg(int key, int ratio, char side, Product product)
        {
			this.key = key;
			this.ratio = ratio;
			this.side = side;
			this.product = product;
		}
		
        public Product getProduct()
        {
	        return product;
        }

        public int getProductKey()
        {
	        return key;
        }

        public int getRatioQuantity()
        {
	        return ratio;
        }

        public char getSide()
        {
	        return side;
        }
	} // end inner class DummyStrategyLeg

    
    // inner class //////////////////////////////////////////
    public static class DummyOption extends DummyProduct
    {
        public DummyOption(char opType, Price price)
        {
            super(ProductTypes.OPTION, opType, price);
        }
        public DummyOption(char opType, Price price, int month)
        {
            super(ProductTypes.OPTION, opType, price, month);
        }
        public DummyOption(char opType, Price price, int day, int month, int year)
        {
            super(ProductTypes.OPTION, opType, price, day, month, year);
        }
    }


    // inner class //////////////////////////////////////////
    public static class DummyFuture extends DummyProduct
    {
        private static final char  NO_TYPE  = ' ';
        private static final Price NO_PRICE = PriceFactory.getNoPrice();

        public DummyFuture()
        {
            super(ProductTypes.FUTURE, NO_TYPE, NO_PRICE);
        }
        public DummyFuture(int month)
        {
            super(ProductTypes.FUTURE, NO_TYPE, NO_PRICE, month);
        }
        public DummyFuture(int day, int month, int year)
        {
            super(ProductTypes.FUTURE, NO_TYPE, NO_PRICE, day, month, year);
        }
    }


    // inner class //////////////////////////////////////////
    public static class DummyEquity extends DummyProduct
    {
        public DummyEquity(char opType, Price price)
        {
            super(ProductTypes.EQUITY, opType, price);
        }
    }

    public static class DummyProduct implements Product {

        private Price exercisePrice;
		private Date  expirationDate;
        private char  optionType;
        private short productType;

        public DummyProduct(short pType, char opType, Price price)
        {
            this(pType, opType, price, todayMonth);
        }
        public DummyProduct(short pType, char opType, Price price, int month)
        {
            this(pType, opType, price, 1, month, todayYear);
        }
        public DummyProduct(short pType, char opType, Price price, int day, int month, int year)
        {
            productType    = pType;
            optionType     = opType;
            exercisePrice  = price;
            expirationDate = new DateImpl(new DateStruct((byte)month,(byte)day,(short)year));
        }

		public void setExpirationDate(Date date){
			expirationDate = date;
		}

        public boolean isCall()
        {
            return optionType == OptionTypes.CALL;
        }

        public boolean isPut()
        {
            return !isCall();
        }

        public DateStruct getActivationDate()
        {
	        return null;
        }

        public String getCompanyName()
        {
	        return null;
        }

        public boolean getLeapIndicator() {
            return false;
        }

        public ExpirationType getExpirationType() {
            return ExpirationType.STANDARD;
        }

        public DateTimeStruct getCreatedTime()
        {
	        return null;
        }

        public String getDescription()
        {
	        return null;
        }

        public Price getExercisePrice()
        {
            return exercisePrice;
        }

        public Date getExpirationDate()
        {
	        return expirationDate;
        }

        public DateStruct getInactivationDate()
        {
	        return null;
        }

        public DateTimeStruct getLastModifiedTime()
        {
	        return null;
        }

        public short getListingState()
        {
	        return 0;
        }

        public DateStruct getMaturityDate()
        {
	        return null;
        }

        public char getOpraMonthCode()
        {
	        return 0;
        }

        public char getOpraPriceCode()
        {
	        return 0;
        }

        public int getProductKey()
        {
	        return 0;
        }

        public ProductKeysStruct getProductKeysStruct()
        {
	        return null;
        }

        public ProductNameStruct getProductNameStruct()
        {
	        return new ProductNameStruct(null, null, null, optionType, null);
        }

        public ProductStruct getProductStruct()
        {
	        return null;
        }

        public short getProductType()
        {
	        return productType;
        }

        public double getStandardQuantity()
        {
	        return 0;
        }

        public String getUnitMeasure()
        {
	        return null;
        }

        public boolean isAllSelectedProduct()
        {
	        return false;
        }

        public boolean isDefaultProduct()
        {
	        return false;
        }

        public Object getKey()
        {
	        return null;
        }
		
	    public Object clone(){
		    return null;
	    }
    } // end inner class DummyProduct
}

		
