package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiUtil.TimeStruct;
public class TimeStructFactory extends com.cboe.domain.util.TLSObjectPool<TimeStruct> 
			implements com.cboe.idl.cmiUtil.TimeStructFactory {
	public TimeStruct createNewInstance() {
		return new TimeStruct ();
	}

	public void clear (TimeStruct value) {
		value.hour = 0;
		value.minute = 0;
		value.second = 0;
		value.fraction = 0;
	}
	public  TimeStruct create(byte hour, byte minute, byte second, byte fraction)
	{
		TimeStruct rval = acquire();
		rval.hour=hour;
		rval.minute=minute;
		rval.second=second;
		rval.fraction=fraction;
		return rval;
	}
}
