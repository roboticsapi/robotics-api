/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.dataflow;

import java.util.List;

import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.runtime.world.netcomm.ReadTwistFromNet;
import org.roboticsapi.runtime.world.types.RPITwist;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;

public class VelocityDataflow extends DataflowType {

	private final Frame moving, reference;
	private final Point pivotPoint;
	private final Orientation orientation;

	public VelocityDataflow(final Frame moving, final Frame reference, final Point pivotPoint,
			final Orientation orientation) {
		super(1);
		this.moving = moving;
		this.reference = reference;
		this.pivotPoint = pivotPoint;
		this.orientation = orientation;
	}

	public Frame getMovingFrame() {
		return moving;
	}

	public Frame getReferenceFrame() {
		return reference;
	}

	public Point getPivotPoint() {
		return pivotPoint;
	}

	public Orientation getOrientation() {
		return orientation;
	}

	@Override
	public boolean providesValueFor(DataflowType other) {
		if (!super.providesValueFor(other)) {
			return false;
		}

		if (other instanceof VelocityDataflow) {
			VelocityDataflow vel = (VelocityDataflow) other;

			if (vel.getMovingFrame() != null && getMovingFrame() != null && vel.getMovingFrame() != getMovingFrame()) {
				return false;
			}

			if (vel.getReferenceFrame() != null && getReferenceFrame() != null
					&& vel.getReferenceFrame() != getReferenceFrame()) {
				return false;
			}

			if (vel.getPivotPoint() != null && getPivotPoint() != null && vel.getPivotPoint() != getPivotPoint()) {
				return false;
			}

			if (vel.getOrientation() != null && getOrientation() != null && vel.getOrientation() != getOrientation()) {
				return false;
			}
		}

		return true;
	}

	@Override
	public String toString() {
		return "VelocityDataflow<M:" + (getMovingFrame() != null ? getMovingFrame().getName() : "null") + ", R:"
				+ (getReferenceFrame() != null ? getReferenceFrame().getName() : "null") + ", P:"
				+ (getPivotPoint() != null ? getPivotPoint().getName() : "null") + ", O:"
				+ (getOrientation() != null ? getOrientation() : "null") + ">";
	}

	@Override
	public ValueReader[] createValueReaders(String uniqueKeyPrefix, List<OutPort> valuePorts, NetFragment fragment)
			throws RPIException, MappingException {
		ReadTwistFromNet reader = fragment.add(new ReadTwistFromNet(uniqueKeyPrefix + "_VelocityDataflow"));

		fragment.connect(valuePorts.get(0), reader.getInValue());

		return new ValueReader[] { new ValueReader(reader) {

			@Override
			public Object interpretValue(String value) {
				RPITwist twist = new RPITwist(value);

				return new Twist(
						new Vector(twist.getVel().getX().get(), twist.getVel().getY().get(),
								twist.getVel().getZ().get()),
						new Vector(twist.getRot().getX().get(), twist.getRot().getY().get(),
								twist.getRot().getZ().get()));
			}
		} };
	}

}
