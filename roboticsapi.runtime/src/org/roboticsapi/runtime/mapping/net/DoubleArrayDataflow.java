/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import java.util.List;

import org.roboticsapi.runtime.core.netcomm.ReadDoubleArrayFromNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * A data flow type transmitting a double array
 */
public class DoubleArrayDataflow extends DataflowType {

	private final int size;

	public DoubleArrayDataflow(int size) {
		super(1);
		this.size = size;
	}

	public int getSize() {
		return size;
	}

	@Override
	public boolean providesValueFor(DataflowType other) {
		if (!super.providesValueFor(other)) {
			return false;
		}
		if (other instanceof DoubleArrayDataflow) {
			final DoubleArrayDataflow o = (DoubleArrayDataflow) other;
			if (o.getSize() != getSize()) {
				return false;
			}
		}
		return true;
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		ReadDoubleArrayFromNet reader = fragment
				.add(new ReadDoubleArrayFromNet(uniqueKeyPrefix + "_DoubleArrayDataflow", getSize()));

		fragment.connect(valuePorts.get(0), reader.getInValue());

		return new ValueReader[] { new ValueReader(reader) {

			@Override
			public Object interpretValue(String line) {
				Double[] ret = new Double[size];

				if (!line.startsWith("[") || !line.endsWith("]")) {
					return null;
				}

				String[] data = line.substring(1, line.length() - 1).split(",");
				if (data.length != size) {
					return null;
				}

				for (int i = 0; i < size; i++) {
					try {
						ret[i] = Double.valueOf(data[i]);
					} catch (NumberFormatException e) {
						ret[i] = Double.NaN;
					}
				}
				return ret;
			}
		} };
	}
}
