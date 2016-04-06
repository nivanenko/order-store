package com.odyssey.util.xml;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLStreamReader;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class AsyncReaderWrapper {
    private final AsyncXMLStreamReader<AsyncByteArrayFeeder> streamReader;
    private final byte[] xmlBytes;
    private final int bytesPerFeed;
    private int offset;

    public AsyncReaderWrapper(AsyncXMLStreamReader<AsyncByteArrayFeeder> sr,
                              String xmlString) {
        this(sr, 1, xmlString);
    }

    public AsyncReaderWrapper(AsyncXMLStreamReader<AsyncByteArrayFeeder> sr,
                              int bytesPerCall, String xmlString) {
        streamReader = sr;
        bytesPerFeed = bytesPerCall;
        try {
            xmlBytes = xmlString.getBytes("UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public AsyncReaderWrapper(AsyncXMLStreamReader<AsyncByteArrayFeeder> sr, int bytesPerCall,
                              byte[] bytes) {
        streamReader = sr;
        xmlBytes = bytes;
        bytesPerFeed = bytesPerCall;
    }

    public String currentText() throws XMLStreamException {
        return streamReader.getText();
    }

    public int currentToken() throws XMLStreamException {
        return streamReader.getEventType();
    }

    public int nextToken() throws XMLStreamException {
        int token;

        while ((token = streamReader.next()) == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
            AsyncByteArrayFeeder feeder = streamReader.getInputFeeder();
            if (!feeder.needMoreInput()) {
                System.out.println("Got EVENT_INCOMPLETE, could not feed more input");
            }

             if (offset >= xmlBytes.length) { // end-of-input?
                feeder.endOfInput();
            } else {
                int amount = Math.min(bytesPerFeed, xmlBytes.length - offset);
                feeder.feedInput(xmlBytes, offset, amount);
                offset += amount;
            }
        }
        return token;
    }
}