/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc.primitives.core;

import org.roboticsapi.facet.javarcc.JInPort;
import org.roboticsapi.facet.javarcc.JOutPort;
import org.roboticsapi.facet.javarcc.JParameter;
import org.roboticsapi.facet.javarcc.JPrimitive;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIbool;

public class JTrigger extends JPrimitive {
	JInPort<RPIbool> inOn = add("inOn", new JInPort<RPIbool>());
	JInPort<RPIbool> inOff = add("inOff", new JInPort<RPIbool>());
	JInPort<RPIbool> inReset = add("inReset", new JInPort<RPIbool>());
	JOutPort<RPIbool> outActive = add("outActive", new JOutPort<RPIbool>());
	JParameter<RPIbool> propOn = add("On", new JParameter<RPIbool>());
	JParameter<RPIbool> propOff = add("Off", new JParameter<RPIbool>());
	private boolean enabled, done;

	@Override
	public void checkParameters() throws IllegalArgumentException {
	}

	@Override
	public void updateData() {
		if (inReset.get() != null && inReset.get().get() == true) {
			enabled = false;
			done = false;
		}
		if (!done) {
			if (inOn.get(propOn) != null && inOn.get(propOn).get() == true) {
				enabled = true;
			}
			if (inOff.get(propOff) != null && inOff.get(propOff).get() == true) {
				enabled = false;
				done = true;
			}
		}
		outActive.set(new RPIbool(enabled));
	}
}
