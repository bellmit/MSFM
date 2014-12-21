// -----------------------------------------------------------------------------------
// Source file: SessionProductContainer.java
//
// PACKAGE: com.cboe.interfaces.presentation.product;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2003 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.interfaces.presentation.product;

public interface SessionProductContainer extends ProductContainer
{
    public SessionProduct getContainedSessionProduct();
    public SessionProductClass getContainedSessionProductClass();
}
