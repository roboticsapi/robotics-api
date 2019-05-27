/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.tool.gripper;

import java.util.List;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.entity.AbstractComposedEntity;
import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.core.exception.EntityException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.StaticConnection;
import org.roboticsapi.world.Transformation;

/**
 * Abstract implementation for {@link BaseJaw}s.
 */
public abstract class AbstractBaseJaw extends AbstractComposedEntity implements BaseJaw {

	private Frame baseFrame;
	private final Frame mountFrame;

	private StaticConnection baseMountConnection;

	/**
	 * Constructor.
	 *
	 * @param mountFrame the base jaw's mount frame.
	 */
	public AbstractBaseJaw(Frame mountFrame) {
		super();
		this.mountFrame = mountFrame;
	}

	@Override
	public final Frame getBase() {
		return this.baseFrame;
	}

	@Override
	@ConfigurationProperty(Optional = false)
	public void setBase(Frame base) {
		immutableWhenInitialized();
		this.baseFrame = base;
	}

	@Override
	public final Frame getMountFrame() {
		return this.mountFrame;
	}

	@Override
	public final GrippingFinger getMountedFinger() {
		Relation mountConnection = findMountRelation();

		if (mountConnection == null) {
			return null;
		}

		Frame frame = mountConnection.getOther(mountFrame);
		ComposedEntity finger = frame.getParent();

		if (finger instanceof GrippingFinger) {
			return (GrippingFinger) finger;
		} else {
			return null;
		}
	}

	@Override
	public final void mount(GrippingFinger finger, Transformation transformation) throws RoboticsException {
		Relation mountConnection = findMountRelation();

		if (mountConnection != null) {
			throw new RoboticsException("Base jaw has already a finger mounted.");
		}

		validateFinger(finger);

		try {
			mountConnection = new StaticConnection(transformation);
			mountFrame.addRelation(mountConnection, finger.getBase());
		} catch (InitializationException e) {
			mountConnection = null;
			throw new RoboticsException("Cannot mount finger to base jaw.", e);
		}
	}

	@Override
	public final GrippingFinger unmount() throws RoboticsException {
		Relation mountRelation = findMountRelation();
		GrippingFinger finger = findMountedFinger(mountRelation);

		if (mountRelation != null) {
			uninitialize(mountRelation);
			mountRelation = null;
		} else {
			throw new RoboticsException("No finger found to unmount.");
		}
		return finger;
	}

	/**
	 * Validates a given {@link GrippingFinger} while mounting this finger to the
	 * base jaw.
	 *
	 * @param finger the finger to validate.
	 * @throws RoboticsException if finger is not valid.
	 *
	 * @see {@link AbstractBaseJaw#mount(GrippingFinger, Transformation)}
	 */
	protected abstract void validateFinger(GrippingFinger finger) throws RoboticsException;

	/**
	 * Finds a {@link Relation} which connects this base jaw (respectively its mount
	 * frame) with a gripping finger.
	 *
	 * @return a {@link Relation} which connects this base jaw with a gripping
	 *         finger.
	 */
	protected final Relation findMountRelation() {
		List<Relation> relations = mountFrame.getRelations();

		for (Relation relation : relations) {
			Frame frame = relation.getOther(mountFrame);
			ComposedEntity finger = frame.getParent();

			if (finger instanceof GrippingFinger) {
				return relation;
			}
		}
		return null;
	}

	/**
	 * Retrieves a {@link GrippingFinger} from a given {@link Relation}.
	 *
	 * @param mountRelation {@link Relation} which connects this base jaw with the
	 *                      gripping finger.
	 * @return the found gripping finger or <code>null</code> if none can be found.
	 */
	protected final GrippingFinger findMountedFinger(Relation mountRelation) {
		if (mountRelation == null) {
			return null;
		}
		Frame frame = mountRelation.getOther(mountFrame);
		ComposedEntity finger = frame.getParent();

		if (finger instanceof GrippingFinger) {
			return (GrippingFinger) finger;
		} else {
			return null;
		}
	}

	@Override
	protected void fillAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		super.fillAutomaticConfigurationProperties(createdObjects);

		if (baseFrame == null) {
			createdObjects.put("base", baseFrame = new Frame(getName() + " Base"));
		}
	}

	@Override
	protected void clearAutomaticConfigurationProperties(Map<String, RoboticsObject> createdObjects) {
		if (createdObjects.containsKey("base")) {
			baseFrame = null;
		}
		super.clearAutomaticConfigurationProperties(createdObjects);
	}

	@Override
	protected void setupEntities() throws EntityException, InitializationException {
		super.setupEntities();

		baseFrame.setParent(this);

		baseMountConnection = new StaticConnection(getBaseMountTransformation());
		baseMountConnection.setParent(this);
		baseFrame.addRelation(baseMountConnection, mountFrame);
	}

	protected abstract Transformation getBaseMountTransformation();

	@Override
	protected void validateBeforeUninitialization() throws InitializationException {
		super.validateBeforeUninitialization();

		Relation mountConnection = findMountRelation();

		if (mountConnection != null) {
			throw new InitializationException("Finger is still mounted to base jaw.");
		}
	}

	@Override
	protected void cleanupEntities() throws EntityException, InitializationException {
		super.cleanupEntities();

		baseMountConnection.setParent(null);
		uninitialize(baseMountConnection);
		baseMountConnection = null;

		baseFrame.setParent(null);
	}

}
