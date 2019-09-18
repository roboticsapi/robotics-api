/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.systemtest;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.ServiceLoader;

import org.junit.AssumptionViolatedException;
import org.junit.runner.Description;
import org.junit.runner.Runner;
import org.junit.runner.notification.Failure;
import org.junit.runner.notification.RunNotifier;
import org.junit.runners.model.InitializationError;
import org.roboticsapi.core.Device;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.DeviceInterface;
import org.roboticsapi.core.NotPresentException;
import org.roboticsapi.core.OnlineObject;
import org.roboticsapi.core.OnlineObject.OperationState;
import org.roboticsapi.core.OnlineObject.OperationStateListener;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.extension.Extension;
import org.roboticsapi.extension.ExtensionHandler;
import org.roboticsapi.extension.RoboticsObjectBuilder;
import org.roboticsapi.facet.runtime.rcc.RccRuntime;
import org.roboticsapi.facet.runtime.rpi.ConfiguredRcc;
import org.roboticsapi.systemtest.RoboticsTestSuite.MethodStore;

public class RoboticsTestRunner extends Runner {

	private final Class<? extends Device> deviceClass;
	private final Class<? extends DeviceDriver> driverClass;
	private final DeviceInterfaceTest<?> interfaceTestClassObject;
	private final Method method;
	private final MethodStore methodStore;
	private final TestRuntimeBuilder runtimeBuilder;
	private final String guid;

	public RoboticsTestRunner(Class<?> testClass, Class<? extends Device> deviceClass,
			Class<? extends DeviceDriver> driverClass, DeviceInterfaceTest<?> interfaceTestClassObject, Method method,
			MethodStore methodStore, TestRuntimeBuilder runtimeBuilder) throws InitializationError {
		this.deviceClass = deviceClass;
		this.driverClass = driverClass;
		this.interfaceTestClassObject = interfaceTestClassObject;
		this.method = method;
		this.methodStore = methodStore;
		this.runtimeBuilder = runtimeBuilder;
		this.guid = testClass.getName() + ":" + deviceClass.getName() + ":" + driverClass.getName() + ":"
				+ interfaceTestClassObject.getClass().getName() + ":" + method.getName();
	}

	@Override
	public Description getDescription() {
		return Description.createTestDescription(interfaceTestClassObject.getClass().getName(), method.getName(), guid);
	}

	@Override
	public void run(RunNotifier notifier) {
		notifier.fireTestStarted(getDescription());
		RoboticsContext context = null;
		try {
			context = createRoboticsContext("Test [" + guid + "]");

			RccRuntime runtime = createRuntime(deviceClass, context, runtimeBuilder);
			context.initialize(runtime);

			Device device = createDevice(deviceClass, context, methodStore);
			methodStore.callBeforeInitializationMethods(context, device);
			context.initialize(device);

			DeviceDriver driver = createDriver(device, runtime, driverClass, context, methodStore, guid);
			methodStore.callBeforeInitializationMethods(context, driver);
			context.initialize(driver);

			DeviceInterface deviceInterface = getDeviceInterface(device,
					interfaceTestClassObject.getDeviceInterfaceType());

			waitForBeingOpOrSafeOp(driver, 1000);

			methodStore.callPrepareMethods(context, device);
			methodStore.callPrepareMethods(context, driver);

			method.invoke(interfaceTestClassObject, new Object[] { deviceInterface });

			cleanupDriver(context, driver, methodStore);
			cleanupDevice(context, device, methodStore);
		} catch (InvocationTargetException e) {
			if (e.getCause() instanceof AssumptionViolatedException) {
				notifier.fireTestAssumptionFailed(new Failure(getDescription(), e.getCause()));
			} else {
				notifier.fireTestFailure(new Failure(getDescription(), e.getCause()));
			}
		} catch (Throwable e) {
			notifier.fireTestFailure(new Failure(getDescription(), e));
		} finally {
			if (context != null) {
				context.destroy();
				context = null;
			}
		}
		notifier.fireTestFinished(getDescription());
	}

	private static void waitForBeingOpOrSafeOp(OnlineObject driver, long timeout) throws NotPresentException {
		class Paket {
			public boolean online = false;
		}
		Paket monitor = new Paket();
		OperationStateListener l = new OperationStateListener() {
			@Override
			public void operationStateChanged(OnlineObject object, OperationState state) {
				if (state != OperationState.OPERATIONAL && state != OperationState.SAFEOPERATIONAL) {
					return;
				}
				synchronized (monitor) {
					monitor.online = true;
					monitor.notify();
				}
			}
		};

		driver.addOperationStateListener(l);
		synchronized (monitor) {
			long start = System.currentTimeMillis();
			while (!monitor.online) {
				long previousWaitTime = System.currentTimeMillis() - start;
				if (previousWaitTime >= timeout) {
					break;
				}
				try {
					monitor.wait(timeout - previousWaitTime);
				} catch (InterruptedException e) {
				}
			}
		}
		driver.removeOperationStateListener(l);
		if (!monitor.online) {
			throw new NotPresentException(driver.getName());
		}
	}

