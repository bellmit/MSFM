package com.cboe.internalPresentation.product;

import com.cboe.interfaces.internalPresentation.product.GroupModel;
import com.cboe.interfaces.internalPresentation.product.ProductClassGroupModel;
import com.cboe.interfaces.presentation.product.ProductClass;
import com.cboe.idl.product.GroupStruct;
import com.cboe.internalPresentation.api.SystemAdminAPIFactory;

import java.util.ArrayList;

import org.omg.CORBA.UserException;

/**
 * @author torresl@cboe.com
 */
class ProductClassGroupModelImpl extends GroupModelImpl implements ProductClassGroupModel
{
    ArrayList productClasses;
    public ProductClassGroupModelImpl(GroupModel group)
    {
        this(group.getStruct());
    }
    public ProductClassGroupModelImpl(GroupStruct groupStruct)
    {
        super(groupStruct);
        initialize();
    }
    private void initialize()
    {
        productClasses = new ArrayList(10);
    }

    public void addProductClass(ProductClass productClass)
    {
        if(productClasses.contains(productClass) == false)
        {
            productClasses.add(productClass);
            firePropertyChange(PRODUCT_CLASS_ADDED, null, productClass);
        }
    }

    public void removeProductClass(ProductClass productClass)
    {
        productClasses.remove(productClass);
        firePropertyChange(PRODUCT_CLASS_REMOVED, productClass, null);
    }

    public ProductClass[] getProductClasses()
    {
        return (ProductClass[]) productClasses.toArray(new ProductClass[0]);
    }

    public void addProductClasses(ProductClass[] mProductClasses)
    {
        for (int i = 0; i < mProductClasses.length; i++)
        {
            ProductClass mProductClass = mProductClasses[i];
            productClasses.add(mProductClass);
        }
        firePropertyChange(PRODUCT_CLASSES_ADDED, null, mProductClasses);
    }

    public void clearAll()
    {
        ProductClass[] productClassList = getProductClasses();
        productClasses.clear();
        firePropertyChange(PRODUCT_CLASS_CLEARED, productClassList, null);
    }
}
