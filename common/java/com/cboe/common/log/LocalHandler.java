package com.cboe.common.log;
import java.io.PrintStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;


public  class LocalHandler implements LogHandler
{

  public LocalHandler()
    {
      if (System.getProperty("LocalLog.ALL") != null)
        {
          _logDebug = true;
          _logSysNotify = true;
          _logSysWarn = true;
          _logSysAlarm = true;
          _logInfo = true;
          return;
        }
      _logDebug = (System.getProperty("LocalLog.Debug") != null);
      _logSysNotify = (System.getProperty("LocalLog.SysNotify") != null);
      _logSysWarn = (System.getProperty("LocalLog.SysWarn") != null);
      _logSysAlarm = (System.getProperty("LocalLog.SysAlarm") != null);
      _logInfo = (System.getProperty("LocalLog.Info") != null);
    }

  private boolean _logDebug = false;
  private boolean _logSysNotify = true;
  private boolean _logSysWarn = true;
  private boolean _logSysAlarm = true;
  private boolean _logInfo = false;
  private SimpleDateFormat dateFormatter = new SimpleDateFormat("'<' EEE yyyy/MM/dd HH:mm:ss:SS '>' ");

  private  PrintStream out = System.out;

	public String getDefaultLoggerName() {
		return "LWL";
	}

	public boolean isLoggable( ResourceBundle rb, String logIdName, int logType ) {
		if ( logType == Logger.DEBUG ) return _logDebug;
		if ( logType == Logger.SYSNOTIFY ) return _logSysNotify;
		if ( logType == Logger.SYSALARM ) return _logSysAlarm;
		return true;
	}

	public String whatsLoggable( ResourceBundle rb, String logIdName ) {
		return "";
	}

	public void setLevel( ResourceBundle rb, String logIdName, int logType ) {
	}

	public LogMessageId createLogMessageId( String logIdName, String msgText ) {
		return new LocalLogMessageId( logIdName, msgText, null, null );
	}

	public LogMessageId createLogMessageId( String logIdName, String msgText, String className, String methodName ) {
		return new LocalLogMessageId( logIdName, msgText, className, methodName );
	}

	public void traceEntry( ResourceBundle rb, String logIdName, Object[] params ) {
	}

	public void traceExit( ResourceBundle rb, String logIdName, Object[] params ) {
	}

	public void debug( ResourceBundle rb, String logIdName, Object[] params ) {
	}

	public void stats( ResourceBundle rb, String logIdName, Object[] params ) {
	}

	public void sysNotify( ResourceBundle rb, String logIdName, Object[] params ) {
	}

	public void sysWarn( ResourceBundle rb, String logIdName, Object[] params ) {
	}

	public void sysAlarm( ResourceBundle rb, String logIdName, Object[] params ) {
	}

	public void debug( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
	}

	public void sysNotify( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
	}

	public void sysWarn( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
	}

	public void sysAlarm( ResourceBundle rb, String logIdName, Object[] params, Throwable t ) {
	}



	public void traceEntry( LogMessageId id, Object[] params ) {
	}

	public void traceExit( LogMessageId id, Object[] params ) {
	}

	public void debug( LogMessageId id, Object[] params ) {
	}

	public void stats( LogMessageId id, Object[] params ) {
	}

	public void sysNotify( LogMessageId id, Object[] params ) {
	}

	public void sysWarn( LogMessageId id, Object[] params ) {
	}

	public void sysAlarm( LogMessageId id, Object[] params ) {
	}

	public void debug( LogMessageId id, Object[] params, Throwable t ) {
	}

	public void sysNotify( LogMessageId id, Object[] params, Throwable t ) {
	}

	public void sysWarn( LogMessageId id, Object[] params, Throwable t ) {
	}

	public void sysAlarm( LogMessageId id, Object[] params, Throwable t ) {
	}

	public boolean isLoggable( int logType ) {
		if ( logType == Logger.DEBUG ) return _logDebug;
		if ( logType == Logger.SYSNOTIFY ) return _logSysNotify;
		if ( logType == Logger.SYSALARM ) return _logSysAlarm;
		return true;
	}
	
	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void debug(String text)
    {
      if (_logDebug)
        out.println("<DEBUG> " + getTimeStamp() + text);
    }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void sysNotify(String text)
    {
      if (_logSysNotify)
        out.println("<SYS-NOTIFY> " + getTimeStamp() + text);
    }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void sysWarn(String text)
  {
   if (_logSysWarn)
     out.println("<SYS-WARNING> " + getTimeStamp() + text);
  }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void sysAlarm(String text)
    {
      if (_logSysAlarm)
        out.println("<SYS-ALARM> " + getTimeStamp() + text);
    }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void info(String text)
    {
      if (_logInfo)
        out.println("<INFO> " + getTimeStamp() + text);
    }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void stats(String text)
    {
        out.println("<STATS> " + getTimeStamp() + text);
    }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void debug(String text, Throwable t)
    {
      if (_logDebug)
        {
          out.println("<DEBUG> " + getTimeStamp() + text);
          out.println("<DEBUG> Stack Trace follows:");
          t.printStackTrace(out);
        }
    }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void sysNotify(String text, Throwable t)
    {
      if (_logSysNotify)
        {
          out.println("<SYS-NOTIFY> " + getTimeStamp() + text);
          out.println("<SYS-NOTIFY> Stack Trace follows:");
          t.printStackTrace(out);
        }
    }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void sysWarn(String text, Throwable t)
  {
   if (_logSysNotify)
     {
       out.println("<SYS-WARNING> " + getTimeStamp() + text);
       out.println("<SYS-WARNING> Stack Trace follows:");
       t.printStackTrace(out);
     }
  }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void sysAlarm(String text, Throwable t)
    {
      if (_logSysAlarm)
        {
          out.println("<SYS-ALARM> " + getTimeStamp() + text);
          out.println("<SYS-ALARM> Stack Trace follows:");
          t.printStackTrace(out);
        }
    }

	/**
	 *
	 * @param text a <code>String</code> value
	 */
  public  void info(String text, Throwable t)
    {
      if (_logInfo)
        {
          out.println("<INFO> " + getTimeStamp() + text);
          out.println("<INFO> Stack Trace follows:");
          t.printStackTrace(out);
        }
    }


  public String getTimeStamp()
    {
    	String timeStamp = null;
    	Date currentDate = new Date(System.currentTimeMillis());
      timeStamp = dateFormatter.format(currentDate);
      return timeStamp;
    }



}
