//
// -----------------------------------------------------------------------------------
// Source file: Role.java
//
// PACKAGE: com.cboe.interfaces.presentation.user
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.user;

import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.BOOTH_OMT_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.BROKER_DEALER_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.CLASS_DISPLAY_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.CROWD_OMT_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.CUSTOMER_BROKER_DEALER_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.DISPLAY_OMT_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.DPM_ROLE_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.EXCHANGE_BROKER_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.EXPECTED_OPENING_PRICE_ROLE_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.FIRM_DISPLAY_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.FIRM_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.HELPDESK_OMT_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.HELP_DESK_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.MARKET_MAKER_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.OPRA_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.PRODUCT_MAINTENANCE_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.REPORTING_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.TFL_ROLE_STRING;
import static com.cboe.interfaces.presentation.common.formatters.UserRoleFormatStrategy.UNKNOWN_ROLE_STRING;

import com.cboe.idl.cmiConstants.UserRoles;

public enum Role
{
	BOOTH_OMT(UserRoles.BOOTH_OMT, BOOTH_OMT_STRING),
	BROKER_DEALER(UserRoles.BROKER_DEALER, BROKER_DEALER_STRING),
	CLASS_DISPLAY(UserRoles.CLASS_DISPLAY, CLASS_DISPLAY_STRING),
	CROWD_OMT(UserRoles.CROWD_OMT, CROWD_OMT_STRING),
	CUSTOMER_BROKER_DEALER(UserRoles.CUSTOMER_BROKER_DEALER, CUSTOMER_BROKER_DEALER_STRING),
	DISPLAY_OMT(UserRoles.DISPLAY_OMT, DISPLAY_OMT_STRING),
	DPM(UserRoles.DPM_ROLE, DPM_ROLE_STRING),
	EOP(UserRoles.EXPECTED_OPENING_PRICE_ROLE, EXPECTED_OPENING_PRICE_ROLE_STRING),
	EXCHANGE_BROKER(UserRoles.EXCHANGE_BROKER, EXCHANGE_BROKER_STRING),
	FIRM(UserRoles.FIRM, FIRM_STRING),
	FIRM_DISPLAY(UserRoles.FIRM_DISPLAY, FIRM_DISPLAY_STRING),
	HELPDESK_OMT(UserRoles.HELP_DESK_OMT, HELPDESK_OMT_STRING),
	HELP_DESK(UserRoles.HELP_DESK, HELP_DESK_STRING),
	MARKET_MAKER(UserRoles.MARKET_MAKER, MARKET_MAKER_STRING),
	OPRA(UserRoles.OPRA, OPRA_STRING),
	PRODUCT_MAINTENANCE(UserRoles.PRODUCT_MAINTENANCE, PRODUCT_MAINTENANCE_STRING),
	REPORTING(UserRoles.REPORTING, REPORTING_STRING),
	TFL(UserRoles.TFL_ROLE, TFL_ROLE_STRING),
	UNKNOWN(UserRoles.UNKNOWN_ROLE, UNKNOWN_ROLE_STRING);

	private final char code;
	private final String displayString;

	private Role(char code, String displayString)
	{
		this.code = code;
		this.displayString = displayString;
	}

	public String getName()
	{
		return displayString;
	}

	public char getRoleChar()
	{
		return code;
	}

	@Override
	public String toString()
	{
		return getName();
	}
}
