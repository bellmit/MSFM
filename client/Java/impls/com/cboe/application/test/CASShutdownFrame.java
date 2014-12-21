package com.cboe.application.test;

import com.cboe.interfaces.application.*;
import com.cboe.application.session.*;
import com.cboe.application.shared.*;
import com.cboe.infrastructureServices.foundationFramework.*;
import com.cboe.util.*;
import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import com.cboe.infrastructureServices.foundationFramework.utilities.*;

public class CASShutdownFrame extends JFrame
                              implements ActionListener
{
    private String  title           = "CAS Control";
    private CASShutdownImpl parent  = null;

    public CASShutdownFrame(CASShutdownImpl parent, String titletext)
    {
        super();
        this.parent = parent;

        if (titletext != null && titletext != " ")
        {
            title = titletext + " - " + title;
        }

        setTitle(title);
        init();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        setVisible(true);
    }

    private void init()
    {
        addWindowListener(
            new WindowAdapter() {
                public void windowClosing() {
                    System.exit(0);
                }
            }
        );

        setSize(200, 50);
        getContentPane().setLayout(new BorderLayout());

        JPanel shutdownPanel = new JPanel(new BorderLayout());

        JButton shutdownButton = new JButton("Shutdown CAS");
        shutdownButton.addActionListener(this);
        shutdownButton.setActionCommand("SHUTDOWN");

        shutdownPanel.add(shutdownButton, BorderLayout.CENTER);
        getContentPane().add(shutdownPanel, BorderLayout.CENTER);
    }

    public void setVisible(boolean value)
    {
        super.setVisible(value);
    }

    public void actionPerformed(ActionEvent evt)
    {
        String command = evt.getActionCommand();
        if (command.equals("SHUTDOWN"))
        {
            parent.shutdownCAS();
        }

    }

}
