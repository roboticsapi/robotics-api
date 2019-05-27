/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.result.impl;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.SensorListener;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.net.DataflowType.ValueReader;
import org.roboticsapi.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;

public abstract class ComposedSensorMapperResult<T> extends BaseSensorMapperResult<T> {

	public ComposedSensorMapperResult(NetFragment fragment, DataflowOutPort sensorPort) {
		super(fragment, sensorPort);
	}

	@Override
	public void addListener(Command command, SensorListener<T> listener) throws MappingException {

		List<ValueReader> readers = createReaders();

		ListenerContext context = new ListenerContext(readers.size());

		for (int i = 0; i < readers.size() - 1; i++) {
			ValueReader reader = readers.get(i);

			NetcommValue netcomm = reader.getPrimitive().getNetcommFromRPI();

			netcomm.addNetcommListener(new ValueListener(i, context, reader));
		}

		ValueReader lastReader = readers.get(readers.size() - 1);
		lastReader.getPrimitive().getNetcommFromRPI()
				.addNetcommListener(new UpdateListener(readers.size() - 1, listener, context, lastReader));

	}

	private List<ValueReader> createReaders() throws MappingException {
		DataflowType type = getSensorPort().getType();

		ValueReader[] readers;
		try {
			readers = type.createValueReaders(UUID.randomUUID().toString(), getSensorPort().getPorts(),
					getNetFragment());
		} catch (RPIException e) {
			throw new MappingException(e);
		}

		if (readers == null) {
			throw new MappingException("DataflowType " + type.getClass().getSimpleName()
					+ " did not provide ValueReaders for netcomm communication");
		}

		return Arrays.asList(readers);

	}

	@Override
	public void addUpdateListener(Command command, SensorUpdateListener<T> listener) throws MappingException {
		throw new UnsupportedOperationException("Not implemented");

	}

	private class ListenerContext {
		private boolean anyUpdated;
		private Object[] values = null;

		public ListenerContext(int valueCount) {
			values = new Object[valueCount];
		}

		public void update(int index, Object value) {
			values[index] = value;
			anyUpdated = true;
		}

		public boolean anyUpdated() {
			return anyUpdated;
		}

		public void reset() {
			anyUpdated = false;
		}

		public boolean allValuesNonNull() {
			boolean nonNull = true;
			for (int i = 0; i < values.length; i++) {
				nonNull &= (values[i] != null);
			}
			return nonNull;
		}

	}

	private class ValueListener extends NetcommListenerAdapter {
		private final int index;
		private final ListenerContext context;
		private final ValueReader reader;

		public ValueListener(int index, ListenerContext context, ValueReader reader) {
			this.index = index;
			this.context = context;
			this.reader = reader;

		}

		@Override
		public void valueChanged(final NetcommValue value) {
			try {
				context.update(index, reader.interpretValue(value.getString()));
			} catch (Exception e) {
				RAPILogger.getLogger().log(RAPILogger.ERRORLEVEL,
						"Exception in ComposedSensorMapper.ValueListener#valueChanged()", e);
			}
		}
	}

	private class UpdateListener extends ValueListener {
		private final SensorListener<T> listener;
		private final ListenerContext context;

		public UpdateListener(int index, SensorListener<T> listener, ListenerContext context, ValueReader reader) {
			super(index, context, reader);
			this.listener = listener;
			this.context = context;
		}

		@Override
		public void updatePerformed() {
			if (context.anyUpdated() && context.allValuesNonNull()) {
				context.reset();
				try {
					listener.onValueChanged(composeValue(context.values));
				} catch (Exception e) {
					RAPILogger.getLogger().log(RAPILogger.ERRORLEVEL,
							"Exception in ComposedSensorMapper.UpdateListener", e);
				}
			}
		}
	}

	protected abstract T composeValue(Object[] values);

}
