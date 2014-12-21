package com.cboe.presentation.user;

import com.cboe.interfaces.presentation.user.Account;
import com.cboe.idl.cmiUser.AccountStruct;


public class AccountFactory
{
    static public final Account createAccount(AccountStruct anAccountStruct)
    {

        return new AccountModelImpl(anAccountStruct);
    }

    static public final Account [] createAccounts(AccountStruct [] accountStructs)
    {
        Account [] accountList = null;

        if(accountStructs != null)
        {
            int length = accountStructs.length;
            accountList = new AccountModelImpl[length];

            for(int x = 0; x < length; x++)
            {
                accountList[x] = createAccount(accountStructs[x]);
            }
        }
        return accountList;
    }

}