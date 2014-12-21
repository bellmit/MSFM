package com.cboe.interfaces.domain;

import com.cboe.exceptions.*;
import com.cboe.idl.firm.FirmStruct;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;

/**
 *  Define what the external interface to an FirmHome is, and also implements
 *  the singleton pattern to access the configured type of FirmHome.
 * @author Matt Sochacki
 */
public interface FirmHome
{
	public final static String HOME_NAME = "FirmHome";

	public Firm create(FirmStruct aFirmStruct)
		throws TransactionFailedException, DataValidationException;

/**
 * Returns array of all firms
 * @return com.cboe.interfaces.domain.Firm[]
 */
Firm[] findAllFirms()  throws TransactionFailedException;

/**
 * Returns array of all firms matching the selection criteria
 * @param active	indicates whether returned firms s/b active
 * @param clearing	indicates whether returned firms s/b clearing firms
 * @return com.cboe.interfaces.domain.Firm[]
 */
Firm[] findFirms( boolean active, boolean clearing )  throws TransactionFailedException;

/**
 * Returns Firm matching membership key
 * @return com.cboe.interfaces.domain.Firm
 */
public Firm findFirmByMembershipKey(int membershipKey)
		throws TransactionFailedException, NotFoundException;

/**
 * Returns Firm matching key
 * @return com.cboe.interfaces.domain.Firm
 */
public Firm findFirmByKey(int firmKey)
		throws TransactionFailedException, NotFoundException;

/**
 * Returns Firm matching number
 * @return com.cboe.interfaces.domain.Firm
 */
public Firm findFirmByNumber(ExchangeFirmStruct firmNumber)
		throws TransactionFailedException, NotFoundException;

/**
 * Returns Firm matching acronym
 * @return com.cboe.interfaces.domain.Firm
 */
public Firm findFirmByAcronym(String acronym,String exchangeAcr)
		throws TransactionFailedException, NotFoundException;

/**
 * Updates the Firm based on contents of firmStruct
 */
public void updateFirm(Firm firm, FirmStruct firmStruct )
		throws TransactionFailedException, DataValidationException;


/**
 * Converts the Firm to a FirmStruct
 * @return FirmStruct
 */
public FirmStruct toStruct(Firm firm);


}

