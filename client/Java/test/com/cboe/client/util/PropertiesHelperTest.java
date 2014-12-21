package com.cboe.client.util;

import java.util.Properties;
import junit.framework.JUnit4TestAdapter;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import org.junit.BeforeClass;   // annotation
import org.junit.Test;      // annotation

public class PropertiesHelperTest
{
    // Allow this JUnit 4 test to run under JUnit3
    public static junit.framework.Test suite()
    {
        return new JUnit4TestAdapter(PropertiesHelperTest.class);
    }

    // Class Under Test is confusing.
    // getInstance() gets a singleton that is created with no member properties,
    //   prefix or keyBuilder. We can set prefix and keyBuilder later.
    //
    static Properties props;

    @BeforeClass public static void setupProperties()
    {
        props = new Properties();
        props.put("ambrosia", "chocolate");

        props.put("one.egg", "yolk");
        props.put("one.fork", "spear");
        props.put("one.defaultsPrefix", "two");
        props.put("two.twist", "shout");
        props.put("two.egg", "omelet");
        props.put("defaults.one.defaultsPrefix", "three");
        props.put("three.fly", "buzz");
        props.put("defaults.two.defaultsPrefix", "apple");
        props.put("apple.egg", "seed");
        props.put("one.number", "42");
        props.put("two.number", "64");
        props.put("three.da", "true");
        props.put("four.da", "true");
        props.put("four.ne", "false");

        props.put("green.acres", "farm");
    }

    // PropertiesHelper.instance() returns a PropertiesHelper object that
    // has no storedProperties, and may or may not have a storedPrefix and
    // a keyBuilder.

