package util;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
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
        AsyncXMLInputFactory inputFactory = new InputFactoryImpl();
        AsyncXMLStreamReader<AsyncByteArrayFeeder> reader = inputFactory.createAsyncForByteArray();
        AsyncReaderWrapper wrapper = new AsyncReaderWrapper(reader, 1, s);
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