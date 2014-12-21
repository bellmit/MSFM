package com.cboe.presentation.user;

import com.cboe.idl.cmiSession.TradingSessionStruct;
import com.cboe.interfaces.presentation.product.SessionProduct;
import com.cboe.interfaces.presentation.product.SessionProductClass;
import com.cboe.interfaces.presentation.user.ExchangeFirm;
import com.cboe.interfaces.presentation.user.Profile;
import com.cboe.interfaces.presentation.user.UserStructModel;
import com.cboe.presentation.api.APIHome;
import com.cboe.presentation.product.ProductHelper;
import com.cboe.presentation.userSession.UserSessionFactory;

public class MarketMakerRoleUtility extends DefaultRoleUtility
{

	@Override
	public ExchangeFirm getDefaultGiveUpFirm(SessionProduct sessionProduct)
	{
		UserStructModel userModel = UserSessionFactory.findUserSession().getUserModel();

		Profile defaultProfile = userModel.getDefaultProfile();
		// set give up firm from the default profile
		ExchangeFirm defaultGiveUpFirm = defaultProfile.getExecutingGiveupFirm();

		SessionProductClass orderProductClass = ProductHelper.getSessionProductClass(sessionProduct.getTradingSessionName(), sessionProduct
		        .getProductKeysStruct().classKey);
		int classKey = orderProductClass.getClassKey();
		String sessionName = orderProductClass.getTradingSessionName();
		TradingSessionStruct defaultTradingSession = APIHome.findTradingSessionAPI().getAllSessionsTradingSession();
		Profile[] profiles = userModel.getProfiles();

		for (int i = 0; i < profiles.length; i++)
		{
			Profile profile = profiles[i];
			if (profile.getSessionName().equals(sessionName) || profile.getSessionName().equals(defaultTradingSession.sessionName))
			{
				SessionProductClass profileSPC = profile.getProductClass();
				if (profileSPC.isDefaultProductClass() || profileSPC.getClassKey() == classKey)
				{
					defaultGiveUpFirm = profile.getExecutingGiveupFirm();
				}

				if (profile.getSessionName().equals(sessionName) && profileSPC.getClassKey() == classKey)
				{
					break;
				}
			}
		}

		return defaultGiveUpFirm;
	}

	@Override
	public ExchangeFirm[] getGiveUpFirms(SessionProduct sessionProduct)
	{
		return new ExchangeFirm[] {getDefaultGiveUpFirm(sessionProduct)};
	}

}
