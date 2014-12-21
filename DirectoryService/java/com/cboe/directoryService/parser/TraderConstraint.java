package com.cboe.directoryService.parser;

import com.cboe.directoryService.persist.TraderOffer;
import java.util.ArrayList;
import java.util.Stack;

public class TraderConstraint
{
	private ArrayList constraintParts;

	public TraderConstraint()
	{
		constraintParts = new ArrayList();
	}

	public ArrayList getConstraints()
	{
		return constraintParts;
	}

	public boolean haveConstraints()
	{
		return !constraintParts.isEmpty();
	}

	public void addToList(OpHandler aHandler)
	{
		constraintParts.add(aHandler);
	}

	public boolean evaluateAgainst(TraderOffer anOffer)
	{
		Stack theStack = new Stack();
		int partsLen = constraintParts.size();
		boolean retVal = true;
		boolean opResult = false;
		if ( partsLen > 0 ) {
			for (int i=0; i<partsLen; i++) {
				OpHandler aHandler = (OpHandler)constraintParts.get(i);
				opResult = aHandler.performOp(anOffer, theStack);
				theStack.push( new Boolean(opResult) );
			}
			retVal = ((Boolean)theStack.pop()).booleanValue();
		}

		return retVal;
	}
}
