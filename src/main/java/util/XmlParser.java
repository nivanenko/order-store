package util;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {
    static String depZip = "";
    static String depState = "";
    static String depCity = "";
    static String delZip = "";
    static String delState = "";
    static String delCity = "";
    static List<Double> itemWeight = new ArrayList<>();
    static List<Double> itemVol = new ArrayList<>();
    static List<Boolean> itemHaz = new ArrayList<>();
    static List<String> itemProd = new ArrayList<>();

    public static String getDepState() {
        return depState;
    }

    public static String getDepCity() {
        return depCity;
    }

    public static String getDelZip() {
        return delZip;
    }

    public static String getDelState() {
        return delState;
    }

    public static String getDelCity() {
        return delCity;
    }

    public static List<Double> getItemWeight() {
        return itemWeight;
    }

    public static List<Double> getItemVol() {
        return itemVol;
    }

    public static List<Boolean> getItemHaz() {
        return itemHaz;
    }

    public static List<String> getItemProd() {
        return itemProd;
    }

    public static String getDepZip() {
        return depZip;
    }

    public static void parseString(String s) {
        parseBytes(s.getBytes());
    }

    public static void parseBytes(byte[] b) {
        AsyncXMLInputFactory inputFactory = new InputFactoryImpl();
        AsyncXMLStreamReader<AsyncByteArrayFeeder> reader = inputFactory.createAsyncForByteArray();
        AsyncReaderWrapper wrapper = new AsyncReaderWrapper(reader, 1, b);
        try {
            int type = wrapper.nextToken();
            while (type != XMLStreamConstants.END_DOCUMENT) {
                if (type == XMLStreamConstants.START_ELEMENT) {
                    switch (reader.getName().toString()) {
                        case "from":
                            depZip = reader.getAttributeValue(0);
                            depState = reader.getAttributeValue(1);
                            depCity = reader.getAttributeValue(2);
                            break;
                        case "to":
                            delZip = reader.getAttributeValue(0);
                            delState = reader.getAttributeValue(1);
                            delCity = reader.getAttributeValue(2);
                            break;
                        case "line":
                            itemWeight.add(reader.getAttributeAsDouble(0));
                            itemVol.add(reader.getAttributeAsDouble(1));
                            itemHaz.add(reader.getAttributeAsBoolean(2));
                            itemProd.add(reader.getAttributeValue(3));
                            break;
                        default:
                            break;
                    }
                }
                type = wrapper.nextToken();
            }
            reader.close();
        } catch (XMLStreamException e) {
            System.err.println("XML error: " + e.getMessage());
        }
    }
}

class AsyncReaderWrapper {
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