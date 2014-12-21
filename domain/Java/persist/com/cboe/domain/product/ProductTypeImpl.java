package com.cboe.domain.product;

// Source file: com/cboe/domain/product/ProductTypeImpl.java

import com.cboe.interfaces.domain.product.*;
import com.cboe.exceptions.*;
import com.cboe.idl.cmiProduct.*;
import com.cboe.idl.cmiConstants.*;
import com.cboe.domain.util.StructBuilder;
import com.cboe.domain.util.*;
import com.cboe.infrastructureServices.persistenceService.*;
import com.cboe.util.*;
import java.lang.reflect.*;
import java.util.*;

/**
 * A persistent implentation of <code>ProductType</code>.
 *
 * @author John Wickberg
 */
public class ProductTypeImpl extends DomainBaseImpl implements ProductType
{
	/**
	 * Name of database table
	 */
	public static final String TABLE_NAME="prod_type";
	/**
	 * Product type as defined in CORBA <code>ProductType</code> enumeration.
	 */
	private short type;
	/**
	 * A name for this product type.
	 */
	private String name;
	/**
	 * A full description of this product type.
	 */
	private String description;

	private static Vector classDescriptor;

	private static Field _type;
	private static Field _name;
	private static Field _description;

	/**
	* This static block will be regenerated if persistence is regenerated.
	*/
	static { /*NAME:fieldDefinition:*/
		try{
			_type = ProductTypeImpl.class.getDeclaredField("type");
			_name = ProductTypeImpl.class.getDeclaredField("name");
			_description = ProductTypeImpl.class.getDeclaredField("description");

		}
		catch (NoSuchFieldException ex) { System.out.println(ex); }
	}
/**
 * Creates an instance with default values.
 */
public ProductTypeImpl()
{
	super();
    setUsing32bitId(true);
}
/**
 * @see ProductType#create
 */
public void create(ProductTypeStruct newType) throws DataValidationException
{
	setType(newType.type);
	setName(newType.name);
	setDescription(newType.description);
}
/**
 * @see ProductType#getDescription
 */
public String getDescription()
{
	return (String) editor.get(_description, description);
}
/**
* A conveince method to get a copy of this static class descriptor.
*/
public static Vector getDescriptor()
{
	return (Vector) classDescriptor.clone();
}
/**
 * @see ProductType#getName
 */
public String getName()
{
	return (String) editor.get(_name, name);
}
/**
 * @see ProductType#getType
 */
public short getType()
{
	return editor.get(_type, type);
}
/**
 * Describe how this class relates to the relational database.
 */
public void initDescriptor()
{
	synchronized (ProductTypeImpl.class)
	{
		if (classDescriptor != null)
			return;
		Vector tempDescriptor = super.getDescriptor();
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("prod_type_code", _type));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("type_name", _name));
		tempDescriptor.addElement(AttributeDefinition.getAttributeRelation("type_desc", _description));
        classDescriptor = tempDescriptor;
	}
}
/**
* Needed to define table name and the description of this class.
*/
public ObjectChangesIF initializeObjectEditor()
{
	final DBAdapter result = (DBAdapter) super.initializeObjectEditor();
	if (classDescriptor == null)
		initDescriptor();
	result.setTableName(TABLE_NAME);
	result.setClassDescription(classDescriptor);
	return result;
}
/**
 * @see ProductType#setDescription
 */
public void setDescription(String newDescription)
{
	editor.set(_description, newDescription, description);
}
/**
 * @see ProductType#setName
 */
public void setName(String newName)
{
	editor.set(_name, newName, name);
}
/**
 * Sets product type.  Only used when type is created.
 *
 * @param newType type for new product
 */
private void setType(short newType)
{
	editor.set(_type, newType, type);
}
/**
 * @see ProductType#toStruct
 */
public ProductTypeStruct toStruct()
{
	ProductTypeStruct aStruct = new ProductTypeStruct();
	aStruct.type = getType();
	aStruct.name = StructBuilder.nullToEmpty(getName());
	aStruct.description = StructBuilder.nullToEmpty(getDescription());
	aStruct.createdTime = DateWrapper.convertToDateTime(getCreatedTime());
	aStruct.lastModifiedTime = DateWrapper.convertToDateTime(getLastModifiedTime());
	return aStruct;
}
/**
 * @see ProductType#update
 */
public void update(ProductTypeStruct updatedType) throws DataValidationException
{
	setName(updatedType.name);
	setDescription(updatedType.description);
}
	/**  This method allows me to get arounds security problems with updating
	* and object from a generic framework.
	*/
	public void update(boolean get, Object [] data, Field [] fields)
	{
		for(int i = 0; i < data.length; i++){
			try{
			if(get)
				data[i] = fields[i].get(this);
			else
				fields[i].set(this, data[i]);
			} catch(IllegalAccessException ex) { System.out.println(ex); }
			catch(IllegalArgumentException ex) { System.out.println(ex); }
		}
	}
}
