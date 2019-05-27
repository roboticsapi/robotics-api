/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

/**
 * An abstract prismatic joint controlling the displacement between the fixed
 * and moving frame in Z direction
 *
 * @param <JD> type of joint driver used
 */
public class PrismaticJoint<JD extends JointDriver> extends AbstractJoint<JD> {

	@Override
	protected JointConnection createConnection() {
		return new PrismaticJointConnection(this, getDriver());
	}

}
