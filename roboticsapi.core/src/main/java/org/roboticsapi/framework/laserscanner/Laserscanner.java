/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.laserscanner;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.DeviceDriver;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDoubleArray;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.PhysicalObject;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.device.AbstractPhysicalDevice;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.relation.StaticPosition;

public abstract class Laserscanner extends AbstractPhysicalDevice<DeviceDriver> implements PhysicalObject {

	private final Dependency<Frame> scanFrame;

	public Laserscanner() {
		scanFrame = createDependency("scanFrame", new Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Scan Frame");
			}
		});
		createDependency("base2scanFrameRelation", new Builder<StaticConnection>() {
			@Override
			public StaticConnection create() {
				return new StaticConnection(getBase(), scanFrame.get());
			}
		});
		createDependency("base2scanFramePosition", new Builder<StaticPosition>() {
			@Override
			public StaticPosition create() {
				return new StaticPosition(getBase(), scanFrame.get(), getBaseFrameScanFrameTransformation());
			}
		});
	}

	public abstract int getPointCount();

	public abstract double getStartAngle();

	public abstract double getEndAngle();

	public final RealtimeDoubleArray getRanges() {
		return use(LaserscannerInterface.class).getRanges();
	}

	public final Double[] getValue() throws RealtimeValueReadException {
		return getRanges().getCurrentValue();
	}

	public final Frame getScanFrame() {
		return scanFrame.get();
	}

	@ConfigurationProperty(Optional = true)
	public final void setScanFrame(Frame scanFrame) {
		this.scanFrame.set(scanFrame);
	}

	/**
	 * Returns the transformations from the base to the scan frame.
	 *
	 * @return the transformations from the base to the scan frame.
	 */
	protected abstract Transformation getBaseFrameScanFrameTransformation();

}
