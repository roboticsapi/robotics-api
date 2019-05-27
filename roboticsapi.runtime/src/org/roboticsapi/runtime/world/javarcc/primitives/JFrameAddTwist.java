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
import org.roboticsapi.runtime.world.types.RPITwist;
import org.roboticsapi.world.mutable.MutableTransformation;
import org.roboticsapi.world.mutable.MutableTwist;

/**
 * This class implements applies a twist to a frame.
 */
public class JFrameAddTwist extends JPrimitive {
	private JInPort<RPIFrame> inFrame = add("inFrame", new JInPort<RPIFrame>());
	private JInPort<RPITwist> inTwist = add("inTwist", new JInPort<RPITwist>());
	private JOutPort<RPIFrame> outValue = add("outValue", new JOutPort<RPIFrame>());
	private MutableTransformation frame = RPICalc.frameCreate();
	private MutableTwist twist = RPICalc.twistCreate();
	private RPIFrame rFrame = RPICalc.rpiFrameCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFrame, inTwist);
	}

	@Override
	public void updateData() {
		if (anyNull(inFrame, inTwist))
			return;
		RPICalc.rpiToFrame(inFrame.get(), frame);
		RPICalc.rpiToTwist(inTwist.get(), twist);
		frame.addDelta(twist, getNet().getCycleTime());
		RPICalc.frameToRpi(frame, rFrame);
		outValue.set(rFrame);
	}

};
