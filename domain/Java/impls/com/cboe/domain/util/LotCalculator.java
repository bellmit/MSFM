package com.cboe.domain.util;

import java.util.HashMap;


/****************************************************************************
 * Performs lot size calculations.
 * <br>
 * There are two ways you can use this class:
 * <ul>
 *      <li>Call the static methods directly as utility functions</li>
 *      <li>Obtain a reference to a LotCalculator from the getInstance method
 *          and use the instance methods.</li>
 * </ul>
 *
 * Depending on your needs one option may be more useful that the other. The
 * instance approach means the lotSize only needs to be determined once when 
 * the calculator is retrieved.  The calculator can be reused, even in a context
 * where the lotSize is unknown.  The static methods provide for a more direct and
 * slightly faster execution, however you must know the lotSize each time you use 
 * them.
 * 
 * 
 */
public class LotCalculator
{
    /********************************************
     * Holds calculators for different lot sizes
     * This is a pool of calculators each handling
     * a different lot size.
     */
    private static HashMap calculators = new HashMap();


    /********************************************
     * Holds the lot size value for the instance.
     */
    private final int lotSize;


    /**************************************************
     * Protected constructor so that users must use 
     * getInstance method to obtain new instances.
     */
     protected LotCalculator( final int lotSize )
     {
        this.lotSize = lotSize;
     }

    /************************************************************************
     *
     * Returns an instance of a calculator to handle
     * the lot size specified.
     *
     *  @param lotSize number of shares per lot
     *  @return LotCalculator for the lotSize specified
     *
     */
    public static LotCalculator getInstance( final int lotSize )
    {
        Integer key = new Integer( lotSize );
        LotCalculator calculator = (LotCalculator)calculators.get( key );

        if( calculator == null )
        {
            calculator = new LotCalculator( lotSize );
            calculators.put( key, 
                             calculator
                           );
        }

        return calculator;
    }


    /************************************************************************
     *  Returns the round lot protion.
     *  For example, 150 shares would be 100 share round lot portion for a 
     *  lotSize of 100
     *
     *  @param quantity in shares
     *  @return int number of shares that are round lots
     *
     */
    public int roundLotPortion( final int quantity )
    {
        return roundLotPortion( quantity,
                                lotSize );
    }
    
    /************************************************************************
     *  Returns the amount that represents an odd lot
     *  For example, 250 shares has an odd lot portion of 50 for a lot size 
     *  of 100.  Also 75 shares has an odd lot portion of 75 for a lot size 
     *  of 100. 
     *
     *  @param quantity in shares
     *  @return the amount of shares from quantity that is not a round lot
     *
     */
    public int oddLotPortion( final int quantity )
    {
        return oddLotPortion( quantity,
                              lotSize );
    }
    
    /************************************************************************
     * Calculates the quantity that makes the next round lot.  If quantity 
     * is a round lot then quantity is the next round lot.  If quantity has 
     * an odd lot portion, quantity is rounded up to the next round lot.
     * 
     *  @param quantity shares 
     *  @return int shrars representing a round lot 
     */
    public int nextRoundLot( final int quantity )
    {
        return nextRoundLot( quantity,
                             lotSize );
    }
    
    
    /************************************************************************
     *  Determines if the quantity is less than the lotSize.  A mixed lot is
     *  not an odd lot therefore this method will return false if the 
     *  quantity is greater than lotSize but still not a roundLot.
     * 
     *  @param quantity in shares
     *  @return boolean true if quantity < lotSize otherwise false
     */
    public boolean isOddLot( final int quantity )
    {
        return isOddLot( quantity,
                         lotSize );
    }

    /************************************************************************
     *  Determines if the quantity is greater than lotSize but still not 
     *  a roundLot.
     * 
     *  @param quantity in shares
     *  @return boolean true if quantity % lotSize != 0 && quantity > lotSize
     */
    public boolean isMixedLot( final int quantity )
    {
        return isMixedLot( quantity,
                           lotSize );
    }

