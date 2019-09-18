/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.systemtest;

import java.lang.annotation.Annotation;
import java.lang.annotation.ElementType;
import java.lang.annotation.Inherited;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import org.junit.runner.RunWith;
import org.junit.runner.Runner;
import org.junit.runners.Suite;
import org.junit.runners.model.InitializationError;
import org.junit.runners.model.RunnerScheduler;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.facet.runtime.rpi.ConfiguredRcc;
import org.roboticsapi.feature.runtime.javarcc.JavaRcc;
import org.roboticsapi.feature.runtime.realtimercc.RealtimeRcc;
import org.roboticsapi.systemtest.RoboticsTestRunner.TestRuntimeBuilder;

public class RoboticsTestSuite extends Suite {

	private static final int MAX_PARALLEL_TEST_RUNS = 5;

	private final List<Runner> runners = new ArrayList<>();
	private final String name;

	/**
	 * The <code>DeviceInterfaceTests</code> annotation specifies the classes to be
	 * run when a class annotated with
	 * <code>@RunWith(RoboticsTestSuite.class)</code> is run.
	 */
	@Retention(RetentionPolicy.RUNTIME)
	@Target(ElementType.TYPE)
	@Inherited
	public @interface DeviceInterfaceTests {
		/**
		 * @return the classes to be run
		 */
		public Class<? extends DeviceInterfaceTest<?>>[] value();
	}

	/**
	 * <p>
	 * Called reflectively on classes annotated with
	 * <code>@RunWith(RoboticsTestSuite.class)</code>
	 * </p>
	 *
	 * <p>
	 * See {@link RunWith} and {@link RoboticsTestSuite}
	 * </p>
	 *
	 * @param testClass
	 *            the root class
	 */
	public RoboticsTestSuite(Class<?> testClass) throws InitializationError {
		this(testClass, readParameterizationFromAnnotations(testClass), getRuntimeBuilderForAnnotation(testClass));
	}

	private static Map<Class<? extends Device>, Map<Class<? extends DeviceDriver>, Class<? extends DeviceInterfaceTest<?>>[]>> readParameterizationFromAnnotations(
			Class<?> testClass) throws InitializationError {
		Class<? extends DeviceInterfaceTest<?>>[] interfaceTestClasses = readInterfaceTestClasses(testClass);
		Map<Class<? extends Device>, Class<? extends DeviceDriver>[]> deviceDriverClasses = readDeviceDriverClasses(
				testClass);

		Map<Class<? extends Device>, Map<Class<? extends DeviceDriver>, Class<? extends DeviceInterfaceTest<?>>[]>> result = new LinkedHashMap<>();
		for (Entry<Class<? extends Device>, Class<? extends DeviceDriver>[]> entry : deviceDriverClasses.entrySet()) {
			Map<Class<? extends DeviceDriver>, Class<? extends DeviceInterfaceTest<?>>[]> map = new LinkedHashMap<>();
			result.put(entry.getKey(), map);
			for (Class<? extends DeviceDriver> driverClass : entry.getValue()) {
				map.put(driverClass, interfaceTestClasses);
			}
		}
		return result;
	}

	/**
	 * <p>
	 * Reads all devices and their related device drivers specified by
	 * <code>@WithDevice</code> annotations
	 * </p>
	 *
	 * <p>
	 * See {@link WithDevice}
	 * </p>
	 *
	 * @param testClass
	 * @return
	 * @throws InitializationError
	 */
	@SuppressWarnings("unchecked")
	private static Map<Class<? extends Device>, Class<? extends DeviceDriver>[]> readDeviceDriverClasses(
			Class<?> testClass) throws InitializationError {
		Map<Class<? extends Device>, Class<? extends DeviceDriver>[]> result = new LinkedHashMap<>();
		if (testClass.isAnnotationPresent(WithDevices.class)) {
			for (WithDevice annotation : getAnnotation(WithDevices.class, testClass).value()) {
				if (result.containsKey(annotation.device())) {
					List<Class<? extends DeviceDriver>> list = new ArrayList<>(
							Arrays.asList(result.get(annotation.device())));
					for (Class<? extends DeviceDriver> driverClass : annotation.deviceDrivers()) {
						if (!list.contains(driverClass)) {
							list.add(driverClass);
						}
					}
					result.put(annotation.device(), list.toArray(new Class[list.size()]));
				} else {
					result.put(annotation.device(), annotation.deviceDrivers());
				}
			}
		} else {
			WithDevice annotation = getAnnotation(WithDevice.class, testClass);
			result.put(annotation.device(), annotation.deviceDrivers());
		}
		return result;
	}

