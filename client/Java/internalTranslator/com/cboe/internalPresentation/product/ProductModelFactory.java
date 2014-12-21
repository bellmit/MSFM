package com.cboe.internalPresentation.product;

import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.interfaces.presentation.product.Product;
import com.cboe.interfaces.internalPresentation.product.ProductModel;
import com.cboe.domain.util.ProductStructBuilder;
import com.cboe.interfaces.internalPresentation.product.ProductCusip;
import com.cboe.interfaces.domain.Price;
import com.cboe.presentation.product.ProductFactoryHome;

public class ProductModelFactory
{

    /**
     *  Hidden to prevent instantiation of this factory since it is a static
     *  factory.
     */
    private ProductModelFactory()
    {}

    /**
     * Creates an instance of a ProductModelImpl from a passed in Product.
     * @param Product to wrap in instance of ProductModel
     * @return ProductModel to represent the Product
     */
    public static ProductModel create(Product product)
    {
        if (product == null)
        {
            throw new IllegalArgumentException("Product can not be null");
        }
        ProductModel productModel = new ProductModelImpl(product);

        return productModel;
    }

    /**
     * Creates an instance of a ProductModelImpl from a passed in Product and cusip.
     * @param   product - Product - to wrap in instance of ProductModel
     * @param   cusip - ProductCusip - cusip to place in the model, since cusip is not in the product
     * @return  ProductModel to represent the cusip-extended Product
     * @since   March 18, 2005     Shawn Khosravani
     */
    public static ProductModel create(Product product, ProductCusip cusip, Price closingPrice, String closingSuffix, boolean restrictedIndicator)
    {
        ProductModel model = create(product);
        model.setCusip(cusip);
        if(closingPrice == null)
        {
            model.setClosingPrice(null);
        }
        else
        {
            model.setClosingPrice(closingPrice.toStruct());
        }
        model.setClosingSuffix(closingSuffix);
        model.setRestrictedProduct(restrictedIndicator);

        return model;
    }

    /**
     * Creates an instance of a ProductModelImpl from a passed in ProductStruct.
     * @param ProductStruct to wrap in instance of ProductModel
     * @return ProductModel to represent the ProductStruct
     */
    public static ProductModel create(ProductStruct productStruct)
    {
        Product product = ProductFactoryHome.find().create(productStruct);
        ProductModel productModel = create(product);

        return productModel;
    }
    /**
     * Creates an new instance of a ProductModelImpl.
     * @param productType - type of a new product
     * @return ProductModel to represent the new Product
     */
    public static ProductModel create(short productType)
    {
        ProductStruct productStruct = ProductStructBuilder.buildProductStruct();
        productStruct.productKeys.productType = productType;

        return create(productStruct);
    }

}
