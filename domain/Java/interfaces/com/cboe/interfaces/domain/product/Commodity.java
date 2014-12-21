package com.cboe.interfaces.domain.product;

// Source file: com/cboe/interfaces/domain/product/Commodity.java

/**
 * A product that specifies the terms used to buy or sell a real-world item.
 *
 * @author John Wickberg
 */
public interface Commodity extends BaseProduct
{
/**
 * Gets commodity description.
 *
 * @return commodity description
 *
 * @author John Wickberg
 * @roseuid 362DF73B000F
 */
public String getDescription();
/**
 * Gets the standard quantity for trades in this commodity.
 *
 * @return standard quantity
 *
 * @author John Wickberg
 */
double getStandardQuantity();
/**
 * Gets the unit of measure used for trades in this commodity, for example,
 * bushels for wheat.
 *
 * @return unit of measure
 *
 * @author John Wickberg
 */
String getUnitMeasure();
/**
 * Sets commodity description.
 *
 * @param newDescription new commodity description
 *
 * @author John Wickberg
 * @roseuid 362DF74E00FD
 */
public void setDescription(String newDescription);
/**
 * Sets standard quantity for trades in this product.
 * 
 * @param newQuantity new standard quantity
 *
 * @author John Wickberg
 */
void setStandardQuantity(double newQuantity);
/**
 * Sets unit of measure for trades in this product.
 * 
 * @param newUnits new unit of measure
 *
 * @author John Wickberg
 */
void setUnitMeasure(String newUnits);
}
