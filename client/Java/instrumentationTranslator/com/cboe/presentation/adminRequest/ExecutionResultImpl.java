//
// -----------------------------------------------------------------------------------
// Source file: ExecutionResultImpl.java
//
// PACKAGE: com.cboe.presentation.adminRequest
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2006 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.presentation.adminRequest;

import com.cboe.interfaces.instrumentation.adminRequest.ExecutionResult;

import com.cboe.infrastructureServices.interfaces.adminService.Command;

class ExecutionResultImpl implements ExecutionResult
{
    private Command resultCommand;
    private boolean returnValue;

    ExecutionResultImpl(Command resultCommand, boolean returnValue)
    {
        this.resultCommand = resultCommand;
        this.returnValue = returnValue;
    }

    /**
     * Gets the Command structure that contains the return value from the command execution
     * @return Command object that contains return values
     */
    public Command getCommandResult()
    {
        return resultCommand;
    }

    /**
     * Determines if the command call was a success.
     * @return true is assumed to be a success, false if assumed that the call failed for
     * some reason, or was not executed successfully.
     */
    public boolean isSuccess()
    {
        return returnValue;
    }
}
