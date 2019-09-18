/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.systemtest.realtimevalue;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.core.realtimevalue.RealtimeValueReadException;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.realtimevalue.realtimeinteger.RealtimeInteger;
import org.roboticsapi.facet.runtime.rcc.RccRuntime;
import org.roboticsapi.feature.runtime.javarcc.JavaRcc;
import org.roboticsapi.feature.startup.launcher.DefaultRapi;


// FIXME: this is not possible at the moment 
//@RunWith(RoboticsTestSuite.class)
//@WithRcc(RCC.DedicatedJavaRcc)
public class RealtimeDoubleFromRealtimeIntegerSystemTest {

	private DefaultRapi rapi;
	private RccRuntime runtime;

	@Before
	public void setup() throws InitializationException {
		rapi = DefaultRapi.createNewEmpty();
		
		JavaRcc javaRCC = new JavaRcc();
		javaRCC.setName("Robotics API system test javaRCC");
		rapi.add(javaRCC);
		
		runtime = new RccRuntime();
		runtime.setName("Runtime");
		runtime.setRcc(javaRCC);
		rapi.add(runtime);
	}
	
	@Test
	public void testGetCurrentValueReturnsCorrectValueForConstantRealtimeInteger() throws RealtimeValueReadException {
		RealtimeDouble integer = RealtimeDouble.createFromInteger(RealtimeInteger.createFromConstant(1));
		
		Assert.assertEquals(new Double(1), integer.getCurrentValue());
	}
	
//	@Test
	public void testGetCurrentValueReturnsCorrectValueForPersistedConstantRealtimeInteger() throws RoboticsException {
		
		// TODO: fix NullEointerException on persist() !
		RealtimeValue<Integer> fromValue = RealtimeInteger.createFromConstant(1).persist(runtime).getValue();
		
		RealtimeDouble integer = RealtimeDouble.createFromInteger(fromValue);
		
		Assert.assertEquals(new Double(1), integer.getCurrentValue());
	}
	
}

