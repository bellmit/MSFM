package com.cboe.delegates.floorApplication;

import com.cboe.interfaces.floorApplication.NBBOService;

public class NBBOServiceDelegate 
		extends	com.cboe.idl.floorApplication.POA_NBBOService_tie{
	public NBBOServiceDelegate(NBBOService delegate) {
		super(delegate);
	}
}
