package com.cboe.test.util;

import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.Modifier;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.BlockJUnit4ClassRunner;
import org.junit.runners.Suite;
import org.junit.runners.model.FrameworkMethod;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.Statement;
import org.junit.runners.model.TestClass;

/**
 * Using <code>Battery</code> as a runner allows you to apply a single
 * implementation across a set of tests using dependency injection.
 * 
 * To use this annotate a class with <code>@RunWith(Battery.class)</code> and
 * <code>@Battery.Tests({Test1.class, ...})</code>. When you run this class it
 * will build a new implementation defined by a method annotated with
 * <code>@Battery.Factory</code>. This factory we be called for each test
 * defined in the <code>@Battery.Tests</code> set.
 */
public class Battery extends Suite {

	// TODO: tests should only allow one factory method
	// TODO: how do we address api's with more than one factory

	/**
	 * Defines a series of test implementations that will be executed when run
	 * with <code>@RunWith(Battery.class)</code>
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public @interface Tests {
		/**
		 * @return the classes to be run
		 */
		public Class<?>[] value();
	}

	/**
	 * Defines a factory method that will produce the value injected into each
	 * test. This will be recreated for each test defined by {@link Tests}
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.METHOD)
	@Inherited
	public @interface Factory {
	}

	/** Contains each inner test class */
	private final List<Runner> fRunners = new ArrayList<Runner>();

	/**
	 * Called reflectively on classes annotated with
	 * <code>@RunWith(Battery.class)</code>
	 * 
	 * @param klass
	 *            the root class
	 * @param builder
	 *            builds runners for classes in the battery
	 * @throws InitializationError
	 */
	public Battery(Class<?> klass) throws InitializationError {
		super(klass, Collections.<Runner> emptyList());

		/** these are the test items */
		Class<?>[] types = getAnnotatedClasses(klass);

		try {

			Object battery = getTestClass().getOnlyConstructor().newInstance();

			/*
			 * Run over all of the tests injecting objects built by our method
			 */
			for (int i = 0; i < types.length; i++) {
				Object testObject = getParametersMethod(getTestClass())
						.invokeExplosively(battery);
				fRunners.add(new TestClassRunnerForBattery(types[i], testObject));
			}

		} catch (Throwable e) {
			throw new InitializationError("Problem running tests:" + e);
		}

	}

	/**
	 * Find our factory method
	 */
	private FrameworkMethod getParametersMethod(TestClass testClass)
			throws Exception {
		List<FrameworkMethod> methods = testClass
				.getAnnotatedMethods(Factory.class);
		for (FrameworkMethod each : methods) {
			int modifiers = each.getMethod().getModifiers();
			if (Modifier.isPublic(modifiers))
				return each;
		}

		throw new Exception("No public static parameters method on class "
				+ testClass.getName());
	}

	@Override
	protected List<Runner> getChildren() {
		return fRunners;
	}

	@Override
	protected Description describeChild(Runner child) {
		return child.getDescription();
	}

	@Override
	protected void runChild(Runner runner, final RunNotifier notifier) {
		runner.run(notifier);
	}

	/**
	 * Execute the provided testObject against the provided test class
	 */
	private class TestClassRunnerForBattery extends BlockJUnit4ClassRunner {

		private final Object testObject;

		TestClassRunnerForBattery(Class<?> type, Object testObject)
				throws InitializationError {
			super(type);
			this.testObject = testObject;

		}

		@Override
		public Object createTest() throws Exception {
			return getTestClass().getOnlyConstructor().newInstance(testObject);
		}

		@Override
		protected String getName() {
			return String.format("%s", getTestClass().getName());
		}

		@Override
		protected String testName(final FrameworkMethod method) {
			return String.format("%s", method.getName());
		}

		@Override
		protected void validateConstructor(List<Throwable> errors) {
			validateOnlyOneConstructor(errors);
		}

		@Override
		protected Statement classBlock(RunNotifier notifier) {
			return childrenInvoker(notifier);
		}
	}

	/**
	 * Grab the annotation for the Battery suite
	 */
	private static Class<?>[] getAnnotatedClasses(Class<?> klass)
			throws InitializationError {
		Tests annotation = klass.getAnnotation(Tests.class);
		if (annotation == null)
			throw new InitializationError(String.format(
					"class '%s' must have a BatteryClasses annotation",
					klass.getName()));
		return annotation.value();
	}

}

