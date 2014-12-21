//
// -----------------------------------------------------------------------------------
// Source file: FavoriteCommandNameComparator.java
//
// PACKAGE: com.cboe.presentation.common.comparators
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.common.comparators;

import java.util.*;

import com.cboe.interfaces.instrumentation.adminRequest.savedCommand.FavoriteCommand;

public class FavoriteCommandNameComparator implements Comparator<FavoriteCommand>
{
    public int compare(FavoriteCommand command1, FavoriteCommand command2)
    {
        return command1.getCommand().getName().compareTo(command2.getCommand().getName());
    }
}
