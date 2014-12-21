package com.cboe.presentation.contingency;

/**
 * This type was created in VisualAge.
 */
public class Contingency {
    
    private short contingencyID;
    private String contingencyName;
    private String displayText;
    
    /**
     * This method was created in VisualAge.
     * @param name java.lang.String
     * @param contingencyType int
     */
    Contingency(String name,String displayText ,short contingencyType) {
        
        setContingencyName(name);
        setDisplayText(displayText);
        setContingencyID(contingencyType);
        
    }

    /**
     * Returns contingency Id
     * @return int
     */
    public short getContingencyID() {
        return contingencyID;
    }
    
    /**
     * Returns contingency name
     * @return String
     */
    public String getContingencyName() {
        return contingencyName;
    }
    
    /**
     * Returns display text
     * @return String
     */
    public String getDisplayText() {
        return displayText;
    }
    /**
     * Sets the contingency id.
     * @param newValue int
     */
    private void setContingencyID(short newValue) {
        contingencyID = newValue;
    }
    
    /**
     * Sets the contingency name
     * @param newValue String
     */
    private void setContingencyName(String newValue) {
        contingencyName = newValue;
    }
    
    /**
     * Sets the display text
     * @param newValue java.lang.String
     */
    private void setDisplayText(String newValue) {
        displayText = newValue;
    }
    
    /**
     * Overrides the toString()
     * @return java.lang.String
     */
    public String toString() {
        return getDisplayText();
    }

    /**
     * overriding default equals method for using comparison based on contingency id.
     * @param other
     * @return boolean status
     */
    public boolean equals(Object other)
    {
        boolean isEqual = false;
        
        if(other instanceof Contingency)
        {
            if( this == other )
            {
                isEqual = true;
            }        
            else
            {
                //Compare the unique contingency id of each contingency for equivalence.
                Contingency otherContingency = ( Contingency ) other;
                isEqual = (getContingencyID() == otherContingency.getContingencyID());
            }
        }
        
        return isEqual;
    }
}
