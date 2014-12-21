package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiUtil.DateTimeStruct;
public class DateTimeStructFactory extends com.cboe.domain.util.TLSObjectPool<DateTimeStruct> 
			implements com.cboe.idl.cmiUtil.DateTimeStructFactory {
	public DateTimeStruct createNewInstance() {
		return new DateTimeStruct ();
	}

	public void clear (DateTimeStruct value) {
		value.date = null;
		value.time = null;
	}
	public  DateTimeStruct create(com.cboe.idl.cmiUtil.DateStruct date, com.cboe.idl.cmiUtil.TimeStruct time)
	{
		DateTimeStruct rval = acquire();
		rval.date=date;
		rval.time=time;
		return rval;
	}
}
