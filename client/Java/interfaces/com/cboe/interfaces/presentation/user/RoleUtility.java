package com.cboe.interfaces.presentation.user;

import com.cboe.interfaces.presentation.product.SessionProduct;

public interface RoleUtility
{
	ExchangeFirm getDefaultGiveUpFirm(SessionProduct sessionProduct);

	ExchangeFirm[] getGiveUpFirms(SessionProduct sessionProduct);

	boolean isGiveUpFirmUpdateAllowed();

}
