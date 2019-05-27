/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.platform;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.world.Connection;
import org.roboticsapi.world.Direction;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Orientation;
import org.roboticsapi.world.Point;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.TransformationSensorConnection;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.sensor.DirectionSensor;
import org.roboticsapi.world.sensor.RotationSensor;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.sensor.VectorSensor;
import org.roboticsapi.world.sensor.VelocityFromComponentsSensor;
import org.roboticsapi.world.sensor.VelocitySensor;

/**
 * Abstract implementation for mobile platforms.
 *
 * @param <PD> the platform's driver
 */
public abstract class AbstractPlatform<PD extends PlatformDriver> extends AbstractActuator<PD> implements Platform {

	/**
	 * The origin frame of the platform (i.e. its intial position).
	 */
	private Frame odometryOrigin;

	/**
	 * The frame moved by the platform (i.e. center of odometry).
	 */
	private Frame odometryFrame;

	/**
	 * The relation connecting the platform's odometry origin with its odometry
	 * frame.
	 */
	private Relation odometryRelation;

	/**
	 * The relation connecting the platform's odometry frame with its base frame.
	 */
	private Connection baseConnection;

	/**
	 * The platform's base frame;
	 */
	private Frame baseFrame;

	/**
	 * The wheels
	 */
	private final Wheel[] wheels;

	/**
	 * The wheel's mount frames
	 */
	private final Frame[] mountFrames;

	/**
	 * The relations connecting the platform's base frame with the mount frames for
	 * wheels.
	 */
	private final Connection[] mountConnections;

	/**
	 * The relations connecting the platform's mount frames with the wheels' base
	 * frames.
	 */
	private final Connection[] wheelConnections;

	/**
	 * Constructor.
	 *
	 * @param wheelCount the number of wheels the platform has.
	 */
	protected AbstractPlatform(int wheelCount) {
		super();
		this.wheels = new Wheel[wheelCount];
		this.mountFrames = new Frame[wheelCount];
		this.mountConnections = new Connection[wheelCount];
		this.wheelConnections = new Connection[wheelCount];
	}

	@Override
	public Frame getOdometryOrigin() {
		return odometryOrigin;
	}

	@ConfigurationProperty
	public void setOdometryOrigin(Frame odometryOrigin) {
		immutableWhenInitialized();
		this.odometryOrigin = odometryOrigin;
	}

	@Override
	public Frame getOdometryFrame() {
		return odometryFrame;
	}

	@ConfigurationProperty
	public void setOdometryFrame(Frame odometryFrame) {
		immutableWhenInitialized();
		this.odometryFrame = odometryFrame;
	}

	@Override
	public Frame getBase() {
		return baseFrame;
	}

	@Override
	@ConfigurationProperty(Optional = false)
	public void setBase(Frame base) {
		immutableWhenInitialized();
		this.baseFrame = base;
	}

	@Override
	public Wheel[] getWheels() {
		return this.wheels;
	}

	@Override
	public Wheel getWheel(int wheelNumber) {
		if (this.wheels != null) {
			return this.wheels[wheelNumber];
		}
		return null;
	}

	@Override
	public Frame[] getMountFrames() {
		return this.mountFrames;
	}

	@Override
	public Frame getMountFrame(int wheelNumber) {
		if (this.mountFrames != null) {
			return this.mountFrames[wheelNumber];
		}
		return null;
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

	@Override
	protected final void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (odometryFrame == null) {
			createdObjects.put("odometryFrame", odometryFrame = new Frame(getName() + " Odometry Frame"));
		}
		if (odometryOrigin == null) {
			createdObjects.put("odometryOrigin", odometryOrigin = new Frame(getName() + " Odometry Origin"));
		}
		if (baseFrame == null) {
			createdObjects.put("baseFrame", baseFrame = new Frame(getName() + " Base Frame"));
		}

		for (int i = 0; i < wheels.length; i++) {
			if (wheels[i] == null) {
				Wheel wheel = createWheel(i);
				wheels[i] = wheel;
				createdObjects.put("wheel[" + i + "]", wheel);
			}

			if (mountFrames[i] == null) {
				mountFrames[i] = createMountFrame(i);
				createdObjects.put("mountFrame[" + i + "]", mountFrames[i]);
			}
		}

		fillMoreAutomaticPlatformProperties(createdObjects);
	}

	/**
	 * Fills automatic configuration properties of derived {@link Platform}s.
	 *
	 * @param createdObjects map with all created objects.
	 *
	 * @see #fillAutomaticConfigurationProperties(Map)
	 */
	protected void fillMoreAutomaticPlatformProperties(Map<String, RoboticsObject> createdObjects) {
		// empty
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
	 * Creates a wheel's mounting {@link Frame} and sets a symbolic name for the
	 * created frame (e.g. 'Mount frame for the left front wheel')
	 *
	 * @param wheelNumber the wheel number
	 * @return the created mount frame.
	 */
	protected abstract Frame createMountFrame(int wheelNumber);

	@Override
	protected final void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		clearAutomaticPlatformProperties(createdObjects);

		if (createdObjects.containsKey("base")) {
			baseFrame = null;
		}
		if (createdObjects.containsKey("odometryFrame")) {
			odometryFrame = null;
		}
		if (createdObjects.containsKey("odometryOrigin")) {
			odometryOrigin = null;
		}
		for (int i = 0; i < wheels.length; i++) {
			if (createdObjects.containsKey("wheel[" + i + "]")) {
				wheels[i] = null;
			}

			if (createdObjects.containsKey("mountFrame[" + i + "]")) {
				mountFrames[i] = null;
			}
		}
	}

