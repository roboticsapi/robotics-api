/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.cartesianmotion.device;

import java.util.Map;

import org.roboticsapi.cartesianmotion.parameter.MotionCenterParameter;
import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.DeviceParameters;
import org.roboticsapi.core.InvalidParametersException;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.actuator.AbstractActuator;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;

public class CartesianActuator extends AbstractActuator<CartesianActuatorDriver> implements CartesianMotionDevice {

	private Frame referenceFrame;
	private Frame movingFrame;
	private Relation relation;

	@Override
	public Frame getReferenceFrame() {
		return referenceFrame;
	}

	@ConfigurationProperty(Optional = false)
	public void setReferenceFrame(Frame referenceFrame) {
		immutableWhenInitialized();
		this.referenceFrame = referenceFrame;
	}

	@Override
	public Frame getDefaultMotionCenter() {
		return movingFrame;
	}

	@Override
	public Frame getMovingFrame() {
		return movingFrame;
	}

	@ConfigurationProperty(Optional = false)
	public void setMovingFrame(Frame movingFrame) {
		immutableWhenInitialized();
		this.movingFrame = movingFrame;
	}

	@Override
	public void validateParameters(DeviceParameters parameters) throws InvalidParametersException {
	}

	@Override
	protected void defineDefaultParameters() throws InvalidParametersException {
		super.defineDefaultParameters();
		addDefaultParameters(new MotionCenterParameter(movingFrame));
	}

	@Override
	protected void defineMaximumParameters() throws InvalidParametersException {
	}

	@Override
	protected void setupEntities() throws EntityException, InitializationException {
		super.setupEntities();

		relation = getDriver().createRelation();
		if (relation != null) {
			getReferenceFrame().addRelation(relation, getMovingFrame());
		}
	}

	@Override
	protected void undoInitializationOfDerivedDevices() throws RoboticsException {
		if (relation != null) {
			getReferenceFrame().removeRelation(relation);
		}
		relation = null;
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();

		checkNotNullAndInitialized("referenceFrame", referenceFrame);
		checkNotNullAndInitialized("movingFrame", movingFrame);
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		super.fillAutomaticConfigurationProperties(createdObjects);
		referenceFrame = fill("referenceFrame", referenceFrame, new Frame(getName() + " ReferenceFrame"),
				createdObjects);

		movingFrame = fill("movingFrame", movingFrame, new Frame(getName() + " MovingFrame"), createdObjects);
	}

	@Override
	protected void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		referenceFrame = clear("referenceFrame", referenceFrame, createdObjects);
		movingFrame = clear("movingFrame", movingFrame, createdObjects);
	}

	@Override
	protected void setupDriver(CartesianActuatorDriver driver) {
		driver.setup(referenceFrame, movingFrame);
	}
}
