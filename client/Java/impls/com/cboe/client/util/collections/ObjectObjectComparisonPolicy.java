package com.cboe.client.util.collections;

/**
 * ObjectObjectComparisonPolicy.java
 *
 * @author Dmitry Volpyansky
 *
 */

public class ObjectObjectComparisonPolicy implements ObjectObjectComparisonPolicyIF
{
    public static final ObjectObjectComparisonPolicyIF AcceptSameObjectComparisonPolicy = new ObjectObjectComparisonPolicy()
    {
        public int compare(Object valueA, Object valueB)
        {
            if (valueA == valueB)
            {
                return ObjectObjectComparisonPolicyIF.ACCEPT;
            }

            return ObjectObjectComparisonPolicyIF.CONTINUE;
        }
    };

    public static final ObjectObjectComparisonPolicyIF AcceptEqualsObjectComparisonPolicy = new ObjectObjectComparisonPolicy()
    {
        public int compare(Object valueA, Object valueB)
        {
            if (valueA == valueB || (valueA != null && valueA.equals(valueB)))
            {
                return ObjectObjectComparisonPolicyIF.ACCEPT;
            }

            return ObjectObjectComparisonPolicyIF.CONTINUE;
        }
    };

    public static final ObjectObjectComparisonPolicyIF RejectSameObjectComparisonPolicy = new ObjectObjectComparisonPolicy()
    {
        public int compare(Object valueA, Object valueB)
        {
            if (valueA == valueB)
            {
                return ObjectObjectComparisonPolicyIF.REJECT;
            }

            return ObjectObjectComparisonPolicyIF.CONTINUE;
        }
    };

    public static final ObjectObjectComparisonPolicyIF RejectEqualsObjectComparisonPolicy = new ObjectObjectComparisonPolicy()
    {
        public int compare(Object valueA, Object valueB)
        {
            if (valueA == valueB || (valueA != null && valueA.equals(valueB)))
            {
                return ObjectObjectComparisonPolicyIF.REJECT;
            }

            return ObjectObjectComparisonPolicyIF.CONTINUE;
        }
    };

    public int compare(Object valueA, Object valueB)
    {
        return ObjectObjectComparisonPolicyIF.CONTINUE;
    }
}

