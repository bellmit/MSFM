/*
 * Created on Oct 2, 2008
 */
package com.cboe.externalIntegrationServices.msgCodec;

public abstract class AbstractFieldCodec implements ICodec {
    private final String fieldName;
    private final String fieldType;

    public AbstractFieldCodec(String p_name, String p_typeName) {
        fieldName = p_name;
        fieldType = p_typeName;
    }

    public final String getFieldType() {
        return fieldType;
    }

    public final String getFieldName() {
        return fieldName;
    }

    public String toString() {
        StringBuilder out = new StringBuilder();
        toString(out, 0);
        return out.toString();
    }
    
    public void toString(StringBuilder out, int printIndent) {
        for (int s = 0; s < printIndent; s++)
            out.append(" ");
        out.append("\"");
        out.append(getFieldName());
        out.append("\" ");
        out.append(getFieldType());
        out.append(" ");
    }

}