	private static RccRuntime createRuntime(Class<? extends Device> deviceClass, RoboticsContext context,
			TestRuntimeBuilder runtimeBuilder) throws Throwable {
		// Runtime
		ConfiguredRcc rcc = runtimeBuilder.build(context);
		RccRuntime runtime = new RccRuntime();
		runtime.setName("Test runtime");
		runtime.setRcc(rcc);
		return runtime;
	}

	private static Device createDevice(Class<? extends Device> deviceClass, RoboticsContext context,
			MethodStore setupStore) throws Throwable {
		// Device
		Device device = build(deviceClass, context);
		device.setName("Test device " + deviceClass.getSimpleName());
		return device;
	}

	private static DeviceDriver createDriver(Device device, RccRuntime runtime,
			Class<? extends DeviceDriver> driverClass, RoboticsContext context, MethodStore setupStore, String guid)
			throws Throwable {
		// Driver
		DeviceDriver driver = build(driverClass, context);
		driver.setName("Test driver " + driverClass.getSimpleName());
		callFunction(driver, "setRpiDeviceName", device.getName());// + '_' +
																	// guid);
		callFunction(driver, "setDevice", device);
		callFunction(driver, "setRuntime", runtime);
		return driver;
	}

	private static <T extends DeviceInterface> T getDeviceInterface(Device device, Class<T> deviceInterfaceClass) {
		// Get device interface
		T deviceInterface = device.use(deviceInterfaceClass);
		if (deviceInterface == null) {
			throw new RuntimeException("Device '" + device.getClass().getSimpleName()
					+ "' does not provide device interfacee '" + deviceInterfaceClass.getSimpleName() + "'.");
		}
		return deviceInterface;
	}

	private static void cleanupDevice(RoboticsContext context, Device device, MethodStore setupStore) throws Throwable {
		// Device
		context.uninitialize(device);
		setupStore.callAfterUninitializationMethods(context, device);
	}

	private static void cleanupDriver(RoboticsContext context, DeviceDriver driver, MethodStore setupStore)
			throws Throwable {
		// Driver
		context.uninitialize(driver);
		setupStore.callAfterUninitializationMethods(context, driver);
	}

	private static RoboticsContext createRoboticsContext(String testName) {
		RoboticsContext context = new RoboticsContextImpl(testName);
		loadExtensionsOverJspi(context);
		return context;
	}

	private static void loadExtensionsOverJspi(RoboticsContext context) {
		List<Extension> extensions = new ArrayList<>();
		List<ExtensionHandler<?>> extensionHandlers = new ArrayList<>();
		for (Extension extension : ServiceLoader.load(Extension.class)) {
			extensions.add(extension);
			if (extension instanceof ExtensionHandler) {
				extensionHandlers.add((ExtensionHandler<?>) extension);
			}
		}
		extensionHandlers.forEach(h -> context.addExtensionHandler(h));
		extensions.forEach(e -> context.addExtension(e));
	}

	@SuppressWarnings("unchecked")
	private static <T extends RoboticsObject> T build(Class<T> type, RoboticsContext context) {
		T result = null;
		for (RoboticsObjectBuilder builder : context.getExtensions(RoboticsObjectBuilder.class)) {
			if (builder.canBuild(type.getCanonicalName())) {
				if (result != null) {
					throw new RuntimeException("Multiple builders found for type '" + type.getSimpleName() + "'.");
				}
				result = (T) builder.build(type.getCanonicalName());
			}
		}
		if (result == null) {
			throw new RuntimeException("No builder found for '" + type.getSimpleName() + "'.");
		}
		return result;
	}

	private static void callFunction(Object object, String functionName, Object... parameters)
			throws IllegalAccessException, IllegalArgumentException, InvocationTargetException {
		Method mehtod = null;
		f1: for (Method m : object.getClass().getMethods()) {
			if (!m.getName().equals(functionName)) {
				continue;
			}
			if (m.getParameterTypes().length != parameters.length) {
				continue;
			}
			for (int i = 0; i < parameters.length; i++) {
				if (!m.getParameterTypes()[i].isAssignableFrom(parameters[i].getClass())) {
					continue f1;
				}
			}
			mehtod = m;
			break;
		}
		if (mehtod == null) {
			throw new RuntimeException(
					"Class '" + object.getClass().getSimpleName() + "' was expected to have function '" + functionName
							+ "' with parameters '" + Arrays.toString(parameters) + "'.");
		}
		mehtod.invoke(object, parameters);
	}

	public static interface TestRuntimeBuilder {
		public ConfiguredRcc build(RoboticsContext context);

		public int getMaxParallelInstances();
	}

}