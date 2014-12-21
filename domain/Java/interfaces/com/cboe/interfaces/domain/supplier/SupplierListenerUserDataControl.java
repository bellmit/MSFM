package com.cboe.interfaces.domain.supplier;

public interface SupplierListenerUserDataControl
{
    public Object getListenerUserData();
    public void addListenerUserData(String data);
    public void removeListenerUserData(String data);
}
