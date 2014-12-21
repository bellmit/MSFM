package com.cboe.domain.util;

import com.cboe.idl.user.*;

/**
 * Used to build default valued CORBA structs.  This class is separated into ClientUserStructBuilder
 * that it is extending to handle structs in cmiUser.IDL
 *
 * Any maitenance on cmiUser structs should be performed in ClientUserStructBuilder
 *
 *
 * @author John Wickberg
 */
public class UserStructBuilder extends ClientUserStructBuilder
{
/**
 * All methods are static, no instance needed.
 *
 * @author John Wickberg
 */
private UserStructBuilder()
{
	super();
}
/**
 * Creates a default valued login struct.
 *
 * @return struct with default values
 *
 * @author John Wickberg
 */
public static LoginStruct buildLoginStruct()
{
	return new LoginStruct("", "", "");
}
}
