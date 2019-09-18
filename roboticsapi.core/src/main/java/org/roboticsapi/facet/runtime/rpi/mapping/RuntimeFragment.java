/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping;

import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.InPort;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.RpiException;

public class RuntimeFragment extends Fragment {

	public void connect(OutPort from, InPort to) throws MappingException {
		try {
			if (from.getPrimitive() != this && !getPrimitives().contains(from.getPrimitive()))
				throw new MappingException("The source primitive '" + from.getPrimitiveName() + "' of type '"
						+ from.getPrimitive().getType() + "' does not belong to this fragment.");
			if (to.getPrimitive() != this && !getPrimitives().contains(to.getPrimitive()))
				throw new MappingException("The destination primitive '" + to.getPrimitiveName() + "' of type '"
						+ to.getPrimitive().getType() + "' does not belong to this fragment.");

			to.connectTo(from);
		} catch (RpiException e) {
			throw new MappingException(e);
		}
	}

	@Override
	public <T extends Primitive> T add(T primitive) {
		if (!getPrimitives().contains(primitive))
			super.add(primitive);
		return primitive;
	}

}
