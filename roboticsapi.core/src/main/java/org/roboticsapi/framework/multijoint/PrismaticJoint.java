/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint;

import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.RealtimeRotation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;
import org.roboticsapi.core.world.relation.PrismaticConnection;
import org.roboticsapi.core.world.relation.SingleDofConnection;

/**
 * An abstract prismatic joint controlling the displacement between the fixed
 * and moving frame in Z direction
 *
 * @param <JD> type of joint driver used
 */
public class PrismaticJoint<JD extends JointDriver> extends AbstractJoint<JD> {

	private final Dependency<Vector> movementDirection;

	/**
	 * Creates a prismatic joint which moves in z direction.
	 */
	public PrismaticJoint() {
		movementDirection = createDependency("movementDirection", new Vector(0, 0, 1));
	}

	/**
	 * Creates a prismatic joint which moves in the given direction basing on frame
	 * "from". The movement scales with the length of the vector. That means: If the
	 * vector length is 1, the movement behaves like the input value. If the vector
	 * length is 2, the movement way is scaled by 2.
	 *
	 * @param movementDirection the direction basing on frame "from". The length
	 *                          determines movement scale.
	 */
	public PrismaticJoint(Vector movingDirection) {
		this();
		setMovementDirection(movingDirection);
	}

	/**
	 * Sets a new moving direction basing on the joint's "from" frame.
	 *
	 * @param movementDirection the new movement direction
	 */
	public final void setMovementDirection(Vector movementDirection) {
		this.movementDirection.set(movementDirection);
	}

	/**
	 * Retrieves the moving direction of this joint basing on its "from" frame.
	 *
	 * @return the current movement direction
	 */
	public final Vector getMovementDirection() {
		return movementDirection.get();
	}

	@Override
	public SingleDofConnection createConnection(Frame from, Frame to) {
		return new PrismaticConnection(from, to, getMovementDirection());
	}

	@Override
	public RealtimeTransformation getTransformationSensor(RealtimeDouble position) {
		return RealtimeTransformation.createFromVectorRotation(
				RealtimeVector.createFromConstant(getMovementDirection()).scale(position), RealtimeRotation.IDENTITY);
	}

}
