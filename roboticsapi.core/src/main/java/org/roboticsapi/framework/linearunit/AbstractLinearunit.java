/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.linearunit;

import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.core.MultiDependency.Builder;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.relation.ConfiguredStaticConnection;
import org.roboticsapi.framework.multijoint.AbstractMultiJointDevice;
import org.roboticsapi.framework.multijoint.Joint;
import org.roboticsapi.framework.multijoint.PrismaticJoint;
import org.roboticsapi.framework.multijoint.link.Link;

public abstract class AbstractLinearunit extends AbstractMultiJointDevice<Joint, LinearunitDriver>
		implements Linearunit {

	protected abstract LinearunitSlider createLinearunitSlider(int index);

	protected abstract PrismaticJoint<?> createLinearunitJoint(int index);

	protected abstract Transformation createLinearunitBaseToSliderBaseTransformation(int index);

	public AbstractLinearunit() {
		MultiDependency<ConfiguredStaticConnection> linearunitConnections = createMultiDependency(
				"linearunitConnections", new Builder<ConfiguredStaticConnection>() {
					@Override
					public ConfiguredStaticConnection create(int index) {
						return new ConfiguredStaticConnection(getBase(), getSlider(index).getBase(),
								createLinearunitBaseToSliderBaseTransformation(index));
					}
				});
		onJointCountChange((o, n) -> {
			linearunitConnections.set(n);
		});
	}

	public AbstractLinearunit(int sliderCount) {
		this();
		setSliderCount(sliderCount);
	}

	public final void setSliderCount(int sliderCount) {
		setJointCount(sliderCount);
		setLinkCount(sliderCount);
	}

	@Override
	public final int getSliderCount() {
		return getJointCount();
	}

	@Override
	public LinearunitSlider[] getSliders() {
		Link[] links = getLinks();
		LinearunitSlider[] result = new LinearunitSlider[links.length];
		for (int index = 0; index < links.length; index++) {
			result[index] = (LinearunitSlider) links[index];
		}
		return result;
	}

	@Override
	public final LinearunitSlider getSlider(int index) {
		return getSliders()[index];
	}

	@Override
	protected final Link createLink(int index) {
		return createLinearunitSlider(index);
	}

	@Override
	protected final Joint createJoint(int number, String name) {
		return createLinearunitJoint(number);
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
		// TODO Auto-generated method stub

	}

	@Override
	protected void defineMaximumParameters() throws InvalidParametersException {
		// TODO Auto-generated method stub

	}

}
