package com.cboe.domain.util;

import com.cboe.idl.tradingProperty.InternalizationPercentageStruct;
import com.cboe.idl.cmiErrorCodes.DataValidationCodes;
import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;

/**
 *   The lowerRange is inclusive and the upperRange is exclusive, and no gap is allowed
 *   between ranges.
 */
public class InternalizationPercentageHelper
{
    public static String toString(InternalizationPercentageStruct struct)
    {
        if(struct == null)
        {
            return " NULL";
        }

        StringBuilder buf = new StringBuilder(100);
        buf.append(" auctionType: ") 
        .append(struct.auctionType)
        .append(" lowerRange: ")
        .append(struct.lowerRange)
        .append(" upperRange: ")
        .append(struct.upperRange)
        .append(" percentage: ")
        .append(struct.percentage);
        return buf.toString();
    }

	public static void validateInternalizationPercentageStruct(InternalizationPercentageStruct[] percentage) throws DataValidationException
	{
        int auctionType = percentage[0].auctionType;
        int previousUpper = percentage[0].lowerRange;
        StringBuilder dve = new StringBuilder(100);
        
		for (int i=0; i<percentage.length; i++)
		{
			dve.setLength(0);
            if (percentage[i].auctionType != auctionType)
            {
                auctionType = percentage[i].auctionType;
                previousUpper = percentage[i].lowerRange;
            }

			if (percentage[i].lowerRange < 0 || percentage[i].upperRange < 1 )
			{
				dve.append(" lowerRange should be >=0 and upperRange should be >=1 in validateInternalizationPercentageStruct[")
				.append(i)
				.append("]");
				throw ExceptionBuilder.dataValidationException(dve.toString(), DataValidationCodes.INVALID_COVERAGE);
			}
            
            if (percentage[i].percentage <0.0 || percentage[i].percentage > 100.0)
            {
            	dve.append(" percentage Value in validateInternalizationPercentageStruct[")
            	.append(i)
            	.append("] should be >= 0.0 and <= 100.0 ");
            	             
				throw ExceptionBuilder.dataValidationException(dve.toString(), DataValidationCodes.INVALID_COVERAGE);
            }

			if (percentage[i].lowerRange >= percentage[i].upperRange)
			{
				dve.append("lowerRange >= upperRange in validateInternalizationPercentageStruct[")
				.append(i)
				.append("]");
				throw ExceptionBuilder.dataValidationException(dve.toString(), DataValidationCodes.INVALID_COVERAGE);
			}

            if( percentage[i].lowerRange != previousUpper )
            {
            	dve.append("Gap between lowerRange and upperRange in InternalizationPercentageStruct[")
            	.append(i)
                .append("] and its previous range");
                throw ExceptionBuilder.dataValidationException(dve.toString(), DataValidationCodes.INVALID_COVERAGE);
            }

            previousUpper = percentage[i].upperRange;
		}

	}
}
