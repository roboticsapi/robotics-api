/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.generic;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;
import java.util.concurrent.Semaphore;

import org.junit.After;
import org.junit.Before;
import org.roboticsapi.core.Command;
import org.roboticsapi.core.CommandException;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.RoboticsContext;
import org.roboticsapi.core.RoboticsContextImpl;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.RoboticsRuntime.CommandHook;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;

public abstract class AbstractRuntimeTest {

	private RoboticsContext context;
	private RuntimeSetup runtimeSetup;
	private RoboticsRuntime runtime;
	protected final List<CommandHandle> testCommandHandles = new ArrayList<CommandHandle>();
	private CommandHook hook;

	public abstract RuntimeSetup getRuntimeSetup();

	public AbstractRuntimeTest() {
		super();
	}

	protected final RoboticsContext getContext() {
		return context;
	}

	protected final RoboticsRuntime getRuntime() {
		return runtime;
	}

	@Before
	public void setUpRuntime() throws InitializationException {
		context = new RoboticsContextImpl("test");
		// extensions!
		runtimeSetup = getRuntimeSetup();
		runtime = runtimeSetup.createRuntime();
		runtimeSetup.registerExtensions(context);

		context.initialize(runtime);
		context.register(runtime);

		hook = new CommandHook() {
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
		runtime.addCommandHook(hook);

		afterRuntimeSetup();
	}

	protected void afterRuntimeSetup() throws InitializationException {
	}

	@After
	public final void teardownRuntime() throws RoboticsException {
		beforeRuntimeTeardown();

		if (runtime != null) {
			runtime.removeCommandHook(hook);
		}

		for (CommandHandle handle : testCommandHandles) {
			try {
				handle.abort();
			} catch (CommandException e) {
				e.printStackTrace();
			}
		}

		testCommandHandles.clear();

		runtimeSetup.unregisterExtensions(context);
		runtimeSetup.destroyRuntime(runtime);

		context.destroy();
		afterRuntimeTeardown();
	}

	protected void beforeRuntimeTeardown() {
	}

	protected void afterRuntimeTeardown() {
	}

	public final <T> T getCurrentSensorValue(Sensor<T> sensor) throws InterruptedException, RoboticsException {
		Command wait = runtime.createWaitCommand();

		final Semaphore sem = new Semaphore(1);

		sem.acquire();

		final List<T> list = new Vector<T>();

		wait.addObserver(sensor, new SensorListener<T>() {

			@Override
			public void onValueChanged(T newValue) {
				list.add(newValue);
				sem.release();

			}
		});

		CommandHandle handle = wait.start();

		sem.acquire();

		handle.cancel();
		handle.waitComplete();

		return list.get(list.size() - 1);
	}

}
