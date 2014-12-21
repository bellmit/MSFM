//
// -----------------------------------------------------------------------------------
// Source file: Transform.java
//
// PACKAGE: com.cboe.xmlParser;
//
// -----------------------------------------------------------------------------------
// Copyright (c) 2000-2005 The Chicago Board Options Exchange. All Rights Reserved.
// -----------------------------------------------------------------------------------
package com.cboe.xmlParser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;

public class Transform
{
    TransformerFactory transformerFactory;

    public Transform()
    {
        transformerFactory = TransformerFactory.newInstance();
    }

    public TransformerFactory getTransformerFactory()
    {
        return transformerFactory;
    }

    public void transform(InputStream xslInputStream, InputStream xmlInputStream, OutputStream outputStream)
            throws TransformerException
    {
        transform(new StreamSource(xslInputStream), new StreamSource(xmlInputStream), new StreamResult(outputStream));
    }

    public void transform(File xslFile, File xmlFile, File outputFile) throws TransformerException
    {
        transform(new StreamSource(xslFile), new StreamSource(xmlFile), new StreamResult(outputFile));
    }

    public void transform(StreamSource xslStreamSource, StreamSource xmlStreamSource, StreamResult streamResult)
            throws TransformerException
    {
        // Use the TransformerFactory to instantiate a Transformer that will work with
        // the stylesheet you specify. This method call also processes the stylesheet
        // into a compiled Templates object.
        Transformer transformer = getTransformerFactory().newTransformer(xslStreamSource);
        // Use the Transformer to apply the associated Templates object to an XML document
        // (foo.xml) and write the output to a file (foo.out).
        transformer.transform(xmlStreamSource, streamResult);
    }

    public static void main(String[] args)
            throws TransformerException, ParserConfigurationException, SAXException, IOException
    {
        if (args.length != 3)
        {
            System.out.println("usage: java com.cboe.xmlParser.Transform <xml_file> <xsl_file> <output_file>");
            System.exit(1);
        }

        String xmlFile = args[0];
        String xslFile = args[1];
        String outputFile = args[2];

        Transform transform = new Transform();

        SAXParserFactory parserFactory = SAXParserFactory.newInstance();
        parserFactory.setValidating(true);
        SAXParser parser = parserFactory.newSAXParser();

        parser.parse(new InputSource(xmlFile), transform.getDefaultHandlerInstance());

        transform.transform(new File(xslFile), new File(xmlFile), new File(outputFile));
    }

    public DefaultHandler getDefaultHandlerInstance()
    {
        return new XMLDefaultHandler();
    }

    class XMLDefaultHandler extends DefaultHandler
    {
        public XMLDefaultHandler()
        {
            super();
        }

        public void error(SAXParseException e)
                throws SAXException
        {
            throw e;
        }

        public void warning(SAXParseException e)
                throws SAXException
        {
            throw e;
        }

    }
}