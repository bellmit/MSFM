package com.cboe.interfaces.domain.user;

//----------------------------------------------------------------------
// Source file: Java/com/cboe/interfaces/domain/user/UserEnablementElement.java
//
// PACKAGE: com.cboe.domain.user
//----------------------------------------------------------------------
// Copyright (c) 1999 The Chicago Board Options Exchange. All Rights Reserved.
//----------------------------------------------------------------------

/**
 * Describes an element of user enablement.  
 *
 * @author Steven Sinclair
 */
public interface UserEnablementElement
{
	public String getUserId();
	public int getUserKey();
	public String getSessionName();
	public short getProductType();

	public void setUserId(String aValue);
	public void setUserKey(int aValue);
	public void setSessionName(String aValue);
	public void setProductType(short aValue);
}

