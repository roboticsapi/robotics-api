/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.multijoint;

/**
 * Implementation for a revolute joint controlling the rotation between the
 * fixed and moving frame around the Z axis
 *
 * @param <JD> type of joint driver used
 */
public class RevoluteJoint<JD extends JointDriver> extends AbstractJoint<JD> {

	@Override
	protected JointConnection createConnection() {
		return new RevoluteJointConnection(this, getDriver());
	}

}
