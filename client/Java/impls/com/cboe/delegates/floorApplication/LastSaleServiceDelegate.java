package com.cboe.delegates.floorApplication;
import com.cboe.interfaces.floorApplication.LastSaleService;
public class LastSaleServiceDelegate  
		extends	com.cboe.idl.floorApplication.POA_LastSaleService_tie   {
	public LastSaleServiceDelegate(LastSaleService delegate) {
		super(delegate);
	}

}
