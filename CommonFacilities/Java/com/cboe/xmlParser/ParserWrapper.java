//
// -----------------------------------------------------------------------------------
// Source file: ParserWrapper.java
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

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXNotRecognizedException;
import org.xml.sax.SAXNotSupportedException;

/*
 * Provides the contract for a standard XML parser wrapper of an implementation
 */
public interface ParserWrapper
{
    /**
     * Parses the specified URI and returns the document.
     * @param uri to parse
     * @return Document that has been parsed
     */
    public Document parse(String uri) throws SAXException, IOException;

    /**
     * Parses the specified URI and returns the document.
     * @param source to parse
     * @return Document that has been parsed
     */
    public Document parse(InputSource source) throws SAXException, IOException;

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
            throws SAXNotRecognizedException, SAXNotSupportedException;

    /**
     * Returns true if the specified text node is ignorable whitespace.
     */
    public boolean isIgnorableWhitespace(Text text);
}
