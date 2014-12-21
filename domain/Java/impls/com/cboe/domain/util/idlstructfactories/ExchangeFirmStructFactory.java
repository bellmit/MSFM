package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiUser.ExchangeFirmStruct;
public class ExchangeFirmStructFactory extends com.cboe.domain.util.TLSObjectPool<ExchangeFirmStruct> 
			implements com.cboe.idl.cmiUser.ExchangeFirmStructFactory {
	public ExchangeFirmStruct createNewInstance() {
		return new ExchangeFirmStruct ();
	}

	public void clear (ExchangeFirmStruct value) {
		value.exchange = null;
		value.firmNumber = null;
	}
	public  ExchangeFirmStruct create(java.lang.String exchange, java.lang.String firmNumber)
	{
		ExchangeFirmStruct rval = acquire();
		rval.exchange=exchange;
		rval.firmNumber=firmNumber;
		return rval;
	}
}
