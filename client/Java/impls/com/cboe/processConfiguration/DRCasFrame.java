package com.cboe.processConfiguration;

import java.awt.*;
import javax.swing.*;
import java.awt.event.*;
import com.cboe.systemsManagementService.commandLine.*;

import java.io.*;
import java.util.Vector;
import java.util.Properties;

/**
 * Title:        Client Application Server
 * Description:  Your description
 * Copyright:    Copyright (c) 1998
 * Company:      Your Company
 * @author Your Name
 * @version
 */

public class DRCasFrame extends JFrame {
    JPanel ListPanel = new JPanel();
    BorderLayout borderLayout1 = new BorderLayout();
    JPanel InfoPanel = new JPanel();
    BorderLayout borderLayout2 = new BorderLayout();
    List DRCasList = new List();
    TextArea DRCasStatus = new TextArea();
    List DRCasInfoTable = new List();

    CommandLine         commandLine = null;
    Properties          properties = null;
    TextField DRCasCommandLine = new TextField();
    GridBagLayout gridBagLayout1 = new GridBagLayout();

    public DRCasFrame() {
        try {
            jbInit();
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }
    private void jbInit() throws Exception {
        ListPanel.setLayout(borderLayout1);
        this.setDefaultCloseOperation(3);
        this.setTitle("DR. CAS");
        this.getContentPane().setLayout(gridBagLayout1);
        InfoPanel.setLayout(borderLayout2);
        ListPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        InfoPanel.setBorder(BorderFactory.createLineBorder(Color.black));
        DRCasList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                DRCasList_mouseReleased(e);
            }
        });
        DRCasList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                DRCasList_keyReleased(e);
            }
        });
        DRCasStatus.setEditable(false);
        DRCasStatus.setRows(5);
        DRCasInfoTable.setFont(new java.awt.Font("Monospaced", 0, 12));
        DRCasCommandLine.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(KeyEvent e) {
                DRCasCommandLine_keyReleased(e);
            }
        });
        this.getContentPane().add(ListPanel, new GridBagConstraints(0, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 4, 0, 0), 307, 653));
        ListPanel.add(DRCasList, BorderLayout.CENTER);
        this.getContentPane().add(InfoPanel, new GridBagConstraints(1, 0, 1, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(5, 0, 145, 3), 523, 508));
        InfoPanel.add(DRCasInfoTable, BorderLayout.CENTER);
        this.getContentPane().add(DRCasStatus, new GridBagConstraints(0, 1, 2, 1, 1.0, 1.0
            ,GridBagConstraints.CENTER, GridBagConstraints.BOTH, new Insets(0, 4, 0, 3), 814, -21));
        this.getContentPane().add(DRCasCommandLine, new GridBagConstraints(0, 2, 2, 1, 1.0, 0.0
            ,GridBagConstraints.WEST, GridBagConstraints.HORIZONTAL, new Insets(8, 4, 10, 3), 826, 14));

        commandLine = new CommandLine();

    }

    public static void main(String[] args)
    {
        DRCasFrame      mainFrame = new DRCasFrame();

//        mainFrame.DRCasList.add("targetresource ior/SysManAgent.ior");
//        mainFrame.DRCasList.add("listrootmbeans");
//        mainFrame.DRCasList.add("listrelatedmbeans agents:Agents_0.Agent_devenv3devcas3env1");
        try
        {
            mainFrame.loadConfigFile();
            mainFrame.processConfigInfo();
            mainFrame.show();
        }
        catch ( Exception e )
        {
            System.out.println( "Exception loading/processing configuration file, drcas.ini" );
            e.printStackTrace();
        }
    }

    void DRCasList_mouseReleased(MouseEvent e) {
        callCommand();
    }

    void DRCasList_keyReleased(KeyEvent e) {
        callCommand();
    }

    void callCommand()
    {
        String[]        commands;
        int             commandIndex = this.DRCasList.getSelectedIndex();
        String          heading;

        this.DRCasInfoTable.removeAll();

        commands = getSelectedCommands( commandIndex );
        for ( int i = 0; i < commands.length; i++ )
        {
            drcasAppendStatus( "Processing command... " + commands[ i ] );
            if (commandLine.parse( commands[ i ] ))
            {
                commandLine.executeCurrentCommand();
                String[] aResult = commandLine.getCurrentResult();
                if (aResult == null || aResult.length < 1)
                {
                    drcasSetStatus("Command Execution Complete");
                }
                else
                {
                    heading = getCommandHeading( commandIndex, i );
                    for (int j = 0; j < aResult.length; j++)
                    {
                        drcasAppendStatus( heading + (j+1) + ") " + aResult[j]);
                        this.DRCasInfoTable.add( heading + (j+1) + ") " + aResult[j]);
                    }
                }
            }
        }
    }

    void callCommand(String command)
    {
        this.DRCasInfoTable.removeAll();
        drcasAppendStatus( "Processing command... " + command );
        if (commandLine.parse( command ))
        {
            commandLine.executeCurrentCommand();
            String[] aResult = commandLine.getCurrentResult();
            if (aResult == null || aResult.length < 1)
            {
                drcasSetStatus("Command Execution Complete");
            }
            else
            {
                for (int j = 0; j < aResult.length; j++)
                {
                    drcasAppendStatus( (j+1) + ") " + aResult[j]);
                    this.DRCasInfoTable.add( (j+1) + ") " + aResult[j]);
                }
            }
        }
    }

    String[] getSelectedCommands( int  index )
    {
        int         i = 0;
        String      commandKey = "drcas[" + index + "].command";
        String      commandText;
        Vector      commandHolder = new Vector( 10 );

        drcasAppendStatus( "Getting command... " + commandKey );
        while ( ( commandText = properties.getProperty( commandKey + "[" + i++ + "]" ) ) != null )
        {
            commandHolder.addElement(commandText);
        }

        return (String[])commandHolder.toArray(new String[0]);
    }

    String getCommandHeading( int majorIndex, int minorIndex )
    {
        String      commandKey = "drcas[" + majorIndex + "].heading[" + minorIndex + "]";

        drcasAppendStatus( "Getting command heading... " + commandKey );
        return properties.getProperty( commandKey );
    }

    void loadConfigFile()
        throws IOException
    {
        properties = new Properties();
        InputStream configStream = new FileInputStream( "drcas.ini" );
        properties.load( configStream );
    }

    void processConfigInfo()
    {
        int         i = 0;
        String      commandText;
        String      command;

        while ( ( commandText = properties.getProperty( "drcas[" + i++ + "].text" ) ) != null )
        {
            this.DRCasList.add(commandText);
        }

    }

    void drcasAppendStatus( String stat )
    {
        this.DRCasStatus.append( stat + "\n" );
    }

    void drcasSetStatus( String stat )
    {
        this.DRCasStatus.setText( stat + "\n" );
    }

    void DRCasCommandLine_keyReleased(KeyEvent e)
    {
        if ( e.getKeyCode() == e.VK_ENTER )
        {
            callCommand( this.DRCasCommandLine.getText() );
        }

    }




}
