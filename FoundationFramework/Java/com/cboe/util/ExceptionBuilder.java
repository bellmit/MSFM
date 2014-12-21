package com.cboe.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import com.cboe.exceptions.*;
/**
 * Classname is ...
 *
 * @author David Wegener
 * @version 0.1
 *
 * @see
 * @since
 */
public class ExceptionBuilder {
/**
 * ExceptionDetailWrapper constructor comment.
 */

    private static SimpleDateFormat formatter = new SimpleDateFormat ("yyyy.MM.dd G 'at' hh:mm:ss a zzz");

private ExceptionBuilder() {
	super();
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static AlreadyExistsException alreadyExistsException(String message, int errorCode){
	return new AlreadyExistsException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static AuthenticationException authenticationException(String message, int errorCode){
	return new AuthenticationException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static AuthorizationException authorizationException(String message, int errorCode){
	return new AuthorizationException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static CommunicationException communicationException(String message, int errorCode){
	return new CommunicationException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static ExceptionDetails createDetails(String message, int errorCode) {

    Date currentTime_1 = new Date();
    String dateString = formatter.format(currentTime_1);

	return new ExceptionDetails(message, dateString, (short) 0, errorCode);
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static DataValidationException dataValidationException(String message, int errorCode){
	return new DataValidationException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static NotAcceptedException notAcceptedException(String message, int errorCode){
	return new NotAcceptedException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static NotFoundException notFoundException(String message, int errorCode){
	return new NotFoundException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static com.cboe.exceptions.NotSupportedException notSupportedException(String message, int errorCode){
	return new com.cboe.exceptions.NotSupportedException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static SystemException systemException(String message, int errorCode){
	return new SystemException(message, createDetails(message, errorCode));
}
/**
 * Performs something...
 *
 * @param name desc
 * @return desc
 * @exception name desc
 * @see
 * @since
 */
public static TransactionFailedException transactionFailedException(String message, int errorCode){
	return new TransactionFailedException(message, createDetails(message, errorCode));
}
}
