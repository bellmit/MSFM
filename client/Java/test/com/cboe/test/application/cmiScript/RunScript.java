package com.cboe.test.application.cmiScript;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Properties;
import org.omg.CORBA.ORB;
import org.omg.PortableServer.POA;
import org.omg.PortableServer.POAHelper;

public class RunScript
{
    private static ORB orb;
    private static POA rootPoa;

    private UserAccess userAccess;
    private static final String PACKAGE_NAME = "cmiScript";

    // Values from errno.h
    private static final int NO_ERROR = 0; // success (not in errno.h)
    private static final int ENOENT = 2;   // no such file or directory
    private static final int EINVAL = 22;  // invalid argument

    // Collection and Collection keys for properties from command-line
    private String casHost;
    private int casPort;
    private static final int DEFAULT_CAS_PORT = 8003;
    private InputStream scriptIn;

    private void usage(String message)
    {
        String us = this.getClass().getName();
        System.out.println("Usage: java " + us + " casHostName casPort [scriptFile]");
        if (message != null)
        {
            System.out.println(message);
        }
        System.exit(EINVAL);
    }

    private static StringBuilder zeropad(int num, int digits)
    {
        StringBuilder s = new StringBuilder().append(num);
        while (s.length() < digits)
        {
            s.insert(0, '0');
        }
        return s;
    }

    private String now()
    {
        Calendar c = new GregorianCalendar();
        StringBuilder result = new StringBuilder()
            .append(c.get(Calendar.YEAR))
            .append(zeropad(c.get(Calendar.MONTH)+1,2))
            .append(zeropad(c.get(Calendar.DAY_OF_MONTH),2)).append('_')
            .append(zeropad(c.get(Calendar.HOUR_OF_DAY),2))
            .append(zeropad(c.get(Calendar.MINUTE),2))
            .append(zeropad(c.get(Calendar.SECOND),2));
        return result.toString();
    }

    /** Set casHost, casPort, scriptIn.
     * @param args Arguments from invocation command line.
     **/
    private void scanArgs(String args[])
    {
        if (args.length < 1)
        {
            usage("Missing casHostName");
        }
        casHost = args[0];

        casPort = (args.length < 2) ? DEFAULT_CAS_PORT
                                    : Integer.parseInt(args[1]);

        String outfileNames[];
        if (args.length < 3)
        {
            scriptIn = System.in;  // read standard input

            // Create possible output file names
            // First choice, file in this directory
            outfileNames = new String[2];
            outfileNames[0] = PACKAGE_NAME + "_" + now() + ".log";

            // Second choice, file in home directory
            Properties props = System.getProperties();
            String home = props.getProperty("user.home");
            String sep = props.getProperty("file.separator");
            outfileNames[1] = home + sep + outfileNames[0];
        }
        else
        {
            // Command line contains input/output file name
            try
            {
                scriptIn = new FileInputStream(args[2]);
            }
            catch (FileNotFoundException fnf)
            {
                System.err.println(fnf);
                System.exit(ENOENT);
            }
            outfileNames = new String[1];
            outfileNames[0] = args[2] + "_" + now() + ".log";
        }
        Log.setOutputFile(outfileNames);
    }

    private RunScript(String args[])
    {
        scanArgs(args);
        userAccess = new UserAccess(orb, rootPoa, casHost, casPort);
    }

    private void go() throws java.io.IOException
    {
        LineNumberReader in = new LineNumberReader(new InputStreamReader(scriptIn));
        String line;
        boolean eof = false;
        int lineNumber = 0;

        // Read lines, echo and process them. If a line ends with \ then
        // take the next line as a continuation of this line. Ignore lines
        // that start with the # character.
        while (!eof)
        {
            line = "";
            for (;;)
            {
                String part = in.readLine();
                if (part == null)
                {
                    eof = true;
            /*for*/ break;
                }
                else
                {
                    ++lineNumber;
                    Log.message(lineNumber + "> " + part);
                    line += " " + part;
                    if (! line.endsWith("\\"))
                    {
            /*for*/     break;
                    }
                }
                // Line ends with \
                // Remove \ and append next line to this one.
                line = line.substring(0, line.length()-1);
            }
            if (! line.equals(""))
            {
                String input[] = CommandLine.parse(line);
                // Process line if it isn't a comment line.
                if (!input[0].startsWith("#"))
                {
                    userAccess.dispatchCommand(input);
                }
            }
        }
    }

    public static void main(String args[])
    {
        try
        {
            orb = ORB.init(args, null);
            rootPoa = POAHelper.narrow(orb.resolve_initial_references("RootPOA"));
            rootPoa.the_POAManager().activate();
            new RunScript(args).go();
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
        System.exit(NO_ERROR);
    }
}