	/**
	 * <p>
	 * Reads all device interface test classes specified by
	 * <code>@DeviceInterfaceTests</code>
	 * </p>
	 *
	 * <p>
	 * See {@link DeviceInterfaceTests}
	 * </p>
	 *
	 * @param testClass
	 * @return
	 * @throws InitializationError
	 */
	private static Class<? extends DeviceInterfaceTest<?>>[] readInterfaceTestClasses(Class<?> testClass)
			throws InitializationError {
		return getAnnotation(DeviceInterfaceTests.class, testClass).value();
	}

	private static <T extends Annotation> T getAnnotation(Class<T> annotation, Class<?> testClass)
			throws InitializationError {
		T result = testClass.getAnnotation(annotation);
		if (result == null) {
			throw new InitializationError(String.format(
					"class '%s' must have a " + annotation.getSimpleName() + " annotation", testClass.getName()));
		}
		return result;
	}

	private static TestRuntimeBuilder getRuntimeBuilderForAnnotation(Class<?> testClass) throws InitializationError {
		WithRcc annotation = getAnnotation(WithRcc.class, testClass);
		switch (annotation.value()) {
		case DedicatedJavaRcc:
			return new TestRuntimeBuilder() {
				@Override
				public ConfiguredRcc build(RoboticsContext context) {
					JavaRcc javaRCC = new JavaRcc();
					javaRCC.setName("Robotics API system test javaRCC");
					return javaRCC;
				}

				@Override
				public int getMaxParallelInstances() {
					return MAX_PARALLEL_TEST_RUNS;
				}
			};
		case SharedRealtimeRcc:
			return new TestRuntimeBuilder() {
				@Override
				public ConfiguredRcc build(RoboticsContext context) {
					RealtimeRcc realtimeRCC = new RealtimeRcc();
					realtimeRCC.setName("Robotics API system test realtimeRCC");
					realtimeRCC.setUri("http://137.250.170.146:8880");
					return realtimeRCC;
					// throw new RuntimeException("TODO: Setup of runtime '" +
					// annotation.value() + "'.");
				}

				@Override
				public int getMaxParallelInstances() {
					return 1;
				}
			};
		default:
			throw new InitializationError("Unsupported runtime type '" + annotation.value() + "'.");
		}

	}

	public RoboticsTestSuite(Class<?> testClass,
			Map<Class<? extends Device>, Map<Class<? extends DeviceDriver>, Class<? extends DeviceInterfaceTest<?>>[]>> parameterization,
			TestRuntimeBuilder runtimeBuilder) throws InitializationError {
		super(testClass, Collections.<Runner>emptyList());
		this.name = null;
		setScheduler(new ParallelRunnerScheduler());

		RunnerSchedulerFactory schedulerFactory = new MyRunnerSchedulerFactory(
				runtimeBuilder.getMaxParallelInstances());
		for (Entry<Class<? extends Device>, Map<Class<? extends DeviceDriver>, Class<? extends DeviceInterfaceTest<?>>[]>> entry : parameterization
				.entrySet()) {
			runners.add(new RoboticsTestSuite(testClass, entry.getKey(), entry.getValue(), runtimeBuilder,
					schedulerFactory));
		}
	}

	public RoboticsTestSuite(Class<?> testClass, Class<? extends Device> deviceClass,
			Map<Class<? extends DeviceDriver>, Class<? extends DeviceInterfaceTest<?>>[]> parameterization,
			TestRuntimeBuilder runtimeBuilder, RunnerSchedulerFactory schedulerFactory) throws InitializationError {
		super(testClass, Collections.<Runner>emptyList());
		this.name = "[" + deviceClass.getSimpleName() + "]";
		setScheduler(new ParallelRunnerScheduler());

		for (Entry<Class<? extends DeviceDriver>, Class<? extends DeviceInterfaceTest<?>>[]> entry : parameterization
				.entrySet()) {
			runners.add(new RoboticsTestSuite(testClass, deviceClass, entry.getKey(), entry.getValue(), runtimeBuilder,
					schedulerFactory));
		}
	}

	public RoboticsTestSuite(Class<?> testClass, Class<? extends Device> deviceClass,
			Class<? extends DeviceDriver> driverClass, Class<? extends DeviceInterfaceTest<?>>[] interfaceTestClasses,
			TestRuntimeBuilder runtimeBuilder, RunnerSchedulerFactory schedulerFactory) throws InitializationError {
		super(testClass, Collections.<Runner>emptyList());
		this.name = "[" + deviceClass.getSimpleName() + "," + driverClass.getSimpleName() + "]";
		setScheduler(new ParallelRunnerScheduler());

		for (Class<? extends DeviceInterfaceTest<?>> interfaceTestClass : interfaceTestClasses) {
			runners.add(new RoboticsTestSuite(testClass, deviceClass, driverClass, interfaceTestClass, runtimeBuilder,
					schedulerFactory));
		}
	}

