/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.simulation;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.world.mutable.MutableTransformation;

public final class SPositionedEntity extends SEntity {

	private final Dependency<Double> x = createDependency("x", 0.), y = createDependency("y", 0.),
			z = createDependency("z", 0.), a = createDependency("a", 0.), b = createDependency("b", 0.),
			c = createDependency("c", 0.);
	private final Dependency<MutableTransformation> relativePosition = createDependency("relativePosition",
			() -> new MutableTransformation(x.get(), y.get(), z.get(), a.get(), b.get(), c.get()));

	@ConfigurationProperty
	public void setX(double x) {
		this.x.set(x);
	}

	@ConfigurationProperty
	public void setY(double y) {
		this.y.set(y);
	}

	@ConfigurationProperty
	public void setZ(double z) {
		this.z.set(z);
	}

	@ConfigurationProperty
	public void setA(double a) {
		this.a.set(a);
	}

	@ConfigurationProperty
	public void setB(double b) {
		this.b.set(b);
	}

	@ConfigurationProperty
	public void setC(double c) {
		this.c.set(c);
	}

	public double getX() {
		return x.get();
	}

	public double getY() {
		return y.get();
	}

	public double getZ() {
		return z.get();
	}

	public double getA() {
		return a.get();
	}

	public double getB() {
		return b.get();
	}

	public double getC() {
		return c.get();
	}

	@Override
	public final MutableTransformation getRelativePosition() {
		return relativePosition.get();
	}

	@Override
	public double getSimulationHz() {
		return 1;
	}

	@Override
	public void simulateStep(Long time) {
	}

}
