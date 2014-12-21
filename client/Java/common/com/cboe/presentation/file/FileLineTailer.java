//
//-----------------------------------------------------------------------------------
//Source file: FileLineTailer.java
//
//-----------------------------------------------------------------------------------
//Copyright (c) 2005 The Chicago Board Options Exchange. All Rights Reserved.
//-----------------------------------------------------------------------------------
package com.cboe.presentation.file;

import java.io.File;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;

/*
 * Instances of this class 'tail' a specified text file The behavior of this
 * class mimics 'tail -f' UNIX command.
 */
public class FileLineTailer
{
    final int DEFAULT_THREAD_STOP_WAIT = 300;
    
    final int DEFAULT_LATENCY = 3000;

    final boolean DEFAULT_SHOW_PREV_LINES = false;

    protected int latency = DEFAULT_LATENCY;

    protected boolean showPrevLines = DEFAULT_SHOW_PREV_LINES;

    protected boolean running = false;

    protected boolean continueRunning = true;

    protected boolean needsRestart = false;

    protected Thread runnerThread;

    protected File file;

    protected ArrayList tailerListeners = new ArrayList();

    private StringBuffer sbuf = new StringBuffer();

    ///////////////////////////////////////////////////////////////////
    // constructors
    ///////////////////////////////////////////////////////////////////
    /*
     * @param File - a file object of the opened file to tail @param latency -
     * an integer value representing the milliseconds to sleep between polling
     * the file for new lines added @param IFileTailerListener - a listener
     * object which should receive the file line data
     */
    public FileLineTailer(
            File file, 
            int latency,
            FileTailerListener initialListener)
        throws IllegalArgumentException
    {
        if (!this.areParmsValid()) {
            throw new IllegalArgumentException();
        }
        this.file = file;
        this.latency = latency;
        this.addTailerListener(initialListener);
    }

    // parameterless ctor
    public FileLineTailer()
    {
    }
            
    ///////////////////////////////////////////////////////////////////
    // public methods
    ///////////////////////////////////////////////////////////////////
    /*
     * validates minimal set of parameters needed to run @return - true if
     * params are valid, false is invalid
     */
    public boolean areParmsValid()
    {
        boolean ret = false;
        if (this.file != null && this.latency > 0)
        {
            ret = true;
        }
        return ret;
    }

    /*
     * Cause this FileLineTailer to spawn a thread which will tail the file
     * supplied in the constructor and send its contents to all of the
     * listeners.
     */
    public synchronized void start()
        throws Exception
    {
        if (this.running && !this.continueRunning) {
            // stop was invoked, wait for thread to stop
            while(this.running) 
            {
                Thread.sleep(this.DEFAULT_THREAD_STOP_WAIT);
            }
        } 
        else if (this.running)
        {
            throw new Exception(
                "FileLineTailer.start() invoked, but thread IS already running!");
        }
        this.continueRunning = true;
        this.runnerThread = new Thread(new Runner());
        this.runnerThread.start();
    }

    /*
     * Cause this FileLineTailer to stop tailing the file supplied in the
     * constructor after it flushes the characters it's currently reading to all
     * its listeners.
     */
    public synchronized void stop()
    {
        if (this.running)
        {
            this.continueRunning = false;
            this.runnerThread.interrupt();
        }
    }

    /*
     * Add another IFileTailerListener to which the tailed file's contents
     * should be sent. @param tailerListener IFileTailerListener to be added
     */
    public boolean addTailerListener(FileTailerListener tailerListener)
    {
        return this.tailerListeners.add((Object) tailerListener);
    }

    /*
     * Remove the supplied IFileTailerListener from the list of
     * IFileTailerListeners to which the tailed file's contents should be sent.
     * @param tailerListener IFileTailerListener to be removed
     */
    public boolean removeListener(FileTailerListener tailerListener)
    {
        return this.tailerListeners.remove(tailerListener);
    }

    ///////////////////////////////////////////////////////////////////
    // definition of Runner class
    ///////////////////////////////////////////////////////////////////
    /*
     * Instances of this class are used to run a thread which tails a
     * FileLineTailer's file and sends its contents to IFileTailerListeners.
     */
    class Runner implements Runnable
    {
        public void run()
        {
            running = true;
            while (continueRunning)
            {
                runAction();
            }
            running = false;
        }

        protected void runAction()
        {
            try
            {
                clear(); // clear the output destination
                needsRestart = false;
                long lastActivityTime = 0;

                RandomAccessFile raf = new RandomAccessFile(file, "rw");
                // Skip to the end of the file, as indicated
                if (!showPrevLines)
                {
                    raf.seek(file.length());
                }
                String line = null;
                while (continueRunning && !needsRestart)
                {
                    while (!needsRestart)
                    {
                        lastActivityTime = System.currentTimeMillis();
                        Date d = new Date(lastActivityTime);
                        line = raf.readLine();
                        if (line != null)
                        {
                            print(line); // call method to send the line data to
                                         // listeners
                        } else
                        {
                            // no data to read ...
                            // now check if the filehandle has become stale
                            // (file was modified, but no data could be read).
                            needsRestart = file.exists() && (file.length() > 0)
                                    && (file.lastModified() > lastActivityTime);
                            // breakout of the while loop, sleep unless stop 
                            // was called during read processing or restart needed
                            break; 
                        }
                    }
                    if (continueRunning && !needsRestart)
                    {
                        try
                        {
                            Thread.sleep(latency);
                        } catch (InterruptedException e)
                        {
                            continueRunning=false;
                        }
                    }
                } //while (continueRunning && !needsRestart)
                raf.close();
            } catch (IOException e)
            {
                   e.printStackTrace();
            }
        }

        ///////////////////////////////////////////////////////////////////
        // listener notification methods
        ///////////////////////////////////////////////////////////////////
        /*
         * send the supplied string to all listeners @param a string containing
         * the file line data to be sent to the listeners
         */
        void print(String s)
        {
            Iterator i = tailerListeners.iterator();
            while (i.hasNext())
            {
                ((FileTailerListener) i.next()).print(s);
            }
        }

        /*
         * clear (remove) all listeners
         */
        void clear()
        {
            Iterator i = tailerListeners.iterator();
            while (i.hasNext())
            {
                ((FileTailerListener) i.next()).clear();
            }
        }
    }

    ///////////////////////////////////////////////////////////////////
    // getters/setters follow
    ///////////////////////////////////////////////////////////////////
    /**
     * @param file
     *            The file to set.
     */
    public synchronized void setFile(File file)
    {
        this.file = file;
    }

    /**
     * @param latency
     *            The latency to set.
     */
    public void setLatency(int latency)
    {
        this.latency = latency;
    }

    /**
     * @param running
     *            The running to set.
     */
    public void setRunning(boolean running)
    {
        this.running = running;
    }

    /**
     * @return Returns the showPrevLines.
     */
    public boolean isShowPrevLines()
    {
        return showPrevLines;
    }

    /**
     * @param showPrevLines
     *            The showPrevLines to set.
     */
    public void setShowPrevLines(boolean showPrevLines)
    {
        this.showPrevLines = showPrevLines;
    }

    /**
     * @return Returns the file.
     */
    public synchronized File getFile()
    {
        return file;
    }

    /**
     * @return Returns the latency.
     */
    public int getLatency()
    {
        return latency;
    }

    /**
     * @return Returns the running.
     */
    public boolean isRunning()
    {
        return running;
    }
}