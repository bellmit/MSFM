package com.cboe.application.test;

/**
 * This class calls the Product Query APIs to get Product data to delimited flat files.
 * These files are output:
 *   SimulatorData_ProductClasses.txt
 *   SimulatorData_Products.txt
 *   SimulatorData_ProductTypes.txt
 *   SimulatorData_ReportingClasses.txt
 *   SimulatorData_Strategies.txt
 *
 * These files can then be read by the simulator to create Products.
 * @author Dean Grippo
 * @version 12-01-2000
 */

import com.cboe.exceptions.*;
import com.cboe.idl.cmi.*;
import com.cboe.idl.cmiConstants.ProductTypes;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiQuote.*;
import com.cboe.idl.cmiStrategy.*;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.product.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.infrastructureServices.systemsManagementService.*;
import com.cboe.interfaces.businessServices.*;
//import com.cboe.interfaces.presentation.*;
import java.util.*;
import java.io.*;


public class ProductQueryClient {

    //===============================================================
    // Private variables
    //===============================================================
    private static OutputStreamWriter outStreamProductType    = null;
    private static OutputStreamWriter outStreamProductClass   = null;
    private static OutputStreamWriter outStreamProduct        = null;
    private static OutputStreamWriter outStreamReportingClass = null;
    private static OutputStreamWriter outStreamStrategy       = null;
    private static OutputStreamWriter outStreamFuture         = null;

    //===============================================================
    // Constants
    //===============================================================
    private static final String DELIMITER                 = "\n";
    private static final String PRODUCT_TYPE_STRUCT_FILE  = "SimulatorData_ProductTypes.txt";
    private static final String PRODUCT_CLASS_STRUCT_FILE = "SimulatorData_ProductClasses.txt";
    private static final String PRODUCT_STRUCT_FILE       = "SimulatorData_Products.txt";
    private static final String REPORTING_CLASS_FILE      = "SimulatorData_ReportingClasses.txt";
    private static final String STRATEGY_STRUCT_FILE      = "SimulatorData_Strategies.txt";
    private static final String FUTURE_STRUCT_FILE        = "SimulatorData_Futures.txt";


   //************************************************************************************************
   //                  P U B L I C    M E T H O D S
   //************************************************************************************************


   //************************************************************************************************
   //                  P R I V A T E   M E T H O D S
   //************************************************************************************************

