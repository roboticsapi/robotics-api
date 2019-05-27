/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.multijoint.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.runtime.core.javarcc.devices.JDevice;
import org.roboticsapi.runtime.core.types.RPIbool;
import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.core.types.RPIint;
import org.roboticsapi.runtime.core.types.RPIstring;
import org.roboticsapi.runtime.javarcc.JInPort;
import org.roboticsapi.runtime.javarcc.JOutPort;
import org.roboticsapi.runtime.javarcc.JParameter;
import org.roboticsapi.runtime.javarcc.JPrimitive;
import org.roboticsapi.runtime.multijoint.javarcc.interfaces.JMultijointInterface;
import org.roboticsapi.runtime.world.types.RPIVector;

public class JToolParameters extends JPrimitive {
	private JOutPort<RPIbool> outCompleted = add("outCompleted", new JOutPort<RPIbool>());
	private JOutPort<RPIint> outError = add("outError", new JOutPort<RPIint>());
	private JInPort<RPIdouble> inMass = add("inMass", new JInPort<RPIdouble>());
	private JInPort<RPIVector> inCOM = add("inCOM", new JInPort<RPIVector>());
	private JInPort<RPIVector> inMOI = add("inMOI", new JInPort<RPIVector>());
	private JParameter<RPIdouble> propMass = add("Mass", new JParameter<RPIdouble>());
	private JParameter<RPIVector> propCOM = add("COM", new JParameter<RPIVector>());
	private JParameter<RPIVector> propMOI = add("MOI", new JParameter<RPIVector>());
	private JParameter<RPIstring> propRobot = add("Robot", new JParameter<RPIstring>());
	private JParameter<RPIint> propAxis = add("Axis", new JParameter<RPIint>());

	private JMultijointInterface dev;
	private boolean finished, changedValues;
	private RPIVector comVal, moiVal;
	private double massVal;
	private int error;

	@Override
	public void checkParameters() throws IllegalArgumentException {
		dev = device(propRobot, JMultijointInterface.class);
	}

	@Override
	public void updateData() {
		if (moiVal != inMOI.get(propMOI) || comVal != inCOM.get(propCOM) || massVal != inMass.get(propMass).get()) {
			changedValues = true;
			moiVal = inMOI.get(propMOI);
			comVal = inCOM.get(propCOM);
			massVal = inMass.get(propMass).get();
			finished = false;
		}
		outCompleted.set(new RPIbool(finished));
		outError.set(new RPIint(error));
	}

	@Override
	public void writeActuator() {
		if (changedValues) {
			changedValues = false;
			dev.setToolCOM(comVal, propAxis.get().get());
			dev.setToolMOI(moiVal, propAxis.get().get());
			dev.setToolMass(massVal, propAxis.get().get());
		}
		error = dev.getToolError(propAxis.get().get());
		finished = dev.getToolFinished(propAxis.get().get());
	}

	@Override
	public Set<JDevice> getActuators() {
		return deviceSet(dev);
	}

}
