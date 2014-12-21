package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiUtil.DateStruct;
public class DateStructFactory extends com.cboe.domain.util.TLSObjectPool<DateStruct> 
			implements com.cboe.idl.cmiUtil.DateStructFactory {
	public DateStruct createNewInstance() {
		return new DateStruct ();
	}

	public void clear (DateStruct value) {
		value.month = 0;
		value.day = 0;
		value.year = 0;
	}
	public  DateStruct create(byte month, byte day, short year)
	{
		DateStruct rval = acquire();
		rval.month=month;
		rval.day=day;
		rval.year=year;
		return rval;
	}
}
