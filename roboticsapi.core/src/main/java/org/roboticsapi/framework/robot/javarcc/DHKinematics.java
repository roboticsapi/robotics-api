/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.robot.javarcc;

import org.roboticsapi.core.world.mutable.MutableTransformation;
import org.roboticsapi.facet.javarcc.primitives.world.RPICalc;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;

public class DHKinematics {

	private double[] d;
	private double[] theta;
	private double[] a;
	private double[] alpha;
	private boolean zDown;

	public DHKinematics(double[] d, double[] theta, double[] a, double[] alpha, boolean zDown) {
		this.d = d;
		this.theta = theta;
		this.a = a;
		this.alpha = alpha;
		this.zDown = zDown;
	}

	MutableTransformation dtheta = RPICalc.frameCreate();
	MutableTransformation aalpha = RPICalc.frameCreate();
	MutableTransformation frame = RPICalc.frameCreate();

	public RPIFrame kin(double[] joints, RPIFrame ret) {
		if (zDown) {
			frame.setVectorEuler(0, 0, 0, 0, 0, Math.PI);
		} else {
			frame.setVectorEuler(0, 0, 0, 0, 0, 0);
		}
		for (int i = 0; i < d.length; i++) {
			dtheta.setVectorEuler(0, 0, d[i], theta[i] + joints[i], 0, 0);
			aalpha.setVectorEuler(a[i], 0, 0, 0, 0, alpha[i]);
			frame.multiply(dtheta);
			frame.multiply(aalpha);
		}
		RPICalc.frameToRpi(frame, ret);
		return ret;
	}

}
