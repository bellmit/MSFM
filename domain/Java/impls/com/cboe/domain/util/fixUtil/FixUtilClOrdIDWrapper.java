package com.cboe.domain.util.fixUtil;

import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;

import java.text.SimpleDateFormat;
import java.text.DecimalFormat;
import java.util.Date;

/**
 * Author: beniwalv
 * Date: Aug 5, 2004
 * Time: 1:45:04 PM
 */

/**
 * ***** Note ******
 * This class is different from the one in FixCas as in that it does no logging. This will be used currently
 * in fixTranslator and the class calling this will handle the logging.
 * *****************
 *
 *
 * This class parses a FIX ClOrdID and provides accessors to the branch sequence number,
 * branch, sequence, and order date.
 *
 * Expects to create a branchSequence string and an order date from the ClOrdID.
 *
 * The branch sequence must be in a format of "BBBSSSS" where:
 * "BBB" is an ALPHABETIC ONLY 1-3 character value assigned by firm
 * "SSSS" is NUMERIC ONLY 1-4 digit value assigned by firm
 *
 * As per CBOE Fix Documentation, Branch is 1-3 alphabetic characters with no embedded blanks.
 * This implies A-ZZZ, not necessarily 3 chars in length. These are all valid.
 *
 * As per CBOE Fix Documentation, BranchSequenceNo. is 1-4 numeric only values with no embedded blanks.
 * This implies 1-9999, not necessarily 4 chars in length. These are all valid.
 *
 * Given the above, this combination can be 2-7 chars in length.
 *
 * The Date is of the format, YYYYMMDD and is separated from the branch sequence by an hyphen, '-'.
 *
 * The BBBSSSS must be unique for the current order ID - THIS ROUTINE DOES NOT VALIDATE
 * FOR UNIQUENESS - ONLY FORMAT.
 *
 * Accessors are provided to the branch and sequence, branch, sequence, orderDate, ClOrdId.
 *
 * Setters are provided for ClOrdId, branchSequence, branch, sequence, orderDate
 *
 * Throws a DataValidationException if there is a problem with the format of the incoming
 * ClOrdID. The exception is not logged by this process - receiver of exception is expected
 * to provide logging and error handling.
 *
 *
 * <br><br>
 * Copyright © 2000 by the Chicago Board Options Exchange ("CBOE"), as an unpublished work.
 * The information contained in this software program constitutes confidential and/or trade
 * secret information belonging to CBOE. This software is provided "AS IS"
 * with all faults and without warranty of any kind, either express or implied.
 *
 * @author Vivek Beniwal
 * @version .01
 */
public class FixUtilClOrdIDWrapper
{
	private String branchSequence;
	private String orderDate;
	private String branch;
	private String sequence;


	public FixUtilClOrdIDWrapper()
	{
		branchSequence = "";
		orderDate = "";
		branch = "";
		sequence = "";
	}

	/**
	* Constructor that takes a string and parses it for a valid ClOrdID
	*/
	public FixUtilClOrdIDWrapper(String fixClOrdID) throws DataValidationException {
		parseClOrdID(fixClOrdID);
	}

	/**
	* Constructor that takes separate branch, sequence, and order dates and parses
	* them for a valid ClOrdID
	*/
	public FixUtilClOrdIDWrapper(String branch, int sequence, String orderDate) throws DataValidationException
	{
		StringBuilder dve = new StringBuilder(100);

		if(!this.validateBranch(branch))
		{
			dve.append("Invalid branch '");
			dve.append(branch);
			dve.append("': Branches must be one to three characters.");
			throw buildDataValidationException("",dve.toString());
		}

		if (sequence < 1 || sequence > 9999)
		{
			dve.append("Invalid sequence no. '");
			dve.append(sequence);
			dve.append("': Sequence No.s should be between 1 and 9999");
			throw buildDataValidationException("", dve.toString());
		}

		if (!this.validateDate(orderDate))
		{
			dve.append("Invalid Order Date '");
			dve.append(orderDate);
			dve.append("': Order Date should be of the format YYYYMMDD");
			throw buildDataValidationException("", dve.toString());
		}
		this.branch = branch;
		this.sequence = sequence + "";
		this.branchSequence = branch + sequence;
		this.orderDate = orderDate;

	}