	public RoboticsTestSuite(Class<?> testClass, Class<? extends Device> deviceClass,
			Class<? extends DeviceDriver> driverClass, Class<? extends DeviceInterfaceTest<?>> interfaceClass,
			TestRuntimeBuilder runtimeBuilder, RunnerSchedulerFactory schedulerFactory) throws InitializationError {
		super(testClass, Collections.<Runner>emptyList());
		this.name = interfaceClass.getName() + "[" + deviceClass.getSimpleName() + "," + driverClass.getSimpleName()
				+ "]";
		this.setScheduler(schedulerFactory.create());

		// Collect BeforeInitialization methods (annotated with
		// @BeforeInitialization)
		MethodStore setupCleanupStore = new MethodStore(testClass);
		for (Method m : getMethodsAnnotatedWith(BeforeInitialization.class, testClass, Object.class)) {
			Class<? extends RoboticsObject> type = m.getAnnotation(BeforeInitialization.class).value();
			checkParameters(m, new Class[] { type });
			setupCleanupStore.registerBeforeInitializationMethod(type, m);
		}

		// Collect Prepare methods (annotated with @Prepare)
		for (Method m : getMethodsAnnotatedWith(Prepare.class, testClass, Object.class)) {
			Class<? extends RoboticsObject> type = m.getAnnotation(Prepare.class).value();
			checkParameters(m, new Class[] { type });
			setupCleanupStore.registerPrepareMethod(type, m);
		}

		// Collect AfterUninitialization methods (annotated with
		// @AfterUninitialization)
		for (Method m : getMethodsAnnotatedWith(AfterUninitialization.class, testClass, Object.class)) {
			Class<? extends RoboticsObject> type = m.getAnnotation(AfterUninitialization.class).value();
			checkParameters(m, new Class[] { type });
			setupCleanupStore.registerAfterUninitializationMethod(type, m);
		}

		// scan test class for methods annotated with @Test
		DeviceInterfaceTest<?> interfaceTestClassObject;
		Class<? extends DeviceInterface> deviceInterfaceType;
		try {
			interfaceTestClassObject = interfaceClass.getDeclaredConstructor().newInstance();
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException
				| NoSuchMethodException | SecurityException e) {
			throw new InitializationError("Test class '" + interfaceClass.getSimpleName() + "' is not instantiatable.");
		}
		deviceInterfaceType = interfaceTestClassObject.getDeviceInterfaceType();
		for (Method m : getMethodsAnnotatedWith(DeviceInterfaceTestMethod.class, interfaceClass,
				DeviceInterfaceTest.class)) {
			checkParameters(m, new Class[] { deviceInterfaceType });
			runners.add(new RoboticsTestRunner(testClass, deviceClass, driverClass, interfaceTestClassObject, m,
					setupCleanupStore, runtimeBuilder));
		}
	}

	private static List<Method> getMethodsAnnotatedWith(Class<? extends Annotation> annotationType, Class<?> clazz,
			Class<?> neededSuperType) {
		List<Method> result = new ArrayList<>();
		while (clazz != null && neededSuperType.isAssignableFrom(clazz)) {
			for (Method m : clazz.getDeclaredMethods()) {
				if (m.isAnnotationPresent(annotationType)) {
					result.add(m);
				}
			}
			clazz = clazz.getSuperclass();
		}
		return result;
	}

	private static void checkParameters(Method m, Class<?>[] expectedParametertypes) throws InitializationError {
		InitializationError initializationError = new InitializationError("Method '" + m.getName() + "' of test class '"
				+ m.getDeclaringClass().getSimpleName() + "' was expected to have following parameter types: '"
				+ Arrays.deepToString(expectedParametertypes) + "'.");

		if (m.getParameterTypes().length != expectedParametertypes.length) {
			throw initializationError;
		}
		for (int i = 0; i < m.getParameterTypes().length; i++) {
			if (!m.getParameterTypes()[i].isAssignableFrom(expectedParametertypes[i])) {
				throw initializationError;
			}
		}
	}

	@Override
	protected String getName() {
		return name == null ? super.getName() : name;
	}

	@Override
	protected List<Runner> getChildren() {
		return runners;
	}

	public static class MethodStore {

		private final Class<?> testClass;
		private final Map<Class<? extends RoboticsObject>, List<Method>> beforeInitializationMethods = new LinkedHashMap<>();
		private final Map<Class<? extends RoboticsObject>, List<Method>> prepareMethods = new LinkedHashMap<>();
		private final Map<Class<? extends RoboticsObject>, List<Method>> afterUninitializationMethods = new LinkedHashMap<>();
		private final Map<Object, Object> suiteObjects = new HashMap<>();

