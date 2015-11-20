package server.object;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;

/**
 * <p>Java class for line complex type.
 * <p>
 * <p>The following schema fragment specifies the expected content contained within this class.
 * <p>
 * <pre>
 * &lt;complexType name="line">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;attribute name="hazard" type="{http://www.w3.org/2001/XMLSchema}boolean" default="false" />
 *       &lt;attribute name="product" use="required" type="{http://www.w3.org/2001/XMLSchema}string" />
 *       &lt;attribute name="volume" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *       &lt;attribute name="weight" use="required" type="{http://www.w3.org/2001/XMLSchema}double" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 */

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "line")
public class Line {

    @XmlAttribute(name = "hazard")
    protected Boolean hazard;
    @XmlAttribute(name = "product", required = true)
    protected String product;
    @XmlAttribute(name = "volume", required = true)
    protected double volume;
    @XmlAttribute(name = "weight", required = true)
    protected double weight;

    /**
     * Gets the value of the hazard property.
     *
     * @return possible object is
     * {@link Boolean }
     */
    public boolean isHazard() {
        if (hazard == null) {
            return false;
        } else {
            return hazard;
        }
    }

    /**
     * Sets the value of the hazard property.
     *
     * @param value allowed object is
     *              {@link Boolean }
     */
    public void setHazard(Boolean value) {
        this.hazard = value;
    }

    /**
     * Gets the value of the product property.
     *
     * @return possible object is
     * {@link String }
     */
    public String getProduct() {
        return product;
    }

    /**
     * Sets the value of the product property.
     *
     * @param value allowed object is
     *              {@link String }
     */
    public void setProduct(String value) {
        this.product = value;
    }

    /**
     * Gets the value of the volume property.
     */
    public double getVolume() {
        return volume;
    }

    /**
     * Sets the value of the volume property.
     */
    public void setVolume(double value) {
        this.volume = value;
    }

    /**
     * Gets the value of the weight property.
     */
    public double getWeight() {
        return weight;
    }

    /**
     * Sets the value of the weight property.
     */
    public void setWeight(double value) {
        this.weight = value;
    }
}