	/**
	*  Parse a FIX Client Order ID for branch, sequence and order date
	*
	* @param fixClOrdID FIX Client Order ID field that must comply with CBOE branch sequence
	*/
	protected void parseClOrdID(String fixClOrdID) throws DataValidationException
	{
		if (!this.validateClOrdID(fixClOrdID))
		{
			StringBuilder dve = new StringBuilder(100);
			dve.append("Invalid orderId '");
			dve.append(fixClOrdID);
			dve.append("': Proper Format is AAA9999-YYYYMMDD");
			throw buildDataValidationException(fixClOrdID, dve.toString());
		}
		// everthing has been validated so can be set.
		int index = fixClOrdID.indexOf('-');
		this.branchSequence = fixClOrdID.substring(0,index);
		this.orderDate = fixClOrdID.substring(index + 1);
		int charIndex = -1;
		for (int i = 0; i < branchSequence.length(); i++)
		{
			if (Character.isDigit(branchSequence.charAt(i)))
			{
				charIndex = i;
				break;
			}
		}
		branch = branchSequence.substring(0,charIndex);
		sequence = branchSequence.substring(charIndex);
	}

	protected void parseBranchSequence(String tmpBranchSequence) throws DataValidationException
	{
    	if (!this.validateBranchSequence(tmpBranchSequence))
		{
    		StringBuilder dve = new StringBuilder(150);
    		dve.append("Invalid BranchSequence '");
    		dve.append(tmpBranchSequence);
			dve.append("': format is AAA9999 comprising of a 1-3 alphabetic chars and a number between 1 and 9999 ");
    		
			throw buildDataValidationException(tmpBranchSequence, dve.toString());
		}
		else
			this.branchSequence = tmpBranchSequence;
		// Should also set the class branch and sequence variables.
		int charIndex = -1;
		for (int i = 0; i < tmpBranchSequence.length(); i++)
		{
			if (Character.isDigit(tmpBranchSequence.charAt(i)))
			{
				charIndex = i;
				break;
			}
		}
        // these have already been validated in calling validateBranchSequence() method.
		branch = tmpBranchSequence.substring(0,charIndex);
		sequence = tmpBranchSequence.substring(charIndex);



	}

	protected void parseDate(String tmpOrderDate) throws DataValidationException
	{
		if (!this.validateDate(tmpOrderDate))
		{
			StringBuilder dve = new StringBuilder(100);
			dve.append("Invalid Date '");
			dve.append(tmpOrderDate);
			dve.append("': The value should be a string in YYYYMMDD format ");
			throw buildDataValidationException(tmpOrderDate,dve.toString());
		}
		else
			this.orderDate = tmpOrderDate;
	}

	protected void parseBranch(String tmpBranch) throws DataValidationException
	{
		if (!this.validateBranch(tmpBranch))
		{
			StringBuilder dve = new StringBuilder(100);
			dve.append("Invalid branch code '");
			dve.append(tmpBranch);
			dve.append("': The value should be 1 - 3 alphabetic only chars. with no embedded blanks. ");
			throw buildDataValidationException(tmpBranch, dve.toString());
		}
		else
			this.branch = tmpBranch;
	}

	protected void parseSequence(String tmpSequence) throws DataValidationException
	{
    	if (!this.validateSequence(tmpSequence))
		{
    		StringBuilder dve = new StringBuilder(100);
    		dve.append("Invalid sequence number '");
    		dve.append(tmpSequence);
			dve.append("': The value should be an integer between 1 and 9999");
			throw buildDataValidationException(tmpSequence,dve.toString());
		}
		else
			this.sequence = tmpSequence;

	}


