package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiUser.ExchangeAcronymStruct;
public class ExchangeAcronymStructFactory extends com.cboe.domain.util.TLSObjectPool<ExchangeAcronymStruct> 
			implements com.cboe.idl.cmiUser.ExchangeAcronymStructFactory {
	public ExchangeAcronymStruct createNewInstance() {
		return new ExchangeAcronymStruct ();
	}

	public void clear (ExchangeAcronymStruct value) {
		value.exchange = null;
		value.acronym = null;
	}
	public  ExchangeAcronymStruct create(java.lang.String exchange, java.lang.String acronym)
	{
		ExchangeAcronymStruct rval = acquire();
		rval.exchange=exchange;
		rval.acronym=acronym;
		return rval;
	}
}
