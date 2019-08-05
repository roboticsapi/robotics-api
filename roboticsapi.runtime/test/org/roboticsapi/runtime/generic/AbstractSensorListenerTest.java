/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RoboticsRuntime.CommandHook;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.SensorListenerRegistration;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.BooleanFromJavaSensor;
import org.roboticsapi.core.sensor.ConstantBooleanSensor;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.VectorSensor;

public abstract class AbstractSensorListenerTest extends AbstractRuntimeTest {

	private static int valueChangeCount;
	private boolean valueUpdated;
	private boolean boolSensorValue;
	private RoboticsRuntime runtime2;
	private CommandHook hook2;
	private boolean listener1Notified;
	private boolean listener2Notified;
	private boolean listener3Notified;

	@Override
	protected void afterRuntimeSetup() throws InitializationException {
		super.afterRuntimeSetup();
		runtime2 = getRuntimeSetup().createRuntime("2");

		hook2 = new CommandHook() {
			@Override
			public void commandHandleHook(CommandHandle handle) {
				testCommandHandles.add(handle);
			}

			@Override
			public void commandSealHook(Command command) {
			}

			@Override
			public void commandLoadHook(Command command) {
			}
		};
		runtime2.addCommandHook(hook2);
	}

	@Override
	protected void beforeRuntimeTeardown() {
		super.beforeRuntimeTeardown();

		if (runtime2 != null) {
			runtime2.removeCommandHook(hook2);
		}
	}

	@Override
	protected void afterRuntimeTeardown() {
		super.afterRuntimeTeardown();

		try {
			getRuntimeSetup().destroyRuntime(runtime2);
		} catch (RoboticsException e) {

		}
	}

	@Before
	public void initSingleTest() {
		valueChangeCount = 0;
		valueUpdated = false;
		boolSensorValue = false;
	}

	@Test
	public void testListenerIsNotifiedWithStandardSensorValueOnce() throws RoboticsException {
		Command command = getRuntime().createWaitCommand(0.3);

		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);

		boolSensorValue = true;

		command.addObserver(sensor, new SensorListener<Boolean>() {

			private boolean oldValue = true;

			@Override
			public void onValueChanged(Boolean newValue) {
				if (newValue != oldValue) {
					valueChangeCount++;
				}

				oldValue = newValue;

				boolSensorValue = newValue;

			}
		});

		try {
			command.execute();

			Thread.sleep(100);

			Assert.assertEquals(1, valueChangeCount);
			Assert.assertEquals(false, boolSensorValue);

		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception occurred");
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception occurred");
		}
	}

	@Test
	public void testAllVectorSensorListenersNotified() throws RoboticsException {
		listener1Notified = false;
		listener2Notified = false;

		Command wait = getRuntime().createWaitCommand(0.3);
		VectorSensor sensor = VectorSensor.fromConstant(new Vector(1, 0, 0));
		wait.addObserver(sensor, new SensorListener<Vector>() {
			@Override
			public void onValueChanged(Vector newValue) {
				listener1Notified = true;
			}

		});
		wait.addObserver(sensor, new SensorListener<Vector>() {
			@Override
			public void onValueChanged(Vector newValue) {
				listener2Notified = true;
			}

		});
		wait.execute();
		Assert.assertTrue(listener1Notified);
		Assert.assertTrue(listener2Notified);
	}

	@Test(timeout = 2000)
	public void testObserverGetsNoNewValueWhenNoSensorValueChange() throws RoboticsException {
		Command command = getRuntime().createWaitCommand(0.3);

		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);

		command.addObserver(sensor, new SensorListener<Boolean>() {

			private boolean firstNotify = true;
			private boolean oldValue;

			@Override
			public void onValueChanged(Boolean newValue) {
				if (!firstNotify) {
					if (newValue != oldValue) {
						valueChangeCount++;
					}
				}
				oldValue = newValue;
				firstNotify = false;

			}
		});

		try {
			command.execute();

			// Thread.sleep(100);

			Assert.assertEquals(0, valueChangeCount);

		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception occurred");
		}
	}

	@Test(timeout = 2000)
	public void testObserverIsNotifiedUponSensorValueChange() throws RoboticsException {
		Command command = getRuntime().createWaitCommand(0.3);

		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);

		command.addObserver(sensor, new SensorListener<Boolean>() {

			@Override
			public void onValueChanged(Boolean newValue) {
				valueUpdated = newValue;

			}
		});

		try {
			CommandHandle handle = command.start();

			sensor.setValue(true);

			handle.waitComplete();

			Assert.assertTrue("Observer should be notified when sensor value changes.", valueUpdated);

		} catch (RoboticsException e) {
			e.printStackTrace();
			Assert.fail("Unexpected Exception occurred");
		}
	}

	@Test(timeout = 2000)
	public void testSensorListenersOfDifferentAndNoRuntimeSensorsAreNotified()
			throws RoboticsException, InterruptedException {
		BooleanFromJavaSensor sensor = new BooleanFromJavaSensor(false);
		BooleanFromJavaSensor sensor2 = new BooleanFromJavaSensor(false);
		ConstantBooleanSensor sensor3 = new ConstantBooleanSensor(true);

		SensorListenerRegistration<Boolean> reg = new SensorListenerRegistration<Boolean>(sensor,
				new SensorListener<Boolean>() {

					@Override
					public void onValueChanged(Boolean newValue) {
						listener1Notified = newValue;

					}
				});
		SensorListenerRegistration<Boolean> reg2 = new SensorListenerRegistration<Boolean>(sensor2,
				new SensorListener<Boolean>() {

					@Override
					public void onValueChanged(Boolean newValue) {
						listener2Notified = newValue;

					}
				});
		SensorListenerRegistration<Boolean> reg3 = new SensorListenerRegistration<Boolean>(sensor3,
				new SensorListener<Boolean>() {

					@Override
					public void onValueChanged(Boolean newValue) {
						listener3Notified = newValue;

					}
				});

		List<SensorListenerRegistration<?>> list = new ArrayList<SensorListenerRegistration<?>>();
		list.add(reg);
		list.add(reg2);
		list.add(reg3);

		Sensor.addListeners(list);

		sensor.setValue(true);
		sensor2.setValue(true);

		// needed to await BooleanFromJavaSensor data transfer
		Thread.sleep(500);

		Sensor.removeListeners(list);

		Assert.assertTrue(listener1Notified);
		Assert.assertTrue(listener2Notified);
		Assert.assertTrue(listener3Notified);
	}
}
