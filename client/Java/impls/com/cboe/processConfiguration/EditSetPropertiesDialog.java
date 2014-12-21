package com.cboe.processConfiguration;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;

/**
 * Title:        Client Application Server
 * Description:  Your description
 * Copyright:    Copyright (c) 1998
 * Company:      Your Company
 * @author Your Name
 * @version
 */

public class EditSetPropertiesDialog extends JDialog {
    JPanel jPanel1 = new JPanel();
    JLabel propertyNameLable = new JLabel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();
    JTextField propertyValueField = new JTextField();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    boolean wasCancelled = true;

    public EditSetPropertiesDialog(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
            pack();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public EditSetPropertiesDialog() {
        this(null, "", false);
    }
    void jbInit() throws Exception {
        this.setModal(true);
        this.setResizable(false);
        jPanel1.setLayout(gridBagLayout1);
        propertyNameLable.setText("text");
        okButton.setHorizontalTextPosition(SwingConstants.CENTER);
        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                okButton_actionPerformed(e);
            }
        });
        cancelButton.setHorizontalTextPosition(SwingConstants.CENTER);
        cancelButton.setText("Cancel");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(ActionEvent e) {
                cancelButton_actionPerformed(e);
            }
        });
        propertyValueField.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                propertyValueField_keyPressed(e);
            }
        });
        this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        jPanel1.add(propertyValueField, new GridBagConstraints(0, 1, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(10, 22, 0, 19), 757, 0));
        jPanel1.add(okButton, new GridBagConstraints(0, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(33, 22, 23, 0), 23, 0));
        jPanel1.add(propertyNameLable, new GridBagConstraints(0, 0, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(14, 22, 0, 384), 378, 0));
        jPanel1.add(cancelButton, new GridBagConstraints(1, 2, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(32, 26, 23, 607), 0, 0));
    }

    void acceptResults( boolean accept )
    {
        wasCancelled = !accept;
    }

    void okButton_actionPerformed(ActionEvent e) {
        acceptResults( true );
        dispose();
    }

    void cancelButton_actionPerformed(ActionEvent e) {
        acceptResults( false );
        dispose();
    }

    void propertyValueField_keyPressed(KeyEvent e) {
        if ( e.getKeyCode() == e.VK_ENTER )
        {
            acceptResults( true );
            dispose();
        }
        else if ( e.getKeyCode() == e.VK_ESCAPE )
        {
            acceptResults( false );
            dispose();
        }
    }
}