	public final boolean validateClOrdID(String tmpClOrdID)
	{
		// validate that the length is between 11 and 16 chars
		if (tmpClOrdID==null || tmpClOrdID.length() > 16 || tmpClOrdID.length() < 11)
		{
			return false;
		}

		//Separate the date and the branchSequence.
		// lastIndex to see if there is only one occurence of the '-' character.
		int index,lastIndex = 0;
		index = tmpClOrdID.indexOf('-');
		lastIndex = tmpClOrdID.lastIndexOf('-');
		if (index == -1)
		{
			return false;
		}

		if (index != lastIndex)
		{
			return false;
		}

		String tmpBranchSequence = tmpClOrdID.substring(0,index);
		String tmpOrderDate = tmpClOrdID.substring(index + 1);

		// validate branchSequence and orderDate.
		if (!(this.validateBranchSequence(tmpBranchSequence) && this.validateDate(tmpOrderDate)))
			return false;

		return true;
	}

	/**
	 * Validate branch-sequence string. Rules:
	 * <li> Must be between 2 and 7 characters
	 * <li> Must contain a numeric portion
	 * <li> Branch and sequence number must pass their own separate validations
	 * @param tmpBranchSequence string to validate
	 * @return true if string passes validation, false otherwise
	 */
	public final boolean validateBranchSequence(String tmpBranchSequence)
	{
		// validate that the branch sequence is at least 2 and no more than 7 chars in length.
		if (tmpBranchSequence==null || tmpBranchSequence.length() < 2 || tmpBranchSequence.length() > 7)
			return false;
		// separate out the branch and the sequence. call validation for each
		int charIndex = -1;
		for (int i = 0; i < tmpBranchSequence.length(); i++)
		{
			if (Character.isDigit(tmpBranchSequence.charAt(i)))
			{
				if (i==0)
				{
					return false;
				}
				charIndex = i;
				break;
			}
		}
		// if there was no numerical character found, we have a problem.
		if (charIndex == -1)
			return false;
		String tmpBranch = tmpBranchSequence.substring(0,charIndex);
		String tmpSequence = tmpBranchSequence.substring(charIndex);
		if (!(this.validateBranch(tmpBranch) && this.validateSequence(tmpSequence)))
			return false;


		return true;
	}

	/**
	 * Validate OrderDate
	 * @param tmpOrderDate a string representing a date in the form yyyyMMDD
	 * @return true if the string represents a valid date, false otherwise
	 */
	public final boolean validateDate(String tmpOrderDate)
	{
		try
		{
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMDD");
			Date aDate = dateFormat.parse(tmpOrderDate);
			if (aDate == null) {
				// A parse error occurred
				return false;
			}
		}
		catch (Exception e)
		{
			// NullPointerException caught if string is null
			// Log.exception(e);
			return false;
		}
		return true;
	}

	/**
	 * Validate branch string. Rules:
	 * <li> Must be between 1 and 3 characters
	 * <li> Contains no spaces
	 * @param tmpBranch string to validate
	 * @return true if string passes validation, false otherwise
	 */
	public final boolean validateBranch(String tmpBranch)
	{
			// validate that the branch is at least 1 char long and at most 3 chars long
			if (tmpBranch==null || tmpBranch.length() < 1 || tmpBranch.length() > 3)
				return false;


			// validate that first char is a letter, and other characters are letters or spaces
			if (!Character.isLetter(tmpBranch.charAt(0)))
			{
				return false;
			}
			if (tmpBranch.length() > 1)
			{
				if (!(Character.isLetter(tmpBranch.charAt(1)) || Character.isSpaceChar(tmpBranch.charAt(1)) ))
			 	{
				 	return false;
			 	}
		 	 	if (tmpBranch.length() > 2)
				{
					if (!(Character.isLetter(tmpBranch.charAt(2)) || Character.isSpaceChar(tmpBranch.charAt(2)) ))
			 		{
				 		return false;
			 		}
					// validate that there are no spaces between the alphabetical part of the branch.
			 		if (Character.isSpaceChar(tmpBranch.charAt(1)) &&  ! Character.isSpaceChar(tmpBranch.charAt(2)) )
			 		{
						return false;
			 		}
				}
			}
		return true;
	}

