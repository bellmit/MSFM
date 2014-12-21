package com.cboe.presentation.common.formatters;

/**
 * Indicates you tried to create a style that already exists.
 * @version (4/6/00 2:09:55 PM)
 * @author Troy Wehrle
 */
public class DuplicateStyleException extends RuntimeException
{
    public String styleName = null;
    public String styleDescription = null;
/**
 * DuplicateStyleException constructor comment.
 */
public DuplicateStyleException()
{
    super();
}
/**
 * DuplicateStyleException constructor comment.
 * @param s java.lang.String
 */
public DuplicateStyleException(String s)
{
    super(s);
}
}
