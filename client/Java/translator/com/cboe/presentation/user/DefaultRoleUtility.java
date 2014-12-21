package com.cboe.presentation.user;

import com.cboe.interfaces.presentation.permissionMatrix.Permission;
import com.cboe.interfaces.presentation.permissionMatrix.UserPermissionMatrix;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.RoleUtility;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.presentation.permissionMatrix.PermissionMatrixFactory;
import com.cboe.presentation.userSession.UserSessionFactory;

public class DefaultRoleUtility implements RoleUtility
{

	@Override
	public ExchangeFirm getDefaultGiveUpFirm(SessionProduct sessionProduct)
	{
		UserStructModel userModel = UserSessionFactory.findUserSession().getUserModel();
		return userModel.getDefaultProfile().getExecutingGiveupFirm();
	}

	@Override
	public ExchangeFirm[] getGiveUpFirms(SessionProduct sessionProduct)
	{
		UserStructModel userModel = UserSessionFactory.findUserSession().getUserModel();
		return userModel.getExecutingGiveUpFirms();
	}

	public boolean isGiveUpFirmUpdateAllowed()
	{
		UserPermissionMatrix permissionMatrix = PermissionMatrixFactory.findUserPermissionMatrix();
		return permissionMatrix.isAllowed(Permission.EXECUTING_GIVE_UP_FIRM_UPDATE);
	}

}
