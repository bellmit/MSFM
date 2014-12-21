package com.cboe.interfaces.application;

import com.cboe.idl.cmi.*;

/**
 * This extends the CORBA Interface into a CBOE Common standard
 * @author Connie Feng
 */
public interface Quote extends QuoteOperations, QuoteV2, QuoteV3
{
}
