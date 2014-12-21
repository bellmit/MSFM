package com.cboe.interfaces.presentation.threading;

import java.awt.event.ActionListener;

public interface APIWorker extends ActionListener
{
    public void process();
    public GUIWorker getWorker();
}