    @Test public void testGetPropertyPropertiesKey()
    {
        // Try all varieties of keyBuilder
        PropertiesHelper ph = PropertiesHelper.instance();
        ph.setKeyBuilder(PropertiesHelper.STRIP_NONE);
        assertNull(ph.getProperty(props, null));
        assertNull(ph.getProperty(props, "cod liver oil"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertNull(ph.getProperty(props, "milk.ambrosia"));
        assertNull(ph.getProperty(props, "low.fat.ambrosia"));
        assertNull(ph.getProperty(props, "high.quality.dark.ambrosia"));
        assertNull(ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        assertNull(ph.getProperty(props, "ambrosia.a.b.c.d.e"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_ONE_FRONT);
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "milk.ambrosia"));
        /* todo: STRIP_ONE_FRONT seems to strip up to 2 names
        assertNull(ph.getProperty(props, "low.fat.ambrosia"));
        todo */
        assertNull(ph.getProperty(props, "high.quality.dark.ambrosia"));
        assertNull(ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        assertNull(ph.getProperty(props, "ambrosia.a.b.c.d.e"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_TWO_FRONT);
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "milk.ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "low.fat.ambrosia"));
        /* todo: STRIP_TWO_FRONT seems to strip up to 3 names
        assertNull(ph.getProperty(props, "high.quality.dark.ambrosia"));
        todo */
        assertNull(ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        assertNull(ph.getProperty(props, "ambrosia.a.b.c.d.e"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_THREE_FRONT);
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "milk.ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "low.fat.ambrosia"));
        assertEquals("chocolate",
                ph.getProperty(props, "high.quality.dark.ambrosia"));
        /* todo: STRIP_THREE_FRONT seems to strip up to 4 names
        assertNull(ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        todo */
        assertNull(ph.getProperty(props, "ambrosia.a.b.c.d.e"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_ALL_FRONT);
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "milk.ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "low.fat.ambrosia"));
        assertEquals("chocolate",
                ph.getProperty(props, "high.quality.dark.ambrosia"));
        assertEquals("chocolate",
                ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        assertNull(ph.getProperty(props, "ambrosia.a.b.c.d.e"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_ONE_BACK);
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.bean"));
        /* todo: STRIP_ONE_BACK seems to strip up to 2 names
        assertNull(ph.getProperty(props, "ambrosia.in.foil"));
        todo */
        assertNull(ph.getProperty(props, "ambrosia.in.a.glass"));
        assertNull(ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        assertNull(ph.getProperty(props, "ambrosia.a.b.c.d.e"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_TWO_BACK);
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.bean"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.in.foil"));
        /* todo: STRIP_TWO_BACK seems to strip up to 3 names
        assertNull(ph.getProperty(props, "ambrosia.in.a.glass"));
        todo */
        assertNull(ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        assertNull(ph.getProperty(props, "ambrosia.a.b.c.d.e"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_THREE_BACK);
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.bean"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.in.foil"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.in.a.glass"));
        /* todo: STRIP_THREE_BACK seems to strip up to 4 names
        assertNull(ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        todo */
        assertNull(ph.getProperty(props, "ambrosia.a.b.c.d.e"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_ALL_BACK);
        assertEquals("chocolate", ph.getProperty(props, "ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.bean"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.in.foil"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.in.a.glass"));
        assertNull(ph.getProperty(props, "a.b.c.d.e.f.g.ambrosia"));
        assertEquals("chocolate", ph.getProperty(props, "ambrosia.a.b.c.d.e"));
    }

    @Test public void testGetPropertyPropertiesPrefixKeyDefault()
    {
        PropertiesHelper ph = PropertiesHelper.instance();
        ph.setKeyBuilder(PropertiesHelper.STRIP_NONE);

        assertEquals("10", ph.getProperty(null, null, null, "10"));
        assertEquals("yes", ph.getProperty(props, null, "forage", "yes"));
        assertEquals("chocolate", ph.getProperty(props,null, "ambrosia", "no"));

        // Exercise alternative paths in internal_getProperty
        assertEquals("yolk", ph.getProperty(props, "one", "egg", "raw"));
        assertEquals("raw", ph.getProperty(props, "zero", "egg", "raw"));
        assertEquals("shout", ph.getProperty(props, "one", "twist", "cry"));
        assertEquals("buzz", ph.getProperty(props, "one", "fly", "shoo"));
        assertEquals("shoo", ph.getProperty(props, "two", "fly", "shoo"));

        // Exercise all varieties of keyBuilder
        assertEquals("grass", ph.getProperty(props, "green", null, "grass"));
        assertEquals("grass",
                ph.getProperty(props, "green", "cod liver oil", "grass"));
        assertEquals("grass",
                ph.getProperty(props, null, "cod liver oil", "grass"));
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "soylent.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "on.the.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "listen.to.the.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_ONE_FRONT);
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "soylent.green", "acres", "grass"));
        /* todo: STRIP_ONE_FRONT seems to strip up to 2 names
        assertEquals("grass",
                ph.getProperty(props, "on.the.green", "acres", "grass"));
        todo */
        assertEquals("grass",
                ph.getProperty(props, "listen.to.the.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_TWO_FRONT);
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "soylent.green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "on.the.green", "acres", "grass"));
        /* todo: STRIP_TWO_FRONT seems to strip up to 3 names
        assertEquals("grass",
                ph.getProperty(props, "listen.to.the.green", "acres", "grass"));
        todo */
        assertEquals("grass",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_THREE_FRONT);
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "soylent.green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "on.the.green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "listen.to.the.green", "acres", "grass"));
        /* todo: STRIP_THREE_FRONT seems to strip up to 4 names
        assertEquals("grass",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        todo */
        assertEquals("grass",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_ALL_FRONT);
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "soylent.green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "on.the.green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "listen.to.the.green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_ONE_BACK);
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.linnet", "acres", "grass"));
        /* todo: STRIP_ONE_BACK seems to strip up to 2 names
        assertEquals("grass",
                ph.getProperty(props, "green.is.for", "acres", "grass"));
        todo */
        assertEquals("grass",
                ph.getProperty(props, "green.eggs.and.ham", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_TWO_BACK);
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.linnet", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.is.for", "acres", "grass"));
        /* todo: STRIP_TWO_BACK seems to strip up to 3 names
        assertEquals("grass",
                ph.getProperty(props, "green.eggs.and.ham", "acres", "grass"));
        todo */
        assertEquals("grass",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_THREE_BACK);
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.linnet", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.is.for", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.eggs.and.ham", "acres", "grass"));
        /* todo: STRIP_THREE_BACK seems to strip up to 4 names
        assertEquals("grass",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        todo */
        assertEquals("grass",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));

        ph.setKeyBuilder(PropertiesHelper.STRIP_ALL_BACK);
        assertEquals("farm", ph.getProperty(props, "green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.linnet", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.is.for", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.eggs.and.ham", "acres", "grass"));
        assertEquals("grass",
                ph.getProperty(props, "a.b.c.d.e.f.g.green", "acres", "grass"));
        assertEquals("farm",
                ph.getProperty(props, "green.a.b.c.d.e", "acres", "grass"));
    }

    @Test public void testGetSet()
    {
        PropertiesHelper ph = new PropertiesHelper();
        assertNull(ph.getProperties());
        assertNull(ph.getPrefix());
        ph.setPrefix("pre.fix");
        assertEquals("pre.fix", ph.getPrefix());
        // setKeyBuilder is tested elsewhere
    }

    @Test public void testConstructor()
    {
        PropertiesHelper ph = new PropertiesHelper(props);
        assertEquals(props, ph.getProperties());
        assertNull(ph.getPrefix());

        ph = new PropertiesHelper(props, "before");
        assertEquals(props, ph.getProperties());
        assertEquals("before", ph.getPrefix());

        ph = new PropertiesHelper(PropertiesHelper.STRIP_ALL_FRONT);
        assertEquals("shout", ph.getProperty(props,"a.one.and.a.two","twist"));

        ph = new PropertiesHelper(props, "one.for.the.money",
                PropertiesHelper.STRIP_ALL_BACK);
        assertEquals("spear", ph.getProperty("fork"));
    }

    @Test public void testGetProperty()
    {
        PropertiesHelper ph = new PropertiesHelper(props, "one",
                PropertiesHelper.STRIP_NONE);
        assertEquals("yolk", ph.getProperty(props, "one", "egg"));
        assertEquals("spear", ph.getProperty("fork"));
        assertEquals("mess", ph.getProperty("missing", "mess"));
    }

    @Test public void testGetPropertyInt()
    {
        PropertiesHelper ph = new PropertiesHelper(props, "one",
                PropertiesHelper.STRIP_NONE);
        assertEquals(42, ph.getPropertyInt("number"));
        assertEquals(17, ph.getPropertyInt("numero", "17"));
        assertEquals(64, ph.getPropertyInt(props, "two", "number"));
        assertEquals(2, ph.getPropertyInt(props, "three", "number", "2"));
    }

    @Test public void testGetPropertyBoolean()
    {
        PropertiesHelper ph = new PropertiesHelper(props, "four",
                PropertiesHelper.STRIP_NONE);

        assertFalse(ph.getPropertyBoolean("missing"));
        assertTrue(ph.getPropertyBoolean("da"));
        assertTrue(ph.getPropertyBoolean("pravda", "true"));
        assertTrue(ph.getPropertyBoolean(props, "three", "da"));
        assertTrue(ph.getPropertyBoolean(props, "three", "ne", "true"));
    }

    @Test public void testGetPrefixedProperty()
    {
        PropertiesHelper ph = new PropertiesHelper(props, "four",
                PropertiesHelper.STRIP_NONE);

        assertEquals("yolk", ph.getPrefixedProperty("one", "egg"));
        assertEquals(64, ph.getPrefixedPropertyInt("two", "number"));
    }
}