	/**
	 * Validate sequence string. Rules:
	 * <li> Must be numeric
	 * <li> Valid range is 1 through 9999 inclusive
	 * @param tmpSequence string to validate
	 * @return true if string passes validation, false otherwise
	 */
	public final boolean validateSequence(String tmpSequence)
	{
		if (tmpSequence==null)
			return false;
		// validate that the sequence no. part is a number between 1 and 9999
		try
		{
			int sequenceInt = Integer.parseInt(tmpSequence);
			if (sequenceInt < 1 || sequenceInt > 9999)
				return false;

		}
		catch(NumberFormatException nfe)
		{
			return false;
		}

		return true;
	}

	/**
	*  returns the concatenation of the branchSequence with the orderDate
	*/
	public String getClOrdID()
	{
		DecimalFormat sequenceFormat = new DecimalFormat("0000");
		if ( ! branchSequence.equals("") )
		{
			return  branch + sequenceFormat.format( Integer.parseInt( sequence ) ) + "-" + orderDate;
		}
		else {
			return "";
		}
	}

	/**
	* Set the ClOrdID using the parse method
	*/
	public void setClOrdID(String fixClOrdID) throws DataValidationException
	{
		parseClOrdID(fixClOrdID);
	}

	/**
	*  Returns the branchSequence number
	*/
	public String getBranchSequence()
	{
		DecimalFormat sequenceFormat = new DecimalFormat("0000");
		return  branch + sequenceFormat.format( Integer.parseInt( sequence ) );
	}

	public void setBranchSequence(String branchSequence) throws DataValidationException
	{
		this.parseBranchSequence(branchSequence);
	}

	/**
	* Return the Branch number
	*/
	public String getBranch()
	{
		return branch;
	}

	public void setBranch(String branch) throws DataValidationException
	{
		this.parseBranch(branch);
	}
	/**
	* Return the Branch Sequence number as a string
	*/
	public String getSequence()
	{
		DecimalFormat sequenceFormat = new DecimalFormat("0000");
		return sequenceFormat.format( Integer.parseInt( sequence ) );
	}

	public void setSequence(String sequenceStr) throws DataValidationException
	{
		this.parseSequence(sequenceStr);
	}
	/**
	* Return the Sequence Number as an Integer
	*/
	public int getSequenceAsInt()
	{
		return Integer.parseInt(sequence);
	}

	public void setSequence(int sequenceInt) throws DataValidationException
	{
		if (sequenceInt >=1 && sequenceInt <= 9999)
			sequence = Integer.toString(sequenceInt);
		else
			throw buildDataValidationException(Integer.toString(sequenceInt),
											   		"Invalid SequenceNo. Values are between 1 and 9999");
	}

	/**
	* returns the orderDate
	*/
	public String getOrderDate()
	{
		return orderDate;
	}
    public void setOrderDate(String orderDate) throws DataValidationException
	{
		this.parseDate(orderDate);
	}



	/**
	* Build a data validation exception and return it to the caller
	*
	* @param invalidData string containing the invalid data that caused the problem
	* @param reason string containing the description of the problem with the data
	*
	*/
	protected DataValidationException buildDataValidationException(String invalidData, String reason)
	{

        return ExceptionBuilder.dataValidationException("Invalid Data - "+ FixUtilConstants.ClOrdID.TAGNAME +
                "(" + FixUtilConstants.ClOrdID.TAGNUMBER + ")= "+ "[" + reason +"]", 0 );
	}
}
