package com.cboe.domain.util;

import com.cboe.idl.cmiUtil.PriceStruct;

public class PriceStructFactory implements  com.cboe.idl.cmiUtil.PriceStructFactory
{
	 public PriceStruct create(short type, int whole, int fraction)
	 {
		 return PriceFactory.createPriceStruct(type, whole, fraction);
	 }

}

