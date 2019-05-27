/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint;

import java.util.List;

import org.roboticsapi.multijoint.MultiJointDeviceDriver;
import org.roboticsapi.runtime.core.netcomm.ReadDoubleFromNet;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;

/**
 * A data flow transmitting a set of joint values
 */
public class JointsDataflow extends DataflowType {

	/** number of joints */
	private final int count;

	/** Device driver containing the joints */
	private final MultiJointDeviceDriver multiJointDeviceDriver;

	@Override
	public boolean providesValueFor(final DataflowType other) {
		// are compatible if the number of joints matches
		if (!super.providesValueFor(other)) {
			return false;
		}
		if (other instanceof JointsDataflow) {
			final JointsDataflow o = (JointsDataflow) other;
			if (o.count != count) {
				return false;
			}
			if (o.multiJointDeviceDriver != null && multiJointDeviceDriver != null
					&& o.multiJointDeviceDriver != multiJointDeviceDriver) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Creates a new joint data flow
	 * 
	 * @param count                  number of joints
	 * @param multiJointDeviceDriver device driver for this dataflow
	 */
	public JointsDataflow(final int count, final MultiJointDeviceDriver multiJointDeviceDriver) {
		super(count);
		this.multiJointDeviceDriver = multiJointDeviceDriver;
		this.count = count;
	}

	public MultiJointDeviceDriver getMultiJointDeviceDriver() {
		return multiJointDeviceDriver;
	}

	public int getCount() {
		return count;
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		ValueReader[] result = new ValueReader[getCount()];

		for (int i = 0; i < getCount(); i++) {
			ReadDoubleFromNet reader = fragment.add(new ReadDoubleFromNet(uniqueKeyPrefix + "_JointsDataflow_" + i));

			fragment.connect(valuePorts.get(i), reader.getInValue());

			result[i] = new ValueReader(reader) {

				@Override
				public Object interpretValue(String value) {
					return Double.parseDouble(value);
				}
			};
		}
		return result;
	}
}
