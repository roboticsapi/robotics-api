/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import java.util.List;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.Dependency.Builder;
import org.roboticsapi.core.RoboticsEntity;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.util.RoboticsEntityUtils;
import org.roboticsapi.core.world.AbstractPhysicalObject;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.relation.StaticConnection;
import org.roboticsapi.core.world.relation.StaticPosition;

/**
 * Abstract implementation for {@link BaseJaw}s.
 */
public abstract class AbstractBaseJaw extends AbstractPhysicalObject implements BaseJaw {

	private final Dependency<Frame> mountFrame;

	public AbstractBaseJaw() {
		this.mountFrame = createDependency("mountFrame", new Frame(getName() + " MountFrame"));

		createDependency("baseMountConnection", new Builder<StaticConnection>() {
			@Override
			public StaticConnection create() {
				return new StaticConnection(getBase(), AbstractBaseJaw.this.mountFrame.get());
			}
		});
		createDependency("baseMountPosition", new Builder<StaticPosition>() {
			@Override
			public StaticPosition create() {
				return new StaticPosition(getBase(), AbstractBaseJaw.this.mountFrame.get(),
						getBaseMountTransformation());
			}
		});
	}

	@Override
	public final GrippingFinger getMountedFinger() {
		Relation mountConnection = findMountRelation();
		if (mountConnection == null) {
			return null;
		}
		return findMountedFinger(mountConnection);
	}

	@Override
	public final void mount(GrippingFinger finger, Transformation transformation) throws RoboticsException {
		Relation mountConnection = findMountRelation();

		if (mountConnection != null) {
			throw new RoboticsException("Base jaw has already a finger mounted.");
		}

		validateFinger(finger);

		try {
			mountConnection = new StaticConnection(mountFrame.get(), finger.getBase());
			mountConnection.establish();
			new StaticPosition(mountFrame.get(), finger.getBase(), transformation).establish();
		} catch (IllegalArgumentException e) {
			mountConnection = null;
			throw new RoboticsException("Cannot mount finger to base jaw.", e);
		}
	}

	@Override
	public final GrippingFinger unmount() throws RoboticsException {
		Relation mountRelation = findMountRelation();
		GrippingFinger finger = findMountedFinger(mountRelation);

		if (mountRelation != null) {
			mountRelation.remove();
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
		List<Relation> relations = mountFrame.get().getRelations(World.getCommandedTopology());

		for (Relation relation : relations) {
			if (findMountedFinger(relation) != null) {
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
		Frame frame = mountRelation.getOther(mountFrame.get());
		RoboticsEntity finger = RoboticsEntityUtils.getParent(frame);

		if (finger instanceof GrippingFinger) {
			return (GrippingFinger) finger;
		} else {
			return null;
		}
	}

	@Override
	public abstract Transformation getBaseMountTransformation();

}
