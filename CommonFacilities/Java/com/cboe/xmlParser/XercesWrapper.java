//
// -----------------------------------------------------------------------------------
// Source file: XercesWrapper.java
//
// PACKAGE: com.cboe.xmlParser;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2002 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.xmlParser;

import java.io.IOException;

import org.w3c.dom.Document;
import org.w3c.dom.Text;

import org.xml.sax.ErrorHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

import org.apache.xerces.dom.TextImpl;
import org.apache.xerces.parsers.DOMParser;

/*
 * Provides our standard wrapper around the Xerces Implementation for XML parsing
 */
public class XercesWrapper implements ParserWrapper
{
    /** Validation feature id (http://xml.org/sax/features/validation). */
    public static final String VALIDATION_FEATURE_ID = "http://xml.org/sax/features/validation";
    private boolean validationFeature = true;

    /** Validation feature id (http://apache.org/xml/features/validation/warn-on-duplicate-attdef). */
    public static final String WARN_DUPE_ATT_FEATURE_ID = "http://apache.org/xml/features/validation/warn-on-duplicate-attdef";
    private boolean warnDupeAttFeature = true;

    /** Validation feature id (http://apache.org/xml/features/validation/warn-on-undeclared-elemdef). */
    public static final String WARN_MISSING_ELEM_FEATURE_ID = "http://apache.org/xml/features/validation/warn-on-undeclared-elemdef";
    private boolean warnMissingElemFeature = true;

    /** Validation feature id (http://apache.org/xml/features/dom/include-ignorable-whitespace). */
    public static final String INCLUDE_WHITESPACE_FEATURE_ID = "http://apache.org/xml/features/dom/include-ignorable-whitespace";
    private boolean includeWhitespaceFeature = false;

    protected DOMParser parser;
    protected ErrorHandler errorHandler;

    /**
     * Default constructor. Must supply error handler
     * @param errorHandler for parser to make callbacks for errors in parsing
     */
    public XercesWrapper(ErrorHandler errorHandler)
    {
        if(errorHandler == null)
        {
            throw new IllegalArgumentException("errorHandler must not be null.");
        }
        this.errorHandler = errorHandler;

        parser = new DOMParser();

        setDefaultFeatures();

        parser.setErrorHandler(errorHandler);
    }

    /**
     * Parses the specified URI and returns the document.
     * @param uri to parse
     * @return Document that has been parsed
     */
    public Document parse(String uri) throws SAXException, IOException
    {
        parser.parse(uri);
        return parser.getDocument();
    }

    /**
     * Parses the specified URI and returns the document.
     * @param source to parse
     * @return Document that has been parsed
     */
    public Document parse(InputSource source) throws SAXException, IOException
    {
        parser.parse(source);
        return parser.getDocument();
    }

    /**
     * Set the state of any feature in a SAX2 parser.  The parser
     * might not recognize the feature, and if it does recognize
     * it, it might not be able to fulfill the request.
     * @param featureId The unique identifier (URI) of the feature.
     * @param state The requested state of the feature (true or false).
     * @exception SAXNotRecognizedException If the
     *            requested feature is not known.
     * @exception SAXNotSupportedException If the
     *            requested feature is known, but the requested
     *            state is not supported.
     */
    public void setFeature(String featureId, boolean state)
            throws SAXNotRecognizedException, SAXNotSupportedException
    {
        parser.setFeature(featureId, state);
    }

    /**
     * Returns true if the specified text node is ignorable whitespace.
     */
    public boolean isIgnorableWhitespace(Text text)
    {
        return ((TextImpl)text).isIgnorableWhitespace();
    }

    /**
     * Sets the default features on the parser
     */
    protected void setDefaultFeatures()
    {
        try
        {
            setFeature(VALIDATION_FEATURE_ID, validationFeature);
        }
        catch(SAXException e)
        {}

        try
        {
            setFeature(WARN_DUPE_ATT_FEATURE_ID, warnDupeAttFeature);
        }
        catch(SAXException e)
        {}

        try
        {
            setFeature(WARN_MISSING_ELEM_FEATURE_ID, warnMissingElemFeature);
        }
        catch(SAXException e)
        {}

        try
        {
            setFeature(INCLUDE_WHITESPACE_FEATURE_ID, includeWhitespaceFeature);
        }
        catch(SAXException e)
        {}
    }
}
