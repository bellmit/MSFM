package com.cboe.domain.util.failover;

import java.io.IOException;
import java.io.InputStream;
import java.io.PrintStream;


    /**
     * @author baranski
     *
     */
    public class LogReaderThread extends Thread{

        private InputStream is;
        private PrintStream ps;

        /**
         * @param p_is
         * @param p_ps
         */
        public LogReaderThread(InputStream p_is, PrintStream p_ps){
            is = p_is;
            ps = p_ps;
        }
        /* (non-Javadoc)
         * @see java.lang.Thread#run()
         */
        public void run(){
            byte[] buffer = new byte[512];
            while (true) {
                try {
                    int n = is.read(buffer);
                    if (n < 0) {
                        break;
                    }
                    if (n > 0) {
                        ps.print(new String(buffer, 0, n));
                    }
                } catch (IOException x) {
                    break;
                }
            }   
        }

    }
