package com.cboe.test.application.cmiScript;

import com.cboe.idl.cmi.ProductQuery;
import com.cboe.idl.cmiProduct.ClassStruct;
import com.cboe.idl.cmiProduct.PendingAdjustmentStruct;
import com.cboe.idl.cmiProduct.PendingNameStruct;
import com.cboe.idl.cmiProduct.ProductStruct;
import com.cboe.idl.cmiProduct.ProductNameStruct;
import com.cboe.idl.cmiProduct.ProductTypeStruct;
import com.cboe.idl.cmiStrategy.StrategyStruct;


public class ProductQueryV1
{
    private EngineAccess engineAccess;
    private ProductQuery productQueryV1;

    private static final int INDEX_FIRST_PARAMETER = 2;
    
    public ProductQueryV1(EngineAccess ea, ProductQuery oq)
    {
        engineAccess = ea;
        productQueryV1 = oq;
    }

    /** Execute a command on a ProductQuery object.
     * @param command Words from command line: ProductQueryV1 function args...
     **/
    public void doCommand(String command[])
    {
        if (command.length < 2)
        {
            Log.message("Command line must have at least object, function");
            return;
        }

        try
        {
            String cmd = command[1];
            if (cmd.equalsIgnoreCase("getProductTypes"))
            {
                doGetProductTypes();
            }
            else if (cmd.equalsIgnoreCase("getProductClasses"))
            {
                doGetProductClasses(command);
            }
            else if (cmd.equalsIgnoreCase("getProductsByClass"))
            {
                doGetProductsByClass(command);
            }
            else if (cmd.equalsIgnoreCase("isValidProductName"))
            {
                doIsValidProductName(command);
            }
            else if (cmd.equalsIgnoreCase("getProductByName"))
            {
                doGetProductByName(command);
            }
            else if (cmd.equalsIgnoreCase("getProductNameStruct"))
            {
                doGetProductNameStruct(command);
            }
            else if (cmd.equalsIgnoreCase("getAllPendingAdjustments"))
            {
                doGetAllPendingAdjustments();
            }
            else if (cmd.equalsIgnoreCase("getPendingAdjustments"))
            {
                doGetPendingAdjustments(command);
            }
            else if (cmd.equalsIgnoreCase("getPendingAdjustmentProducts"))
            {
                doGetPendingAdjustmentProducts(command);
            }
            else if (cmd.equalsIgnoreCase("getStrategyByKey"))
            {
                doGetStrategyByKey(command);
            }
            else if (cmd.equalsIgnoreCase("getClassByKey"))
            {
                doGetClassByKey(command);
            }
            else if (cmd.equalsIgnoreCase("getProductByKey"))
            {
                doGetProductByKey(command);
            }
            else if (cmd.equalsIgnoreCase("getClassBySymbol"))
            {
                doGetClassBySymbol(command);
            }
            else if (cmd.equalsIgnoreCase("getStrategiesByComponent"))
            {
                doGetStrategiesByComponent(command);
            }
            else if (cmd.equalsIgnoreCase("getStrategiesByClass"))
            {
                doGetStrategiesByClass(command);
            }
            else
            {
                Log.message("Unknown function:" + cmd + "  for " + command[0]);
            }
        }
        catch (Throwable t)
        {
            Log.throwable(t);
        }
    }

    private void doGetProductTypes() throws Throwable
    {
        ProductTypeStruct ptseq[] = productQueryV1.getProductTypes();
        Log.message(Struct.toString(ptseq));
    }

    private void doGetProductClasses(String command[]) throws Throwable
    {
        String names[] = { "productType" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productType");
            return;
        }
        short productType = Short.parseShort(values[0]);
        ClassStruct result[] = productQueryV1.getProductClasses(productType);
        Log.message(Struct.toString(result));
    }

    private void doGetProductsByClass(String command[]) throws Throwable
    {
        String names[] = { "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);
        ProductStruct result[] = productQueryV1.getProductsByClass(classKey);
        Log.message(Struct.toString(result));
    }

    private void doIsValidProductName(String command[]) throws Throwable
    {
        String names[] = { "productName" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof ProductNameStruct))
        {
            Log.message("Not a ProductNameStruct:" + objName);
            return;
        }
        ProductNameStruct pn = (ProductNameStruct) o;
        boolean result = productQueryV1.isValidProductName(pn);
        StringBuilder s = new StringBuilder().append(result);
        Log.message(s);
    }

