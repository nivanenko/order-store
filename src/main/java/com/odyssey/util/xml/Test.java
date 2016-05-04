package com.odyssey.util.xml;

import com.fasterxml.aalto.AsyncByteArrayFeeder;
import com.fasterxml.aalto.AsyncInputFeeder;
import com.fasterxml.aalto.AsyncXMLInputFactory;
import com.fasterxml.aalto.AsyncXMLStreamReader;
import com.fasterxml.aalto.stax.InputFactoryImpl;
import com.odyssey.model.Order;

import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import java.nio.charset.Charset;

public class Test {
    public static void main(String[] args) {
        Order order = new Order();
        XMLParser parser = new XMLParser(order);
        String xml1 = "<?xml version=\"1.0\" encoding=\"utf-8\"?>\n" +
                "<order>\n" +
                "    <from zip=\"10001\" state=\"NY\" city=\"NEW YORK\"/>\n" +
                "    <to zip=\"20001\" state=\"DC\" city=\"WASHINGTON\"/>\n" +
                "    <lines>\t\n" +
                "        <line weight=\"1000.1\" volume=\"";

        String xml2 = "666\" hazard=\"true\" product=\"petrol\"/>\n" +
                "        <line weight=\"2000\" volume=\"666\" hazard=\"false\" product=\"water\"/>\n" +
                "    </lines>\n" +
                "    <instructions>here be dragons</instructions>\n" +
                "</order> ";
        byte[] bytes1 = xml1.getBytes(Charset.forName("UTF-8"));
        byte[] bytes2 = xml2.getBytes(Charset.forName("UTF-8"));

//        parser.parseBytes(bytes1);
//        parser.feedMoreBytes(bytes2);
        AsyncXMLInputFactory inputFactory = new InputFactoryImpl();
        AsyncXMLStreamReader<AsyncByteArrayFeeder> reader = inputFactory.createAsyncForByteArray();
        AsyncInputFeeder feeder = reader.getInputFeeder();

        try {
            int type = 0;

            while ((type = reader.next()) == AsyncXMLStreamReader.EVENT_INCOMPLETE) {
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
                }
            }

        } catch (XMLStreamException e) {
            e.printStackTrace();
        }
    }
}
