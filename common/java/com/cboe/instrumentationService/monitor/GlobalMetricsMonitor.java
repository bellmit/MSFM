package com.cboe.instrumentationService.monitor;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Map;

import com.cboe.lwt.eventLog.Logger;
import com.cboe.lwt.thread.ThreadTask;
import com.cboe.lwt.transactionLog.LogDestination;

import com.cboe.instrumentationService.factories.InstrumentorFactoryObserver;
import com.cboe.instrumentationService.factories.CountInstrumentorFactoryVisitor;
import com.cboe.instrumentationService.instrumentors.CountInstrumentor;
import com.cboe.instrumentationService.instrumentors.Instrumentor;
import com.cboe.instrumentationService.InstrumentorHome;

/**
 * @author dotyl
 *
 * To change the template for this generated type comment go to
 * Window>Preferences>Java>Code Generation>Code and Comments
 */
public class GlobalMetricsMonitor 
    extends ThreadTask
    implements InstrumentorFactoryObserver,
			   CountInstrumentorFactoryVisitor
{
    private int              pollingInterval_MS ;
    private DecimalFormat    numFmt;

    private LinkedList       meters;
    private SimpleDateFormat timeFmt;
    private long             lastTime;
    private int              reportType;
    private StringBuffer     outBuf;
    private PrintStream      out;
        
    private static final int REPORT_THROUGHPUT   = 0;
    private static final int REPORT_DELTA_VOLUME = 1;
    
    
    private GlobalMetricsMonitor( PrintStream p_out,
                                  int         p_pollingInterval_MS, 
                                  int         p_reportType )
    {
        super( "Metrics log : Console" );
        
        pollingInterval_MS = p_pollingInterval_MS;
        
        timeFmt = new SimpleDateFormat( "HH:mm:ss" );
        
        numFmt = new DecimalFormat( "0,000,000,000" ); 
        numFmt.setParseIntegerOnly( true );
        
        outBuf = new StringBuffer( 2048 );
        
        lastTime = 0;
        reportType = p_reportType;
        
        meters = new LinkedList();
		InstrumentorHome.findCountInstrumentorFactory().addObserver( this );

		// Get the initial set of instrumentors.
		InstrumentorHome.findCountInstrumentorFactory().accept( this, true );
        
        out = p_out;
    }


	public boolean start( Map instrumentorCollection ) {
		Iterator iter = instrumentorCollection.values().iterator();
		while( iter.hasNext() ) {
			meters.add( new MeterMonitor( (CountInstrumentor)iter.next() ) );
		}

		return false; // Done with the visit.
	}

	public boolean visit( CountInstrumentor ci ) {
		return false;
	}

	public void end() {
	}

    public static GlobalMetricsMonitor initThroughputMonitor( String p_fileBaseName,
                                                              int    p_pollingInterval_MS )
    {
        GlobalMetricsMonitor result = null;
        
        try
        {
            File outFile = LogDestination.openLogFile( p_fileBaseName );
            PrintStream logOut = new PrintStream( new BufferedOutputStream( new FileOutputStream( outFile ) ) );

            result = new GlobalMetricsMonitor( logOut,
                                               p_pollingInterval_MS,
                                               REPORT_THROUGHPUT );
        }
        catch( IOException ex )
        {
            Logger.error( "Error creating Metrics file for : " + p_fileBaseName, ex );
        }

        return result;
    }


    public static GlobalMetricsMonitor initThroughputMonitor( PrintStream p_out,
                                                              int         p_pollingInterval_MS )
    {
        return new GlobalMetricsMonitor( p_out,
                                         p_pollingInterval_MS,
                                         REPORT_THROUGHPUT );
    }


    public static GlobalMetricsMonitor initDeltaMonitor( String p_fileBaseName,
                                                         int    p_pollingInterval_MS )
    {
        GlobalMetricsMonitor result = null;
        
        try
        {
            File outFile = LogDestination.openLogFile( p_fileBaseName );
            PrintStream logOut = new PrintStream( new BufferedOutputStream( new FileOutputStream( outFile ) ) );

            result = new GlobalMetricsMonitor( logOut,
                                               p_pollingInterval_MS,
                                               REPORT_DELTA_VOLUME );
        }
        catch( IOException ex )
        {
            Logger.error( "Error creating Metrics file for : " + p_fileBaseName, ex );
        }

        return result;
    }


    public static GlobalMetricsMonitor initDeltaMonitor( PrintStream p_out,
                                                         int         p_pollingInterval_MS )
    {
        return new GlobalMetricsMonitor( p_out,
                                         p_pollingInterval_MS,
                                         REPORT_DELTA_VOLUME );
    }
    
    
    private void poll()
        throws InterruptedException
    {
        // wait for reporting interval
        Thread.sleep( pollingInterval_MS );

        // collect the stats        
        long newTime = System.currentTimeMillis();   
        String newTimeStr = timeFmt.format( new Date( newTime ) );
        
        Iterator meterIter = meters.iterator();

        switch ( reportType ) 
        {
            case REPORT_THROUGHPUT :
                
                long deltaTime = newTime - lastTime;
                
                while ( meterIter.hasNext() )
                {
                    MeterMonitor m = (MeterMonitor)meterIter.next();
                    outBuf.setLength( 0 );
                    outBuf.append( m.meter.getName() )
                          .append( "\t" )
                          .append( newTimeStr )
                          .append( "\t" )
                          .append( m.pollThroughput( deltaTime ) );
                    out.println( outBuf.toString() );
                }
                break;
                
            case REPORT_DELTA_VOLUME :
                while ( meterIter.hasNext() )
                {
                    MeterMonitor m = (MeterMonitor)meterIter.next();
                    outBuf.setLength( 0 );
                    outBuf.append( m.meter.getName() )
                          .append( "\t" )
                          .append( newTimeStr )
                          .append( "\t" )
                          .append( m.pollDelta() );
                    out.println( outBuf.toString() );
                }
                break;
                
            default:
                assert( false ) : "Impossible report type of : " + reportType;            
        }
        
        lastTime = newTime;
    }
        
    
    public void doTask()
        throws InterruptedException
    {
        if ( lastTime == 0 )
        {
            lastTime = System.currentTimeMillis();
        }
     
        while ( true )
        {
            poll();
        }
    }
    
    
    ////////////////////////////////////////////////////////////////////////////
    // observer part of the subject-observer pattern
    
    
    public synchronized void observeInstrumentorAdded( Instrumentor p_addedMeter )
    {
        meters.add( new MeterMonitor( (CountInstrumentor)p_addedMeter ) );
    }


    public synchronized void observeInstrumentorRemoved( Instrumentor p_removedMeter )
    {
        meters.remove( p_removedMeter );
    }

    
    ////////////////////////////////////////////////////////////////////////////
    // inner class
    
    private static class MeterMonitor
    {
        CountInstrumentor meter;
        long  lastPollValue;

        MeterMonitor( CountInstrumentor p_meter )
        {
            meter = p_meter;
            lastPollValue = 0;
        }
        
        public boolean equals( Object p_other )
        {
            return meter.getName().equals( ((MeterMonitor)p_other).meter.getName( ) );
        }


        public long pollDelta()
        {
            long currentTotal = meter.getCount();
            long result       = currentTotal - lastPollValue;
            
            lastPollValue = currentTotal;
            return result;
        }
        
        
        public long pollThroughput( long p_elapsedTime )
        {
            long result = 0;
            
            if ( p_elapsedTime > 0 )
            {
                result = ( pollDelta() * 1000l ) / p_elapsedTime;
            }
            else
            {
                Logger.info( "(" + pollDelta() + ") processed in 0 time... possible test error or compiler optimization\n" );
                result = 0;
            }
            
            return result;
        }
    
    };
    
    // inner class
    ////////////////////////////////////////////////////////////////////////////
    
}
