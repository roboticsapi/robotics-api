/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.mapper;

import org.roboticsapi.runtime.mapping.AbstractMapperRuntime;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.mapping.net.DataflowOutPort;
import org.roboticsapi.runtime.mapping.net.DoubleDataflow;
import org.roboticsapi.runtime.mapping.net.NetFragment;
import org.roboticsapi.runtime.mapping.parts.SensorMapper;
import org.roboticsapi.runtime.mapping.parts.SensorMappingContext;
import org.roboticsapi.runtime.mapping.parts.SensorMappingPorts;
import org.roboticsapi.runtime.mapping.result.SensorMapperResult;
import org.roboticsapi.runtime.mapping.result.impl.DoubleSensorMapperResult;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.world.dataflow.RotationDataflow;
import org.roboticsapi.runtime.world.primitives.RotationToABC;
import org.roboticsapi.runtime.world.primitives.RotationToQuaternion;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.sensor.RotationComponentSensor;
import org.roboticsapi.world.sensor.RotationComponentSensor.RotationComponent;

public class RotationComponentSensorMapper
		implements SensorMapper<AbstractMapperRuntime, Double, RotationComponentSensor> {

	@Override
	public SensorMapperResult<Double> map(AbstractMapperRuntime runtime, RotationComponentSensor sensor,
			SensorMappingPorts ports, SensorMappingContext context) throws MappingException {

		NetFragment fragment = new NetFragment("RotationComponentSensor");
		SensorMapperResult<Rotation> mapSensor = runtime.getMapperRegistry().mapSensor(runtime, sensor.getSensor(),
				fragment, context);

		DataflowOutPort componentOut;

		RotationComponent component = sensor.getComponent();
		OutPort out = null;
		if (component == RotationComponent.A || component == RotationComponent.B || component == RotationComponent.C) {

			RotationToABC rotABC = fragment.add(new RotationToABC());
			fragment.connect(mapSensor.getSensorPort(), rotABC.getInValue(), new RotationDataflow());
			switch (component) {
			case A:
				out = rotABC.getOutA();
				break;
			case B:
				out = rotABC.getOutB();
				break;
			case C:
				out = rotABC.getOutC();
				break;
			default:
				break;
			}
		} else if (component == RotationComponent.QuaternionX || component == RotationComponent.QuaternionY
				|| component == RotationComponent.QuaternionZ || component == RotationComponent.QuaternionW) {

			RotationToQuaternion rotQuaternion = fragment.add(new RotationToQuaternion());
			fragment.connect(mapSensor.getSensorPort(), rotQuaternion.getInValue(), new RotationDataflow());
			switch (component) {
			case QuaternionX:
				out = rotQuaternion.getOutX();
				break;
			case QuaternionY:
				out = rotQuaternion.getOutY();
				break;
			case QuaternionZ:
				out = rotQuaternion.getOutZ();
				break;
			case QuaternionW:
				out = rotQuaternion.getOutW();
				break;
			default:
				break;
			}
		}
		componentOut = fragment.addOutPort(new DoubleDataflow(), true, out);

		return new DoubleSensorMapperResult(fragment, componentOut);
	}
}
