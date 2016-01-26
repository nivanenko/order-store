package util.xml;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.util.ArrayList;
import java.util.List;

public class XMLParser {
    private String depZip = "";
    private String depState = "";
    private String depCity = "";
    private String delZip = "";
    private String delState = "";
    private String delCity = "";
    private List<Double> itemWeight = new ArrayList<>();
    private List<Double> itemVol = new ArrayList<>();
    private List<Boolean> itemHaz = new ArrayList<>();
    private List<String> itemProd = new ArrayList<>();

    public String getDepState() {
        return depState;
    }

    public String getDepCity() {
        return depCity;
    }

    public String getDelZip() {
        return delZip;
    }

    public String getDelState() {
        return delState;
    }

    public String getDelCity() {
        return delCity;
    }

    public List<Double> getItemWeight() {
        return itemWeight;
    }

    public List<Double> getItemVol() {
        return itemVol;
    }

    public List<Boolean> getItemHaz() {
        return itemHaz;
    }

    public List<String> getItemProd() {
        return itemProd;
    }

    public String getDepZip() {
        return depZip;
    }

    public void parseString(String s) {
        parseBytes(s.getBytes());
    }

    private void parseBytes(byte[] b) {
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