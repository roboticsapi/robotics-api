/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.runtime.realtimercc.dionet;

import java.io.IOException;

import org.junit.Assert;
import org.junit.Test;
import org.roboticsapi.facet.runtime.rpi.Fragment;
import org.roboticsapi.facet.runtime.rpi.FragmentInPort;
import org.roboticsapi.facet.runtime.rpi.FragmentOutPort;
import org.roboticsapi.facet.runtime.rpi.NetParser;
import org.roboticsapi.facet.runtime.rpi.NetSerializer;
import org.roboticsapi.facet.runtime.rpi.RpiException;
import org.roboticsapi.facet.runtime.rpi.core.primitives.Cancel;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleAdd;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleNetcommOut;
import org.roboticsapi.facet.runtime.rpi.core.primitives.DoubleValue;

public class DIONetTests {

	@Test
	public void testDIONetWithCancel() throws RpiException, IOException {
		Fragment frag = new Fragment();
		Cancel cancel = frag.add(new Cancel());
		frag.provideOutPort(cancel.getOutCancel(), "outTerminate");

		String newFormat = NetSerializer.serialize(frag);
		Fragment fragment = NetParser.parse(newFormat);
		String roundtrip = NetSerializer.serialize(fragment);

		Assert.assertEquals(newFormat, roundtrip);
	}

	@Test
	public void testDIONetWithFragment() throws RpiException, IOException {
		Fragment frag = new Fragment();
		Cancel cancel = frag.add(new Cancel());
		frag.provideOutPort(cancel.getOutCancel(), "outTerminate");

		Fragment addFrag = frag.add(new Fragment());
		DoubleValue value = addFrag.add(new DoubleValue(1.0));
		FragmentInPort inSecond = addFrag.addInPort("inSecond");
		DoubleAdd add = addFrag.add(new DoubleAdd(1.0, 2.0));
		add.getInFirst().connectTo(value.getOutValue());
		add.getInFirst().setDebug(0.2);
		add.getInSecond().connectTo(inSecond.getInternalOutPort());

		DoubleValue second = frag.add(new DoubleValue());
		inSecond.connectTo(second.getOutValue());
		FragmentOutPort outValue = addFrag.provideOutPort(add.getOutValue(), "outValue");

		DoubleNetcommOut write = frag.add(new DoubleNetcommOut("outValue"));
		write.getInValue().connectTo(outValue);

		String newFormat = NetSerializer.serialize(frag);
		Fragment fragment = NetParser.parse(newFormat);
		String roundtrip = NetSerializer.serialize(fragment);

		Assert.assertEquals(newFormat, roundtrip);
	}

}
