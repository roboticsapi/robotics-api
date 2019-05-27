/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.sensor.DoubleSensor;
import org.roboticsapi.core.sensor.SensorReadException;
import org.roboticsapi.tool.gripper.parameters.AccelerationParameter;
import org.roboticsapi.tool.gripper.parameters.ForceParameter;
import org.roboticsapi.tool.gripper.parameters.VelocityParameter;
import org.roboticsapi.world.DynamicConnection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.TransformationSensorConnection;
import org.roboticsapi.world.sensor.TransformationSensor;

/**
 * Abstract implementation for {@link ParallelGripper}s.
 * <p>
 * All methods for skilled grippers are already implemented. Subclasses only
 * need to implement the corresponding interfaces and override methods for
 * retrieving the sensor if one is available.
 *
 * @param <GD> The gripper's driver
 *
 * @see VelocityConsideringGripper
 * @see ForceConsideringGripper
 * @see AccelerationConsideringGripper
 */
public abstract class AbstractParallelGripper<GD extends GripperDriver> extends AbstractGripper<GD>
		implements ParallelGripper {

	private static final double NEG_90DEG_IN_RAD = Math.toRadians(-90);
	private static final double POS_90DEG_IN_RAD = Math.toRadians(+90);

	private final BaseJaw[] baseJaws;

	private Frame fingerCenter, contactCenter;
	private final Frame[] mountFrames;

	private StaticConnection base2CenterConnection;
	private final DynamicConnection[] center2BaseJawConnections;

	private final double minimalBaseJawDistance;
	private final double strokePerFinger;

	private final double minimumVelocity;
	private final double maximumVelocity;

	private final double minimumForce;
	private final double maximumForce;

	private final double minimumAcceleration;
	private final double maximumAcceleration;

	public AbstractParallelGripper(final double recommendedWorkpieceWeight, final double minimalBaseJawDistance,
			final double strokePerFinger, final double minimumVelocity, final double maximumVelocity,
			final double minimumForce, final double maximumForce, final double minimumAcceleration,
			final double maximumAcceleration) {
		super(2, recommendedWorkpieceWeight); // parallel grippers should have
		// two baseJaws...
		baseJaws = new BaseJaw[2];
		mountFrames = new Frame[2];
		center2BaseJawConnections = new DynamicConnection[2];

		this.minimalBaseJawDistance = minimalBaseJawDistance;
		this.strokePerFinger = strokePerFinger;

		this.minimumVelocity = minimumVelocity;
		this.maximumVelocity = maximumVelocity;

		this.minimumForce = minimumForce;
		this.maximumForce = maximumForce;

		this.minimumAcceleration = minimumAcceleration;
		this.maximumAcceleration = maximumAcceleration;
	}

	public AbstractParallelGripper(final double recommendedWorkpieceWeight, final double minimalBaseJawDistance,
			final double strokePerFinger, final double minimumVelocity, final double maximumVelocity,
			final double minimumForce, final double maximumForce) {
		this(recommendedWorkpieceWeight, minimalBaseJawDistance, strokePerFinger, minimumVelocity, maximumVelocity,
				minimumForce, maximumForce, 0d, 0d);
	}

	@Override
	public final BaseJaw getBaseJaw(int index) {
		if (baseJaws == null || index < 0 || index >= baseJaws.length) {
			return null;
		}
		return baseJaws[index];
	}

	@Override
	public final GrippingFinger getFinger(int index) {
		BaseJaw baseJaw = getBaseJaw(index);
		return getFingerFrom(baseJaw);
	}

	@Override
	public BaseJaw[] getBaseJaws() {
		return this.baseJaws;
	}

	@Override
	public GrippingFinger[] getFingers() {
		GrippingFinger[] fingers = new GrippingFinger[baseJaws.length];

		for (int i = 0; i < fingers.length; i++) {
			fingers[i] = getFingerFrom(baseJaws[i]);
		}
		return fingers;
	}

	@Override
	public Frame getFingerCenter() {
		return this.fingerCenter;
	}

	@Override
	public Frame getContactFrame() {
		return this.contactCenter;
	}

	@ConfigurationProperty(Optional = true)
	public void setContactFrame(Frame contactCenter) {
		immutableWhenInitialized();
		this.contactCenter = contactCenter;
	}

	public Frame getMountFrame(int index) {
		return mountFrames[index];
	}

	@ConfigurationProperty(Optional = true)
	public void setMountFrame(int index, Frame mountFrame) {
		immutableWhenInitialized();
		this.mountFrames[index] = mountFrame;
	}

	@Override
	public final double getStrokePerFinger() {
		return strokePerFinger;
	}

	@Override
	public final double getMinimalOpeningWidth() throws RoboticsException {
		double minFingerDistance = getOpeningWidthFrom(getMinimumBaseJawDistance());

		return minFingerDistance < 0 ? 0 : minFingerDistance;
	}

	@Override
	public final double getMaximalOpeningWidth() throws RoboticsException {
		double maxBaseJawOpeningWidth = getMaximumBaseJawDistance();
		double maxFingerDistance = getOpeningWidthFrom(maxBaseJawOpeningWidth);

		return maxFingerDistance;
	}

	@Override
	public final DoubleSensor getOpeningWidthSensor() {
		DoubleSensor baseJawOpeningWidthSensor = getBaseJawOpeningWidthSensor();

		if (baseJawOpeningWidthSensor == null) {
			return null;
		}
		double offset = getAccumulatedFingerOffsets();
		return baseJawOpeningWidthSensor.add(offset);
	}

	@Override
	public final double getCurrentOpeningWidth() throws RoboticsException {
		DoubleSensor distanceSensor = getOpeningWidthSensor();

		if (distanceSensor == null) {
			throw new RoboticsException("No sensor available for measuring the finger distance.");
		}
		return distanceSensor.getCurrentValue();
	}

	@Override
	public final double getCurrentBaseJawOpeningWidth() throws RoboticsException {
		DoubleSensor distanceSensor = getBaseJawOpeningWidthSensor();

		if (distanceSensor == null) {
			throw new RoboticsException("No sensor available for measuring the base jaw distance.");
		}
		return distanceSensor.getCurrentValue();
	}

	@Override
	public final double getBaseJawOpeningWidthFrom(double openingWidth) throws TransformationException {
		return openingWidth - getAccumulatedFingerOffsets();
	}

	@Override
	public final double getOpeningWidthFrom(double baseJawOpeningWidth) throws TransformationException {
		return baseJawOpeningWidth + getAccumulatedFingerOffsets();
	}

	/**
	 * Returns the gripper's min. permitted velocity in [m/s].
	 *
	 * @return the min. permitted velocity in [m/s].
	 * @see VelocityConsideringGripper#getMinimumVelocity()
	 */
	public final double getMinimumVelocity() {
		return this.minimumVelocity;
	}

	/**
	 * Returns the gripper's max. permitted velocity in [m/s].
	 *
	 * @return the max. permitted velocity in [m/s].
	 * @see VelocityConsideringGripper#getMaximumVelocity()
	 */
	public final double getMaximumVelocity() {
		return this.maximumVelocity;
	}

	/**
	 * Returns the gripper's current velocity in [m/s].
	 *
	 * @return the current velocity in [m/s].
	 * @throws SensorReadException if a force sensor is not available or the current
	 *                             value cannot be retrieved.
	 * @see VelocityConsideringGripper#getCurrentVelocity()
	 */
	public final double getCurrentVelocity() throws SensorReadException {
		DoubleSensor sensor = getVelocitySensor();

		if (sensor == null) {
			throw new SensorReadException("Velocity sensor is not available!");
		}
		return sensor.getCurrentValue();
	}

	/**
	 * Empty implementation returning <code>null</code>. Subclasses can return a
	 * sensor for the gripper's velocity.
	 *
	 * @return a sensor for velocity. It might be <code>null</code> if sensor is not
	 *         available.
	 * @see VelocityConsideringGripper#getVelocitySensor()
	 */
	public DoubleSensor getVelocitySensor() {
		// empty implementation
		return null;
	}

	/**
	 * Returns the gripper's min. permitted force in [N].
	 *
	 * @return the min. permitted force in [N].
	 * @see ForceConsideringGripper#getMinimumForce()
	 */
	public final double getMinimumForce() {
		return this.minimumForce;
	}

	/**
	 * Returns the gripper's max. permitted force in [N].
	 *
	 * @return the max. permitted force in [N].
	 * @see ForceConsideringGripper#getMaximumForce()
	 */
	public final double getMaximumForce() {
		return this.maximumForce;
	}

	/**
	 * Returns the gripper's current force in [N].
	 *
	 * @return the current force in [N].
	 * @throws SensorReadException if a force sensor is not available or the current
	 *                             value cannot be retrieved.
	 * @see ForceConsideringGripper#getCurrentForce()
	 */
	public final double getCurrentForce() throws SensorReadException {
		DoubleSensor sensor = getForceSensor();

		if (sensor == null) {
			throw new SensorReadException("Force sensor is not available!");
		}
		return sensor.getCurrentValue();
	}

	/**
	 * Empty implementation returning <code>null</code>. Subclasses can return a
	 * sensor for the gripper's force in [N].
	 *
	 * @return a sensor for force. It might be <code>null</code> if sensor is not
	 *         available.
	 * @see ForceConsideringGripper#getForceSensor()
	 */
	public DoubleSensor getForceSensor() {
		// empty implementation
		return null;
	}

	/**
	 * Returns the gripper's min. permitted acceleration in [m/s^2].
	 *
	 * @return the min. permitted acceleration in [m/s^2].
	 * @see AccelerationConsideringGripper#getMinimumAcceleration()
	 */
	public final double getMinimumAcceleration() {
		return this.minimumAcceleration;
	}

	/**
	 * Returns the gripper's max. permitted acceleration in [m/s^2].
	 *
	 * @return the max. permitted acceleration in [m/s^2].
	 * @see AccelerationConsideringGripper#getMaximumAcceleration()
	 */
	public final double getMaximumAcceleration() {
		return this.maximumAcceleration;
	}

	/**
	 * Returns the gripper's current acceleration in [m/s^2].
	 *
	 * @return the current acceleration in [m/s^2].
	 * @throws SensorReadException if an acceleration sensor is not available or the
	 *                             current value cannot be retrieved.
	 * @see AccelerationConsideringGripper#getCurrentAcceleration()
	 */
	public final double getCurrentAcceleration() throws SensorReadException {
		DoubleSensor sensor = getAccelerationSensor();

		if (sensor == null) {
			throw new SensorReadException("Acceleration sensor is not available!");
		}
		return sensor.getCurrentValue();
	}

	/**
	 * Empty implementation returning <code>null</code>. Subclasses can return a
	 * sensor for the gripper's acceleration.
	 *
	 * @return a sensor for acceleration. It might be <code>null</code> if sensor is
	 *         not available.
	 * @see AccelerationConsideringGripper#getAccelerationSensor()
	 */
	public DoubleSensor getAccelerationSensor() {
		// empty implementation
		return null;
	}

	@Override
	public final void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
		if (parameters instanceof VelocityParameter) {
			VelocityParameter vParameter = (VelocityParameter) parameters;

			checkValueRange(vParameter.getVelocity(), minimumVelocity, maximumVelocity, "velocity");
		} else if (parameters instanceof ForceParameter) {
			ForceParameter fParameter = (ForceParameter) parameters;

			checkValueRange(fParameter.getForce(), minimumForce, maximumForce, "force");
		} else if (parameters instanceof AccelerationParameter) {
			AccelerationParameter aParameter = (AccelerationParameter) parameters;
			checkValueRange(aParameter.getAcceleration(), minimumAcceleration, maximumAcceleration, "acceleration");
		}

		validateMoreParameters(parameters);
	}

	/**
	 * Empty implementation. Subclasses can override to validate further parameters.
	 * Velocity, force and acceleration are already validated by default.
	 *
	 * @param parameters device parameters to validate
	 * @throws InvalidParametersException if the parameters are not valid
	 */
	protected void validateMoreParameters(DeviceParameters parameters) throws InvalidParametersException {
		// empty implementation
	}

	@Override
	protected final void defineMaximumParameters() throws InvalidParametersException {
		// Set maximum values
		if (maximumVelocity != 0d) {
			addMaximumParameters(new VelocityParameter(maximumVelocity));
		}

		if (maximumForce != 0d) {
			addMaximumParameters(new ForceParameter(maximumForce));
		}

		if (maximumAcceleration != 0d) {
			addMaximumParameters(new AccelerationParameter(maximumAcceleration));
		}
		defineMoreMaximumParameters();
	}

	protected void defineMoreMaximumParameters() throws InvalidParametersException {
		// empty implementation
	}

	/**
	 * Checks whether a value is within a certain range. If not, a
	 * {@link InvalidParametersException} is thrown.
	 *
	 * @param value        the value to check
	 * @param min          the allowed maximum value
	 * @param max          the allowed minimum value
	 * @param propertyName the parameter's name
	 *
	 * @throws InvalidParametersException
	 */
	protected final void checkValueRange(double value, double min, double max, String propertyName)
			throws InvalidParametersException {
		if (value < min || value > max) {
			throw new InvalidParametersException(
					"Only values in the range [" + min + "," + max + "] are permitted for " + propertyName + ".");
		}
	}

	@Override
	protected final void fillAutomaticToolProperties(Map<String, RoboticsObject> createdObjects) {
		for (int i = 0; i < mountFrames.length; i++) {
			if (mountFrames[i] == null || !mountFrames[i].isInitialized()) {
				mountFrames[i] = new Frame(getName() + " MountFrame " + i);
				createdObjects.put("mountFrame[" + i + "]", mountFrames[i]);
			}
		}

		fillAutomaticParallelGripperProperties(createdObjects);
	}

	protected void fillAutomaticParallelGripperProperties(Map<String, RoboticsObject> createdObjects) {
		// empty
	}

	@Override
	protected final void clearAutomaticToolProperties(Map<String, RoboticsObject> createdObjects) {
		clearAutomaticParallelGripperProperties(createdObjects);

		for (int i = 0; i < mountFrames.length; i++) {
			if (createdObjects.containsKey("mountFrames[" + i + "]")) {
				mountFrames[i] = null;
			}
		}
	}

	protected void clearAutomaticParallelGripperProperties(Map<String, RoboticsObject> createdObjects) {
		// empty
	}

	@Override
	protected final void setupToolEntities() throws EntityException, InitializationException {
		fingerCenter = new Frame(getName() + " Center");
		setup(fingerCenter);

		if (contactCenter == null) {
			contactCenter = fingerCenter;
		}

		base2CenterConnection = new StaticConnection(createBase2CenterTransformation());
		getBase().addRelation(base2CenterConnection, fingerCenter);
		setup(base2CenterConnection);

		for (int i = 0; i < baseJaws.length; i++) {
			baseJaws[i] = createBaseJaw(i, mountFrames[i]);
			baseJaws[i].setName(getName() + " BaseJaw " + i);
			initialize(baseJaws[i]);
			baseJaws[i].setParent(this);

			center2BaseJawConnections[i] = createCenter2BaseJawConnection(i);

			if (center2BaseJawConnections[i] == null) {
				throw new InitializationException(
						"No dynamic connections between gripper and base jaws can be created.");
			}
			fingerCenter.addRelation(center2BaseJawConnections[i], baseJaws[i].getBase());
			setup(center2BaseJawConnections[i]);
		}

		setupParallelGripperEntities();
	}

	/**
	 * Method that can be overridden for setting up internal gripper entities (e.g.
	 * Frames, Relations).
	 *
	 * @param context the initialization context
	 * @throws EntityException         if an {@link EntityException} occurs
	 * @throws InitializationException if an {@link InitializationException} occurs
	 */
	protected void setupParallelGripperEntities() throws EntityException, InitializationException {
		// empty
	}

	@Override
	protected final void cleanupToolEntities() throws EntityException, InitializationException {
		cleanupParallelGripperEntities();

		for (int i = 0; i < baseJaws.length; i++) {
			cleanup(center2BaseJawConnections[i]);
			center2BaseJawConnections[i] = null;

			baseJaws[i].setParent(null);
			uninitialize(baseJaws[i]);
			baseJaws[i] = null;
		}
		cleanup(base2CenterConnection);

		if (contactCenter == fingerCenter) {
			contactCenter = null;
		}
		cleanup(fingerCenter);
	}

	/**
	 * Method that can be overridden for cleaning up internal gripper entities (e.g.
	 * Frames, Relations).
	 *
	 * @throws EntityException         if an {@link EntityException} occurs
	 * @throws InitializationException if an {@link InitializationException} occurs
	 */
	protected void cleanupParallelGripperEntities() throws EntityException, InitializationException {
		// empty
	}

	protected abstract Transformation createBase2CenterTransformation();

	protected abstract BaseJaw createBaseJaw(int index, Frame mountFrame);

	/**
	 * Creates a {@link DynamicConnection} from the gripper's center frame to the
	 * base frame of the specified base jaw. The default implementation uses the
	 * base jaw opening width sensor and assumes symmetrically moving base jaws.
	 *
	 * @param index the index of the base jaw
	 * @return the dynamic connection.
	 *
	 * @see #getBaseJawOpeningWidthSensor()
	 */
	protected DynamicConnection createCenter2BaseJawConnection(int index) {
		DoubleSensor baseJawSensor = getBaseJawOpeningWidthSensor();

		if (baseJawSensor == null) {
			return null;
		}
		DoubleSensor offsetSensor = baseJawSensor.divide(2d);

		switch (index) {
		case 0:
			TransformationSensor sensor = createTransformationSensor(offsetSensor.negate(), NEG_90DEG_IN_RAD);
			return new TransformationSensorConnection(sensor, null);
		case 1:
			sensor = createTransformationSensor(offsetSensor, POS_90DEG_IN_RAD);
			return new TransformationSensorConnection(sensor, null);
		default:
			return null;
		}
	}

	private final TransformationSensor createTransformationSensor(DoubleSensor ySensor, double aValue) {
		return TransformationSensor.fromComponents(DoubleSensor.fromValue(0d), ySensor, DoubleSensor.fromValue(0d),
				DoubleSensor.fromValue(aValue), DoubleSensor.fromValue(0d), DoubleSensor.fromValue(0d));
	}

	protected final double getMinimumBaseJawDistance() {
		return this.minimalBaseJawDistance;
	}

	protected final double getMaximumBaseJawDistance() {
		return minimalBaseJawDistance + 2 * strokePerFinger;
	}

	protected final double getAccumulatedFingerOffsets() {
		double offset = 0;

		for (BaseJaw baseJaw : getBaseJaws()) {
			offset += getFingerOffset(baseJaw);
		}
		return offset;
	}

	/**
	 * Gets the offset between a given base jaw and its mounted gripping finger. A
	 * positive finger offset increases the finger distance compared to the base
	 * jaws; a negative finger offset decreases the finger distance.
	 *
	 * @param baseJaw the base jaw
	 * @return the offset between base jaw and gripping finger in [m] or 0.0 if the
	 *         offset cannot be retrieved.
	 */
	protected final double getFingerOffset(BaseJaw baseJaw) {
		GrippingFinger finger = baseJaw.getMountedFinger();

		if (finger == null) {
			return 0d;
		}
		return finger.getOffset();
	}

	private GrippingFinger getFingerFrom(BaseJaw baseJaw) {
		if (baseJaw == null) {
			return null;
		}
		return baseJaw.getMountedFinger();
	}

}
