/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.multijoint.javarcc.primitives;

import java.util.Set;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.javarcc.devices.JDevice;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIint;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIstring;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;
import org.roboticsapi.framework.multijoint.javarcc.interfaces.JMultijointInterface;

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
