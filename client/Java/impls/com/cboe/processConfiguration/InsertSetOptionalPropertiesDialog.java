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

public class InsertSetOptionalPropertiesDialog extends JDialog {
    JPanel jPanel1 = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();
    boolean wasCancelled = true;
    JTable setOptionalPropertiesTable = new JTable(0,2);
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    public InsertSetOptionalPropertiesDialog(Frame frame, String title, boolean modal) {
        super(frame, title, modal);
        try {
            jbInit();
            pack();
        }
        catch(Exception ex) {
            ex.printStackTrace();
        }
    }

    public InsertSetOptionalPropertiesDialog() {
        this(null, "", false);
    }
    void jbInit() throws Exception {
        this.setModal(true);
        this.setResizable(false);
        jPanel1.setLayout(gridBagLayout1);
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
        setOptionalPropertiesTable.setBorder(BorderFactory.createLoweredBevelBorder());
        setOptionalPropertiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        setOptionalPropertiesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                setOptionalPropertiesTable_mouseClicked(e);
            }
        });
        setOptionalPropertiesTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                setOptionalPropertiesTable_keyPressed(e);
            }
        });
        this.getContentPane().add(jPanel1, BorderLayout.CENTER);
        jPanel1.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(45, 53, 35, 25), 0, 0));
        jPanel1.add(okButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(46, 494, 35, 0), 23, 0));
        jPanel1.add(setOptionalPropertiesTable, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(24, 8, 0, 8), 553, 349));
//        setOptionalPropertiesTable.setHeaderValue("Property Name");
//        setOptionalPropertiesTable.setHeaderValue("Property Value");
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
    void setOptionalPropertiesTable_mouseClicked(MouseEvent e) {

    }
    void setOptionalPropertiesTable_mouseEntered(MouseEvent e) {

    }
    void setOptionalPropertiesTable_mouseExited(MouseEvent e) {

    }
    void setOptionalPropertiesTable_mousePressed(MouseEvent e) {

    }
    void setOptionalPropertiesTable_mouseReleased(MouseEvent e) {

    }
    void setOptionalPropertiesTable_keyPressed(KeyEvent e) {

    }
    void setOptionalPropertiesTable_keyReleased(KeyEvent e) {

    }
    void setOptionalPropertiesTable_keyTyped(KeyEvent e) {

    }
}
