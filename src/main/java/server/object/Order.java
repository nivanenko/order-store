package server.object;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * <p>Java class for order complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="order">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="from" type="{}location"/>
 *         &lt;element name="to" type="{}location"/>
 *         &lt;element name="lines" type="{}lines"/>
 *         &lt;element name="instructions" type="{http://www.w3.org/2001/XMLSchema}string"/>
 *       &lt;/sequence>
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "order", propOrder = {
        "from",
        "to",
        "lines",
        "instructions"
})
public class Order {

    @XmlElement(required = true)
    protected Location from;
    @XmlElement(required = true)
    protected Location to;
    @XmlElement(required = true)
    protected Lines lines;
    @XmlElement(required = true)
    protected String instructions;

    /**
     * Gets the value of the from property.
     *
     * @return possible object is
     * {@link Location }
     */
    public Location getFrom() {
        return from;
    }

    /**
     * Sets the value of the from property.
     *
     * @param value allowed object is
     *              {@link Location }
     */
    public void setFrom(Location value) {
        this.from = value;
    }

    /**
     * Gets the value of the to property.
     *
     * @return possible object is
     * {@link Location }
     */
    public Location getTo() {
        return to;
    }

    /**
     * Sets the value of the to property.
     *
     * @param value allowed object is
     *              {@link Location }
     */
    public void setTo(Location value) {
        this.to = value;
    }

    /**
     * Gets the value of the lines property.
     *
     * @return possible object is
     * {@link Lines }
     */
    public Lines getLines() {
        return lines;
    }

    /**
     * Sets the value of the lines property.
     *
     * @param value allowed object is
     *              {@link Lines }
     */
    public void setLines(Lines value) {
        this.lines = value;
    }

    /**
     * Gets the value of the instructions property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getInstructions() {
        return instructions;
    }

    /**
     * Sets the value of the instructions property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setInstructions(String value) {
        this.instructions = value;
    }
}