package com.cboe.delegates.application;

import com.cboe.interfaces.application.Administrator;

public class AdministratorDelegate extends com.cboe.idl.cmi.POA_Administrator_tie {
    public AdministratorDelegate(Administrator delegate) {
        super(delegate);
    }
}