    /************************************************************************
     *  Determines if the quantity is evenly divisible by the lotSize
     * 
     *  @param quantity in shares
     *  @return boolean true if quantity % lotSize == 0
     */ 
    public boolean isRoundLot( final int quantity )
    {
        return isRoundLot( quantity,
                           lotSize );
    }


    /************************************************************************
     *  
     *  Behaves identically to the instance method
     *
     *  @param quantity in shares
     *  @param lotSize in shares
     *  @return int number of shares that are round lots
     *
     */
    public static int roundLotPortion( final int quantity, 
                                       final int lotSize )
    {
        //By dividing quantity by lotSize and taking the floor we 
        //get the whole lot amount.  By multiplying back by lotSize 
        //we get the total shares.  I am rounding because of precision 
        //problems when representing decimal numbers as floating point 
        //values. The round method explicitly returns an integer and should
        //not create problems that casting could due to loss of precision.

        int roundLotPortion = quantity - (quantity % lotSize);

        return roundLotPortion;
    }
    
    /************************************************************************
     *  
     *  Behaves identically to the instance method
     *
     *  @param quantity in shares
     *  @param lotSize in shares
     *  @return the amount of shares from quantity that is not a round lot
     *
     */
    public static int oddLotPortion( final int quantity,
                                     final int lotSize )
    {
        int oddLotPortion = quantity - roundLotPortion( quantity, 
                                                        lotSize );
        return oddLotPortion;
    }
    
    /************************************************************************
     *  
     *  Behaves identically to the instance method
     *
     *  @param quantity in shares
     *  @param lotSize in shares
     *  @return int shrars representing a round lot 
     */
    public static int nextRoundLot( final int quantity,
                                    final int lotSize )
    {
        int nextRoundLot = quantity;

        //Check to see if the quantity is not a round lot.
        if( !isRoundLot( quantity,
                         lotSize ) )
        {
            int roundLotPortion = roundLotPortion( quantity,
                                                   lotSize );
            //Add 1 lot to the round lot portion
            nextRoundLot = roundLotPortion + lotSize;
        }

        return nextRoundLot;
    }
    
    
    /************************************************************************
     *  
     *  Behaves identically to the instance method
     *
     *  @param quantity in shares
     *  @param lotSize in shares
     *  @return boolean true if quantity < lotSize otherwise false
     */
    public static boolean isOddLot( final int quantity,
                                    final int lotSize )
    {
        boolean isOddLot = false;
        if( quantity < lotSize && quantity > 0 )
        {
            isOddLot = true;
        }

        return isOddLot;
    }

    /************************************************************************
     *  
     *  Behaves identically to the instance method
     *
     *  @param quantity in shares
     *  @param lotSize in shares
     *  @return boolean true if quantity % lotSize != 0 && quantity > lotSize
     */
    public static boolean isMixedLot( final int quantity,
                                      final int lotSize )
    {
        boolean isMixedLot = false;
        if( (quantity % lotSize) != 0 && quantity > lotSize )
        {
            isMixedLot = true;
        }

        return isMixedLot;
    }

    /************************************************************************
     *  
     *  Behaves identically to the instance method
     *
     *  @param quantity in shares
     *  @param lotSize in shares
     *  @return boolean true if quantity % lotSize == 0
     */ 
    public static boolean isRoundLot( final int quantity,
                                      final int lotSize )
    {
        boolean isRoundLot = false;
        if( (quantity % lotSize) == 0 )
        {
            isRoundLot = true;
        }

        return isRoundLot;
    }


    /************************************************************************
     *
     *
     */
    public static void main( String[] args )
    {
        System.exit( test() );
    }

        