    private void doGetProductByName(String command[]) throws Throwable
    {
        String names[] = { "productName" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        String objName = values[0];
        Object o = engineAccess.getObjectFromStore(objName);
        if (o == null)
        {
            Log.message("Cannot find object:" + objName);
            return;
        }
        if (! (o instanceof ProductNameStruct))
        {
            Log.message("Not a ProductNameStruct:" + objName);
            return;
        }
        ProductNameStruct pn = (ProductNameStruct) o;
        ProductStruct result = productQueryV1.getProductByName(pn);
        Log.message(Struct.toString(result));
    }

    private void doGetProductNameStruct(String command[]) throws Throwable
    {
        String names[] = { "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[0]);

        ProductNameStruct result =
                productQueryV1.getProductNameStruct(productKey);
        Log.message(Struct.toString(result));
    }

    private void doGetAllPendingAdjustments() throws Throwable
    {
        PendingAdjustmentStruct paseq[] =
                productQueryV1.getAllPendingAdjustments();
        Log.message(Struct.toString(paseq));
    }

    private void doGetPendingAdjustments(String command[]) throws Throwable
    {
        String names[] = { "classKey", "includeProducts" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing includeProducts");
            return;
        }
        boolean includeProducts = CommandLine.booleanValue(values[1]);

        PendingAdjustmentStruct paseq[] =
                productQueryV1.getPendingAdjustments(classKey, includeProducts);
        Log.message(Struct.toString(paseq));
    }

    private void doGetPendingAdjustmentProducts(String command[])
            throws Throwable
    {
        String names[] = { "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        PendingNameStruct pnseq[] =
                productQueryV1.getPendingAdjustmentProducts(classKey);
        Log.message(Struct.toString(pnseq));
    }

    private void doGetStrategyByKey(String command[]) throws Throwable
    {
        String names[] = { "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[0]);

        StrategyStruct s = productQueryV1.getStrategyByKey(productKey);
        Log.message(Struct.toString(s));
    }

    private void doGetClassByKey(String command[]) throws Throwable
    {
        String names[] = { "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        ClassStruct c = productQueryV1.getClassByKey(classKey);
        Log.message(Struct.toString(c));
    }

    private void doGetProductByKey(String command[]) throws Throwable
    {
        String names[] = { "productKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing productKey");
            return;
        }
        int productKey = Integer.parseInt(values[0]);

        ProductStruct c = productQueryV1.getProductByKey(productKey);
        Log.message(Struct.toString(c));
    }

    private void doGetClassBySymbol(String command[]) throws Throwable
    {
        String names[] = { "productType", "className" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }
        
        if (values[0] == null)
        {
            Log.message("Missing productType");
            return;
        }
        short productType = Short.parseShort(values[0]);

        if (values[1] == null)
        {
            Log.message("Missing className");
            return;
        }
        String className = values[1];

        ClassStruct c = productQueryV1.getClassBySymbol(productType, className);
        Log.message(Struct.toString(c));
    }

    private void doGetStrategiesByComponent(String command[]) throws Throwable
    {
        String names[] = { "componentKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing componentKey");
            return;
        }
        int componentKey = Integer.parseInt(values[0]);

        StrategyStruct sseq[] =
                productQueryV1.getStrategiesByComponent(componentKey);
        Log.message(Struct.toString(sseq));
    }

    private void doGetStrategiesByClass(String command[]) throws Throwable
    {
        String names[] = { "classKey" };
        String values[] = engineAccess.getParameters(names, command,
                INDEX_FIRST_PARAMETER);
        if (values == null)
        {
            return; // error already reported, leave now.
        }

        if (values[0] == null)
        {
            Log.message("Missing classKey");
            return;
        }
        int classKey = Integer.parseInt(values[0]);

        StrategyStruct sseq[] = productQueryV1.getStrategiesByClass(classKey);
        Log.message(Struct.toString(sseq));
    }
}
