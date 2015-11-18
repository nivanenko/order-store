package server;

import javax.xml.namespace.QName;
import javax.xml.stream.XMLEventReader;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.events.Attribute;
import javax.xml.stream.events.StartElement;
import javax.xml.stream.events.XMLEvent;
import java.io.InputStream;
import java.util.ArrayList;

public class Parser {
    protected static String getAttrStr(String tag, String attribute, InputStream stream) {
        String value = "";
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(stream);

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals(tag)) {
                        Attribute attr = startElement.getAttributeByName(new QName(attribute));
                        if (attr != null) {
                            value = attr.getValue();
                        } else {
                            System.out.println("Attribute is not found!");
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("XML parsing error!");
        }
        return value;
    }

    protected static ArrayList<String> getAttrItemStr(String attribute, InputStream stream) {
        ArrayList<String> list = new ArrayList<>();
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(stream);

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("line")) {
                        Attribute zipAttr = startElement.getAttributeByName(new QName(attribute));
                        if (zipAttr != null) {
                            list.add(zipAttr.getValue());
                        } else {
                            System.out.println("Attribute not found!");
                        }
                    } else if (startElement.getName().getLocalPart().equals("line")) {
                        Attribute zipAttr = startElement.getAttributeByName(new QName(attribute));
                        if (zipAttr != null) {
                            list.add(zipAttr.getValue());
                        } else {
                            System.out.println("Attribute not found!");
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("XML parsing error!");
        }
        return list;
    }

    protected static ArrayList<Double> getAttrItemDouble(String attribute, InputStream stream) {
        ArrayList<Double> list = new ArrayList<>();
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(stream);

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("line")) {
                        Attribute zipAttr = startElement.getAttributeByName(new QName(attribute));
                        if (zipAttr != null) {
                            list.add(Double.parseDouble(zipAttr.getValue()));
                        } else {
                            System.out.println("Attribute not found!");
                        }
                    } else if (startElement.getName().getLocalPart().equals("line")) {
                        Attribute zipAttr = startElement.getAttributeByName(new QName(attribute));
                        if (zipAttr != null) {
                            list.add(Double.parseDouble(zipAttr.getValue()));
                        } else {
                            System.out.println("Attribute not found!");
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("XML parsing error!");
        }
        return list;
    }

    protected static ArrayList<Integer> getAttrItemInt(String attribute, InputStream stream) {
        ArrayList<Integer> list = new ArrayList<>();
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(stream);

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("line")) {
                        Attribute zipAttr = startElement.getAttributeByName(new QName(attribute));
                        if (zipAttr != null) {
                            list.add(Integer.parseInt(zipAttr.getValue()));
                        } else {
                            System.out.println("Attribute not found!");
                        }
                    } else if (startElement.getName().getLocalPart().equals("line")) {
                        Attribute zipAttr = startElement.getAttributeByName(new QName(attribute));
                        if (zipAttr != null) {
                            list.add(Integer.parseInt(zipAttr.getValue()));
                        } else {
                            System.out.println("Attribute not found!");
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("XML parsing error!");
        }
        return list;
    }

    protected static ArrayList<Boolean> getAttrItemBool(String attribute, InputStream stream) {
        ArrayList<Boolean> list = new ArrayList<>();
        try {
            XMLInputFactory xmlInputFactory = XMLInputFactory.newInstance();
            XMLEventReader xmlEventReader = xmlInputFactory.createXMLEventReader(stream);

            while (xmlEventReader.hasNext()) {
                XMLEvent xmlEvent = xmlEventReader.nextEvent();
                if (xmlEvent.isStartElement()) {
                    StartElement startElement = xmlEvent.asStartElement();
                    if (startElement.getName().getLocalPart().equals("line")) {
                        Attribute zipAttr = startElement.getAttributeByName(new QName(attribute));
                        if (zipAttr != null) {
                            list.add(Boolean.parseBoolean(zipAttr.getValue()));
                        } else {
                            System.out.println("Attribute not found!");
                        }
                    } else if (startElement.getName().getLocalPart().equals("line")) {
                        Attribute zipAttr = startElement.getAttributeByName(new QName(attribute));
                        if (zipAttr != null) {
                            list.add(Boolean.parseBoolean(zipAttr.getValue()));
                        } else {
                            System.out.println("Attribute not found!");
                        }
                    }
                }
            }
        } catch (XMLStreamException e) {
            System.out.println("XML parsing error!");
        }
        return list;
    }
}