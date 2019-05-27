/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.dataflow;

import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DataflowType;
import org.roboticsapi.runtime.rpi.RPIException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.sensor.RelationSensor;

/**
 * A data flow transmitting a frame (transformation matrix)
 */
public class RelationDataflow extends TransformationDataflow {

	/** start and destination frame the transformation matrix links */
	private final Frame from, to;

	/**
	 * Creates a new frame data flow
	 * 
	 * @param from source frame
	 * @param to   destination frame
	 */
	public RelationDataflow(final Frame from, final Frame to) {
		this.from = from;
		this.to = to;
	}

	/**
	 * Retrieves the source frame
	 * 
	 * @return source frame of the data flow
	 */
	public Frame getFrom() {
		return from;
	}

	/**
	 * Retrieves the destination frame
	 * 
	 * @return destination frame of the data flow
	 */
	public Frame getTo() {
		return to;
	}

	public static RelationSensor createRelationSensor(DataflowOutPort port, RoboticsRuntime runtime)
			throws RPIException {

		if (!(port.getType() instanceof RelationDataflow)) {
			throw new RPIException("Can only create RelationSensor on a port of type RelationDataflow");
		}

		RelationDataflow flow = (RelationDataflow) port.getType();

		return new RelationSensor(TransformationDataflow.createTransformationSensor(port, runtime), flow.getFrom(),
				flow.getTo());
	}

	@Override
	public boolean providesValueFor(final DataflowType other) {
		// are compatible if source and destination frame match. Otherwise,
		// we'll need the world as LinkBuilder
		if (!super.providesValueFor(other)) {
			return false;
		}
		if (other instanceof RelationDataflow) {
			final RelationDataflow o = (RelationDataflow) other;
			if (o.from != null && from != null && o.from != from) {
				return false;
			}
			if (o.to != null && to != null && o.to != to) {
				return false;
			}
		}
		return true;
	}

	@Override
	public String toString() {
		return "RelationDataflow<" + (from != null ? from.getName() : "<null>") + ","
				+ (to != null ? to.getName() : "<null>") + ">";
	}
}
