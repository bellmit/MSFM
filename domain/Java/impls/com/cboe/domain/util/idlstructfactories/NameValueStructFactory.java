package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.util.NameValueStruct;;
public class NameValueStructFactory extends com.cboe.domain.util.TLSObjectPool<NameValueStruct> 
			implements com.cboe.idl.util.NameValueStructFactory {
	public NameValueStruct createNewInstance() {
		return new NameValueStruct ();
	}

	public void clear (NameValueStruct value) {
		value.keyName = "";
		value.value = "";
	}
	public NameValueStruct create(java.lang.String keyName, java.lang.String value)
	{
	    NameValueStruct rval = acquire();
		rval.keyName=keyName;
		rval.value=value;
		return rval;
	}
}
