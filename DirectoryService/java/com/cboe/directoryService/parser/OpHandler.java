package com.cboe.directoryService.parser;

public interface OpHandler
{
	public boolean performOp(com.cboe.directoryService.persist.TraderOffer anOffer, java.util.Stack opStack);
}
