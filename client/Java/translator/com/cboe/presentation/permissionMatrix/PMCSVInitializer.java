//
// -----------------------------------------------------------------------------------
// Source file: PMCSVInitializer.java
//
// PACKAGE: com.cboe.presentation.permissionMatrix
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2007 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.permissionMatrix;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.*;

import com.cboe.interfaces.presentation.permissionMatrix.Permission;
import com.cboe.interfaces.presentation.permissionMatrix.PermissionMatrix;
import com.cboe.interfaces.presentation.user.Role;

import com.cboe.presentation.common.exceptionHandling.DefaultExceptionHandlerHome;
import com.cboe.presentation.resources.Resources;

public class PMCSVInitializer
{
    // pm.csv "lives" in the resources/permissions directory.
    // Use Resources.getPermissionResource("pm.csv") to obtain
    // the file and pass the file name to PMCSVInitializer.
    private static final String PERMISSION_CSV_FILE = "pm.csv";

    private static final String PERMISSIONS_TOKEN = "Permissions";

    private URL permissionUrl;
    private PermissionMatrix permissionMatrix;

    public PMCSVInitializer(PermissionMatrix permissionMatrix)
    {
        setPermissionMatrix(permissionMatrix);
    }

    public PMCSVInitializer()
    {
    }

    public PermissionMatrix getPermissionMatrix()
    {
        return permissionMatrix;
    }

    public void setPermissionMatrix(PermissionMatrix permissionMatrix)
    {
        this.permissionMatrix = permissionMatrix;
    }

    public void initialize(PermissionMatrix permissionMatrix)
    {
        permissionUrl = Resources.getPermissionResource(PERMISSION_CSV_FILE);
        if(permissionUrl != null)
        {
            setPermissionMatrix(permissionMatrix);

            InputStream ioStream = null;
            InputStreamReader ioReader = null;
            BufferedReader reader = null;

            try
            {
                ioStream = permissionUrl.openStream();
                ioReader = new InputStreamReader(ioStream);
                reader = new BufferedReader(ioReader);

                processCSVFile(reader);
            }
            catch(IOException e)
            {
                IllegalStateException newException =
                        new IllegalStateException("Permission resource [" + PERMISSION_CSV_FILE +
                                                  "] cannot be read.");
                newException.initCause(e);
                throw newException;
            }
            finally
            {
                if(reader != null)
                {
                    //noinspection UnusedCatchParameter
                    try
                    {
                        reader.close();
                    }
                    catch(IOException e)
                    {
                        //ignore since we are just trying to close the stream
                    }
                }
                else if(ioReader != null)
                {
                    //noinspection UnusedCatchParameter
                    try
                    {
                        ioReader.close();
                    }
                    catch(IOException e)
                    {
                        //ignore since we are just trying to close the stream
                    }
                }
                else if(ioStream != null)
                {
                    //noinspection UnusedCatchParameter
                    try
                    {
                        ioStream.close();
                    }
                    catch(IOException e)
                    {
                        //ignore since we are just trying to close the stream
                    }
                }
            }
        }
        else
        {
            throw new IllegalStateException("Permission resource [" + PERMISSION_CSV_FILE +
                                            "] cannot be found.");
        }
    }

    private void processCSVFile(BufferedReader reader)
    {
        String line;
        StringTokenizer tokenizer;
        Role[] roleOrder = new Role[Role.values().length];

        try
        {
            line = reader.readLine();
            tokenizer = new StringTokenizer(line, ",", false);
            if(tokenizer.countTokens() > 1)
            {
                String permissionsToken = tokenizer.nextToken();
                if(!PERMISSIONS_TOKEN.equals(permissionsToken))
                {
                    throw new IllegalArgumentException(PERMISSION_CSV_FILE +
                                                       " is not in the recognized format. " +
                                                       "Permissions header on role row missing.");
                }
                int posCount = 0;
                while(tokenizer.hasMoreTokens())
                {
                    String roleToken = tokenizer.nextToken();
                    Role foundRole = Role.valueOf(roleToken);
                    if(foundRole != null)
                    {
                        roleOrder[posCount] = foundRole;
                        posCount++;
                    }
                }
            }

            int posCount;
            line = reader.readLine();
            while(line != null)
            {
                posCount = 0;
                tokenizer = new StringTokenizer(line, ",", false);
                if(tokenizer.countTokens() != roleOrder.length + 1)
                {
                    throw new IllegalArgumentException(PERMISSION_CSV_FILE +
                                                       " is not in the recognized format. " +
                                                       "Column count for row is off. " +
                                                       line);

                }
                String permissionToken = tokenizer.nextToken();
                Permission permission = Permission.valueOf(permissionToken);
                if(permission != null)
                {
                    while(tokenizer.hasMoreTokens())
                    {
                        String valueToken = tokenizer.nextToken();
                        Boolean booleanValue = Boolean.valueOf(valueToken);
                        Role role = roleOrder[posCount];
                        getPermissionMatrix().set(permission, role, booleanValue);
                        posCount++;
                    }
                }
                line = reader.readLine();
            }
        }
        catch(IOException e)
        {
            DefaultExceptionHandlerHome.find().process(e, "Permission resource [" +
                                                          PERMISSION_CSV_FILE +
                                                          "] cannot be found.");
        }
    }
}
