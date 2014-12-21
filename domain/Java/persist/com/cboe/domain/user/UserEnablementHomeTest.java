package com.cboe.domain.user;

import com.cboe.idl.user.*;
import com.cboe.infrastructureServices.foundationFramework.BObject;
import java.util.*;
import junit.framework.*;

public class UserEnablementHomeTest extends TestCase
{
	protected HashMap enablements = new HashMap();	 // userId -> MyUserEnablementElement[] map, used to emulate database queries
	protected ArrayList createdByHome = new ArrayList();	// MyUserEnablementElement's created my MyUserEnablementHome go here.

	public UserEnablementHomeTest(String methodName)
	{
		super(methodName);
	}

	/**
	 *  If there's no data in the home, make sure that an appropriate empty struct is returned.
	 */
	public void testEmptyToStruct() throws Exception
	{
		MyUserEnablementHome home = new MyUserEnablementHome();

		MyUserEnablementElement[] data =
			{
			};
		enablements.put("BOB", data);

		UserEnablementStruct actual = home.getUserEnablement("BOB");
		UserEnablementStruct expect = new UserEnablementStruct();
		expect.userId = "BOB";
		expect.sessionEnablements = new UserSessionEnablementStruct[0];

		assertStruct("basic to struct failed", expect, actual);
	}

	/**
	 *  make sure that a single element creates a properly-nested struct.
	 */
	public void testBasicToStruct() throws Exception
	{
		MyUserEnablementHome home = new MyUserEnablementHome();

		MyUserEnablementElement[] data =
			{
				create("BOB", "W_AM1", 1),
			};
		enablements.put("BOB", data);

		UserEnablementStruct actual = home.getUserEnablement("BOB");
		UserEnablementStruct expect = new UserEnablementStruct();
		expect.userId = "BOB";
		expect.sessionEnablements = new UserSessionEnablementStruct[1];
		expect.sessionEnablements[0] = new UserSessionEnablementStruct();
		expect.sessionEnablements[0].sessionName = "W_AM1";
		expect.sessionEnablements[0].productTypeEnablements = new short[] { 1 };

		assertStruct("basic to struct failed", expect, actual);
	}

	/**
	 * verify that a user with multiple sessions and types converts to struct ok.
	 */
	public void testComplexToStruct() throws Exception
	{
		MyUserEnablementHome home = new MyUserEnablementHome();

		MyUserEnablementElement[] data =	// use unsorted data to make sure the home's sort method works.
			{
				create("BOB", "W_AM1", 1),
				create("BOB", "W_MAIN", 4),
				create("BOB", "W_OTHER", 12),
				create("BOB", "W_MAIN", 1),
				create("BOB", "W_AM1", 2),
			};

		enablements.put("BOB", data);

		UserEnablementStruct actual = home.getUserEnablement("BOB");
		UserEnablementStruct expect = createComplexStruct();
		assertStruct("usorted complex to struct failed", expect, actual);
	}

	/**
	 *  The struct created by this method is used in several tests.  Take care if modifying this struct.
	 */
	protected UserEnablementStruct createComplexStruct()
	{
		UserEnablementStruct expect = new UserEnablementStruct();
		expect.userId = "BOB";
		expect.sessionEnablements = new UserSessionEnablementStruct[3];

		expect.sessionEnablements[0] = new UserSessionEnablementStruct();
		expect.sessionEnablements[0].sessionName = "W_AM1";
		expect.sessionEnablements[0].productTypeEnablements = new short[] { 1, 2};

		expect.sessionEnablements[1] = new UserSessionEnablementStruct();
		expect.sessionEnablements[1].sessionName = "W_MAIN";
		expect.sessionEnablements[1].productTypeEnablements = new short[] { 1, 4};

		expect.sessionEnablements[2] = new UserSessionEnablementStruct();
		expect.sessionEnablements[2].sessionName = "W_OTHER";
		expect.sessionEnablements[2].productTypeEnablements = new short[] { 12 };

		return expect;
	}

