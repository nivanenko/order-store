package servlet.async;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLStreamReader;

import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class AsyncReaderWrapper {
    private final AsyncXMLStreamReader<AsyncByteArrayFeeder> _streamReader;
    private final byte[] _xml;
    private final int _bytesPerFeed;
    private int _offset = 0;

    public AsyncReaderWrapper(AsyncXMLStreamReader<AsyncByteArrayFeeder> sr, String xmlString) {
        this(sr, 1, xmlString);
    }

    public AsyncReaderWrapper(AsyncXMLStreamReader<AsyncByteArrayFeeder> sr, int bytesPerCall, String xmlString) {
        _streamReader = sr;
        _bytesPerFeed = bytesPerCall;
        try {
            _xml = xmlString.getBytes("UTF-8");
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String currentText() throws XMLStreamException {
        return _streamReader.getText();
    }

    public int currentToken() throws XMLStreamException {
        return _streamReader.getEventType();
    }

    public int nextToken() throws XMLStreamException {
        int token;

        while ((token = _streamReader.next()) == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
            AsyncByteArrayFeeder feeder = _streamReader.getInputFeeder();
            if (!feeder.needMoreInput()) {
                System.out.println("Got EVENT_INCOMPLETE, could not feed more input");
            }
            if (_offset >= _xml.length) { // end-of-input?
                feeder.endOfInput();
            } else {
                int amount = Math.min(_bytesPerFeed, _xml.length - _offset);
                feeder.feedInput(_xml, _offset, amount);
                _offset += amount;
            }
        }
        return token;
    }
}