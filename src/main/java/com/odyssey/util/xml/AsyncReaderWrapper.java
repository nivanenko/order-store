package com.odyssey.util.xml;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLStreamReader;

import javax.xml.stream.XMLStreamException;

class AsyncReaderWrapper {
    private final AsyncXMLStreamReader<AsyncByteArrayFeeder> streamReader;

    private byte[] xmlBytes;
    private final int bytesPerFeed;
    private int offset;

    AsyncReaderWrapper(AsyncXMLStreamReader<AsyncByteArrayFeeder> sr, int bytesPerCall,
                       byte[] bytes) {
        streamReader = sr;
        xmlBytes = bytes;
        bytesPerFeed = bytesPerCall;
    }

    int nextToken() throws XMLStreamException {
        int token;

        while ((token = streamReader.next()) == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
            AsyncByteArrayFeeder feeder = streamReader.getInputFeeder();

            if (!feeder.needMoreInput()) {
                throw new XMLStreamException("Need more input, error!");
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