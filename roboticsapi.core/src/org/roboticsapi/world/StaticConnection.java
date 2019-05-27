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
 * A Robotics API world model static connection (between two frames).
 * 
 * StaticConnections are intended to be fixed, i.e. their {@link Transformation}
 * does not change and they are not removed during runtime of the application.
 */
public class StaticConnection extends Connection {

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

	/**
	 * Creates a new static connection with the given {@link Transformation}.
	 * 
	 * @param transformation the Transformation of this StaticConnection
	 */
	public StaticConnection(final Transformation transformation) {
		super();
		setTransformation(transformation);
	}

	/**
	 * Creates a new static connection with an identity {@link Transformation}.
	 */
	public StaticConnection() {
		this(new Transformation());
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.world.Relation#getRelationSensor()
	 */
	@Override
	public RelationSensor getRelationSensor() {

		return relSensor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.world.Relation#getMeasuredRelationSensor()
	 */
	@Override
	public RelationSensor getMeasuredRelationSensor() {
		return getRelationSensor();
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.world.Relation#getVelocitySensor()
	 */
	@Override
	public VelocitySensor getVelocitySensor() {

		return velSensor;
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see org.roboticsapi.world.Relation#getMeasuredVelocitySensor()
	 */
	@Override
	public VelocitySensor getMeasuredVelocitySensor() {
		return getVelocitySensor();
	}

	/**
	 * Retrieves the transformation of this StaticConnection.
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
	 * Sets the translation along x axis of this StaticConnection.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param x the new x value
	 */
	@ConfigurationProperty(Optional = true)
	public void setX(double x) {
		immutableWhenInitialized();
		this.x = x;
	}

	/**
	 * Sets the translation along y axis of this StaticConnection.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param y the new y value
	 */
	@ConfigurationProperty(Optional = true)
	public void setY(double y) {
		immutableWhenInitialized();
		this.y = y;
	}

	/**
	 * Sets the translation along z axis of this StaticConnection.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param z the new z value
	 */
	@ConfigurationProperty(Optional = true)
	public void setZ(double z) {
		immutableWhenInitialized();
		this.z = z;
	}

	/**
	 * Sets the rotation around z axis (A) of this StaticConnection.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param a the new A value
	 */
	@ConfigurationProperty(Optional = true)
	public void setA(double a) {
		immutableWhenInitialized();
		this.a = a;
	}

	/**
	 * Sets the rotation around y axis (B) of this StaticConnection.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param b the new B value
	 */
	@ConfigurationProperty(Optional = true)
	public void setB(double b) {
		immutableWhenInitialized();
		this.b = b;
	}

	/**
	 * Sets the rotation around x axis (C) of this StaticConnection.
	 * 
	 * This is a {@link ConfigurationProperty}.
	 * 
	 * @param c the new C value
	 */
	@ConfigurationProperty(Optional = true)
	public void setC(double c) {
		immutableWhenInitialized();
		this.c = c;
	}

	/**
	 * Gets the rotation around z axis (A) of this StaticConnection.
	 * 
	 * @return the A rotation value
	 */
	public double getA() {
		return a;
	}

	/**
	 * Gets the rotation around y axis (B) of this StaticConnection.
	 * 
	 * @return the B rotation value
	 */
	public double getB() {
		return b;
	}

	/**
	 * Gets the rotation around x axis (C) of this StaticConnection.
	 * 
	 * @return the C rotation value
	 */
	public double getC() {
		return c;
	}

	/**
	 * Gets the translation along x axis of this StaticConnection.
	 * 
	 * @return the x translation value
	 */
	public double getX() {
		return x;
	}

	/**
	 * Gets the translation along y axis of this StaticConnection.
	 * 
	 * @return the y translation value
	 */
	public double getY() {
		return y;
	}

	/**
	 * Gets the translation along z axis of this StaticConnection.
	 * 
	 * @return the z translation value
	 */
	public double getZ() {
		return z;
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();
		setTransformation(new Transformation(x, y, z, a, b, c));
	}

	/**
	 * Sets the transformation of this StaticConnection.
	 * 
	 * @param transformation new transformation
	 */
	protected void setTransformation(final Transformation transformation) {
		this.transformation = transformation;
		this.x = transformation.getTranslation().getX();
		this.y = transformation.getTranslation().getY();
		this.z = transformation.getTranslation().getZ();
		this.a = transformation.getRotation().getA();
		this.b = transformation.getRotation().getB();
		this.c = transformation.getRotation().getC();

		createSensors();
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