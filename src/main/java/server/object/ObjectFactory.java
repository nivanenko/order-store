package server.object;

import javax.xml.bind.JAXBElement;
import javax.xml.bind.annotation.XmlElementDecl;
import javax.xml.bind.annotation.XmlRegistry;
import javax.xml.namespace.QName;


/**
 * This object contains factory methods for each
 * Java content interface and Java element interface
 * generated in the objects package.
 * <p>An ObjectFactory allows you to programatically
 * construct new instances of the Java representation
 * for XML content. The Java representation of XML
 * content can consist of schema derived interfaces
 * and classes representing the binding of schema
 * type definitions, element declarations and model
 * groups.  Factory methods for each of these are
 * provided in this class.
 */
@XmlRegistry
public class ObjectFactory {

    private final static QName _Order_QNAME = new QName("ordr", "order");

    /**
     * Create a new ObjectFactory that can be used to create new instances of schema derived classes for package: objects
     */
    public ObjectFactory() {
    }

    /**
     * Create an instance of {@link Order }
     */
    public Order createOrder() {
        return new Order();
    }

    /**
     * Create an instance of {@link Line }
     */
    public Line createLine() {
        return new Line();
    }

    /**
     * Create an instance of {@link Location }
     */
    public Location createLocation() {
        return new Location();
    }

    /**
     * Create an instance of {@link Lines }
     */
    public Lines createLines() {
        return new Lines();
    }

    /**
     * Create an instance of {@link JAXBElement }{@code <}{@link Order }{@code >}}
     */
    @XmlElementDecl(namespace = "ordr", name = "order")
    public JAXBElement<Order> createOrder(Order value) {
        return new JAXBElement<Order>(_Order_QNAME, Order.class, null, value);
    }
}