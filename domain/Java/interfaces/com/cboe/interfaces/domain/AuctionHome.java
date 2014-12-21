package com.cboe.interfaces.domain;


import java.util.List;

import com.cboe.idl.cmiUtil.CboeIdStruct;
import com.cboe.exceptions.*;

/**
 *  Define interface to hold a reference to an implementation of the AuctionHome.
 * @author Mei Wu
 */
public interface AuctionHome
{
	public final static String HOME_NAME = "AuctionHome";


    /**
     * find auctin object by auction Id struct and productKey
     * @param auctionId
     * @param productKey
     * @return
     * @throws TransactionFailedException
     * @throws NotFoundException
     */
    public Auction find (CboeIdStruct auctionId,  int productKey)
            throws TransactionFailedException, NotFoundException;
    /**
     * find auctin object by auction DB Id and productKey
     * @param auctionKey
     * @param productKey
     * @return
     * @throws TransactionFailedException
     * @throws NotFoundException
     */
    public Auction find (long auctionKey, int productKey)
            throws TransactionFailedException, NotFoundException;
    /**
     * find auction objects by productKey
     * @param productKey
     * @param isActive
     * @return the auction array for this productKey,
     * when isActive is ture, it should only return one auction based on the current business requirement.
     * @throws TransactionFailedException
     */
    public Auction[] findForProduct(int productKey, boolean isActive)
            throws TransactionFailedException;

    /**
     * create Internalization Auction with pair internalization orders
     * @param theInternalzationPair
     * @param startingPrice
     * @return
     * @throws TransactionFailedException
     * @throws SystemException
     * @throws DataValidationException
     * @throws AlreadyExistsException
     */
    public Auction create(InternalizationPair theInternalzationPair, Price startingPrice )
            throws TransactionFailedException, SystemException, DataValidationException, AlreadyExistsException;

    /**
     * create auction for single order
     * @param auctionedOrder
     * @param startingPrice
     * @return
     * @throws TransactionFailedException
     * @throws SystemException
     * @throws DataValidationException
     * @throws AlreadyExistsException
     */
    public Auction create(Order auctionedOrder, Price startingPrice, short auctionTYpe)
            throws TransactionFailedException, SystemException, DataValidationException,AlreadyExistsException;

    public Auction create(List<Order> auctionedOrders, Price startingPrice, short auctionType)
        throws TransactionFailedException, SystemException, DataValidationException,AlreadyExistsException;
}