		private MethodStore(Class<?> testClass) {
			this.testClass = testClass;
		}

		private void registerBeforeInitializationMethod(Class<? extends RoboticsObject> clazz, Method m) {
			registerMehtod(clazz, m, beforeInitializationMethods);
		}

		private void registerPrepareMethod(Class<? extends RoboticsObject> clazz, Method m) {
			registerMehtod(clazz, m, prepareMethods);
		}

		private void registerAfterUninitializationMethod(Class<? extends RoboticsObject> clazz, Method m) {
			registerMehtod(clazz, m, afterUninitializationMethods);
		}

		private static void registerMehtod(Class<? extends RoboticsObject> clazz, Method m,
				Map<Class<? extends RoboticsObject>, List<Method>> methods) {
			if (!methods.containsKey(clazz)) {
				methods.put(clazz, new ArrayList<>());
			}
			methods.get(clazz).add(m);
		}

		public void callBeforeInitializationMethods(Object testIdentifier, RoboticsObject ro) throws Throwable {
			callMethods(testIdentifier, ro, beforeInitializationMethods, false);
		}

		public void callPrepareMethods(Object testIdentifier, RoboticsObject ro) throws Throwable {
			callMethods(testIdentifier, ro, prepareMethods, false);
		}

		public void callAfterUninitializationMethods(Object testIdentifier, RoboticsObject ro) throws Throwable {
			callMethods(testIdentifier, ro, afterUninitializationMethods, true);
		}

		private void callMethods(Object testIdentifier, RoboticsObject ro,
				Map<Class<? extends RoboticsObject>, List<Method>> methods, boolean continueAfterException)
				throws Throwable {
			if (!suiteObjects.containsKey(testIdentifier)) {
				suiteObjects.put(testIdentifier, testClass.getDeclaredConstructor().newInstance());
			}
			Object suiteObject = suiteObjects.get(testIdentifier);

			// call user specific setup methods
			Throwable e = null;
			for (Entry<Class<? extends RoboticsObject>, List<Method>> entry : methods.entrySet()) {
				if (entry.getKey().isAssignableFrom(ro.getClass())) {
					for (Method m : entry.getValue()) {
						try {
							m.invoke(suiteObject, ro);
						} catch (Throwable _e) {
							if (!continueAfterException) {
								throw _e;
							}
							if (e != null) {
								e = _e;
							}
						}
					}
				}
			}
			if (e != null) {
				throw e;
			}
		}
	}

	public static interface RunnerSchedulerFactory {
		public RunnerScheduler create();
	}

	/**
	 * Erzeugt beliebig viele RunnerScheduler, die Tests parallel auswerten.
	 * Speziell ist, dass eine globale Maximalanzahl gleichzitig laufender Tests
	 * spezifiziert werden kann, an die sich alle RunnerScheduler halten, bzw. diese
	 * unter sich ausmachen.
	 */
	private static class MyRunnerSchedulerFactory implements RunnerSchedulerFactory {

		private final RunnerScheduler runnerScheduler;
		private final Object monitor = new Object();
		private int count = 0;

		public MyRunnerSchedulerFactory(int maxParallelTestRuns) {
			runnerScheduler = new ParallelRunnerScheduler(maxParallelTestRuns);
		}

		@Override
		public RunnerScheduler create() {
			synchronized (monitor) {
				count++;
			}
			return new RunnerScheduler() {
				@Override
				public void schedule(Runnable childStatement) {
					runnerScheduler.schedule(childStatement);
				}

				@Override
				public void finished() {
					synchronized (monitor) {
						count--;
						if (count == 0) {
							runnerScheduler.finished();
						}
					}
				}
			};
		}
	}

	/**
	 * Erlaubt die parallele Ausführung von Tests (Anzahl gleichzeitiger Tests ist
	 * spezifizierbar oder unbeschränkt).
	 */
	private static class ParallelRunnerScheduler implements RunnerScheduler {
		private final ExecutorService fService;

		public ParallelRunnerScheduler(int maxParallelTestRuns) {
			fService = Executors.newFixedThreadPool(maxParallelTestRuns);
		}

		public ParallelRunnerScheduler() {
			fService = Executors.newCachedThreadPool();
		}

		@Override
		public void schedule(Runnable childStatement) {
			fService.submit(childStatement);
		}

		@Override
		public void finished() {
			try {
				fService.shutdown();
				fService.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace(System.err);
			}
		}
	}

}
