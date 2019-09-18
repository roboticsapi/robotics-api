/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.relation;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.GeometricRelation;
import org.roboticsapi.core.world.Transformation;

public abstract class AbstractConfiguredKnownRelation extends AbstractConfiguredRelation {

	private final Dependency<Double> x, y, z, a, b, c;

	public AbstractConfiguredKnownRelation() {
		x = createDependency("x", 0.0);
		y = createDependency("y", 0.0);
		z = createDependency("z", 0.0);
		a = createDependency("a", 0.0);
		b = createDependency("b", 0.0);
		c = createDependency("c", 0.0);
		createDependency("position", new Dependency.Builder<GeometricRelation>() {
			@Override
			public GeometricRelation create() {
				return new StaticPosition(getFrom(), getTo(),
						new Transformation(getX(), getY(), getZ(), getA(), getB(), getC()));
			}
		});
	}

	public AbstractConfiguredKnownRelation(Frame from, Frame to, Transformation transformation) {
		this();
		setFrom(from);
		setTo(to);
		setTransformation(transformation);
	}

	/**
	 * Gets the overall transformation.
	 *
	 * @return the transformation
	 */
	public final Transformation getTransformation() {
		return new Transformation(getX(), getY(), getZ(), getA(), getB(), getC());
	}

	/**
	 * Sets the overall transformation.
	 *
	 * @param transformation the overall transformation
	 */
	public final void setTransformation(Transformation transformation) {
		setX(transformation.getTranslation().getX());
		setY(transformation.getTranslation().getY());
		setZ(transformation.getTranslation().getZ());
		setA(transformation.getRotation().getA());
		setB(transformation.getRotation().getB());
		setC(transformation.getRotation().getC());
	}

	/**
	 * Gets the translation along x axis of this Relation.
	 *
	 * @return the x translation value
	 */
	public double getX() {
		return x.get();
	}

	/**
	 * Sets the translation along X axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param x the new x value
	 */
	@ConfigurationProperty(Optional = true)
	public void setX(double x) {
		this.x.set(x);
	}

	/**
	 * Gets the translation along y axis of this Relation.
	 *
	 * @return the y translation value
	 */
	public double getY() {
		return y.get();
	}

	/**
	 * Sets the translation along Y axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param y the new y value
	 */
	@ConfigurationProperty(Optional = true)
	public void setY(double y) {
		this.y.set(y);
	}

	/**
	 * Gets the translation along z axis of this Relation.
	 *
	 * @return the z translation value
	 */
	public double getZ() {
		return z.get();
	}

	/**
	 * Sets the translation along Z axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param z the new z value
	 */
	@ConfigurationProperty(Optional = true)
	public void setZ(double z) {
		this.z.set(z);
	}

	/**
	 * Gets the rotation around z axis (A) of this Relation.
	 *
	 * @return the A rotation value
	 */
	public double getA() {
		return a.get();
	}

	/**
	 * Sets the rotation around Z axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param a the new a value
	 */
	@ConfigurationProperty(Optional = true)
	public void setA(double a) {
		this.a.set(a);
	}

	/**
	 * Gets the rotation around y axis (B) of this Relation.
	 *
	 * @return the B rotation value
	 */
	public double getB() {
		return b.get();
	}

	/**
	 * Sets the rotation around Y axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param b the new b value
	 */
	@ConfigurationProperty(Optional = true)
	public void setB(double b) {
		this.b.set(b);
	}

	/**
	 * Gets the rotation around x axis (C) of this Relation.
	 *
	 * @return the C rotation value
	 */
	public double getC() {
		return c.get();
	}

	/**
	 * Sets the rotation around X axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param c the new c value
	 */
	@ConfigurationProperty(Optional = true)
	public void setC(double c) {
		this.c.set(c);
	}

	/**
	 * Sets the rotation around Z axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param aDeg the new a value in degrees
	 */
	@ConfigurationProperty(Optional = true)
	public void setADeg(double aDeg) {
		this.setA(Math.toRadians(aDeg));
	}

	/**
	 * Sets the rotation around Y axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param bDeg the new b value in degrees
	 */
	@ConfigurationProperty(Optional = true)
	public void setBDeg(double bDeg) {
		this.setB(Math.toRadians(bDeg));
	}

	/**
	 * Sets the rotation around X axis of this Relation.
	 *
	 * This is a {@link ConfigurationProperty}.
	 *
	 * @param cDeg the new c value in degrees
	 */
	@ConfigurationProperty(Optional = true)
	public void setCDeg(double cDeg) {
		this.setC(Math.toRadians(cDeg));
	}

}
