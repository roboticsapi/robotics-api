/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.world.mutable.MutableRotation;
import org.roboticsapi.world.mutable.MutableTransformation;
import org.roboticsapi.world.mutable.MutableVector;

/**
 * This class implements a frame linear interpolation module
 */
public class JFrameLerp extends JPrimitive {
	private JInPort<RPIFrame> inFrom = add("inFrom", new JInPort<RPIFrame>());
	private JInPort<RPIFrame> inTo = add("inTo", new JInPort<RPIFrame>());
	private JInPort<RPIdouble> inAmount = add("inAmount", new JInPort<RPIdouble>());
	private JOutPort<RPIFrame> outValue = add("outValue", new JOutPort<RPIFrame>());

	RPIFrame value = RPICalc.rpiFrameCreate();
	MutableTransformation frame = RPICalc.frameCreate();
	MutableTransformation first = RPICalc.frameCreate();
	MutableTransformation second = RPICalc.frameCreate();
	MutableTransformation firstInv = RPICalc.frameCreate();
	MutableRotation aim = RPICalc.rotationCreate();
	MutableVector axis = RPICalc.vectorCreate();

	@Override
	public void checkParameters() throws IllegalArgumentException {
		connected(inFrom, inTo, inAmount);
	}

	@Override
	public void updateData() {
		if (anyNull(inFrom, inTo, inAmount))
			return;
		RPICalc.rpiToFrame(inFrom.get(), first);
		RPICalc.rpiToFrame(inTo.get(), second);
		first.invertTo(firstInv);
		RPIdouble amount = inAmount.get();
		double b = amount.get();
		double a = 1 - b;
		firstInv.getRotation().multiplyTo(second.getRotation(), aim);
		aim.getAxisTo(axis);
		axis.scale(b);
		aim.setAxis(axis);
		first.getRotation().multiplyTo(aim, frame.getRotation());
		first.getTranslation().scale(a);
		second.getTranslation().scale(b);
		first.getTranslation().addTo(second.getTranslation(), frame.getTranslation());
		RPICalc.frameToRpi(frame, value);
		outValue.set(value);
	}

};
