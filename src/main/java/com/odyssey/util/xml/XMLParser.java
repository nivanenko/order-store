package com.odyssey.util.xml;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.odyssey.model.Order;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.io.IOException;

public class XMLParser {
    private Order order;
    private String xml;

    public XMLParser(Order order, String xml) {
        this.order = order;
        this.xml = xml;
    }

    public XMLParser(String xml) {
        this.xml = xml;
    }

    public void parseString() {
        try {
            parseBytes(xml.getBytes("UTF-8"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void parseBytes(byte[] bytes) {
        AsyncXMLInputFactory inputFactory = new InputFactoryImpl();
        AsyncXMLStreamReader<AsyncByteArrayFeeder> reader = inputFactory.createAsyncForByteArray();
        AsyncReaderWrapper wrapper = new AsyncReaderWrapper(reader, 1, bytes);
        try {
            int type = wrapper.nextToken();
            while (type != XMLStreamConstants.END_DOCUMENT) {
                if (type == XMLStreamConstants.START_ELEMENT) {
                    switch (reader.getName().toString()) {
                        case "from":
                            order.setDepZip(reader.getAttributeValue(0));
                            order.setDepState(reader.getAttributeValue(1));
                            order.setDepCity(reader.getAttributeValue(2));
                            break;
                        case "to":
                            order.setDelZip(reader.getAttributeValue(0));
                            order.setDelState(reader.getAttributeValue(1));
                            order.setDelCity(reader.getAttributeValue(2));
                            break;
                        case "line":
                            order.getItemWeight().add(reader.getAttributeAsDouble(0));
                            order.getItemVol().add(reader.getAttributeAsDouble(1));
                            order.getItemHazBool().add(reader.getAttributeAsBoolean(2));
                            order.getItemProd().add(reader.getAttributeValue(3));
                            break;
                        default:
                            break;
                    }
                }
                type = wrapper.nextToken();
            }
            reader.close();
        } catch (XMLStreamException e) {
            System.err.println("XML parsing error: " + e.getMessage());
            e.printStackTrace();
        }
    }
}