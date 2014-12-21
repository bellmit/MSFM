package com.cboe.cfix.util;

/**
 * PrettyPrintWriter.java
 *
 * @author Dmitry Volpyansky
 *
 */

import com.cboe.client.util.*;

public class PrettyPrintWriter implements PrettyPrintWriterIF
{
    public StringBuffer buffer;
    public int          indentationLevel;
    public int          indentationAmount;
    public String       tokenGroupBegan;
    public String       tokenGroupBeganEOL;
    public String       tokenGroupItemBegan;
    public String       tokenGroupItemEnded;
    public String       separatorGroupItemEnded;
    public String       separatorGroupEnded;
    public String       tokenGroupEnded;
    public String       tokenItemBegan;
    public String       tokenItemEnded;
    public String       separatorItemEnded;

    public PrettyPrintWriter()
    {
        this(new StringBuffer());
    }

    public PrettyPrintWriter(StringBuffer stringBuffer)
    {
        String sep = System.getProperty("line.separator");
        if (sep == null)
        {
            sep = "\n";
        }

        this.buffer                  = stringBuffer;
        this.indentationLevel        = 0;
        this.indentationAmount       = 4;
        this.tokenGroupBegan         = " {";
        this.tokenGroupBeganEOL      = sep;
        this.tokenGroupItemBegan     = "";
        this.tokenGroupItemEnded     = "";
        this.separatorGroupItemEnded = sep;
        this.separatorGroupEnded     = sep;
        this.tokenGroupEnded         = "}";
        this.tokenItemBegan          = "";
        this.tokenItemEnded          = "";
        this.separatorItemEnded      = sep;
    }

    public String toString()
    {
        return buffer.toString();
    }

    public StringBuffer getBuffer()
    {
        return buffer;
    }

    public void setBuffer(StringBuffer stringBuffer)
    {
        this.buffer = stringBuffer;
    }

    public int getIndentationAmount()
    {
        return indentationAmount;
    }

    public void setIndentationAmount(int indentationAmount)
    {
        this.indentationAmount = indentationAmount;
    }

    public PrettyPrintWriterIF incLevel()
    {
        indentationLevel++;

        return this;
    }

    public PrettyPrintWriterIF decLevel()
    {
        if (indentationLevel > 0)
        {
            indentationLevel--;
        }

        return this;
    }

    public PrettyPrintWriterIF startPrintingGroup(String string)
    {
        if (indentationAmount > 0 && indentationLevel > 0)
        {
            buffer.append(StringHelper.spaces(indentationLevel * indentationAmount));
        }
        buffer.append(string).append(tokenGroupBegan).append(tokenGroupBeganEOL);
        incLevel();

        return this;
    }

    public PrettyPrintWriterIF printGroupItem(Object object)
    {
        if (object != null)
        {
            if (indentationAmount > 0 && indentationLevel > 0)
            {
                buffer.append(StringHelper.spaces(indentationLevel * indentationAmount));
            }
            buffer.append(tokenGroupItemBegan).append(object.toString()).append(tokenGroupItemEnded);

            if (separatorGroupItemEnded.indexOf('\n') >= 0)
            {
                buffer.append(separatorGroupItemEnded);
            }
            else
            {
                buffer.append(separatorGroupItemEnded);
            }
        }

        return this;
    }

    public PrettyPrintWriterIF endPrintingGroup()
    {
        decLevel();
        if (indentationAmount > 0 && indentationLevel > 0)
        {
            buffer.append(StringHelper.spaces(indentationLevel * indentationAmount));
        }
        buffer.append(tokenGroupEnded).append(separatorGroupEnded);

        return this;
    }

    public PrettyPrintWriterIF printItem(Object object)
    {
        if (object != null)
        {
            if (indentationAmount > 0 && indentationLevel > 0)
            {
                buffer.append(StringHelper.spaces(indentationLevel * indentationAmount));
            }
            buffer.append(tokenItemBegan).append(object.toString()).append(tokenItemEnded).append(separatorItemEnded);
        }

        return this;
    }
}
