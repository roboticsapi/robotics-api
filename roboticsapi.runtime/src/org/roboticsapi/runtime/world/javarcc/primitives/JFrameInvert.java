/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.javarcc.primitives;

import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.world.mutable.MutableTransformation;

/**
 * This class implements a frame inversion module
 */
public class JFrameInvert extends JPrimitive {
	private JInPort<RPIFrame> inValue = add("inValue", new JInPort<RPIFrame>());
	private JOutPort<RPIFrame> outValue = add("outValue", new JOutPort<RPIFrame>());
	private RPIFrame rFrame = RPICalc.rpiFrameCreate();
	private MutableTransformation frame = RPICalc.frameCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inValue);
	}

	@Override
	public void updateData() {
		if (anyNull(inValue))
			return;
		RPICalc.rpiToFrame(inValue.get(), frame);
		frame.invert();
		RPICalc.frameToRpi(frame, rFrame);
		outValue.set(rFrame);
	}

};
