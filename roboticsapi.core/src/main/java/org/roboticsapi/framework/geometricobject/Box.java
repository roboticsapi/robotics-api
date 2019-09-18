/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.geometricobject;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.world.AbstractPhysicalObject;

public class Box extends AbstractPhysicalObject {
	private final Dependency<Double> xExtent = createDependency("xExtent");
	private final Dependency<Double> yExtent = createDependency("yExtent");
	private final Dependency<Double> zExtent = createDependency("zExtent");

	public double getXExtent() {
		return xExtent.get();
	}

	@ConfigurationProperty
	public void setXExtent(double xExtent) {
		this.xExtent.set(xExtent);
	}

	public double getYExtent() {
		return yExtent.get();
	}

	@ConfigurationProperty
	public void setYExtent(double yExtent) {
		this.yExtent.set(yExtent);
	}

	public double getZExtent() {
		return zExtent.get();
	}

	@ConfigurationProperty
	public void setZExtent(double zExtent) {
		this.zExtent.set(zExtent);
	}

}
