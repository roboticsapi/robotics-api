/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.softrobot;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.roboticsapi.core.CommandHandle;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.runtime.SoftRobotRuntime;
import org.roboticsapi.runtime.core.netcomm.ReadDoubleFromNet;
import org.roboticsapi.runtime.core.primitives.BooleanValue;
import org.roboticsapi.runtime.core.primitives.DoubleSetNull;
import org.roboticsapi.runtime.core.primitives.DoubleValue;
import org.roboticsapi.runtime.generic.AbstractRuntimeTest;
import org.roboticsapi.runtime.generic.RuntimeSetup;
import org.roboticsapi.runtime.rpi.Fragment;
import org.roboticsapi.runtime.rpi.NetcommListenerAdapter;
import org.roboticsapi.runtime.rpi.NetcommValue;
import org.roboticsapi.runtime.rpi.RPIException;

public class SoftRobotRPIPrimitiveTest extends AbstractRuntimeTest {

	boolean flag = false;

	@Override
	public RuntimeSetup getRuntimeSetup() {
		return new SoftRobotRuntimeSetup();
	}

	@Before
	public void setup() {
		flag = false;
	}

	@Test
	public void testDoubleSetNullSetsValueToNull() throws RPIException, RoboticsException, InterruptedException {
		Fragment f = createBasicFragment();

		DoubleValue zeroValue = new DoubleValue(0d);
		f.add(zeroValue);

		// this test passes if DoubleSetNull.inNull is set to false instead of
		// true - the block seems to work exactly the other way than expected
		BooleanValue trueValue = new BooleanValue(true);
		f.add(trueValue);

		DoubleSetNull setNull = new DoubleSetNull();
		f.add(setNull);
		setNull.getInValue().connectTo(zeroValue.getOutValue());
		setNull.getInNull().connectTo(trueValue.getOutValue());

		ReadDoubleFromNet readDouble = new ReadDoubleFromNet("Key" + Math.random());
		f.add(readDouble);
		readDouble.getInValue().connectTo(setNull.getOutValue());

		CommandHandle handle = ((SoftRobotRuntime) getRuntime()).loadNet(f, "testDoubleSetNullSetsValueToNull");

		flag = false;

		readDouble.getNetcomm().addNetcommListener(new NetcommListenerAdapter() {
			@Override
			public void valueChanged(NetcommValue value) {
				flag = true;
			}
		});

		handle.start();
		Thread.sleep(2000);

		handle.abort();

		if (flag) {
			Assert.fail("Got a NetcommValue, but expected it to be null");
		}
	}

	private Fragment createBasicFragment() {
		Fragment f = new Fragment();

		BooleanValue falseValue = new BooleanValue(false);
		f.add(falseValue);

		DoubleValue zero = new DoubleValue(0d);
		f.add(zero);

		f.provideOutPort(falseValue.getOutValue(), "outTerminate");
		f.provideOutPort(zero.getOutValue(), "outFail");

		return f;
	}

}
