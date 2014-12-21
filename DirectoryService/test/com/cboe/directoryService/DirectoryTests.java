package com.cboe.directoryService;

import junit.framework.Test;
import junit.framework.TestCase;
import junit.framework.TestSuite;

/**
 * Some tests on the directory service.  
 */
public class DirectoryTests extends TestCase {
    public static Test suite() {
        return new TestSuite(DirectoryTests.class);
    }

    public DirectoryTests(String name) {
        super(name);
    }

    /**
     * The log formatter shouldn't throw exceptions or break under normal conditions. This test just
     * makes sure common calls will not throw exceptions thus breaking the server.
     */
    public void testNormalFormatting() {
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main"));
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value"));
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value %s", "str"));
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value %d", 123));
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value %f", 123.0f));
    }

    /**
     * test to make sure the enter exist TraderLogBuilder.formatting doesn't generate exceptions
     */
    public void testEnterExitFormating() {
        System.out.println(TraderLogBuilder.formatEnter(TraderLogBuilder.class.getSimpleName(), "main"));
        System.out.println(TraderLogBuilder.formatExit(TraderLogBuilder.class.getSimpleName(), "main"));
        System.out.println(TraderLogBuilder.formatEnter(TraderLogBuilder.class.getSimpleName(), "main", "Value"));
        System.out.println(TraderLogBuilder.formatExit(TraderLogBuilder.class.getSimpleName(), "main", "Value"));
        System.out.println(TraderLogBuilder.formatEnter(TraderLogBuilder.class.getSimpleName(), "main", "Value %s", "str"));
        System.out.println(TraderLogBuilder.formatExit(TraderLogBuilder.class.getSimpleName(), "main", "Value %s", "str"));
        System.out.println(TraderLogBuilder.formatEnter(TraderLogBuilder.class.getSimpleName(), "main", "Value %d", 123));
        System.out.println(TraderLogBuilder.formatExit(TraderLogBuilder.class.getSimpleName(), "main", "Value %d", 123));
        System.out.println(TraderLogBuilder.formatEnter(TraderLogBuilder.class.getSimpleName(), "main", "Value %f", 123.0f));
        System.out.println(TraderLogBuilder.formatExit(TraderLogBuilder.class.getSimpleName(), "main", "Value %f", 123.0f));
        System.out.println(TraderLogBuilder.formatEnter(TraderLogBuilder.class.getSimpleName(), "main", "Value %f", 123.0f));
        System.out.println(TraderLogBuilder.formatExit(TraderLogBuilder.class.getSimpleName(), "main", "Value %f", 123.0f));

    }

    /**
     * Tests weird cases where incorrect values are passed. No exceptions should be generated
     */
    public void testOutlierFormatting() {
        // incorrect param, string will handle
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value %s", 4));
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value %s", 4.0f));

        // no sub is normal for the following
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value %s"));
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value %d"));
        System.out.println(TraderLogBuilder.format(TraderLogBuilder.class.getSimpleName(), "main", "Value %f"));
    }

}
