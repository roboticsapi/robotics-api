/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.world.realtimevalue.realtimerotation.GetFromRotationRealtimeDouble;
import org.roboticsapi.core.world.realtimevalue.realtimerotation.GetFromRotationRealtimeDouble.RotationComponent;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.core.RealtimeDoubleFragment;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationToABC;
import org.roboticsapi.facet.runtime.rpi.world.primitives.RotationToQuaternion;

public class RealtimeRotationComponentMapper
		extends TypedRealtimeValueFragmentFactory<Double, GetFromRotationRealtimeDouble> {

	public RealtimeRotationComponentMapper() {
		super(GetFromRotationRealtimeDouble.class);
	}

	@Override
	protected RealtimeValueFragment<Double> createFragment(GetFromRotationRealtimeDouble value)
			throws MappingException, RpiException {

		RealtimeDoubleFragment ret = new RealtimeDoubleFragment(value);

		RotationComponent component = value.getComponent();
		if (component == RotationComponent.A || component == RotationComponent.B || component == RotationComponent.C) {

			RotationToABC rotABC = ret.add(new RotationToABC());
			ret.addDependency(value.getRotation(), ret.addInPort("inValue", rotABC.getInValue()));
			switch (component) {
			case A:
				ret.defineResult(rotABC.getOutA());
				break;
			case B:
				ret.defineResult(rotABC.getOutB());
				break;
			case C:
				ret.defineResult(rotABC.getOutC());
				break;
			default:
				break;
			}
		} else if (component == RotationComponent.QuaternionX || component == RotationComponent.QuaternionY
				|| component == RotationComponent.QuaternionZ || component == RotationComponent.QuaternionW) {

			RotationToQuaternion rotQuaternion = ret.add(new RotationToQuaternion());
			ret.addDependency(value.getRotation(), ret.addInPort("inValue", rotQuaternion.getInValue()));
			switch (component) {
			case QuaternionX:
				ret.defineResult(rotQuaternion.getOutX());
				break;
			case QuaternionY:
				ret.defineResult(rotQuaternion.getOutY());
				break;
			case QuaternionZ:
				ret.defineResult(rotQuaternion.getOutZ());
				break;
			case QuaternionW:
				ret.defineResult(rotQuaternion.getOutW());
				break;
			default:
				break;
			}
		}

		return ret;
	}
}
