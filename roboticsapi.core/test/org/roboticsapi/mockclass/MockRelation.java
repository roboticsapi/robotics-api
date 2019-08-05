/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.mockclass;

import org.roboticsapi.world.Relation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public class MockRelation extends Relation {
	private Transformation transformation;
	private RelationSensor relationSensor;
	private VelocitySensor velocitySensor;

	public void setTransformation(Transformation t) {
		transformation = t;
	}

	@Override
	public Transformation getTransformation() throws TransformationException {
		return transformation;
	}

	@Override
	public Transformation getMeasuredTransformation() throws TransformationException {
		return getTransformation();
	}

	public void setRelationSensor(RelationSensor rs) {
		relationSensor = rs;
	}

	@Override
	public RelationSensor getRelationSensor() {
		return relationSensor;
	}

	@Override
	public RelationSensor getMeasuredRelationSensor() {
		return getRelationSensor();
	}

	public void setVelocitySensor(VelocitySensor vs) {
		velocitySensor = vs;
	}

	@Override
	public VelocitySensor getVelocitySensor() {
		return velocitySensor;
	}

	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		return getVelocitySensor();
	}
}