	/**
	 *  Make sure that an empty struct is accepted ok, and no elements are created.
	 */
	public void testFromEmptyStruct() throws Exception
	{
		MyUserEnablementHome home = new MyUserEnablementHome();
		enablements.put("BOB", new MyUserEnablementElement[0]); // no data 'already in database'
		UserEnablementStruct struct = new UserEnablementStruct("BOB", false, new UserSessionEnablementStruct[0]);

		// Execute test!
		//
		home.setUserEnablement(struct);

		MyUserEnablementElement[] result = new MyUserEnablementElement[ createdByHome.size() ];
		assertEquals("element count", 0, createdByHome.size()); // expect no row creation
	}

	/**
	 *  Make sure that an empty struct is accepted ok, no elements are created, and existing elements are removed.
	 */
	public void testFromEmptyStructUpdate() throws Exception
	{
		MyUserEnablementHome home = new MyUserEnablementHome();

		MyUserEnablementElement[] preData = // home 'already exists' this data, update with struct.
			{
				create("BOB", "W_AM1", 0), 
				create("BOB", "W_AM1", 1),
				create("BOB", "W_BLAH", 1),
				create("BOB", "W_MAIN", 4), 
				create("BOB", "W_XXX", 12),
			};
		enablements.put("BOB", preData); // populate data to emulate 'already in database'

		UserEnablementStruct struct = new UserEnablementStruct("BOB", false, new UserSessionEnablementStruct[0]);

		// Execute test!
		//
		home.setUserEnablement(struct);

		MyUserEnablementElement[] result = new MyUserEnablementElement[ createdByHome.size() ];
		assertEquals("element count", 0, createdByHome.size()); // expect no row creation
		for (int i=0; i < preData.length; i++)
		{
			assertTrue("expected deletion", preData[i].markedForDelete);
		}
	}

	/**
	 *  Make sure that a struct having multiple sessions and product types creates the expected elements.
	 */
	public void testFromStruct() throws Exception
	{
		MyUserEnablementHome home = new MyUserEnablementHome();

		MyUserEnablementElement[] expect = // sorted in ascending order: session, prodtype, classkey
			{
				create("BOB", "W_AM1", 1),
				create("BOB", "W_AM1", 2),
				create("BOB", "W_MAIN", 1),
				create("BOB", "W_MAIN", 4),
				create("BOB", "W_OTHER", 12),
			};
		enablements.put("BOB", new MyUserEnablementElement[0]); // no data 'already in database'

		UserEnablementStruct struct = createComplexStruct();

		// Execute test!
		//
		home.setUserEnablement(struct);

		MyUserEnablementElement[] result = new MyUserEnablementElement[ createdByHome.size() ];
		createdByHome.toArray(result);
		home.sortEnablements(result);

		assertEquals("element count", expect.length, result.length);
		for (int i=0; i < result.length; i++)
		{
			assertElement("element[" + i + "]", result[i], expect[i]);
		}
	}

	/**
	 *  Make sure that a struct having multiple sessions and product types creates the expected elements.
	 *  Further, make sure that existing elements are re-used (if appropriate), or deleted.
	 */
	public void testFromStructUpdate() throws Exception
	{
		MyUserEnablementHome home = new MyUserEnablementHome();

		MyUserEnablementElement[] expectCreated = // sorted in ascending order: session, prodtype, classkey
			{
				create("BOB", "W_AM1", 2),
				create("BOB", "W_MAIN", 1),
				create("BOB", "W_OTHER", 12),
			};

		MyUserEnablementElement[] preData = // home 'already exists' this data, update with struct.
			{
				create("BOB", "W_AM1", 0),  // delete
				create("BOB", "W_AM1", 1),
				create("BOB", "W_BLAH", 1), // delete
				create("BOB", "W_MAIN", 4), 
				create("BOB", "W_XXX", 12), // delete
			};
		boolean[] expectPreDataDeletion =
			{
				true,
				false,
				true,
				false,
				true,
			};

		enablements.put("BOB", preData); // populate data to emulate 'already in database'

		UserEnablementStruct struct = createComplexStruct();

		// Execute test!
		//
		home.setUserEnablement(struct);

		MyUserEnablementElement[] result = new MyUserEnablementElement[ createdByHome.size() ];
		createdByHome.toArray(result);
		home.sortEnablements(result);

		assertEquals("element count", expectCreated.length, result.length);
		for (int i=0; i < result.length; i++)
		{
			assertElement("element[" + i + "]", result[i], expectCreated[i]);
		}

		for (int i=0; i < preData.length; i++)
		{
			assertEquals("expected deletion state for preData["+i+"]",
				new Boolean(expectPreDataDeletion[i]), new Boolean(preData[i].markedForDelete));
		}
	}

