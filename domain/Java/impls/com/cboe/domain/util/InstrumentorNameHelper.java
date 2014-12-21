package com.cboe.domain.util;

import com.cboe.instrumentationService.instrumentors.Instrumentor;

import java.util.StringTokenizer;
import java.util.Vector;
import java.util.ArrayList;

public class InstrumentorNameHelper
{
    public static String createInstrumentorName(String[] components, Object object)
    {
        StringBuilder name = new StringBuilder(100);
        for(int i=0; i<components.length-1; i++)
        {
            name.append(components[i]);
            name.append(Instrumentor.NAME_DELIMITER);
        }
        name.append(components[components.length-1])
        .append("@")
        .append(object.hashCode());
        return name.toString();
    }

    public static String[] getInstrumentorComponents(String instrumentorName)
    {
        ArrayList components = new ArrayList(5);
        StringTokenizer stringTokenizer  =
                    new StringTokenizer(instrumentorName, Instrumentor.NAME_DELIMITER, false);
        while(stringTokenizer.hasMoreTokens())
        {
            String nextToken = stringTokenizer.nextToken();
            components.add(nextToken);
        }
        String[] nameComponents = (String[])components.toArray(new String[0]);
        return nameComponents;
    }
}
