/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.world;

import org.roboticsapi.core.world.mutable.MutableTransformation;
import org.roboticsapi.core.world.mutable.MutableTwist;
import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPITwist;

/**
 * This class calculates a twist from two frames
 */
public class JTwistFromFrames extends JPrimitive {
	private JInPort<RPIFrame> inPrevFrame = add("inPrevFrame", new JInPort<RPIFrame>());
	private JInPort<RPIFrame> inFrame = add("inFrame", new JInPort<RPIFrame>());
	private JOutPort<RPITwist> outValue = add("outValue", new JOutPort<RPITwist>());

	private MutableTransformation prev = RPICalc.frameCreate();
	private MutableTransformation cur = RPICalc.frameCreate();
	private MutableTwist twist = RPICalc.twistCreate();
	private RPITwist value = RPICalc.rpiTwistCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inPrevFrame, inFrame);
	}

	@Override
	public void updateData() {
		if (anyNull(inPrevFrame, inFrame))
			return;
		RPICalc.rpiToFrame(inPrevFrame.get(), prev);
		RPICalc.rpiToFrame(inFrame.get(), cur);
		prev.getDeltaTo(cur, getNet().getCycleTime(), twist);
		RPICalc.twistToRpi(twist, value);
		outValue.set(value);
	}
};