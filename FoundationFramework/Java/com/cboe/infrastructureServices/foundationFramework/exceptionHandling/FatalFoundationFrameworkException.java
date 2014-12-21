package com.cboe.infrastructureServices.foundationFramework.exceptionHandling;


/**
 *
 * New Typed Exception that is Fatal...
 * Any process that throws this exception will be aborted
 *
 * @author Uma Diddi
 * Date : 11/25/05
 *
 */

public class FatalFoundationFrameworkException extends RuntimeException
{
	Exception exception;

	public Exception getException()
	{
		return exception;
	}

	/**
	 */
	public FatalFoundationFrameworkException(Exception error, String errMessage) {
		super(errMessage);
		exception = error;
	}

	/**
	 */
	public FatalFoundationFrameworkException(String errorMessage) {
		super(errorMessage);
	}
}
