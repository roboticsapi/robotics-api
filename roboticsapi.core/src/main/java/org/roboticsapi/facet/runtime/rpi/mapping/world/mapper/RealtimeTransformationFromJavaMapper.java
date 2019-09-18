/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.mapping.world.mapper;

import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.WritableRealtimeTransformation;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdouble;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.TypedRealtimeValueFragmentFactory;
import org.roboticsapi.facet.runtime.rpi.mapping.world.RealtimeTransformationFragment;
import org.roboticsapi.facet.runtime.rpi.world.netcomm.WriteFrameToNet;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIFrame;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIRotation;
import org.roboticsapi.facet.runtime.rpi.world.types.RPIVector;

public class RealtimeTransformationFromJavaMapper
		extends TypedRealtimeValueFragmentFactory<Transformation, WritableRealtimeTransformation> {
	int nr = 0;

	public RealtimeTransformationFromJavaMapper() {
		super(WritableRealtimeTransformation.class);
	}

	private RPIFrame convert(Transformation value) {
		return new RPIFrame(
				new RPIVector(new RPIdouble(value.getTranslation().getX()),
						new RPIdouble(value.getTranslation().getY()), new RPIdouble(value.getTranslation().getZ())),
				new RPIRotation(new RPIdouble(value.getRotation().getA()), new RPIdouble(value.getRotation().getB()),
						new RPIdouble(value.getRotation().getC())));
	}

	@Override
	protected RealtimeValueFragment<Transformation> createFragment(WritableRealtimeTransformation value)
			throws MappingException, RpiException {

		final WriteFrameToNet netcomm = new WriteFrameToNet("Frame" + nr++, convert(value.getCheapValue()));
		RealtimeValueListener<Transformation> listener = newValue -> netcomm.getNetcomm()
				.setString(convert(newValue).toString());
		try {
			value.addListener(listener);
		} catch (RoboticsException e) {
			throw new MappingException(e);
		}
		return new RealtimeTransformationFragment(value, netcomm.getOutValue(), netcomm.getOutLastUpdated()) {
			@Override
			protected void finalize() throws Throwable {
				super.finalize();
				value.removeListener(listener);
			}
		};
	}

}
