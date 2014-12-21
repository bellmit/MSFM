package com.cboe.directoryService.parser;

import com.cboe.directoryService.persist.TraderOffer;
//import com.cboe.loggingService.Log;

public class OrObject
implements OpHandler, DirectoryServiceParserConstants
{
	public boolean performOp(TraderOffer anOffer, java.util.Stack theStack)
	{
		boolean rhs = ((Boolean)theStack.pop()).booleanValue();
		boolean lhs = ((Boolean)theStack.pop()).booleanValue();
		boolean retVal = lhs | rhs;

		//Log.trace(this, "performOp: " + lhs + " OR " + rhs + " = " + retVal);
		return retVal;
	}
}
