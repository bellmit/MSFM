package com.cboe.domain.util;

import com.cboe.interfaces.domain.*;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
import com.cboe.idl.cmiUtil.*;
import com.cboe.idl.cmiOrder.*;

/**
 * Factory to provide Cboe, CMI or Complete OrderIdStructContainers for use
 * as hash key components.
 *
 * This factory also houses the business rules to determine which of the 2
 * containers ( Cboe or CMI ) is valid for any given OrderIdStruct
 *
 * @author Keith A. Korecky
 */

public class OrderIdStructContainerFactory
{

    /**
     * Constructor - not really used for factory
     */
    private OrderIdStructContainerFactory()
    {
    }

    /**
     * create a valid, complete container from the OrderIdStruct
     *
     * @param   orderId                         - OrderIdStruct to create from
     * @return  CompleteOrderIdStructContainer  - complete ( container Cboe & CMI ) OrderIdStructContainer
     */
    public static CompleteOrderIdStructContainer createCompleteOrderIdStructContainer( OrderIdStruct    orderId )
    {
        CompleteOrderIdStructContainer      completeContainer = new CompleteOrderIdStructContainer( orderId );
        return ( completeContainer.isValid() ? completeContainer : null );
    }

    /**
     * create a valid, CMI container from the OrderIdStruct
     *
     * @param   orderId                     - OrderIdStruct to create from
     * @return  CmiOrderIdStructContainer   - CMI OrderIdStructContainer
     */
    public static CmiOrderIdStructContainer createCmiOrderIdStructContainer( OrderIdStruct  orderId )
    {
        CmiOrderIdStructContainer      cmiContainer = new CmiOrderIdStructContainer( orderId );
        return ( cmiContainer.isValid() ? cmiContainer : null );
    }

    /**
     * create a valid, Cboe container from the OrderIdStruct
     *
     * @param   orderId                     - OrderIdStruct to create from
     * @return  CboeOrderIdStructContainer  - Cboe ( high/low UID ) OrderIdStructContainer
     */
    public static CboeOrderIdStructContainer createCboeOrderIdStructContainer( OrderIdStruct    orderId )
    {
        CboeOrderIdStructContainer      cboeContainer = new CboeOrderIdStructContainer( orderId );
        return ( cboeContainer.isValid() ? cboeContainer : null );
    }

    /**
     * create a miniumum, valid container based on business rules
     *  try the Cboe high/low UID first
     *  then use the CMI values
     *
     * @param   orderId                     - OrderIdStruct to create from
     * @return  BaseOrderIdStructContainer  - valid OrderIdStructContainer
     */
    public static BaseOrderIdStructContainer createValidOrderIdStructContainer( OrderIdStruct   orderId )
    {
    /*
        BaseOrderIdStructContainer  baseContainer = createCboeOrderIdStructContainer( orderId );
        if ( baseContainer == null )
        {
            baseContainer = createCmiOrderIdStructContainer( orderId );
        }
    */
        BaseOrderIdStructContainer  baseContainer;
        if ( orderId.highCboeId == 0 && orderId.lowCboeId == 0 )
        {
            baseContainer = createCmiOrderIdStructContainer( orderId );
        }
        else
        {
            return createCboeOrderIdStructContainer( orderId );
        }

        return (BaseOrderIdStructContainer)baseContainer;
    }
    
    /**
     * create a valid, CMI container from the OrderEntryStruct
     *
     * @param   orderEntryStruct                     - OrderEntryStruct to create from
     * @return  CmiOrderIdStructContainer   - CMI OrderIdStructContainer
     */
    public static CmiOrderIdStructContainer createCmiOrderIdStructContainer( OrderEntryStruct  orderEntry )
    {
        CmiOrderIdStructContainer      cmiContainer = new CmiOrderIdStructContainer( orderEntry.executingOrGiveUpFirm, 
        			orderEntry.branch, orderEntry.branchSequenceNumber, orderEntry.correspondentFirm, orderEntry.orderDate );
        return ( cmiContainer.isValid() ? cmiContainer : null );
    }

}
