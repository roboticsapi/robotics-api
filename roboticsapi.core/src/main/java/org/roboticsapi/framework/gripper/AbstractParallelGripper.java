/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gripper;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.MultiDependency;
import org.roboticsapi.core.MultiDependency.Builder;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.relation.ConfiguredStaticConnection;
import org.roboticsapi.core.world.relation.PrismaticConnection;

/**
 * Abstract implementation for {@link ParallelGripper}s.
 *
 * @param <GD> The gripper's driver
 */
public abstract class AbstractParallelGripper<GD extends GripperDriver> extends AbstractGripper<GD>
		implements ParallelGripper {

	private final Dependency<Frame> effectorFrame;
	private final Dependency<Transformation> baseEffectorTransformation;

	private final MultiDependency<BaseJaw> baseJaws;
	private final MultiDependency<Frame> baseJawOrigins;

	private final double minimalBaseJawDistance;
	private final double strokePerFinger;

	public AbstractParallelGripper(final double recommendedWorkpieceWeight, final double minimalBaseJawDistance,
			final double strokePerFinger) {
		super(2, recommendedWorkpieceWeight);

		this.minimalBaseJawDistance = minimalBaseJawDistance;
		this.strokePerFinger = strokePerFinger;

		effectorFrame = createDependency("effectorFrame", new Dependency.Builder<Frame>() {
			@Override
			public Frame create() {
				return new Frame(getName() + " Effector Frame");
			}
		});
		baseEffectorTransformation = createDependency("baseEffectorTransformation",
				() -> createBaseToEffectorTransformation());
		createDependency("baseEffectorConnection", new Dependency.Builder<ConfiguredStaticConnection>() {
			@Override
			public ConfiguredStaticConnection create() {
				return new ConfiguredStaticConnection(getBase(), effectorFrame.get(), baseEffectorTransformation.get());
			}
		});

		baseJaws = createMultiDependency("baseJaw", 2, new MultiDependency.Builder<BaseJaw>() {
			@Override
			public BaseJaw create(int index) {
				BaseJaw baseJaw = createBaseJaw(index);
				baseJaw.setName(getName() + " BaseJaw[" + index + "]");
				return baseJaw;
			}
		});

		baseJawOrigins = createMultiDependency("baseJawOrigin", 2, new MultiDependency.Builder<Frame>() {
			@Override
			public Frame create(int index) {
				return new Frame(getName() + " BaseJawOrigin[" + index + "]");
			}
		});

		createMultiDependency("baseToOriginConnection", 2, new Builder<ConfiguredStaticConnection>() {
			@Override
			public ConfiguredStaticConnection create(int index) {
				Transformation offset = createBaseToCenterOfBaseJawBasesTransformation();
				offset = offset.multiply(new Transformation(0, 0, 0, index == 0 ? Math.PI / 2 : -Math.PI / 2, 0, 0));
				offset = offset.multiply(new Transformation((minimalBaseJawDistance / 2.0), 0, 0, 0, 0, 0));
				return new ConfiguredStaticConnection(getBase(), baseJawOrigins.get(index), offset);
			}
		});

		createMultiDependency("originToBaseJawConnection", 2, new Builder<PrismaticConnection>() {
			@Override
			public PrismaticConnection create(int index) {
				return new PrismaticConnection(baseJawOrigins.get(index), baseJaws.get(index).getBase(),
						new Vector(1, 0, 0));
			}
		});
	}

	@Override
	public final Frame getEffectorFrame() {
		return effectorFrame.get();
	}

	@Override
	public final Transformation getBaseToEffectorTransformation() {
		return baseEffectorTransformation.get();
	}

	@Override
	public double getMinimalBaseJawOpeningWidth() throws RoboticsException {
		return minimalBaseJawDistance;
	}

	@Override
	public double getMaximalBaseJawOpeningWidth() throws RoboticsException {
		return minimalBaseJawDistance + strokePerFinger * 2;
	}

	protected abstract BaseJaw createBaseJaw(int index);

	protected abstract Transformation createBaseToEffectorTransformation();

	protected abstract Transformation createBaseToCenterOfBaseJawBasesTransformation();

	@Override
	public final BaseJaw getBaseJaw(int index) {
		return baseJaws.get(index);
	}

	@Override
	public final Frame getBaseJawOrigin(int index) {
		return baseJawOrigins.get(index);
	}

	@Override
	public final GrippingFinger getFinger(int index) {
		BaseJaw baseJaw = getBaseJaw(index);
		if (baseJaw == null) {
			return null;
		}
		return baseJaw.getMountedFinger();
	}

	@Override
	public final BaseJaw[] getBaseJaws() {
		return this.baseJaws.getAll().toArray(new BaseJaw[0]);
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
	public final RealtimeDouble getBaseJawOpeningWidthFrom(RealtimeDouble openingWidth) {
		return openingWidth.add(-getAccumulatedFingerOffsets());
	}

	@Override
	public final double getBaseJawOpeningWidthFrom(double openingWidth) {
		return openingWidth - getAccumulatedFingerOffsets();
	}

	@Override
	public final RealtimeDouble getOpeningWidthFrom(RealtimeDouble baseJawOpeningWidth) {
		return baseJawOpeningWidth.add(getAccumulatedFingerOffsets());
	}

	@Override
	public final double getOpeningWidthFrom(double baseJawOpeningWidth) {
		return baseJawOpeningWidth + getAccumulatedFingerOffsets();
	}

	private final double getMinimumBaseJawDistance() {
		return minimalBaseJawDistance;
	}

	private final double getMaximumBaseJawDistance() {
		return minimalBaseJawDistance + 2 * strokePerFinger;
	}

	private final double getAccumulatedFingerOffsets() {
		return getFingerOffset(0) + getFingerOffset(1);
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
	private final double getFingerOffset(int index) {
		GrippingFinger finger = getFinger(index);
		if (finger == null) {
			return 0d;
		}
		return finger.getOffset();
	}

}
