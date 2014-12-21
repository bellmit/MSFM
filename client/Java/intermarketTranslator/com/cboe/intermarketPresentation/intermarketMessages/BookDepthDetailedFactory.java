//
// ------------------------------------------------------------------------
// FILE: BookDepthDetailedFactory.java
// 
// PACKAGE: com.cboe.intermarketPresentation.intermarketMessages
// 
// ------------------------------------------------------------------------
// Copyright (c) 1999-2003 The Chicago Board Options Exchange.  All Rights Reserved.
// 
// ------------------------------------------------------------------------
//

package com.cboe.intermarketPresentation.intermarketMessages;

import com.cboe.idl.cmiIntermarketMessages.BookDepthDetailedStruct;
import com.cboe.interfaces.intermarketPresentation.intermarketMessages.BookDepthDetailed;

public class BookDepthDetailedFactory
{
    public static BookDepthDetailed createBookDepthDetailed(BookDepthDetailedStruct bookDepthDetailedStruct)
    {
        return new BookDepthDetailedImpl(bookDepthDetailedStruct);
    }
}