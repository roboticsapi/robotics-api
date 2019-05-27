/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.PersistContext.PersistedSensorFactory;
import org.roboticsapi.core.Sensor;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.runtime.CommandSensorListenerException;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.runtime.rpi.NetcommPrimitive;
import org.roboticsapi.runtime.rpi.NetcommValue;

public abstract class AtomicSensorMapperResult<T> extends BaseSensorMapperResult<T> {
	private NetcommPrimitive netcomm;

	public AtomicSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	public AtomicSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort, DataflowOutPort sensorTimePort) {
		super(fragment, sensorPort, sensorTimePort);
	}

	@Override
	public void addListener(final Command command, final SensorListener<T> listener) throws MappingException {

		if (netcomm == null) {
			netcomm = createNetcommFragment();
		}

		netcomm.getNetcommFromRPI().addNetcommListener(new NetcommListenerAdapter() {
			@Override
			public void valueChanged(final NetcommValue value) {
				T realValue = convertNetcommValue(value);

				if (realValue != null) {
					try {
						listener.onValueChanged(realValue);
					} catch (Throwable exc) {
						command.handleException(new CommandSensorListenerException("Exception in SensorListener", exc));
					}
				}
			}
		});

	}

	@Override
	public void assign(Command command, PersistContext<T> target) throws MappingException {

		if (netcomm == null) {
			netcomm = createNetcommFragment();
		}
		final NetcommValue key = netcomm.getNetcommFromRPI();
		target.setFactory(new PersistedSensorFactory<T>() {
			@Override
			public Sensor<T> createSensor(Command command) {
				return createInterNetcommSensor(command, key);
			}
		});

	}

	@Override
	public void addUpdateListener(final Command command, final SensorUpdateListener<T> listener)
			throws MappingException {
		if (netcomm == null) {
			netcomm = createNetcommFragment();
		}

		netcomm.getNetcommFromRPI().addNetcommListener(new NetcommListenerAdapter() {
			@Override
			public void valueChanged(final NetcommValue value) {
				try {
					listener.onValueChanged(convertNetcommValue(value));

				} catch (Throwable exc) {
					command.handleException(new CommandSensorListenerException(
							"Exception in SensorUpdateListener#onValueChanged()", exc));
				}
			}

			@Override
			public void updatePerformed() {
				try {
					listener.updatePerformed();
				} catch (Throwable exc) {
					command.handleException(new CommandSensorListenerException(
							"Exception in SensorUpdateListener#updatePerformed()", exc));
				}
			}
		});

	}

	protected abstract NetcommPrimitive createNetcommFragment() throws MappingException;

	protected abstract T convertNetcommValue(NetcommValue value);

	protected abstract Sensor<T> createInterNetcommSensor(Command command, NetcommValue key);

}