	/**
	 * If there is no data, return an empty array.
	 */
	public void testAllEnablementsEmpty() throws Exception
	{
		enablements.clear();
		MyUserEnablementHome home = new MyUserEnablementHome();
		UserEnablementStruct[] result = home.getAllUserEnablements();

		assertEquals("result array length", result.length, 0);
	}

	/**
	 * Make sure that a good struct array is built from a populated database.
	 */
	public void testAllEnablements() throws Exception
	{
		enablements.clear();
		enablements.put("ALC", new MyUserEnablementElement[] 
			{
				create("ALC", "W_AM1", 7), 
			});
		enablements.put("BOB", new MyUserEnablementElement[] 
			{
				create("BOB", "W_AM1", 2),
				create("BOB", "W_AM1", 7),
				create("BOB", "W_MAIN", 4), 
			});
		enablements.put("CDY", new MyUserEnablementElement[] 
			{
				create("CDY", "W_AM1", 3),
				create("CDY", "W_AM1", 4),
				create("CDY", "W_MAIN", 4), 
				create("CDY", "W_PM1", 1),
			});
		enablements.put("DAV", new MyUserEnablementElement[] 
			{
				create("DAV", "W_MAIN", 7),
				create("DAV", "W_MAIN", 12),
			});

		UserEnablementStruct[] structs = new UserEnablementStruct[4];

		structs[0] = new UserEnablementStruct();
		structs[0].userId = "ALC";
		structs[0].sessionEnablements = new UserSessionEnablementStruct[1];
		structs[0].sessionEnablements[0] = new UserSessionEnablementStruct("W_AM1", new short[] { 7 });
		structs[1] = new UserEnablementStruct();
		structs[1].userId = "BOB";
		structs[1].sessionEnablements = new UserSessionEnablementStruct[2];
		structs[1].sessionEnablements[0] = new UserSessionEnablementStruct("W_AM1",  new short[] { 2, 7 });
		structs[1].sessionEnablements[1] = new UserSessionEnablementStruct("W_MAIN", new short[] { 4 });
		structs[2] = new UserEnablementStruct();
		structs[2].userId = "CDY";
		structs[2].sessionEnablements = new UserSessionEnablementStruct[3];
		structs[2].sessionEnablements[0] = new UserSessionEnablementStruct("W_AM1",  new short[] { 3, 4 });
		structs[2].sessionEnablements[1] = new UserSessionEnablementStruct("W_MAIN", new short[] { 4 });
		structs[2].sessionEnablements[2] = new UserSessionEnablementStruct("W_PM1",  new short[] { 1 });
		structs[3] = new UserEnablementStruct();
		structs[3].userId = "DAV";
		structs[3].sessionEnablements = new UserSessionEnablementStruct[1];
		structs[3].sessionEnablements[0] = new UserSessionEnablementStruct("W_MAIN", new short[] { 7, 12 });


		MyUserEnablementHome home = new MyUserEnablementHome();
		UserEnablementStruct[] result = home.getAllUserEnablements();

		assertEquals("result array length", result.length, structs.length);
		for ( int i=0; i < result.length; i++)
		{
			assertStruct("result[" + i + "]", result[i], structs[i]);
		}
	}

	/**
	 * assert that the enablement elements are equal
	 */
	protected void assertElement(String msg, MyUserEnablementElement actual, MyUserEnablementElement expected)
	{
		assertEquals(msg + " userid", actual.getUserId(), expected.getUserId());
		assertEquals(msg + " sessionName", actual.getSessionName(), expected.getSessionName());
		assertEquals(msg + " productType", actual.getProductType(), expected.getProductType());
	}

