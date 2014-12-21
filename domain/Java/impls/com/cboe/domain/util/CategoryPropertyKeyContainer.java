package com.cboe.domain.util;

public class CategoryPropertyKeyContainer extends Object {
    private String category;
    private String propertyKey;
    private int hashCode;

    /**
      * Sets the internal fields to the passed values
      */
    public CategoryPropertyKeyContainer(String category, String propertyKey) {
		this.category = category;
		this.propertyKey = propertyKey;
		hashCode = category.hashCode()+propertyKey.hashCode();
    }
    public String getCategory()
    {
        return category;
    }

    public String getPropertyKey()
    {
        return propertyKey;
    }

    public int hashCode()
    {
        return hashCode;
    }

    public boolean equals(Object obj)
    {
        if ((obj != null) && (obj instanceof CategoryPropertyKeyContainer))
        {
            String tempCategory = ((CategoryPropertyKeyContainer)obj).getCategory();
            String tempPropertyKey = ((CategoryPropertyKeyContainer)obj).getPropertyKey();
            return (this.category.equals(tempCategory)
                    &&  this.propertyKey.equals(tempPropertyKey)
                    );

        }
        return false;
    }
}
