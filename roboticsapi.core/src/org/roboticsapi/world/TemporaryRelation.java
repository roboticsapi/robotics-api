/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import java.lang.ref.WeakReference;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.world.sensor.ConstantVelocitySensor;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

public final class TemporaryRelation extends Relation {

	private final Transformation transformation;
	private WeakReference<Frame> to;

	TemporaryRelation(Transformation transformation) {
		this.transformation = transformation;
	}

	@Override
	protected void setFrames(Frame from, Frame to) {
		super.setFrames(from, null);
		this.to = new WeakReference<Frame>(to);
	}

	@Override
	public Frame getTo() {
		return to.get();
	}

	@Override
	@ConfigurationProperty
	public void setFrom(Frame from) {
		throw new IllegalStateException("Source frame of temporary placement cannot be changed.");
	}

	@Override
	@ConfigurationProperty
	public void setTo(Frame to) {
		throw new IllegalStateException("Target frame of temporary placement cannot be changed.");
	}

	@Override
	public Transformation getTransformation() {
		return this.transformation;
	}

	@Override
	public Transformation getMeasuredTransformation() {
		return getTransformation();
	}

	@Override
	public RelationSensor getRelationSensor() {
		return RelationSensor.fromConstant(getFrom(), getTo(), getTransformation());
	}

	@Override
	public RelationSensor getMeasuredRelationSensor() {
		return getRelationSensor();
	}

	@Override
	public VelocitySensor getVelocitySensor() {
		return new ConstantVelocitySensor(getTo(), getFrom(), getFrom().getPoint(), getFrom().getOrientation(),
				new Twist());
	}

	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		return getVelocitySensor();
	}

}
