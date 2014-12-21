package com.cboe.domain.util.idlstructfactories;
import com.cboe.idl.cmiOrder.OrderContingencyStruct;
public class OrderContingencyStructFactory extends com.cboe.domain.util.TLSObjectPool<OrderContingencyStruct> 
			implements com.cboe.idl.cmiOrder.OrderContingencyStructFactory {
	public OrderContingencyStruct createNewInstance() {
		return new OrderContingencyStruct ();
	}

	public void clear (OrderContingencyStruct value) {
		value.type = 0;
		value.price = null;
		value.volume = 0;
	}
	public  OrderContingencyStruct create(short type, com.cboe.idl.cmiUtil.PriceStruct price, int volume)
	{
		OrderContingencyStruct rval = acquire();
		rval.type=type;
		rval.price=price;
		rval.volume=volume;
		return rval;
	}
}
