package com.odyssey.util.xml;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLStreamReader;

import javax.xml.stream.XMLStreamException;

public class AsyncReaderWrapper {
    private final AsyncXMLStreamReader<AsyncByteArrayFeeder> streamReader;

    public void setXmlBytes(byte[] xmlBytes) {
        this.xmlBytes = xmlBytes;
    }

    private byte[] xmlBytes;
    private final int bytesPerFeed;
    private int offset;

    public AsyncReaderWrapper(AsyncXMLStreamReader<AsyncByteArrayFeeder> sr, int bytesPerCall,
                              byte[] bytes) {
        streamReader = sr;
        xmlBytes = bytes;
        bytesPerFeed = bytesPerCall;
    }

    public AsyncReaderWrapper(AsyncXMLStreamReader<AsyncByteArrayFeeder> sr, int bytesPerCall) {
        streamReader = sr;
        bytesPerFeed = bytesPerCall;
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