    /************************************************************************
     *
     *
     */
    private static int test()
    {

        ///////////////////////////////////
        //Test 1
        {
            int lotSize = 100;
            LotCalculator calculator = LotCalculator.getInstance( lotSize );
            LotCalculator calculator1 = LotCalculator.getInstance( lotSize );

            if( calculator != calculator1 )
            {
                System.err.println( "LotCalculator.getInstance() returned diffent calculators for the same lot size" );
                return 1;
            }
        }
        //
        ///////////////////////////////////

        ///////////////////////////////////
        //Test 2
        {
            int lotSize = 100;
            int quantity1 = 0;
            int quantity2 = 1;
            int quantity3 = 99;

            LotCalculator calculator = LotCalculator.getInstance( lotSize );
            int roundLot1 = calculator.roundLotPortion( quantity1 );
            int roundLot2 = calculator.roundLotPortion( quantity2 );
            int roundLot3 = calculator.roundLotPortion( quantity3 );

            if( roundLot1 != roundLot2 || 
                roundLot2 != roundLot3 ||
                roundLot3 != 0 )
            {
                System.err.println( "LotCalculator.roundLotPortion() returned a wrong value for test2" );
                return 1;
            }
        }
        //
        ///////////////////////////////////

        ///////////////////////////////////
        //Test 3
        {
            int lotSize = 100;
            int quantity1 = 100;
            int quantity2 = 101;
            int quantity3 = 199;

            LotCalculator calculator = LotCalculator.getInstance( lotSize );
            int roundLot1 = calculator.roundLotPortion( quantity1 );
            int roundLot2 = calculator.roundLotPortion( quantity2 );
            int roundLot3 = calculator.roundLotPortion( quantity3 );

            if( roundLot1 != roundLot2 || 
                roundLot2 != roundLot3 ||
                roundLot3 != 100 )
            {
                System.err.println( "LotCalculator.roundLotPortion() returned a wrong value for test 3" );
                return 1;
            }
        }
        //
        ///////////////////////////////////

        ///////////////////////////////////
        //Test 4
        {
            int lotSize = 100;
            int quantity1 = 0;
            int quantity2 = 1 ;
            int quantity3 = 99;
            int quantity4 = 100;
            int quantity5 = 101;
            int quantity6 = 199;

            LotCalculator calculator = LotCalculator.getInstance( lotSize );
            int oddLot1 = calculator.oddLotPortion( quantity1 );
            int oddLot2 = calculator.oddLotPortion( quantity2 );
            int oddLot3 = calculator.oddLotPortion( quantity3 );
            int oddLot4 = calculator.oddLotPortion( quantity4 );
            int oddLot5 = calculator.oddLotPortion( quantity5 );
            int oddLot6 = calculator.oddLotPortion( quantity6 );

            if( oddLot1 != 0 ||
                oddLot2 != quantity2 ||
                oddLot3 != quantity3 ||
                oddLot4 != 0 ||
                oddLot5 != quantity5-calculator.roundLotPortion(quantity5) ||
                oddLot6 != quantity6-calculator.roundLotPortion(quantity6) )
            {
                System.err.println( "LotCalculator.oddLotPortion() returned a wrong value for test 4" );
                return 1;
            }
        }
        //
        ///////////////////////////////////


        ///////////////////////////////////
        //Test 5
        {
            int lotSize = 100;
            int quantity1 = 0;
            int quantity2 = 1 ;
            int quantity3 = 99;
            int quantity4 = 100 ;
            int quantity5 = 101;
            int quantity6 = 199;

            LotCalculator calculator = LotCalculator.getInstance( lotSize );
            int roundLot1 = calculator.nextRoundLot( quantity1 );
            int roundLot2 = calculator.nextRoundLot( quantity2 );
            int roundLot3 = calculator.nextRoundLot( quantity3 );
            int roundLot4 = calculator.nextRoundLot( quantity4 );
            int roundLot5 = calculator.nextRoundLot( quantity5 );
            int roundLot6 = calculator.nextRoundLot( quantity6 );

            if( roundLot1 != 0 ||
                roundLot2 != 100 ||
                roundLot3 != 100 ||
                roundLot4 != 100 ||
                roundLot5 != 200 ||
                roundLot6 != 200 )
            {
                System.err.println( "LotCalculator.nextRoundLot() returned a wrong value for test 5" );
                return 1;
            }
        }
        //
        ///////////////////////////////////

        ///////////////////////////////////
        //Test 6
        {
            int lotSize = 100;
            int quantity1 = 0;
            int quantity2 = 1 ;
            int quantity3 = 99;
            int quantity4 = 100 ;
            int quantity5 = 101;
            int quantity6 = 199;

            LotCalculator calculator = LotCalculator.getInstance( lotSize );
            boolean isOddLot1 = calculator.isOddLot( quantity1 );
            boolean isOddLot2 = calculator.isOddLot( quantity2 );
            boolean isOddLot3 = calculator.isOddLot( quantity3 );
            boolean isOddLot4 = calculator.isOddLot( quantity4 );
            boolean isOddLot5 = calculator.isOddLot( quantity5 );
            boolean isOddLot6 = calculator.isOddLot( quantity6 );

            if( isOddLot1 ||
                !isOddLot2 ||
                !isOddLot3 ||
                isOddLot4 ||
                isOddLot5 ||
                isOddLot6 )
            {
                System.err.println( "LotCalculator.isOddLot() returned a wrong value for test 6" );
                return 1;
            }
        }
        //
        ///////////////////////////////////

        ///////////////////////////////////
        //Test 7
        {
            int lotSize = 100;
            int quantity1 = 0;
            int quantity2 = 1;
            int quantity3 = 99;
            int quantity4 = 100;
            int quantity5 = 101;
            int quantity6 = 199;

            LotCalculator calculator = LotCalculator.getInstance( lotSize );
            boolean isMixedLot1 = calculator.isMixedLot( quantity1 );
            boolean isMixedLot2 = calculator.isMixedLot( quantity2 );
            boolean isMixedLot3 = calculator.isMixedLot( quantity3 );
            boolean isMixedLot4 = calculator.isMixedLot( quantity4 );
            boolean isMixedLot5 = calculator.isMixedLot( quantity5 );
            boolean isMixedLot6 = calculator.isMixedLot( quantity6 );

            if( isMixedLot1 ||
                isMixedLot2 ||
                isMixedLot3 ||
                isMixedLot4 ||
                !isMixedLot5 ||
                !isMixedLot6 )
            {
                System.err.println( "LotCalculator.isMixedLot() returned a wrong value for test 7" );
                return 1;
            }
        }
        //
        ///////////////////////////////////

        ///////////////////////////////////
        //Test 8
        {
            int lotSize = 100;
            int quantity1 = 0;
            int quantity2 = 1;
            int quantity3 = 99;
            int quantity4 = 100;
            int quantity5 = 101;
            int quantity6 = 199;

            LotCalculator calculator = LotCalculator.getInstance( lotSize );
            boolean isRoundLot1 = calculator.isRoundLot( quantity1 );
            boolean isRoundLot2 = calculator.isRoundLot( quantity2 );
            boolean isRoundLot3 = calculator.isRoundLot( quantity3 );
            boolean isRoundLot4 = calculator.isRoundLot( quantity4 );
            boolean isRoundLot5 = calculator.isRoundLot( quantity5 );
            boolean isRoundLot6 = calculator.isRoundLot( quantity6 );

            if( !isRoundLot1 ||
                isRoundLot2 ||
                isRoundLot3 ||
                !isRoundLot4 ||
                isRoundLot5 ||
                isRoundLot6 )
            {
                System.err.println( "LotCalculator.isRoundLot() returned a wrong value for test 8" );
                return 1;
            }
        }
        //
        ///////////////////////////////////

        return 0;

    }
    
}
