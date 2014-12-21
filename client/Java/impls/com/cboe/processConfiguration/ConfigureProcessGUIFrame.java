package com.cboe.processConfiguration;

import java.util.StringTokenizer;

import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import javax.swing.table.*;
import java.io.File;

public class ConfigureProcessGUIFrame extends JFrame {
    ConfigureProcessGUIHelper   configProcHelper            = new ConfigureProcessGUIHelper();
    private static boolean      internalLoading             = false;
    private static int          COL_HOME_NAME               = 0;
    private static int          COL_PROPERTY_NAME           = 1;
    private static int          COL_PROPERTY_VALUE          = 2;
    private static int          COL_OPT_PROPERTY_NAME       = 0;
    private static int          COL_OPT_PROPERTY_VALUE      = 1;
    private String              xmlFilePath                 = "";
    private String              xmlFileName                 = "";
    private boolean             changesMade                 = false;
    private boolean             haveSaved                   = false;
    JPanel contentPane;
    JMenuBar jMenuBar1 = new JMenuBar();
    JMenu jMenuFile = new JMenu();
    JMenuItem jMenuFileExit = new JMenuItem();
    JMenu jMenuHelp = new JMenu();
    JMenuItem jMenuHelpAbout = new JMenuItem();
    JToolBar jToolBar = new JToolBar();
    JButton jButton1 = new JButton();
    JButton jButton2 = new JButton();
    JButton jButton3 = new JButton();
    ImageIcon image1;
    ImageIcon image2;
    ImageIcon image3;
    JLabel statusBar = new JLabel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel mainPanel = new JPanel();
    JLabel processNameLabel = new JLabel();
    JTextField processNameField = new JTextField();
    JLabel setPropertiesLabel = new JLabel();
    JLabel setOptionalPropertiesLabel = new JLabel();
    JMenuItem jMenuFileOpen = new JMenuItem();
    JMenuItem jMenuFileSave = new JMenuItem();
    JTable setPropertiesTable = new JTable(0,3);
    JTable setOptionalPropertiesTable = new JTable(0,2);
    JButton setPropertiesInsertButton = new JButton();
    JButton setPropertiesDeleteButton = new JButton();
    JButton setOptPropertiesInsertButton = new JButton();
    JButton setOptPropertiesDeleteButton = new JButton();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    /**Construct the frame*/
    public ConfigureProcessGUIFrame() {
        enableEvents(AWTEvent.WINDOW_EVENT_MASK);
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    /**Component initialization*/
    private void jbInit() throws Exception  {
        image1 = new ImageIcon(com.cboe.processConfiguration.ConfigureProcessGUIFrame.class.getResource("openFile.gif"));
        image2 = new ImageIcon(com.cboe.processConfiguration.ConfigureProcessGUIFrame.class.getResource("closeFile.gif"));
        image3 = new ImageIcon(com.cboe.processConfiguration.ConfigureProcessGUIFrame.class.getResource("help.gif"));
        //setIconImage(Toolkit.getDefaultToolkit().createImage(ConfigureProcessGUIFrame.class.getResource("[Your Icon]")));
        contentPane = (JPanel) this.getContentPane();
        contentPane.setLayout(borderLayout1);
        this.setSize(new Dimension(833, 595));
        this.setTitle("Configure Process");
        statusBar.setText(" ");
        jMenuFile.setText("File");
        jMenuFileExit.setText("Exit");
        jMenuFileExit.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent e) {
                jMenuFileExit_actionPerformed(e);
            }
        });
        jMenuHelp.setText("Help");
        jMenuHelpAbout.setText("About");
        jMenuHelpAbout.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent e) {
                jMenuHelpAbout_actionPerformed(e);
            }
        });
        jButton1.setIcon(image1);
        jButton1.setToolTipText("Open File");
        jButton2.setIcon(image2);
        jButton2.setToolTipText("Close File");
        jButton3.setIcon(image3);
        jButton3.setToolTipText("Help");
        mainPanel.setLayout(gridBagLayout1);
        processNameLabel.setText("Process Name:");
        processNameField.setEditable(false);
        setPropertiesLabel.setText("Set Properties:");
        setOptionalPropertiesLabel.setText("Set Optional Properties:");
        jMenuFileOpen.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent e) {
                jMenuFileOpen_actionPerformed(e);
            }
        });
        jMenuFileOpen.setText("Open");
        jMenuFileSave.addActionListener(new ActionListener()  {
            public void actionPerformed(ActionEvent e) {
                jMenuFileSave_actionPerformed(e);
            }
        });
        jMenuFileSave.setText("Save");
        setPropertiesTable.setBorder(BorderFactory.createLoweredBevelBorder());
        setPropertiesTable.setAutoResizeMode(JTable.AUTO_RESIZE_NEXT_COLUMN);
        setPropertiesTable.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                setPropertiesTable_mouseClicked(e);
            }
        });
        setPropertiesTable.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                setPropertiesTable_keyPressed(e);
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
        setPropertiesInsertButton.setEnabled(false);
        setPropertiesInsertButton.setText("Insert");
        setPropertiesInsertButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setPropertiesInsertButton_mouseReleased(e);
            }
        });
        setPropertiesDeleteButton.setEnabled(false);
        setPropertiesDeleteButton.setText("Delete");
        setPropertiesDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setPropertiesDeleteButton_mouseReleased(e);
            }
        });
        setOptPropertiesInsertButton.setEnabled(false);
        setOptPropertiesInsertButton.setText("Insert");
        setOptPropertiesInsertButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setOptPropertiesInsertButton_mouseReleased(e);
            }
        });
        setOptPropertiesDeleteButton.setEnabled(false);
        setOptPropertiesDeleteButton.setText("Delete");
        setOptPropertiesDeleteButton.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                setOptPropertiesDeleteButton_mouseReleased(e);
            }
        });
        jToolBar.add(jButton1);
        jToolBar.add(jButton2);
        jToolBar.add(jButton3);
        jMenuFile.add(jMenuFileOpen);
        jMenuFile.add(jMenuFileSave);
        jMenuFile.add(jMenuFileExit);
        jMenuHelp.add(jMenuHelpAbout);
        jMenuBar1.add(jMenuFile);
        jMenuBar1.add(jMenuHelp);
        this.setJMenuBar(jMenuBar1);
        contentPane.add(jToolBar, BorderLayout.NORTH);
        contentPane.add(statusBar, BorderLayout.SOUTH);
        contentPane.add(mainPanel, BorderLayout.CENTER);
        mainPanel.add(processNameField, new GridBagConstraints(1, 0, 3, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(25, 0, 0, 30), 670, 5));
        mainPanel.add(processNameLabel, new GridBagConstraints(0, 0, 1, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(25, 30, 9, 0), 13, 0));
        mainPanel.add(setPropertiesLabel, new GridBagConstraints(0, 1, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(33, 30, 0, 50), 97, 0));
        mainPanel.add(setOptionalPropertiesLabel, new GridBagConstraints(0, 4, 2, 1, 0.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.NONE, new Insets(0, 30, 0, 0), 98, 0));
        mainPanel.add(setPropertiesTable, new GridBagConstraints(0, 2, 4, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(11, 30, 0, 30), 548, 136));
        mainPanel.add(setOptionalPropertiesTable, new GridBagConstraints(0, 5, 4, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(12, 30, 0, 30), 623, 136));
        mainPanel.add(setPropertiesDeleteButton, new GridBagConstraints(3, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 25, 0, 48), 19, 0));
        mainPanel.add(setPropertiesInsertButton, new GridBagConstraints(2, 3, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(15, 322, 0, 0), 25, 0));
        mainPanel.add(setOptPropertiesInsertButton, new GridBagConstraints(2, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(17, 322, 16, 0), 25, 0));
        mainPanel.add(setOptPropertiesDeleteButton, new GridBagConstraints(3, 6, 1, 1, 0.0, 0.0
            ,GridBagConstraints.CENTER, GridBagConstraints.NONE, new Insets(17, 25, 16, 48), 19, 0));

        setPropertiesTable.getColumnModel().getColumn(0).setHeaderValue("Home Name");
        setPropertiesTable.getColumnModel().getColumn(1).setHeaderValue("Property Name");
        setPropertiesTable.getColumnModel().getColumn(2).setHeaderValue("Property Value");

        setOptionalPropertiesTable.getColumnModel().getColumn(0).setHeaderValue("Property Name");
        setOptionalPropertiesTable.getColumnModel().getColumn(1).setHeaderValue("Property Value");

    }
    /**File | Exit action performed*/
    public void jMenuFileExit_actionPerformed(ActionEvent e) {
        if ( changesMade && ! haveSaved )
        {
            SystemMessageDialog msgDialog = new SystemMessageDialog(this);
            msgDialog.messageText.setText( "Configure Process File, \"" + configProcHelper.getProcessConfigurationName() + "\", has changed!"
                                            + "\nSave changes and exit?"
                                            );
            msgDialog.setLocationRelativeTo(this);
            msgDialog.setModal(true);
            msgDialog.okButton.setText("Yes");
            msgDialog.cancelButton.setText("No");
            msgDialog.show();
            if ( msgDialog.okButtonPressed )
            {
            saveConfigurationProcess();
            }
        }
        System.exit(0);
    }
    /**Help | About action performed*/
    public void jMenuHelpAbout_actionPerformed(ActionEvent e) {
        ConfigureProcessGUIFrame_AboutBox dlg = new ConfigureProcessGUIFrame_AboutBox(this);
        Dimension dlgSize = dlg.getPreferredSize();
        Dimension frmSize = getSize();
        Point loc = getLocation();
        dlg.setLocation((frmSize.width - dlgSize.width) / 2 + loc.x, (frmSize.height - dlgSize.height) / 2 + loc.y);
        dlg.setModal(true);
        dlg.show();
    }
    /**Overridden so we can exit when window is closed*/
    protected void processWindowEvent(WindowEvent e) {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING) {
            jMenuFileExit_actionPerformed(null);
        }
    }
    void jMenuFileOpen_actionPerformed(ActionEvent e) {
        String          fileName    = "";
        JFileChooser    fileChooser = new JFileChooser();

        fileChooser.setCurrentDirectory( new File( "." + File.separator ) );
        int returnVal = fileChooser.showOpenDialog( this );
        if ( returnVal == JFileChooser.APPROVE_OPTION )
        {
            xmlFilePath = fileName = fileChooser.getSelectedFile().getName();
            xmlFileName = fileName = fileName.substring( 0, fileName.lastIndexOf( ".xml" ) );
            if ( configProcHelper.loadProcessConfigurationInfo( fileName ) )
            {
                internalLoading = true;
                processNameField.setText( configProcHelper.getProcessConfigurationName() );

                setPropertiesTable.removeAll();
                setOptionalPropertiesTable.removeAll();

                Object[] values = configProcHelper.getSetProperties();
                setPropertiesInsertButton.setEnabled(true);
                if ( values.length > 0 ) setPropertiesDeleteButton.setEnabled(true);
                for ( int i = 0; i < values.length; i++ )
                {
                    if ( i >= setPropertiesTable.getRowCount() )
                    {
                        DefaultTableModel defTabMod = (DefaultTableModel)setPropertiesTable.getModel();
                        defTabMod.addRow( new Object[0] );
                    }
                    StringTokenizer strtok = new StringTokenizer( (String)values[ i ], " " );
                    setPropertiesTable.setValueAt(strtok.nextElement(), i, COL_HOME_NAME);
                    setPropertiesTable.setValueAt(strtok.nextElement(), i, COL_PROPERTY_NAME);
                    setPropertiesTable.setValueAt(strtok.nextElement(), i, COL_PROPERTY_VALUE);
                }

                values = configProcHelper.getSetOptionalProperties();
                setOptPropertiesInsertButton.setEnabled(true);
                if ( values.length > 0 ) setOptPropertiesDeleteButton.setEnabled(true);
                for ( int i = 0; i < values.length; i++ )
                {
                    if ( i >= setOptionalPropertiesTable.getRowCount() )
                    {
                        DefaultTableModel defTabMod = (DefaultTableModel)setOptionalPropertiesTable.getModel();
                        defTabMod.addRow( new Object[0] );
                    }
                    StringTokenizer strtok = new StringTokenizer( (String)values[ i ], " " );
                    setOptionalPropertiesTable.setValueAt(strtok.nextElement(), i, COL_OPT_PROPERTY_NAME);
                    setOptionalPropertiesTable.setValueAt(strtok.nextElement(), i, COL_OPT_PROPERTY_VALUE);
                }
                internalLoading = false;
                changesMade     = false;
                haveSaved       = false;
            }
        }
    }
    void jMenuFileSave_actionPerformed(ActionEvent e) {
        saveConfigurationProcess();
    }

    void saveConfigurationProcess()
    {
        String[]    setProperties           = new String[ setPropertiesTable.getRowCount() ];
        String[]    setOptionalProperties   = new String[ setOptionalPropertiesTable.getRowCount() ];
        String      propVal;
        int         i;

        for( i = 0; i < setProperties.length; i++ )
        {
            setProperties[ i ] =    setPropertiesTable.getValueAt( i, COL_HOME_NAME ) + " "
                                    + setPropertiesTable.getValueAt( i, COL_PROPERTY_NAME ) + " "
                                    + setPropertiesTable.getValueAt( i, COL_PROPERTY_VALUE );
        }
        configProcHelper.setSetProperties( setProperties );

        for( i = 0; i < setOptionalProperties.length; i++ )
        {
            setOptionalProperties[ i ] =    setOptionalPropertiesTable.getValueAt( i, COL_OPT_PROPERTY_NAME ) + " "
                                            + setOptionalPropertiesTable.getValueAt( i, COL_OPT_PROPERTY_VALUE );
        }
        configProcHelper.setSetOptionalProperties( setOptionalProperties );

        configProcHelper.writeProcessConfigurationInfo();
        haveSaved = true;
    }

    void editCurrentPropertyValue()
    {
        int curRow = setPropertiesTable.getSelectedRow();
        EditSetPropertiesDialog editDlg = new EditSetPropertiesDialog( this, (String)setPropertiesTable.getValueAt( curRow, COL_HOME_NAME ), true);
        editDlg.propertyNameLable.setText((String)setPropertiesTable.getValueAt( curRow, COL_PROPERTY_NAME ));
        editDlg.propertyValueField.setText((String)setPropertiesTable.getValueAt( curRow, COL_PROPERTY_VALUE ));
        editDlg.setLocationRelativeTo(this);
        editDlg.show();
        if ( !editDlg.wasCancelled )
        {
            internalLoading = true;
            setPropertiesTable.setValueAt(editDlg.propertyValueField.getText(), curRow, COL_PROPERTY_VALUE);
            internalLoading = false;
            changesMade     = true;
        }
    }

    void editCurrentOptionalPropertyValue()
    {
        int curRow = setOptionalPropertiesTable.getSelectedRow();
        EditSetPropertiesDialog editDlg = new EditSetPropertiesDialog( this, "Edit Optional Property Value", true);
        editDlg.propertyNameLable.setText((String)setOptionalPropertiesTable.getValueAt( curRow, COL_OPT_PROPERTY_NAME ));
        editDlg.propertyValueField.setText((String)setOptionalPropertiesTable.getValueAt( curRow, COL_OPT_PROPERTY_VALUE ));
        editDlg.setLocationRelativeTo(this);
        editDlg.show();
        if ( !editDlg.wasCancelled )
        {
            internalLoading = true;
            setOptionalPropertiesTable.setValueAt(editDlg.propertyValueField.getText(), curRow, 1);
            internalLoading = false;
            changesMade     = true;
        }
    }

    // override default to prevent any user, hand editing
    // must use dialog interface for now
    public boolean isCellEditable( int row, int col )
    {
        if ( internalLoading ) return true;
        else return false;
    }

    void setOptionalPropertiesTable_keyPressed(KeyEvent e) {
        if ( e.getKeyCode() == e.VK_ENTER )
        {
            editCurrentPropertyValue();
        }
    }

    void setPropertiesTable_mouseClicked(MouseEvent e) {
        if ( setPropertiesTable.getSelectedColumn() == COL_PROPERTY_VALUE )
        {
            editCurrentPropertyValue();
        }
    }

    void setOptionalPropertiesTable_mouseClicked(MouseEvent e) {
        if ( setOptionalPropertiesTable.getSelectedColumn() == COL_OPT_PROPERTY_VALUE )
        {
            editCurrentOptionalPropertyValue();
        }
    }

    void setPropertiesTable_keyPressed(KeyEvent e) {
        if ( e.getKeyCode() == e.VK_ENTER )
        {
            editCurrentPropertyValue();
        }
    }

    void setPropertiesInsertButton_mouseReleased(MouseEvent e) {
        InsertSetPropertiesDialog   insDialog = new InsertSetPropertiesDialog(this, "Insert Property", true);

        Object[] values = SetPropertiesHelper.getSetPropertiesChoices(SetPropertiesHelper.PROCESS_TYPE_CAS, processNameField.getText());
        for ( int i = 0; i < values.length; i++ )
        {
            if ( i >= insDialog.setPropertiesTable.getRowCount() )
            {
                DefaultTableModel defTabMod = (DefaultTableModel)insDialog.setPropertiesTable.getModel();
                defTabMod.addRow( new Object[0] );
            }
            StringTokenizer strtok = new StringTokenizer( (String)values[ i ], " " );
            insDialog.setPropertiesTable.setValueAt(strtok.nextElement(), i, COL_HOME_NAME);
            insDialog.setPropertiesTable.setValueAt(strtok.nextElement(), i, COL_PROPERTY_NAME);
            insDialog.setPropertiesTable.setValueAt(strtok.nextElement(), i, COL_PROPERTY_VALUE);
        }
        insDialog.setLocationRelativeTo(this);
        insDialog.show();
        if ( !insDialog.wasCancelled )
        {
            int curInsRow = setPropertiesTable.getRowCount();
            int curRow = insDialog.setPropertiesTable.getSelectedRow();
            DefaultTableModel defTabMod = (DefaultTableModel)setPropertiesTable.getModel();
            defTabMod.insertRow( curInsRow, new Object[0]);
            setPropertiesTable.setValueAt(insDialog.setPropertiesTable.getValueAt(curRow,COL_HOME_NAME), curInsRow, COL_HOME_NAME);
            setPropertiesTable.setValueAt(insDialog.setPropertiesTable.getValueAt(curRow,COL_PROPERTY_NAME), curInsRow, COL_PROPERTY_NAME);
            setPropertiesTable.setValueAt(insDialog.setPropertiesTable.getValueAt(curRow,COL_PROPERTY_VALUE), curInsRow, COL_PROPERTY_VALUE);
            changesMade     = true;
        }
        setPropertiesDeleteButton.setEnabled( setPropertiesTable.getRowCount() > 0 ? true : false );
    }

    void setPropertiesDeleteButton_mouseReleased(MouseEvent e) {
        int curRow = setPropertiesTable.getSelectedRow();
        if ( setPropertiesTable.getRowCount() > 0 && curRow != -1 )
        {
            DefaultTableModel defTabMod = (DefaultTableModel)setPropertiesTable.getModel();
            defTabMod.removeRow( curRow );
            changesMade     = true;
        }
        setPropertiesDeleteButton.setEnabled( setPropertiesTable.getRowCount() > 0 ? true : false );
    }

    void setOptPropertiesInsertButton_mouseReleased(MouseEvent e) {
        InsertSetOptionalPropertiesDialog   insDialog = new InsertSetOptionalPropertiesDialog(this, "Insert Optional Property", true);

        Object[] values = SetOptionalPropertiesHelper.getSetOptionalPropertiesChoices(SetOptionalPropertiesHelper.PROCESS_TYPE_CAS);
        for ( int i = 0; i < values.length; i++ )
        {
            if ( i >= insDialog.setOptionalPropertiesTable.getRowCount() )
            {
                DefaultTableModel defTabMod = (DefaultTableModel)insDialog.setOptionalPropertiesTable.getModel();
                defTabMod.addRow( new Object[0] );
            }
            StringTokenizer strtok = new StringTokenizer( (String)values[ i ], " " );
            insDialog.setOptionalPropertiesTable.setValueAt(strtok.nextElement(), i, COL_OPT_PROPERTY_NAME);
            insDialog.setOptionalPropertiesTable.setValueAt(strtok.nextElement(), i, COL_OPT_PROPERTY_VALUE);
        }
        insDialog.setLocationRelativeTo(this);
        insDialog.show();
        if ( !insDialog.wasCancelled )
        {
            int curInsRow = setOptionalPropertiesTable.getRowCount();
            int curRow = insDialog.setOptionalPropertiesTable.getSelectedRow();
            DefaultTableModel defTabMod = (DefaultTableModel)setOptionalPropertiesTable.getModel();
            defTabMod.insertRow( curInsRow, new Object[0]);
            setOptionalPropertiesTable.setValueAt(insDialog.setOptionalPropertiesTable.getValueAt(curRow,COL_OPT_PROPERTY_NAME), curInsRow, COL_OPT_PROPERTY_NAME);
            setOptionalPropertiesTable.setValueAt(insDialog.setOptionalPropertiesTable.getValueAt(curRow,COL_OPT_PROPERTY_VALUE), curInsRow, COL_OPT_PROPERTY_VALUE);
            changesMade     = true;
        }
        setOptPropertiesDeleteButton.setEnabled( setOptionalPropertiesTable.getRowCount() > 0 ? true : false );
    }

    void setOptPropertiesDeleteButton_mouseReleased(MouseEvent e) {
        int curRow = setOptionalPropertiesTable.getSelectedRow();
        if ( setOptionalPropertiesTable.getRowCount() > 0 && curRow != -1 )
        {
            DefaultTableModel defTabMod = (DefaultTableModel)setOptionalPropertiesTable.getModel();
            defTabMod.removeRow( curRow );
            changesMade     = true;
        }
        setOptPropertiesDeleteButton.setEnabled( setOptionalPropertiesTable.getRowCount() > 0 ? true : false );
    }
}
