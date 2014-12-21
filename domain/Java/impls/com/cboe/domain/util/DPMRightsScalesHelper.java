package com.cboe.domain.util;

import com.cboe.idl.cmiProduct.EPWStruct;
import com.cboe.idl.tradingProperty.DpmRightsScaleStruct;
import com.cboe.exceptions.DataValidationException;
import com.cboe.util.ExceptionBuilder;

/**
 *   Helper class for DPMRightsScaleStructs validation and use.
 *   This class exists to make sure that the DPMRights behavior is consistent across services.
 *
 *   The lowNbrParticipants is inclusive and the highNbrParticipants is exclusive
 *   The First one has to be 0 for the low.
 *   No Gap is allowed in between scales.
 *   @author Connie Liang
 */
public class DPMRightsScalesHelper
{
	// a convenient constant for exception building
	//
	private static final int INVALID_SCALE = com.cboe.idl.cmiErrorCodes.DataValidationCodes.INVALID_COVERAGE;

    static public String toString(DpmRightsScaleStruct struct)
    {
        if(struct == null)
        {
            return " NULL";
        }

        StringBuilder buf = new StringBuilder(100);
        buf.append(" lowNbrParticipants: ")
        .append(struct.lowNbrParticipants)
        .append(" highNbrParticipants: ")
        .append(struct.highNbrParticipants)
        .append(" scalePercentage: ")
        .append(struct.scalePercentage);
        return buf.toString();
    }
	/**
	 *  Validates that the structArray's values fully describe the scale.
	 *  The min:max pairs should 'chain' to cover the minimum required range: ex,
	 *
	 *  <ul>
	 *  	<li> all values must be positive, the first low will have to be 0.
	 *  	<li> for an DpmRightsScaleStruct, low must be less than high
	 *		<li> no [low:high) range can overlap with another.
	 *  </ul>
	 *
	 *  @param structArray - the DpmRightsScaleStruct array which should fully described the price range [0:minSpreadRequired)
	 *  @param minRequired - the <code>structArray</code> must fully cover [0:minSpreadRequired).
	 *  @param v - ignored if negative.  All DpmRightsScaleStruct.maximumAllowableSpread values must
	 *		be leq this value.
	 *  @exception DataValidationException - thrown if any validation conditions are violated.
	 */
	static public void validateDPMRightsScales(DpmRightsScaleStruct[] structArray)
		throws DataValidationException
	{
        int low = 0;
        int prevhigh = 0;

		for (int i=0; i < structArray.length; i++)
		{
			DpmRightsScaleStruct dpmScale = structArray[i];

            if(i == 0)  // only do this to init the first one so prevHigh = first low
            {
                prevhigh =  dpmScale.lowNbrParticipants;
                if(dpmScale.lowNbrParticipants != 0)
                {
                    throw ExceptionBuilder.dataValidationException(" lowNbrParticipants Value in first DpmRightsScaleStruct should be 0", INVALID_SCALE);
                }
            }

			if (dpmScale.lowNbrParticipants < 0 || dpmScale.highNbrParticipants < 1 )
			{
				throw ExceptionBuilder.dataValidationException(" lowNbrParticipants should be >=0 and highNbrParticipants should be >=1 in DpmRightsScaleStruct["+i+"]", INVALID_SCALE);
			}
            if (dpmScale.scalePercentage <0 || dpmScale.scalePercentage > 1)
            {
				throw ExceptionBuilder.dataValidationException(" scalePercentage Value in DpmRightsScaleStruct["+i+"] should be >= 0 and <= 1 ", INVALID_SCALE);
            }
			if (dpmScale.lowNbrParticipants >= dpmScale.highNbrParticipants)
			{
				throw ExceptionBuilder.dataValidationException("lowNbrParticipants >= highNbrParticipants in DpmRightsScaleStruct["+i+"]", INVALID_SCALE);
			}

            if( dpmScale.lowNbrParticipants != prevhigh )
            {
                throw ExceptionBuilder.dataValidationException("Gap between lowNbrParticipants and highNbrParticipants in DpmRightsScaleStruct["+i+"] and its previous range", INVALID_SCALE);
            }
            prevhigh = dpmScale.highNbrParticipants;
		}

	}
}