	/**
	 * Clears automatic configuration properties of derived {@link Platform}s.
	 *
	 * @param createdObjects map with all created objects.
	 *
	 * @see #clearAutomaticConfigurationProperties(Map)
	 */
	protected void clearAutomaticPlatformProperties(Map<String, RoboticsObject> createdObjects) {
		// empty
	}

	@Override
	protected final void setupEntities() throws EntityException, InitializationException {
		odometryRelation = getDriver().createNewOdometryRelation();
		odometryRelation.setParent(this);
		getOdometryOrigin().addRelation(odometryRelation, odometryFrame);

		baseConnection = createBaseConnection();
		baseConnection.setParent(this);

		odometryFrame.addRelation(baseConnection, baseFrame);
		odometryFrame.setParent(this);

		baseFrame.setParent(this);

		for (int i = 0; i < wheels.length; i++) {
			wheels[i].setParent(this);

			mountConnections[i] = createMountConnection(i);
			mountConnections[i].setParent(this);
			baseFrame.addRelation(mountConnections[i], mountFrames[i]);

			wheelConnections[i] = createWheelConnection(i);
			wheelConnections[i].setParent(this);
			mountFrames[i].addRelation(wheelConnections[i], wheels[i].getBase());
		}

		setupPlatformEntities();
	}

	/**
	 * Method that can be overridden for setting up internal platform entities.
	 *
	 * @throws EntityException         if an {@link EntityException} occurs
	 * @throws InitializationException if an {@link InitializationException} occurs
	 */
	protected void setupPlatformEntities() throws EntityException, InitializationException {
		// empty implementation
	}

	@Override
	protected final void cleanupEntities() throws EntityException, InitializationException {
		cleanupPlatformEntities();

		for (int i = 0; i < wheels.length; i++) {
			wheels[i].setParent(null);

			mountConnections[i].setParent(null);
			mountConnections[i] = uninitialize(mountConnections[i]);

			wheelConnections[i].setParent(null);
			wheelConnections[i] = uninitialize(wheelConnections[i]);
		}
		baseFrame.setParent(null);
		baseConnection = uninitialize(baseConnection);
		odometryRelation = uninitialize(odometryRelation);
	}

	/**
	 * Method that can be overridden for cleaning up internal platform entities.
	 *
	 * @throws EntityException         if an {@link EntityException} occurs
	 * @throws InitializationException if an {@link InitializationException} occurs
	 */
	protected void cleanupPlatformEntities() throws EntityException, InitializationException {
		// empty implementation
	}

	/**
	 * Creates a {@link Connection} from the platform's odometry frame to its base
	 * frame.
	 *
	 * @return the created connection.
	 */
	protected abstract Connection createBaseConnection();

	/**
	 * Creates a {@link Connection} from the platform's base frame to the mount
	 * frame of the specified wheel.
	 *
	 * @param wheelNumber the wheel number
	 * @return the created connection.
	 */
	protected abstract Connection createMountConnection(int wheelNumber);

	/**
	 * Creates a {@link Connection} from a mount frame to the base frame of the
	 * specified wheel.
	 *
	 * @param wheelNumber the wheel number
	 * @return the created connection.
	 */
	protected Connection createWheelConnection(int wheelNumber) {
		DoubleSensor pSensor = getDriver().getWheelPositionSensor(wheelNumber);
		DoubleSensor vSensor = getDriver().getWheelVelocitySensor(wheelNumber);
		DoubleSensor cSensor = DoubleSensor.fromValue(0);

		TransformationSensor sensor = TransformationSensor.fromComponents(VectorSensor.fromConstant(new Vector()),
				RotationSensor.fromABC(pSensor, cSensor, cSensor));

		Frame movingFrame = this.wheels[wheelNumber].getBase();
		Frame referenceFrame = this.mountFrames[wheelNumber];
		Point pivotPoint = referenceFrame.getPoint();
		Orientation orientation = referenceFrame.getOrientation();

		DirectionSensor translationVelocitySensor = DirectionSensor
				.fromConstant(new Direction(orientation, new Vector()));
		VectorSensor vectorSensor = VectorSensor.fromComponents(cSensor, cSensor, vSensor);
		DirectionSensor rotationVelocitySensor = new DirectionSensor(vectorSensor, orientation);

		VelocitySensor velSensor = new VelocityFromComponentsSensor(translationVelocitySensor, rotationVelocitySensor,
				movingFrame, referenceFrame, pivotPoint);

		return new TransformationSensorConnection(sensor, velSensor);
	}

	@Override
	protected final void setupDriver(PD driver) {
		driver.setup(odometryOrigin, odometryFrame);
	}

}
