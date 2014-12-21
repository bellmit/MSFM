package com.cboe.directoryService.parser;

import com.cboe.directoryService.persist.TraderOffer;
//import com.cboe.loggingService.Log;

public class NotObject
implements OpHandler, DirectoryServiceParserConstants
{
	public boolean performOp(TraderOffer anOffer, java.util.Stack theStack)
	{
		boolean rhs = ((Boolean)theStack.pop()).booleanValue();
		boolean retVal = !rhs;

		//Log.trace(this, "performOp: NOT " + rhs + " = " + retVal);
		return retVal;
	}
}
