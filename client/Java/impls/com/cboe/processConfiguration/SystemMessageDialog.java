package com.cboe.processConfiguration;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.border.*;

public class SystemMessageDialog extends JDialog implements ActionListener {

    JPanel panel1 = new JPanel();
    JButton okButton = new JButton();
    JButton cancelButton = new JButton();
    JTextArea messageText = new JTextArea();
    GridBagLayout gridBagLayout1 = new GridBagLayout();
    public static boolean   okButtonPressed = false;
    public SystemMessageDialog(Frame parent) {
        super(parent);
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
        pack();
    }
    /**Component initialization*/
    private void jbInit() throws Exception  {
        //imageLabel.setIcon(new ImageIcon(SystemMessageDialog.class.getResource("[Your Image]")));
        this.setTitle("System Message");
        setResizable(false);
        panel1.setLayout(gridBagLayout1);
        okButton.setText("Ok");
        okButton.addActionListener(this);
        cancelButton.addActionListener(this);
        cancelButton.setActionCommand("Cancel");
        cancelButton.setText("Cancel");
        messageText.setLineWrap(true);
        messageText.setWrapStyleWord(true);
        messageText.setBorder(BorderFactory.createLoweredBevelBorder());
        messageText.setBackground(Color.lightGray);
        messageText.setEditable(false);
        this.getContentPane().add(panel1, null);
        panel1.add(messageText, new GridBagConstraints(0, 0, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(16, 14, 0, 15), 367, 198));
        panel1.add(cancelButton, new GridBagConstraints(1, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 74, 18, 83), 7, 0));
        panel1.add(okButton, new GridBagConstraints(0, 1, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(20, 83, 18, 0), 31, 0));
    }
    /**Overridden so we can exit when window is closed*/
    protected void processWindowEvent(WindowEvent e) {
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            cancel();
        }
        super.processWindowEvent(e);
    }
    /**Close the dialog*/
    void cancel() {
        dispose();
    }
    /**Close the dialog on a button event*/
    public void actionPerformed(ActionEvent e) {
        if (e.getSource() == okButton) {
            okButtonPressed = true;
            cancel();
        }
        if (e.getSource() == cancelButton) {
            okButtonPressed = false;
            cancel();
        }
    }
}
