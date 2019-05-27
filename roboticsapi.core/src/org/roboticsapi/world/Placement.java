/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.world.sensor.ConstantVelocitySensor;
import org.roboticsapi.world.sensor.RelationSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * A Robotics API world model placement (between frames)
 */
public class Placement extends Relation {

	/** transformation between the frames */
	private Transformation transformation;
	private double x;
	private double y;
	private double z;
	private double a;
	private double b;
	private double c;
	private RelationSensor relSensor;
	private ConstantVelocitySensor velSensor;

	public Placement() {
		this(new Transformation());
	}

	/**
	 * Creates a new placement
	 * 
	 * @param transformation current transformation
	 */
	public Placement(final Transformation transformation) {
		setTransformationInternal(transformation);
	}

	/**
	 * Creates a new placement
	 * 
	 * @param x the X translation value
	 * @param y the Y translation value
	 * @param z the Z translation value
	 * @param a the A rotation value (i.e. rotation around Z axis)
	 * @param b the B rotation value (i.e. rotation around Y axis)
	 * @param c the C rotation value (i.e. rotation around X axis)
	 */
	public Placement(double x, double y, double z, double a, double b, double c) {
		this(new Transformation(x, y, z, a, b, c));
	}

	@Override
	public RelationSensor getRelationSensor() {
		return relSensor;
	}

	@Override
	public RelationSensor getMeasuredRelationSensor() {
		return getRelationSensor();
	}

	/**
	 * Retrieves the transformation
	 * 
	 * @return transformation between the frames
	 */
	@Override
	public Transformation getTransformation() {
		return transformation;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.world.Relation#getMeasuredTransformation()
	 */
	@Override
	public Transformation getMeasuredTransformation() {
		return getTransformation();
	}

	@Override
	public VelocitySensor getVelocitySensor() {
		return velSensor;
	}

	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		return getVelocitySensor();
	}

	/**
	 * Sets the transformation
	 * 
	 * @param transformation new transformation
	 */
	public void setTransformation(final Transformation transformation) throws RelationException {
		// TODO: Check consistency of graph in methods setFrom() and setTo()
		if ((getFrom() == null) || (getTo() == null)) {
			setTransformationInternal(transformation);

			return;
		}

		try {
			Transformation t = getFrom().getTransformationTo(getTo(), this);

			if (t == null || transformation.isEqualTransformation(t)) {
				setTransformationInternal(transformation);
			} else {
				throw new RelationException("Desired transformation violates existing Frame-Relation graph");
			}
		} catch (TransformationException e) {
			// exception is thrown if no transformation was found
			setTransformationInternal(transformation);
		}
	}

	private void setTransformationInternal(final Transformation transformation) {
		this.transformation = transformation;
		this.x = transformation.getTranslation().getX();
		this.y = transformation.getTranslation().getY();
		this.z = transformation.getTranslation().getZ();
		this.a = transformation.getRotation().getA();
		this.b = transformation.getRotation().getB();
		this.c = transformation.getRotation().getC();

		createSensors();
	}

	@Override
	protected void setFrames(Frame from, Frame to) {
		super.setFrames(from, to);

		createSensors();
	}

	@Override
	@ConfigurationProperty
	public void setFrom(Frame from) {
		super.setFrom(from);
		createSensors();
	}

	@Override
	@ConfigurationProperty
	public void setTo(Frame to) {
		super.setTo(to);
		createSensors();
	}

	/**
	 * Sets the translation along x axis of this placement.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param x the new x value
	 */
	@ConfigurationProperty(Optional = true)
	public void setX(double x) throws RelationException {
		if (!isInitialized()) {
			this.x = x;
		} else {
			setTransformation(new Transformation(x, y, z, a, b, c));
		}
	}

	/**
	 * Sets the translation along y axis of this placement.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param y the new y value
	 */
	@ConfigurationProperty(Optional = true)
	public void setY(double y) throws RelationException {
		if (!isInitialized()) {
			this.y = y;
		} else {
			setTransformation(new Transformation(x, y, z, a, b, c));
		}
	}

	/**
	 * Sets the translation along z axis of this placement.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param z the new z value
	 */
	@ConfigurationProperty(Optional = true)
	public void setZ(double z) throws RelationException {
		if (!isInitialized()) {
			this.z = z;
		} else {
			setTransformation(new Transformation(x, y, z, a, b, c));
		}
	}

	/**
	 * Sets the rotation around z axis (A) of this placement.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param a the new A value
	 */
	@ConfigurationProperty(Optional = true)
	public void setA(double a) throws RelationException {
		if (!isInitialized()) {
			this.a = a;
		} else {
			setTransformation(new Transformation(x, y, z, a, b, c));
		}
	}

	/**
	 * Sets the rotation around y axis (B) of this placement.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param b the new B value
	 */
	@ConfigurationProperty(Optional = true)
	public void setB(double b) throws RelationException {
		if (!isInitialized()) {
			this.b = b;
		} else {
			setTransformation(new Transformation(x, y, z, a, b, c));
		}
	}

	/**
	 * Sets the rotation around x axis (C) of this placement.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param c the new C value
	 */
	@ConfigurationProperty(Optional = true)
	public void setC(double c) throws RelationException {
		if (!isInitialized()) {
			this.c = c;
		} else {
			setTransformation(new Transformation(x, y, z, a, b, c));
		}
	}

	/**
	 * Gets the rotation around z axis (A) of this placement.
	 * 
	 * @return the A rotation value
	 */
	public double getA() {
		return a;
	}

	/**
	 * Gets the rotation around y axis (B) of this placement.
	 * 
	 * @return the B rotation value
	 */
	public double getB() {
		return b;
	}

	/**
	 * Gets the rotation around x axis (C) of this placement.
	 * 
	 * @return the C rotation value
	 */
	public double getC() {
		return c;
	}

	/**
	 * Gets the translation along x axis of this placement.
	 * 
	 * @return the x translation value
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the translation along y axis of this placement.
	 * 
	 * @return the y translation value
	 */
	public double getY() {
		return y;
	}

	/**
	 * Gets the translation along z axis of this placement.
	 * 
	 * @return the z translation value
	 */
	public double getZ() {
		return z;
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		try {
			setTransformation(new Transformation(x, y, z, a, b, c));
		} catch (RelationException e) {
			throw new ConfigurationException("", e.getMessage(), e);
		}
	}

	private void createSensors() {
		if (getTransformation() == null || getFrom() == null || getTo() == null) {
			return;
		}
		relSensor = RelationSensor.fromConstant(getFrom(), getTo(), getTransformation());

		velSensor = new ConstantVelocitySensor(getTo(), getFrom(), getFrom().getPoint(), getFrom().getOrientation(),
				new Twist());
	}

}