   /**
    * Looks up the product query service.
    *
    * @return com.cboe.interfaces.businessServices.ProductQueryService - A reference to the product query service
    */
    private static ProductQueryService getQueryService()
    {
        HomeFactory homefactoryInstance = null;
        ProductQueryServiceHome home = null;

        homefactoryInstance = HomeFactory.getInstance();

        try
        {
              home = (ProductQueryServiceHome) homefactoryInstance.findHome(ProductQueryServiceHome.HOME_NAME);
        }
        catch (com.cboe.infrastructureServices.foundationFramework.exceptionHandling.CBOELoggableException e)
        {
              throw new NullPointerException("Could not find ProductService home");
        }

        return home.find();
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
              String[] args = {"ProductServiceClient.properties"};
              configService.initialize(args, 0);
              ff.initialize("ApplicationServer", configService);
        }
        catch (Exception e)
        {
              e.printStackTrace();
        }
        catch (Throwable t)
        {
             t.printStackTrace();
        }
    }


    /**
     * Get the product type structs, write them out to a flat file.
     *
     * Preconditions:
     *   None
     * Postconditions:
     *   1. The ProductType structs are converted to name-value strings and written to a file.
     *
     */
    private static void queryProductTypes()
    {
       ProductTypeStruct[]    productTypes = null;

       System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
       System.out.println("Getting Product Types");

       //===============================================================
       // Get the Product Types.
       //===============================================================
        try
        {
              productTypes = getQueryService().getProductTypes();
              System.out.println(" - Got " + productTypes.length + " product types");
        }
        catch (Exception e) {
              System.out.println("Unable to perform query for product types.");
              e.printStackTrace();
        }

       //===============================================================
       // Write the Product Types out to a file
       //===============================================================
       try
       {
             if ( productTypes != null )
             {
                for ( int i=0; i<productTypes.length; i++)
                {
                    writeToTextFile( productTypes[i] );
                }
             }
       }
      catch (Exception e)
      {
            e.printStackTrace();
      }

   }  // end method


    /**
     * Get the product structs, write them out to a flat file.
     *
     * Preconditions:
     *   1. The productKeys parm contains at least one productKey.
     * Postconditions:
     *   1. The Product structs are converted to name-value strings and written to a file.
     */
    private static void queryProducts( int[] productKeys )
    {
       ProductStruct[]    products = null;

       //===============================================================
       // Tell the user what's happening
       //===============================================================
       StringBuffer keyList = new StringBuffer();
       for (int i=0; i<productKeys.length; i++)
       {
           keyList.append( productKeys[i] );
           keyList.append(", ");
       }
       System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
       System.out.println("Getting Products for productKeys=" + keyList );

       //===============================================================
       // Get the Products.
       //===============================================================
        try
        {
              products = getQueryService().getProductsByKey( productKeys );
              System.out.println(" - Got " + products.length + " products");
        }
        catch (Exception e) {
              System.out.println("Unable to perform query for products.");
              e.printStackTrace();
        }

       //===============================================================
       // Write the Products out to a file
       //===============================================================
       try
       {
             if ( products != null )
             {
                for ( int i=0; i<products.length; i++ )
                {
                    writeToTextFile( products[i] );
                }
             }
       }
      catch (Exception e)
      {
            e.printStackTrace();
      }

   }  // end method


    /**
     * Get the strategy struct, write them out to a flat file.
     *
     * Preconditions:
     *   1. The productKey parm has a valid product key.
     * Postconditions:
     *   1. The Strategy structs are converted to name-value strings and written to a file.
     */
    private static void queryStrategy( int productKey )
    {
       StrategyStruct    strategy = null;

       System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
       System.out.println("Getting a strategy for productKey=" + productKey);

       //===============================================================
       // Get the strategy
       //===============================================================
        try
        {
              strategy = getQueryService().getStrategyByKey( productKey );
              if (strategy != null)
              {
                 System.out.println(" - Got strategy with productKey=" + strategy.product.productKeys.productKey + " productType= " + strategy.product.productKeys.productType );
              }
              else
              {
                 System.out.println("Error getting a strategy for productKey=" + productKey);
              }
        }
        catch (Exception e) {
              System.out.println("Unable to perform query for strategy.");
              e.printStackTrace();
        }

       //===============================================================
       // Write the strategy out to a file
       //===============================================================
       try
       {
             if ( strategy != null )
             {
                writeToTextFile( strategy );
             }
       }
      catch (Exception e)
      {
            e.printStackTrace();
      }

   }  // end method


    /**
     * Get the product classes (Options, Strategies, Equitites) for the specified product symbol.
     * Write each out to a flat text file, specified by the outStream class variable.
     *
     * Preconditions:
     *   1. The symbol parm contains a valid symbol.
     * Postconditions:
     *   1. The ProductClass structs are converted to name-value strings and written to a file.
     *   2. The contained Product structs are converted to name-value strings and written to a file.
     *   3. The contained ReportingClass structs are converted to name-value strings and written to a file.
     *
     * @param  String symbol - the product symbol
     */
    private static void queryProductClassStructs( String symbol ) {
       ProductClassStruct[] options    = null;
       ProductClassStruct[] strategies = null;
       ProductClassStruct[] equities   = null;
       ProductClassStruct[] indexes    = null;
       ProductClassStruct[] futures    = null;

       String[] symbols = new String[1];
       symbols[0] = symbol;

       //===============================================================
       // Query for the OPTIONS.
       //===============================================================
       System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
       System.out.println("Getting OPTIONS Product Classes for " + symbol );

       try
       {
             // Get a productClass for options for this symbol
             options = getQueryService().getProductClassesBySymbol( symbols                     // product symbols
                                                                    ,ProductTypes.OPTION        // productType
                                                                    ,true                       // include reporting classes
                                                                    ,true                       // include products
                                                                    ,false);                    // include active only
       }
       catch (DataValidationException dve)
       {
             System.out.println("Got no Options Product Classes");
       }
       catch (Exception e)
       {
             e.printStackTrace();
       }

       // Let the user know what happened
       if ( options != null )
       {
            System.out.println(" - Got " + options.length + " Options Product Classes" );
            for (int i=0; i < options.length; i++)
            {
                 System.out.println("\t Option[" + i + "] classKey=" + options[i].info.classKey +
                                    " symbol=" + options[i].info.classSymbol +
                                    " has " + options[i].products.length + " products" );

                 for (int j=0; j<options[i].products.length; j++)
                 {
                      System.out.println("\t\t Option[" + i + "].products[" + j + "].productKey = " + options[i].products[j].productKeys.productKey);
                 }
                 for (int j=0; j<options[i].info.reportingClasses.length; j++)
                 {
                      System.out.println("\t\t Option[" + i + "].reportingClasses[" + j + "].classKey = " + options[i].info.reportingClasses[j].classKey +
                                         " productClassKey = " + options[i].info.reportingClasses[j].productClassKey );
                 }
            }
       }

       //===============================================================
       // Query for the FUTURE.
       //===============================================================
       System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
       System.out.println("Getting OPTIONS Product Classes for " + symbol );

       try
       {
             // Get a productClass for options for this symbol
             futures = getQueryService().getProductClassesBySymbol( symbols                     // product symbols
                                                                    ,ProductTypes.FUTURE        // productType
                                                                    ,true                       // include reporting classes
                                                                    ,true                       // include products
                                                                    ,false);                    // include active only
       }
       catch (DataValidationException dve)
       {
             System.out.println("Got no Future Product Classes");
       }
       catch (Exception e)
       {
             e.printStackTrace();
       }

       // Let the user know what happened
       if ( futures != null )
       {
            System.out.println(" - Got " + futures.length + " Future Product Classes" );
            for (int i=0; i < futures.length; i++)
            {
                 System.out.println("\t Option[" + i + "] classKey=" + futures[i].info.classKey +
                                    " symbol=" + futures[i].info.classSymbol +
                                    " has " + futures[i].products.length + " products" );

                 for (int j=0; j<futures[i].products.length; j++)
                 {
                      System.out.println("\t\t Future[" + i + "].products[" + j + "].productKey = " + futures[i].products[j].productKeys.productKey);
                 }
                 for (int j=0; j<futures[i].info.reportingClasses.length; j++)
                 {
                      System.out.println("\t\t Future[" + i + "].reportingClasses[" + j + "].classKey = " + futures[i].info.reportingClasses[j].classKey +
                                         " productClassKey = " + futures[i].info.reportingClasses[j].productClassKey );
                 }
            }
       }

       //===============================================================
       // Query for the STRATEGIES.
       //===============================================================
       System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
       System.out.println("Getting STRATEGIES Product Classes for " + symbol );

       try
       {
             // Get a productClass for strategies for this symbol
             strategies = getQueryService().getProductClassesBySymbol(  symbols                 // product symbols
                                                                        ,ProductTypes.STRATEGY  // productType
                                                                        ,true                   // include reporting classes
                                                                        ,true                   // include products
                                                                        ,false);                // include active only
       }
       catch (DataValidationException dve)
       {
             System.out.println("Got no Strategies Product Classes");
       }
       catch (Exception e)
       {
             e.printStackTrace();
       }

       // Let the user know what happened
       if ( strategies != null )
       {
            System.out.println(" - Got " + strategies.length + " Strategies Product Classes" );
            for (int i=0; i < strategies.length; i++)
            {
                 System.out.println("\t Strategy[" + i + "] classKey=" + strategies[i].info.classKey +
                                    " symbol=" + strategies[i].info.classSymbol +
                                    " has " + strategies[i].products.length + " products" );

                 for (int j=0; j<strategies[i].products.length; j++)
                 {
                      System.out.println("\t\t Strategy[" + i + "].products[" + j + "].productKey = " + strategies[i].products[j].productKeys.productKey);
                 }
                 for (int j=0; j<strategies[i].info.reportingClasses.length; j++)
                 {
                      System.out.println("\t\t Strategy[" + i + "].info.reportingClasses[" + j + "].classKey = " + strategies[i].info.reportingClasses[j].classKey +
                                         " productClassKey = " + strategies[i].info.reportingClasses[j].productClassKey );
                 }
            }
       }

       //===============================================================
       // Query for the EQUITIES.
       //===============================================================
       // Index products don't have equities, so check for these.

       if ( symbol.compareTo("DJX") != 0 && symbol.compareTo("SPX") != 0 && symbol.compareTo("OEX") != 0 )
       {
            System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
            System.out.println("Getting EQUITIES Product Classes for " + symbol );

            try
            {
                  // Get a productClass for equities for this symbol
                  equities = getQueryService().getProductClassesBySymbol(  symbols                 // product symbols
                                                                           ,ProductTypes.EQUITY    // productType
                                                                           ,true                   // include reporting classes
                                                                           ,true                   // include products
                                                                           ,false);                // include active only
            }
            catch (DataValidationException dve)
            {
                  System.out.println("Got no Equities Product Classes");
            }
            catch (Exception e)
            {
                  e.printStackTrace();
            }

            // Let the user know what happened
            if ( equities != null )
            {
                 System.out.println(" - Got " + equities.length + " Equities Product Classes" );
                 for (int i=0; i < equities.length; i++)
                 {
                      System.out.println("\t Equity[" + i + "] classKey=" + equities[i].info.classKey +
                                         " symbol=" + equities[i].info.classSymbol +
                                         " has " + equities[i].products.length + " products" );

                      //com.cboe.application.test.ReflectiveStructTester.printStruct(equities[i],"equities["+i+"]");
                      for (int j=0; j<equities[i].products.length; j++)
                      {
                           System.out.println("\t\t Equity[" + i + "].products[" + j + "].productKey = " + equities[i].products[j].productKeys.productKey);
                      }
                      for (int j=0; j<equities[i].info.reportingClasses.length; j++)
                      {
                           System.out.println("\t\t Equity[" + i + "].info.reportingClasses[" + j + "].classKey = " + equities[i].info.reportingClasses[j].classKey +
                                              " productClassKey = " + equities[i].info.reportingClasses[j].productClassKey );
                      }

                 }
            }
       }  // end equities


       //===============================================================
       // Do we have any INDEX products? (SPX, DJX or OEX). If so, query for them.
       //===============================================================
       if ( symbol.compareTo("DJX") == 0 || symbol.compareTo("SPX") == 0  || symbol.compareTo("OEX") == 0)
       {
           System.out.println("-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-");
           System.out.println("Getting INDEX Product Classes for " + symbol );

           try
           {
                 // Get a productClass for indexes for this symbol
                 indexes = getQueryService().getProductClassesBySymbol(  symbols                 // product symbols
                                                                         ,ProductTypes.INDEX     // productType
                                                                         ,true                   // include reporting classes
                                                                         ,true                   // include products
                                                                         ,false);                // include active only
           }
           catch (DataValidationException dve)
           {
                 System.out.println("Got no Index Product Classes");
           }
           catch (Exception e)
           {
                 e.printStackTrace();
           }

           // Let the user know what happened
           if ( indexes != null )
           {
                System.out.println(" - Got " + indexes.length + " Index Product Classes" );

                for (int i=0; i < indexes.length; i++)
                {
                     System.out.println("\t Index[" + i + "] classKey=" + indexes[i].info.classKey +
                                        " symbol=" + indexes[i].info.classSymbol +
                                        " has " + indexes[i].products.length + " products" );

                     //com.cboe.application.test.ReflectiveStructTester.printStruct(indexes[i],"indexes["+i+"]");
                     for (int j=0; j<indexes[i].products.length; j++)
                     {
                          System.out.println("\t\t Index[" + i + "].products[" + j + "].productKey = " + indexes[i].products[j].productKeys.productKey);
                     }
                     for (int j=0; j<indexes[i].info.reportingClasses.length; j++)
                     {
                          System.out.println("\t\t Index[" + i + "].info.reportingClasses[" + j + "].classKey = " + indexes[i].info.reportingClasses[j].classKey +
                                             " productClassKey = " + indexes[i].info.reportingClasses[j].productClassKey );
                     }

                } // end for
           } // end if indexes != null
       }  // end if index products


       //===============================================================
       // Query for the underlying products for all options
       //===============================================================
       if ( options != null )
       {
           int[] keys = new int[options.length];

           for (int i=0; i<options.length; i++)
           {
               keys[i] = options[i].info.underlyingProduct.productKeys.productKey;
           }

           queryProducts( keys );
       }

       //===============================================================
       // Write the product class structs to files.
       //===============================================================
      try
      {
           if ( options != null )
           {
                writeProductClass( options );
           }
           if ( strategies != null )
           {
                writeProductClass( strategies );
           }
           if ( equities != null )
           {
                writeProductClass( equities );
           }
           if ( indexes != null )
           {
                writeProductClass( indexes );
           }
           if ( futures != null )
           {
                writeProductClass( futures );
           }
      }
      catch (Exception e)
      {
            e.printStackTrace();
      }

    } // end of method






   /**
     * Produce a delimited string from the individual fields of the specified ProductTypeStruct and write it to a file.
     *
     * Preconditions:
     *   1. The ProductTypeStruct parm is not null.
     * Postconditions:
     *   1. The ProductType struct is converted to name-value strings and written to a file.
     *
     * @param com.cboe.idl.cmiProduct.ProductTypeStruct productType
     */
   private static void writeToTextFile( ProductTypeStruct productType )
   {
      try
      {
           //com.cboe.application.test.ReflectiveStructTester.printStruct(productType,"productType");
           StringBuffer buffer = new StringBuffer(1024);

           buffer.append( "<ProductType>" );
           buffer.append( DELIMITER + "createdTime.date.day="           + productType.createdTime.date.day );
           buffer.append( DELIMITER + "createdTime.date.month="         + productType.createdTime.date.month  );
           buffer.append( DELIMITER + "createdTime.date.year="          + productType.createdTime.date.year  );
           buffer.append( DELIMITER + "createdTime.time.hour="          + productType.createdTime.time.hour  );
           buffer.append( DELIMITER + "createdTime.time.minute="        + productType.createdTime.time.minute  );
           buffer.append( DELIMITER + "createdTime.time.second="        + productType.createdTime.time.second  );
           buffer.append( DELIMITER + "createdTime.time.fraction="      + productType.createdTime.time.fraction  );
           buffer.append( DELIMITER + "description="                    + productType.description );
           buffer.append( DELIMITER + "lastModifiedTime.date.day="      + productType.lastModifiedTime.date.day );
           buffer.append( DELIMITER + "lastModifiedTime.date.month="    + productType.lastModifiedTime.date.month );
           buffer.append( DELIMITER + "lastModifiedTime.date.year="     + productType.lastModifiedTime.date.year );
           buffer.append( DELIMITER + "lastModifiedTime.time.hour="     + productType.lastModifiedTime.time.hour );
           buffer.append( DELIMITER + "lastModifiedTime.time.minute="   + productType.lastModifiedTime.time.minute );
           buffer.append( DELIMITER + "lastModifiedTime.time.second="   + productType.lastModifiedTime.time.second );
           buffer.append( DELIMITER + "lastModifiedTime.time.fraction=" + productType.lastModifiedTime.time.fraction );
           buffer.append( DELIMITER + "name="                           + productType.name );
           buffer.append( DELIMITER + "type="                           + productType.type );
           buffer.append( "\n</ProductType>\n" );

           outStreamProductType.write( buffer.toString() );
     }
     catch (Exception e)
     {
          e.printStackTrace();
     }
   }


   /**
     * Produce a delimited string from the individual fields of the specified StrategyStruct and write it to a file.
     *
     * Preconditions:
     *   1. The StrategyStruct parm is not null.
     * Postconditions:
     *   1. The Strategy struct is converted to name-value strings and written to a file.
     * @param com.cboe.idl.cmiStrategy.Strategy strategy
     */
   private static void writeToTextFile( StrategyStruct strategy )
   {
      try
      {
           //com.cboe.application.test.ReflectiveStructTester.printStruct(strategy,"strategy");
           StringBuffer buffer = new StringBuffer(1024);

           buffer.append( "<Strategy>" );
           buffer.append( DELIMITER + "product.activationDate.day="                 + strategy.product.activationDate.day );
           buffer.append( DELIMITER + "product.activationDate.month="               + strategy.product.activationDate.month );
           buffer.append( DELIMITER + "product.activationDate.year="                + strategy.product.activationDate.year );
           buffer.append( DELIMITER + "product.companyName="                        + strategy.product.companyName );
           buffer.append( DELIMITER + "product.createdTime.date.day="               + strategy.product.createdTime.date.day );
           buffer.append( DELIMITER + "product.createdTime.date.month="             + strategy.product.createdTime.date.month );
           buffer.append( DELIMITER + "product.createdTime.date.year="              + strategy.product.createdTime.date.year );
           buffer.append( DELIMITER + "product.createdTime.time.hour="              + strategy.product.createdTime.time.hour );
           buffer.append( DELIMITER + "product.createdTime.time.minute="            + strategy.product.createdTime.time.minute );
           buffer.append( DELIMITER + "product.createdTime.time.second="            + strategy.product.createdTime.time.second );
           buffer.append( DELIMITER + "product.createdTime.time.fraction="          + strategy.product.createdTime.time.fraction );
           buffer.append( DELIMITER + "product.description="                        + strategy.product.description );
           buffer.append( DELIMITER + "product.inactivationDate.day="               + strategy.product.inactivationDate.day );
           buffer.append( DELIMITER + "product.inactivationDate.month="             + strategy.product.inactivationDate.month );
           buffer.append( DELIMITER + "product.inactivationDate.year="              + strategy.product.inactivationDate.year );
           buffer.append( DELIMITER + "product.lastModifiedTime.date.day="          + strategy.product.lastModifiedTime.date.day );
           buffer.append( DELIMITER + "product.lastModifiedTime.date.month="        + strategy.product.lastModifiedTime.date.month );
           buffer.append( DELIMITER + "product.lastModifiedTime.date.year="         + strategy.product.lastModifiedTime.date.year );
           buffer.append( DELIMITER + "product.lastModifiedTime.time.hour="         + strategy.product.lastModifiedTime.time.hour );
           buffer.append( DELIMITER + "product.lastModifiedTime.time.minute="       + strategy.product.lastModifiedTime.time.minute );
           buffer.append( DELIMITER + "product.lastModifiedTime.time.second="       + strategy.product.lastModifiedTime.time.second );
           buffer.append( DELIMITER + "product.lastModifiedTime.time.fraction="     + strategy.product.lastModifiedTime.time.fraction );
           buffer.append( DELIMITER + "product.listingState="                       + strategy.product.listingState );
           buffer.append( DELIMITER + "product.maturityDate.day="                   + strategy.product.maturityDate.day );
           buffer.append( DELIMITER + "product.maturityDate.month="                 + strategy.product.maturityDate.month );
           buffer.append( DELIMITER + "product.maturityDate.year="                  + strategy.product.maturityDate.year );
           buffer.append( DELIMITER + "product.opraMonthCode="                      + strategy.product.opraMonthCode );
           buffer.append( DELIMITER + "product.opraPriceCode="                      + strategy.product.opraPriceCode );
           buffer.append( DELIMITER + "product.productKeys.classKey="               + strategy.product.productKeys.classKey );
           buffer.append( DELIMITER + "product.productKeys.productKey="             + strategy.product.productKeys.productKey );
           buffer.append( DELIMITER + "product.productKeys.productType="            + strategy.product.productKeys.productType );
           buffer.append( DELIMITER + "product.productKeys.reportingClass="         + strategy.product.productKeys.reportingClass );
           buffer.append( DELIMITER + "product.productName.exercisePrice.type="     + strategy.product.productName.exercisePrice.type );
           buffer.append( DELIMITER + "product.productName.exercisePrice.whole="    + strategy.product.productName.exercisePrice.whole );
           buffer.append( DELIMITER + "product.productName.exercisePrice.fraction=" + strategy.product.productName.exercisePrice.fraction );
           buffer.append( DELIMITER + "product.productName.expirationDate.day="     + strategy.product.productName.expirationDate.day );
           buffer.append( DELIMITER + "product.productName.expirationDate.month="   + strategy.product.productName.expirationDate.month );
           buffer.append( DELIMITER + "product.productName.expirationDate.year="    + strategy.product.productName.expirationDate.year );
           buffer.append( DELIMITER + "product.productName.optionType="             + strategy.product.productName.optionType );
           buffer.append( DELIMITER + "product.productName.productSymbol="          + strategy.product.productName.productSymbol );
           buffer.append( DELIMITER + "product.productName.reportingClass="         + strategy.product.productName.reportingClass );
           buffer.append( DELIMITER + "product.standardQuantity="                   + strategy.product.standardQuantity );
           buffer.append( DELIMITER + "product.unitMeasure="                        + strategy.product.unitMeasure );
           buffer.append( DELIMITER + "strategyType="                               + strategy.strategyType);

           for (int i=0; i<strategy.strategyLegs.length; i++)
           {
                buffer.append( DELIMITER + "strategyLegs[" + i + "].product="           + strategy.strategyLegs[i].product );
                buffer.append( DELIMITER + "strategyLegs[" + i + "].ratioQuantity="     + strategy.strategyLegs[i].ratioQuantity );
                buffer.append( DELIMITER + "strategyLegs[" + i + "].side="              + strategy.strategyLegs[i].side );
           }

           buffer.append( "\n</Strategy>\n" );

           outStreamStrategy.write( buffer.toString() );
      }
      catch (Exception e)
      {
            e.printStackTrace();
      }
   }


    /**
     * Produce a delimited string from the individual fields of the specified ProductClassStruct.
     *
     * Preconditions:
     *   1. The ProductClassStructs parm is not null.
     * Postconditions:
     *   1. The ProductClass structs are converted to name-value strings and written to a file. This
     *      includes its contained products and reporting classes.
     *
     * @param com.cboe.idl.cmiProduct.ProductClassStruct[] productClass
     */
    private static void writeProductClass( ProductClassStruct[] productClasses )
    {

      try
      {
           for (int i = 0; i < productClasses.length; i++)
           {
               StringBuffer buffer = new StringBuffer(8192);

               // Info struct
               buffer.append( "<ProductClass>" );
               buffer.append( DELIMITER + "sessionCode="                      + productClasses[i].sessionCode );
               buffer.append( DELIMITER + "info.activationDate.day="          + productClasses[i].info.activationDate.day );
               buffer.append( DELIMITER + "info.activationDate.month="        + productClasses[i].info.activationDate.month );
               buffer.append( DELIMITER + "info.activationDate.year="         + productClasses[i].info.activationDate.year );
               buffer.append( DELIMITER + "info.classKey="                    + productClasses[i].info.classKey );
               buffer.append( DELIMITER + "info.classSymbol="                 + productClasses[i].info.classSymbol );
               buffer.append( DELIMITER + "info.createdTime.date.day="        + productClasses[i].info.createdTime.date.day );
               buffer.append( DELIMITER + "info.createdTime.date.month="      + productClasses[i].info.createdTime.date.month );
               buffer.append( DELIMITER + "info.createdTime.date.year="       + productClasses[i].info.createdTime.date.year );
               buffer.append( DELIMITER + "info.createdTime.time.hour="       + productClasses[i].info.createdTime.time.hour );
               buffer.append( DELIMITER + "info.createdTime.time.minute="     + productClasses[i].info.createdTime.time.minute );
               buffer.append( DELIMITER + "info.createdTime.time.second="     + productClasses[i].info.createdTime.time.second );
               buffer.append( DELIMITER + "info.createdTime.time.fraction="   + productClasses[i].info.createdTime.time.fraction );
               buffer.append( DELIMITER + "info.epwFastMarketMultiplier="     + productClasses[i].info.epwFastMarketMultiplier );

               for (int j=0; j<productClasses[i].info.epwValues.length; j++)
               {
                   buffer.append( DELIMITER + "info.epwValues[" + j + "].maximumAllowableSpread=" + productClasses[i].info.epwValues[j].maximumAllowableSpread );
                   buffer.append( DELIMITER + "info.epwValues[" + j + "].maximumBidRange="         + productClasses[i].info.epwValues[j].maximumBidRange );
                   buffer.append( DELIMITER + "info.epwValues[" + j + "].minimumBidRange="         + productClasses[i].info.epwValues[j].minimumBidRange );
               }

               buffer.append( DELIMITER + "info.inactivationDate.day="            + productClasses[i].info.inactivationDate.day );
               buffer.append( DELIMITER + "info.inactivationDate.month="          + productClasses[i].info.inactivationDate.month );
               buffer.append( DELIMITER + "info.inactivationDate.year="           + productClasses[i].info.inactivationDate.year );
               buffer.append( DELIMITER + "info.lastModifiedTime.date.day="       + productClasses[i].info.lastModifiedTime.date.day );
               buffer.append( DELIMITER + "info.lastModifiedTime.date.month="     + productClasses[i].info.lastModifiedTime.date.month );
               buffer.append( DELIMITER + "info.lastModifiedTime.date.year="      + productClasses[i].info.lastModifiedTime.date.year );
               buffer.append( DELIMITER + "info.lastModifiedTime.time.hour="      + productClasses[i].info.lastModifiedTime.time.hour );
               buffer.append( DELIMITER + "info.lastModifiedTime.time.minute="    + productClasses[i].info.lastModifiedTime.time.minute );
               buffer.append( DELIMITER + "info.lastModifiedTime.time.second="    + productClasses[i].info.lastModifiedTime.time.second );
               buffer.append( DELIMITER + "info.lastModifiedTime.time.fraction="  + productClasses[i].info.lastModifiedTime.time.fraction );
               buffer.append( DELIMITER + "info.listingState="                    + productClasses[i].info.listingState );
               buffer.append( DELIMITER + "info.primaryExchange="                 + productClasses[i].info.primaryExchange );
               //
               buffer.append( DELIMITER + "info.productDescription.baseDescriptionName="                  + productClasses[i].info.productDescription.baseDescriptionName );
               buffer.append( DELIMITER + "info.productDescription.maxStrikePrice.type="                  + productClasses[i].info.productDescription.maxStrikePrice.type );
               buffer.append( DELIMITER + "info.productDescription.maxStrikePrice.whole="                 + productClasses[i].info.productDescription.maxStrikePrice.whole );
               buffer.append( DELIMITER + "info.productDescription.maxStrikePrice.fraction="              + productClasses[i].info.productDescription.maxStrikePrice.fraction );
               buffer.append( DELIMITER + "info.productDescription.minimumAbovePremiumFraction.type="     + productClasses[i].info.productDescription.minimumAbovePremiumFraction.type );
               buffer.append( DELIMITER + "info.productDescription.minimumAbovePremiumFraction.whole="    + productClasses[i].info.productDescription.minimumAbovePremiumFraction.whole );
               buffer.append( DELIMITER + "info.productDescription.minimumAbovePremiumFraction.fraction=" + productClasses[i].info.productDescription.minimumAbovePremiumFraction.fraction );
               buffer.append( DELIMITER + "info.productDescription.minimumBelowPremiumFraction.type="     + productClasses[i].info.productDescription.minimumBelowPremiumFraction.type );
               buffer.append( DELIMITER + "info.productDescription.minimumBelowPremiumFraction.whole="    + productClasses[i].info.productDescription.minimumBelowPremiumFraction.whole );
               buffer.append( DELIMITER + "info.productDescription.minimumBelowPremiumFraction.fraction=" + productClasses[i].info.productDescription.minimumBelowPremiumFraction.fraction );
               buffer.append( DELIMITER + "info.productDescription.minimumStrikePriceFraction.type="      + productClasses[i].info.productDescription.minimumStrikePriceFraction.type );
               buffer.append( DELIMITER + "info.productDescription.minimumStrikePriceFraction.whole="     + productClasses[i].info.productDescription.minimumStrikePriceFraction.whole );
               buffer.append( DELIMITER + "info.productDescription.minimumStrikePriceFraction.fraction="  + productClasses[i].info.productDescription.minimumStrikePriceFraction.fraction );
               buffer.append( DELIMITER + "info.productDescription.name="                                 + productClasses[i].info.productDescription.name );
               buffer.append( DELIMITER + "info.productDescription.premiumBreakPoint.type="               + productClasses[i].info.productDescription.premiumBreakPoint.type );
               buffer.append( DELIMITER + "info.productDescription.premiumBreakPoint.whole="              + productClasses[i].info.productDescription.premiumBreakPoint.whole );
               buffer.append( DELIMITER + "info.productDescription.premiumBreakPoint.fraction="           + productClasses[i].info.productDescription.premiumBreakPoint.fraction );
               buffer.append( DELIMITER + "info.productDescription.premiumPriceFormat="                   + productClasses[i].info.productDescription.premiumPriceFormat );
               buffer.append( DELIMITER + "info.productDescription.priceDisplayType="                     + productClasses[i].info.productDescription.priceDisplayType );
               buffer.append( DELIMITER + "info.productDescription.strikePriceFormat="                    + productClasses[i].info.productDescription.strikePriceFormat );
               buffer.append( DELIMITER + "info.productDescription.underlyingPriceFormat="                + productClasses[i].info.productDescription.underlyingPriceFormat );
               buffer.append( DELIMITER + "info.productType="                                             + productClasses[i].info.productType );
               buffer.append( DELIMITER + "info.underlyingProduct.activationDate.day="                    + productClasses[i].info.underlyingProduct.activationDate.day );
               buffer.append( DELIMITER + "info.underlyingProduct.activationDate.month="                  + productClasses[i].info.underlyingProduct.activationDate.month );
               buffer.append( DELIMITER + "info.underlyingProduct.activationDate.year="                   + productClasses[i].info.underlyingProduct.activationDate.year );
               buffer.append( DELIMITER + "info.underlyingProduct.companyName="                           + productClasses[i].info.underlyingProduct.companyName );
               buffer.append( DELIMITER + "info.underlyingProduct.createdTime.date.day="                  + productClasses[i].info.underlyingProduct.createdTime.date.day );
               buffer.append( DELIMITER + "info.underlyingProduct.createdTime.date.month="                + productClasses[i].info.underlyingProduct.createdTime.date.month );
               buffer.append( DELIMITER + "info.underlyingProduct.createdTime.date.year="                 + productClasses[i].info.underlyingProduct.createdTime.date.year );
               buffer.append( DELIMITER + "info.underlyingProduct.createdTime.time.hour="                 + productClasses[i].info.underlyingProduct.createdTime.time.hour );
               buffer.append( DELIMITER + "info.underlyingProduct.createdTime.time.minute="               + productClasses[i].info.underlyingProduct.createdTime.time.minute );
               buffer.append( DELIMITER + "info.underlyingProduct.createdTime.time.second="               + productClasses[i].info.underlyingProduct.createdTime.time.second );
               buffer.append( DELIMITER + "info.underlyingProduct.createdTime.time.fraction="             + productClasses[i].info.underlyingProduct.createdTime.time.fraction );
               buffer.append( DELIMITER + "info.underlyingProduct.description="                           + productClasses[i].info.underlyingProduct.description );
               buffer.append( DELIMITER + "info.underlyingProduct.inactivationDate.day="                  + productClasses[i].info.underlyingProduct.inactivationDate.day );
               buffer.append( DELIMITER + "info.underlyingProduct.inactivationDate.month="                + productClasses[i].info.underlyingProduct.inactivationDate.month );
               buffer.append( DELIMITER + "info.underlyingProduct.inactivationDate.year="                 + productClasses[i].info.underlyingProduct.inactivationDate.year );
               buffer.append( DELIMITER + "info.underlyingProduct.lastModifiedTime.date.day="             + productClasses[i].info.underlyingProduct.lastModifiedTime.date.day );
               buffer.append( DELIMITER + "info.underlyingProduct.lastModifiedTime.date.month="           + productClasses[i].info.underlyingProduct.lastModifiedTime.date.month );
               buffer.append( DELIMITER + "info.underlyingProduct.lastModifiedTime.date.year="            + productClasses[i].info.underlyingProduct.lastModifiedTime.date.year );
               buffer.append( DELIMITER + "info.underlyingProduct.lastModifiedTime.time.hour="            + productClasses[i].info.underlyingProduct.lastModifiedTime.time.hour );
               buffer.append( DELIMITER + "info.underlyingProduct.lastModifiedTime.time.minute="          + productClasses[i].info.underlyingProduct.lastModifiedTime.time.minute );
               buffer.append( DELIMITER + "info.underlyingProduct.lastModifiedTime.time.second="          + productClasses[i].info.underlyingProduct.lastModifiedTime.time.second );
               buffer.append( DELIMITER + "info.underlyingProduct.lastModifiedTime.time.fraction="        + productClasses[i].info.underlyingProduct.lastModifiedTime.time.fraction );
               buffer.append( DELIMITER + "info.underlyingProduct.listingState="                          + productClasses[i].info.underlyingProduct.listingState );
               buffer.append( DELIMITER + "info.underlyingProduct.maturityDate.day="                      + productClasses[i].info.underlyingProduct.maturityDate.day );
               buffer.append( DELIMITER + "info.underlyingProduct.maturityDate.month="                    + productClasses[i].info.underlyingProduct.maturityDate.month );
               buffer.append( DELIMITER + "info.underlyingProduct.maturityDate.year="                     + productClasses[i].info.underlyingProduct.maturityDate.year );
               buffer.append( DELIMITER + "info.underlyingProduct.opraMonthCode="                         + productClasses[i].info.underlyingProduct.opraMonthCode );
               buffer.append( DELIMITER + "info.underlyingProduct.opraPriceCode="                         + productClasses[i].info.underlyingProduct.opraPriceCode );
               buffer.append( DELIMITER + "info.underlyingProduct.productKeys.classKey="                  + productClasses[i].info.underlyingProduct.productKeys.classKey );
               buffer.append( DELIMITER + "info.underlyingProduct.productKeys.productKey="                + productClasses[i].info.underlyingProduct.productKeys.productKey );
               buffer.append( DELIMITER + "info.underlyingProduct.productKeys.productType="               + productClasses[i].info.underlyingProduct.productKeys.productType );
               buffer.append( DELIMITER + "info.underlyingProduct.productKeys.reportingClass="            + productClasses[i].info.underlyingProduct.productKeys.reportingClass );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.exercisePrice.type="        + productClasses[i].info.underlyingProduct.productName.exercisePrice.type );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.exercisePrice.whole="       + productClasses[i].info.underlyingProduct.productName.exercisePrice.whole );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.exercisePrice.fraction="    + productClasses[i].info.underlyingProduct.productName.exercisePrice.fraction );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.expirationDate.day="        + productClasses[i].info.underlyingProduct.productName.expirationDate.day );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.expirationDate.month="      + productClasses[i].info.underlyingProduct.productName.expirationDate.month );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.expirationDate.year="       + productClasses[i].info.underlyingProduct.productName.expirationDate.year );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.optionType="                + productClasses[i].info.underlyingProduct.productName.optionType );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.productSymbol="             + productClasses[i].info.underlyingProduct.productName.productSymbol );
               buffer.append( DELIMITER + "info.underlyingProduct.productName.reportingClass="            + productClasses[i].info.underlyingProduct.productName.reportingClass );
               buffer.append( DELIMITER + "info.underlyingProduct.standardQuantity="                      + productClasses[i].info.underlyingProduct.standardQuantity );
               buffer.append( DELIMITER + "info.underlyingProduct.unitMeasure="                           + productClasses[i].info.underlyingProduct.unitMeasure );
               buffer.append( "\n</ProductClass>\n" );

               outStreamProductClass.write( buffer.toString() );


               // Process its contained Product structs
               for (int k=0; k<productClasses[i].products.length; k++)
               {
                   writeToTextFile( productClasses[i].products[k] );

                   if ( productClasses[i].products[k].productKeys.productType == ProductTypes.STRATEGY )
                   {
                        queryStrategy( productClasses[i].products[k].productKeys.productKey );
                   }
               }


               // Process its contained Reporting Class structs
               for (int l=0; l<productClasses[i].info.reportingClasses.length; l++)
               {
                   //com.cboe.application.test.ReflectiveStructTester.printStruct( productClasses[i].reportingClasses[l]," productClasses[" + i + "].reportingClass[" + l + "]");
                   writeToTextFile( productClasses[i].info.reportingClasses[l] );
               }

           } // end for

      }
      catch (Exception e)
      {
            e.printStackTrace();
      }


    }  // end method



    /**
     * Produce a delimited string from the individual fields of the specified ProductStruct.
     *
     * Preconditions:
     *   1. The ProductStruct parm is not null.
     * Postconditions:
     *   1. The Product struct is converted to name-value strings and written to a file.
     *
     * @param com.cboe.idl.cmiProduct.ProductStruct productStruct
     */
    private static void writeToTextFile( ProductStruct product )
    {

      try
      {
           // Convert each ProductStruct to a string and write it to the file.
           StringBuffer buffer = new StringBuffer(8192);

           buffer.append( "<Product>");
           buffer.append( DELIMITER + "activationDate.day="                 + product.activationDate.day );
           buffer.append( DELIMITER + "activationDate.month="               + product.activationDate.month );
           buffer.append( DELIMITER + "activationDate.year="                + product.activationDate.year );
           buffer.append( DELIMITER + "companyName="                        + product.companyName );
           buffer.append( DELIMITER + "createdTime.date.day="               + product.createdTime.date.day );
           buffer.append( DELIMITER + "createdTime.date.month="             + product.createdTime.date.month );
           buffer.append( DELIMITER + "createdTime.date.year="              + product.createdTime.date.year );
           buffer.append( DELIMITER + "createdTime.time.hour="              + product.createdTime.time.hour );
           buffer.append( DELIMITER + "createdTime.time.minute="            + product.createdTime.time.minute );
           buffer.append( DELIMITER + "createdTime.time.second="            + product.createdTime.time.second );
           buffer.append( DELIMITER + "createdTime.time.fraction="          + product.createdTime.time.fraction );
           buffer.append( DELIMITER + "description="                        + product.description );
           buffer.append( DELIMITER + "inactivationDate.day="               + product.inactivationDate.day );
           buffer.append( DELIMITER + "inactivationDate.month="             + product.inactivationDate.month );
           buffer.append( DELIMITER + "inactivationDate.year="              + product.inactivationDate.year );
           buffer.append( DELIMITER + "lastModifiedTime.date.day="          + product.lastModifiedTime.date.day );
           buffer.append( DELIMITER + "lastModifiedTime.date.month="        + product.lastModifiedTime.date.month );
           buffer.append( DELIMITER + "lastModifiedTime.date.year="         + product.lastModifiedTime.date.year );
           buffer.append( DELIMITER + "lastModifiedTime.time.hour="         + product.lastModifiedTime.time.hour );
           buffer.append( DELIMITER + "lastModifiedTime.time.minute="       + product.lastModifiedTime.time.minute );
           buffer.append( DELIMITER + "lastModifiedTime.time.second="       + product.lastModifiedTime.time.second );
           buffer.append( DELIMITER + "lastModifiedTime.time.fraction="     + product.lastModifiedTime.time.fraction );
           buffer.append( DELIMITER + "listingState="                       + product.listingState );
           buffer.append( DELIMITER + "maturityDate.day="                   + product.maturityDate.day );
           buffer.append( DELIMITER + "maturityDate.month="                 + product.maturityDate.month );
           buffer.append( DELIMITER + "maturityDate.year="                  + product.maturityDate.year );
           buffer.append( DELIMITER + "opraMonthCode="                      + product.opraMonthCode );
           buffer.append( DELIMITER + "opraPriceCode="                      + product.opraPriceCode );
           buffer.append( DELIMITER + "productKeys.classKey="               + product.productKeys.classKey );
           buffer.append( DELIMITER + "productKeys.productKey="             + product.productKeys.productKey );
           buffer.append( DELIMITER + "productKeys.productType="            + product.productKeys.productType );
           buffer.append( DELIMITER + "productKeys.reportingClass="         + product.productKeys.reportingClass );
           buffer.append( DELIMITER + "productName.exercisePrice.type="     + product.productName.exercisePrice.type );
           buffer.append( DELIMITER + "productName.exercisePrice.whole="    + product.productName.exercisePrice.whole );
           buffer.append( DELIMITER + "productName.exercisePrice.fraction=" + product.productName.exercisePrice.fraction );
           buffer.append( DELIMITER + "productName.expirationDate.day="     + product.productName.expirationDate.day );
           buffer.append( DELIMITER + "productName.expirationDate.month="   + product.productName.expirationDate.month );
           buffer.append( DELIMITER + "productName.expirationDate.year="    + product.productName.expirationDate.year );
           buffer.append( DELIMITER + "productName.optionType="             + product.productName.optionType );
           buffer.append( DELIMITER + "productName.productSymbol="          + product.productName.productSymbol );
           buffer.append( DELIMITER + "productName.reportingClass="         + product.productName.reportingClass );
           buffer.append( DELIMITER + "standardQuantity="                   + product.standardQuantity );
           buffer.append( DELIMITER + "unitMeasure="                        + product.unitMeasure );
           buffer.append( "\n</Product>\n");

           if (product.productKeys.productType == ProductTypes.FUTURE)
               outStreamFuture.write( buffer.toString() );
           else
               outStreamProduct.write( buffer.toString() );
      }
      catch (Exception e)
      {
            e.printStackTrace();
      }

    }  // end method


    /**
     * Produce a delimited string from the individual fields of the specified ReportingClassStruct.
     *
     * Preconditions:
     *   1. The ReportingClass Struct parm is not null.
     * Postconditions:
     *   1. The ReportingClass struct is converted to name-value strings and written to a file.
     *
     * @param com.cboe.idl.Product.ReportingClassStruct ReportingClassStruct
     */
    private static void writeToTextFile( ReportingClassStruct reportingClass )
    {

      try
      {
                   StringBuffer buffer = new StringBuffer(8192);

                   buffer.append( "<ReportingClass>");
                   buffer.append( DELIMITER + "activationDate.day="              + reportingClass.activationDate.day );
                   buffer.append( DELIMITER + "activationDate.month="            + reportingClass.activationDate.month );
                   buffer.append( DELIMITER + "activationDate.year="             + reportingClass.activationDate.year );
                   buffer.append( DELIMITER + "classKey="                        + reportingClass.classKey );
                   buffer.append( DELIMITER + "contractSize="                    + reportingClass.contractSize );
                   buffer.append( DELIMITER + "createdTime.date.day="            + reportingClass.createdTime.date.day );
                   buffer.append( DELIMITER + "createdTime.date.month="          + reportingClass.createdTime.date.month );
                   buffer.append( DELIMITER + "createdTime.date.year="           + reportingClass.createdTime.date.year );
                   buffer.append( DELIMITER + "createdTime.time.hour="           + reportingClass.createdTime.time.hour );
                   buffer.append( DELIMITER + "createdTime.time.minute="         + reportingClass.createdTime.time.minute );
                   buffer.append( DELIMITER + "createdTime.time.second="         + reportingClass.createdTime.time.second );
                   buffer.append( DELIMITER + "createdTime.time.fraction="       + reportingClass.createdTime.time.fraction );
                   buffer.append( DELIMITER + "inactivationDate.day="            + reportingClass.inactivationDate.day );
                   buffer.append( DELIMITER + "inactivationDate.month="          + reportingClass.inactivationDate.month );
                   buffer.append( DELIMITER + "inactivationDate.year="           + reportingClass.inactivationDate.year );
                   buffer.append( DELIMITER + "lastModifiedTime.date.day="       + reportingClass.lastModifiedTime.date.day );
                   buffer.append( DELIMITER + "lastModifiedTime.date.month="     + reportingClass.lastModifiedTime.date.month );
                   buffer.append( DELIMITER + "lastModifiedTime.date.year="      + reportingClass.lastModifiedTime.date.year );
                   buffer.append( DELIMITER + "lastModifiedTime.time.hour="      + reportingClass.lastModifiedTime.time.hour );
                   buffer.append( DELIMITER + "lastModifiedTime.time.minute="    + reportingClass.lastModifiedTime.time.minute );
                   buffer.append( DELIMITER + "lastModifiedTime.time.second="    + reportingClass.lastModifiedTime.time.second );
                   buffer.append( DELIMITER + "lastModifiedTime.time.fraction="  + reportingClass.lastModifiedTime.time.fraction );
                   buffer.append( DELIMITER + "listingState="                    + reportingClass.listingState );
                   buffer.append( DELIMITER + "productClassKey="                 + reportingClass.productClassKey );
                   buffer.append( DELIMITER + "productClassSymbol="              + reportingClass.productClassSymbol );
                   buffer.append( DELIMITER + "productType="                     + reportingClass.productType );
                   buffer.append( DELIMITER + "reportingClassSymbol="            + reportingClass.reportingClassSymbol );
                   buffer.append( "\n</ReportingClass>\n");

                   outStreamReportingClass.write( buffer.toString() );
      }
      catch (Exception e)
      {
            e.printStackTrace();
      }

    }  // end method


   /**
    * The main entry point.
    *
    */
    public static void main(String[] args)
    {
        // Check program arguments.
        if (args.length == 0)
        {
              System.out.println("Usage: ProductQueryClient [symbols]");
              System.out.println("Example: ProductQueryClient AOL GM IBM" );
              System.exit(1);
        }

        initialize();

        // Open the files
        try
        {
             outStreamProductType  = new OutputStreamWriter( new FileOutputStream( PRODUCT_TYPE_STRUCT_FILE ) );
             outStreamProductClass = new OutputStreamWriter( new FileOutputStream( PRODUCT_CLASS_STRUCT_FILE ) );
             outStreamProduct      = new OutputStreamWriter( new FileOutputStream( PRODUCT_STRUCT_FILE ) );
             outStreamReportingClass = new OutputStreamWriter( new FileOutputStream( REPORTING_CLASS_FILE ) );
             outStreamStrategy     = new OutputStreamWriter( new FileOutputStream( STRATEGY_STRUCT_FILE ) );
             outStreamFuture     = new OutputStreamWriter( new FileOutputStream( FUTURE_STRUCT_FILE ) );
        }
        catch (Exception e)
        {
             e.printStackTrace();
        }

        // Get all the product types
        queryProductTypes();

        // Get product classess for the specified product symbols. Convert each symbol to uppercase or ProductQueryService bombs.
        for (int i=0; i<args.length; i++)
        {
            queryProductClassStructs( args[i].toUpperCase() );
        }

        // Close the files
        try
        {
            outStreamProductType.close();
            outStreamProductClass.close();
            outStreamProduct.close();
            outStreamReportingClass.close();
            outStreamStrategy.close();
            outStreamFuture.close();
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

        // Need to force exit.
        System.exit(0);

   } // end main

}  // end class

