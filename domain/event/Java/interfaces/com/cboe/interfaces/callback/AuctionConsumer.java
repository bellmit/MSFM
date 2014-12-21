package com.cboe.interfaces.callback;

import com.cboe.idl.cmiCallbackV3.CMIAuctionConsumerOperations;

/** Interface for CAS to notify user of an Auction. This simply renames the
 * interface created by the IDL compiler, to isolate the rest of the system
 * from possible changes in the IDL compiler.
 */
public interface AuctionConsumer extends CMIAuctionConsumerOperations
{
}