	/**
	 * assert that the enablement structs are equal
	 */
	protected void assertStruct(String msg, UserEnablementStruct exp, UserEnablementStruct act)
	{
		assertEquals(msg + ": userids", exp.userId, act.userId);
		assertEquals(msg + ": session enablement length", exp.sessionEnablements.length, act.sessionEnablements.length);
		for (int i=0; i < act.sessionEnablements.length; i++)
		{
			String session = act.sessionEnablements[i].sessionName;
			assertEquals(msg + ": session name", exp.sessionEnablements[i].sessionName, session);

			short[] actProd = act.sessionEnablements[i].productTypeEnablements;
			short[] expProd = exp.sessionEnablements[i].productTypeEnablements;

			assertEquals(msg + ": Session " + session + " prod type length", actProd.length, expProd.length);
			for (int j=0; j < actProd.length; j++)
			{
				assertEquals(msg + ": Session " + session + " prod type", expProd[j], actProd[j]);
			}
		}
	}

	protected MyUserEnablementElement create(String userId, String sessionName, int prodType)
	{
		MyUserEnablementElement result = new MyUserEnablementElement();
		result.setUserId(userId);
		result.setSessionName(sessionName);
		result.setProductType((short)prodType);
		return result;
	}

	/**
	 *  non-persistent element.
	 */
	protected class MyUserEnablementElement extends UserEnablementElementImpl
	{
		String userId;
		String sessionName;
		short productType;

		boolean markedForDelete;
		
		public String getUserId() { return userId; }
		public String getSessionName() { return sessionName; }
		public short getProductType() { return productType; }
		public void setUserId(String val) { userId = val; }
		public void setSessionName(String val) { sessionName = val; }
		public void setProductType(short val) { productType = val; }

		public void markForDelete() { markedForDelete = true; }
	}

	protected class MyUserEnablementHome extends UserEnablementHomeImpl
	{
		protected UserEnablementElementImpl[] findElementsForUser(String userId)
		{
			 UserEnablementElementImpl[] result = (UserEnablementElementImpl[])enablements.get(userId);
			 sortEnablements(result);
			 return result;
		}

		protected UserEnablementElementImpl[] findAllElements()
		{
			ArrayList all = new ArrayList();
			Iterator iter = enablements.values().iterator();
			while (iter.hasNext())
			{
				UserEnablementElementImpl[] userElements = (UserEnablementElementImpl[])iter.next();
				for (int i=0; i < userElements.length; i++)
				{
					all.add(userElements[i]);
				}
			}
			UserEnablementElementImpl[] allArray = new UserEnablementElementImpl[all.size()];
			all.toArray(allArray);
			sortEnablements(allArray);
			return allArray;
		}

		protected UserEnablementElementImpl createElement(String userId, String sessionName, short prodType)
		{
			UserEnablementElementImpl element = create(userId, sessionName, prodType);
			createdByHome.add(element);
			return element;
		}

		public void addToContainer(BObject obj) {}
	}

	public static void main(String args[])
	{
		System.out.println("Unit test UserEnablementHomeTest");
		TestSuite suite = new TestSuite();
		suite.addTest(new UserEnablementHomeTest("testEmptyToStruct"));
		suite.addTest(new UserEnablementHomeTest("testBasicToStruct"));
		suite.addTest(new UserEnablementHomeTest("testComplexToStruct"));
		suite.addTest(new UserEnablementHomeTest("testFromEmptyStruct"));
		suite.addTest(new UserEnablementHomeTest("testFromEmptyStructUpdate"));
		suite.addTest(new UserEnablementHomeTest("testFromStruct"));
		suite.addTest(new UserEnablementHomeTest("testFromStructUpdate"));
		suite.addTest(new UserEnablementHomeTest("testAllEnablements"));
		suite.addTest(new UserEnablementHomeTest("testAllEnablementsEmpty"));
		junit.textui.TestRunner.run(suite);
	}
}
