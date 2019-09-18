/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.platform;

import java.sql.Connection;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.actuator.AbstractPhysicalActuator;
import org.roboticsapi.core.world.relation.DynamicConnection;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.relation.StaticPosition;

/**
 * Abstract implementation for mobile platforms.
 *
 * @param <PD> the platform's driver
 */
public abstract class AbstractPlatform<PD extends PlatformDriver> extends AbstractPhysicalActuator<PD>
		implements Platform {

	/**
	 * The origin frame of the platform (i.e. its intial position).
	 */
	private final Dependency<Frame> odometryOrigin;

	/**
	 * The frame moved by the platform (i.e. center of odometry).
	 */
	private final Dependency<Frame> odometryFrame;

	/**
	 * The wheels
	 */
	private final MultiDependency<Wheel> wheels;

	/**
	 * The wheel's mount frames
	 */
	private final MultiDependency<Frame> mountFrames;

	/**
	 * Constructor.
	 *
	 * @param wheelCount the number of wheels the platform has.
	 */
	protected AbstractPlatform(int wheelCount) {
		odometryOrigin = createDependency("odometryOrigin", new Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Odometry Origin");
			}
		});
		odometryFrame = createDependency("odometryFrame", new Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Odometry Frame");
			};
		});

		// The relation connecting the platform's odometry frame with its base
		// frame.
		createDependency("baseConnection", new Dependency.Builder<Relation>() {
			@Override
			public Relation create() {
				return new StaticConnection(odometryFrame.get(), getBase());
			}
		});
		createDependency("basePosition", new Dependency.Builder<Relation>() {
			@Override
			public Relation create() {
				return new StaticPosition(odometryFrame.get(), getBase(), getBaseTransformation());
			}
		});

		this.wheels = createMultiDependency("wheel", wheelCount, new MultiDependency.Builder<Wheel>() {
			@Override
			public Wheel create(int index) {
				return createWheel(index);
			}
		});
		this.mountFrames = createMultiDependency("mountFrame", wheelCount, new MultiDependency.Builder<Frame>() {
			@Override
			public Frame create(int index) {
				return new Frame(getName() + " - " + createMountName(index));
			}
		});
		createMultiDependency("mountWheelConnection", wheelCount, new MultiDependency.Builder<DynamicConnection>() {
			@Override
			public DynamicConnection create(int index) {
				return new WheelConnection(getMountFrame(index), getWheel(index).getBase());
			}
		});
		// The relations connecting the platform's base frame with the mount
		// frames for wheels.
		createMultiDependency("mountPosition", wheelCount, new MultiDependency.Builder<Relation>() {
			@Override
			public Relation create(int index) {
				return new StaticPosition(getBase(), mountFrames.get(index), getMountTransformation(index));
			}
		});
		createMultiDependency("mountConnection", wheelCount, new MultiDependency.Builder<Relation>() {
			@Override
			public Relation create(int index) {
				return new StaticConnection(getBase(), mountFrames.get(index));
			}
		});
	}

	@Override
	public Frame getOdometryOrigin() {
		return odometryOrigin.get();
	}

	@Override
	public Frame getOdometryFrame() {
		return odometryFrame.get();
	}

	@Override
	public Wheel[] getWheels() {
		return wheels.getAll().toArray(new Wheel[0]);
	}

	@Override
	public Wheel getWheel(int wheelNumber) {
		return wheels.get(wheelNumber);
	}

	@Override
	public Frame[] getMountFrames() {
		return mountFrames.getAll().toArray(new Frame[0]);
	}

	@Override
	public Frame getMountFrame(int wheelNumber) {
		return mountFrames.get(wheelNumber);
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
		// FIXME: move MotionCenterParameter somewhere it belongs
		// if (parameters instanceof MotionCenterParameter) {
		// try {
		// if (((MotionCenterParameter) parameters).getMotionCenter()
		// .getTransformationTo(getOdometryFrame(), false) == null) {
		// throw new InvalidParametersException(
		// "Motion center has to be connected to the odometry frame.");
		// }
		// } catch (RoboticsException e) {
		// throw new InvalidParametersException(e.getMessage());
		// }
		// }
	}

	/**
	 * Creates a wheel for the given wheel number and sets a symbolic name (e.g.
	 * 'Left front wheel').
	 *
	 * @param wheelNumber the wheel number
	 * @return the created wheel
	 */
	protected abstract Wheel createWheel(int wheelNumber);

	/**
	 * Creates a wheel's symbolic mounting name (e.g. 'Mount frame for the left
	 * front wheel')
	 *
	 * @param wheelNumber the wheel number
	 * @return the created name.
	 */
	protected abstract String createMountName(int wheelNumber);

	/**
	 * Creates a {@link Connection} from the platform's odometry frame to its base
	 * frame.
	 *
	 * @return the created connection.
	 */
	protected abstract Transformation getBaseTransformation();

	/**
	 * Creates a {@link Connection} from the platform's base frame to the mount
	 * frame of the specified wheel.
	 *
	 * @param wheelNumber the wheel number
	 * @return the created connection.
	 */
	protected abstract Transformation getMountTransformation(int wheelNumber);

}
