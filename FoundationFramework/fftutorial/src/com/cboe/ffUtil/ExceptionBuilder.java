package com.cboe.ffUtil;

import com.cboe.ffidl.ffExceptions.*;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ExceptionBuilder
{
    protected static final SimpleDateFormat DATE_FORMAT = new SimpleDateFormat("MM/dd/yyyy hh:mm:ss.SSS");

    public static AlreadyExistsException alreadyExistsException(String msg, int errCode)
    {
        AlreadyExistsException ex = new AlreadyExistsException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    public static AuthorizationException authorizationException(String msg, int errCode)
    {
        AuthorizationException ex = new AuthorizationException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    public static AuthenticationException authenticationException(String msg, int errCode)
    {
        AuthenticationException ex = new AuthenticationException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    public static CommunicationException communicationException(String msg, int errCode)
    {
        CommunicationException ex = new CommunicationException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    public static DataValidationException dataValidationException(String msg, int errCode)
    {
        DataValidationException ex = new DataValidationException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    public static NotFoundException notFoundException(String msg, int errCode)
    {
        NotFoundException ex = new NotFoundException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    public static NotAcceptedException notAcceptedException(String msg, int errCode)
    {
        NotAcceptedException ex = new NotAcceptedException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    public static NotSupportedException notSupportedException(String msg, int errCode)
    {
        NotSupportedException ex = new NotSupportedException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    public static SystemException systemException(String msg, int errCode)
    {
        SystemException ex = new SystemException();
        ex.details = createDetails(msg, errCode);
        return ex;
    }

    protected static ExceptionDetails createDetails(String msg, int errCode)
    {
        ExceptionDetails details = new ExceptionDetails();
        details.message =msg; 
        details.dateTime = DATE_FORMAT.format(new Date());
        details.severity = (short)0;
        details.error = errCode;
        return details;
    }
}
