/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping.net;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.roboticsapi.runtime.mapping.MappingException;
import org.roboticsapi.runtime.multijoint.JointsDataflow;
import org.roboticsapi.runtime.robot.AlphaDataflow;
import org.roboticsapi.runtime.rpi.InPort;
import org.roboticsapi.runtime.rpi.OutPort;
import org.roboticsapi.runtime.world.dataflow.RelationDataflow;

public class ComposedDataflowTest {

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
	}

	@AfterClass
	public static void tearDownAfterClass() throws Exception {
	}

	@Before
	public void setUp() throws Exception {
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInvalidTypeConnection() throws MappingException {
		NetFragment snf = new NetFragment("Test");
		OutPort o = new OutPort("o1");
		InPort i = new InPort("i");
		DataflowOutPort out = snf.addOutPort(new StateDataflow(), true, o);
		DataflowInPort in = snf.addInPort(new DoubleDataflow(), true, i);
		try {
			in.connectTo(out);
			Assert.fail("Should not be able to connect ports of incompatible types");
		} catch (MappingException ex) {
		}
	}

	@Test
	public void testComposedOutPort() throws MappingException {
		NetFragment snf = new NetFragment("Test");
		OutPort o1 = new OutPort("o1"), o2 = new OutPort("o2");
		DataflowOutPort out1 = snf.addOutPort(new StateDataflow(), true, o1);
		DataflowOutPort out2 = snf.addOutPort(new DoubleDataflow(), true, o2);
		ComposedDataflowOutPort out = new ComposedDataflowOutPort(true, out1, out2);
		snf.addOutPort(out);

		InPort i1 = new InPort("i1");
		DataflowInPort in1 = snf.addInPort(new StateDataflow(), true, i1);
		in1.connectTo(out);
		assertEquals(in1.isConnected(), true);
		in1.connectInNet();
		assertEquals(i1.getConnectedPort(), o1);
	}

	@Test
	public void testCastToComposedPort() throws MappingException {
		NetFragment snf = new NetFragment("Test");
		OutPort o1 = new OutPort("o1"), o2 = new OutPort("o2");
		DataflowOutPort out = snf.addOutPort(new JointsDataflow(2, null), true, o1, o2);

		DataflowThroughInPort tIn = new DataflowThroughInPort(new JointsDataflow(2, null));
		ComposedDataflow ctype = new ComposedDataflow();
		ctype.addDataflow(new DoubleDataflow());
		ctype.addDataflow(new StateDataflow());
		DataflowThroughOutPort tOut = new DataflowThroughOutPort(true, tIn, ctype);

		InPort i1 = new InPort("i1"), i2 = new InPort("i2");
		DataflowInPort in1 = snf.addInPort(new StateDataflow(), false, i1);
		DataflowInPort in2 = snf.addInPort(new DoubleDataflow(), false, i2);
		ComposedDataflowInPort in = new ComposedDataflowInPort(true, in1, in2);
		in.connectTo(tOut);
		assertEquals(in.isConnected(), true);
		assertEquals(in1.isConnected(), true);
		assertEquals(in2.isConnected(), true);
		assertEquals(tIn.isConnected(), false);
		tIn.connectTo(out);

		in.connectInNet();
		assertEquals(i1.getConnectedPort(), o2);
		assertEquals(i2.getConnectedPort(), o1);
	}

	@Test
	public void testComposedInPort() throws MappingException {
		NetFragment snf = new NetFragment("Test");
		OutPort o1 = new OutPort("o1");
		DataflowOutPort out1 = snf.addOutPort(new StateDataflow(), true, o1);

		InPort i1 = new InPort("i1"), i2 = new InPort("i2");
		DataflowInPort in1 = snf.addInPort(new StateDataflow(), false, i1);
		DataflowInPort in2 = snf.addInPort(new DoubleDataflow(), false, i2);
		ComposedDataflowInPort in = new ComposedDataflowInPort(true, in1, in2);
		in.connectTo(out1);
		assertEquals(in.isConnected(), true);
		assertEquals(in1.isConnected(), true);
		assertEquals(in2.isConnected(), false);
		in.connectInNet();
		assertEquals(i1.getConnectedPort(), o1);
	}

	@Test
	public void testComposedInOutPort() throws MappingException {
		NetFragment snf = new NetFragment("Test");
		OutPort o1 = new OutPort("o1"), o2 = new OutPort("o2");
		DataflowOutPort out1 = snf.addOutPort(new StateDataflow(), true, o1);
		DataflowOutPort out2 = snf.addOutPort(new DoubleDataflow(), true, o2);
		ComposedDataflowOutPort out = new ComposedDataflowOutPort(true, out1, out2);
		snf.addOutPort(out);

		InPort i1 = new InPort("i1"), i2 = new InPort("i2");
		DataflowInPort in1 = snf.addInPort(new DoubleDataflow(), true, i1);
		DataflowInPort in2 = snf.addInPort(new StateDataflow(), true, i2);
		ComposedDataflowInPort in = new ComposedDataflowInPort(true, in1, in2);
		in.connectTo(out);
		assertEquals(in.isConnected(), true);
		assertEquals(in1.isConnected(), true);
		assertEquals(in2.isConnected(), true);
		in.connectInNet();
		assertEquals(i1.getConnectedPort(), o2);
		assertEquals(i2.getConnectedPort(), o1);
	}

	@Test
	public void testComposedOutFlow() {
		DoubleDataflow dd1 = new DoubleDataflow(), dd2 = new DoubleDataflow();
		AlphaDataflow ad1 = new AlphaDataflow(), ad2 = new AlphaDataflow();

		ComposedDataflow cd1 = new ComposedDataflow(), cd2 = new ComposedDataflow(), cd3 = new ComposedDataflow();
		cd1.addDataflow(dd1);
		cd1.addDataflow(ad1);
		cd2.addDataflow(dd2);
		cd2.addDataflow(ad2);
		cd3.addDataflow(ad2);
		cd3.addDataflow(dd2);

		OutPort odd = new OutPort("dd"), oad = new OutPort("ad");
		NetFragment snf = new NetFragment("Test");
		DataflowOutPort outPort = snf.addOutPort(cd1, true, odd, oad);

		assertEquals(2, outPort.getPorts().size());

		List<OutPort> ports = outPort.getPorts(dd2);
		assertEquals(1, ports.size());
		assertEquals(odd, ports.get(0));

		ports = outPort.getPorts(ad2);
		assertEquals(1, ports.size());
		assertEquals(oad, ports.get(0));

		ports = outPort.getPorts(cd1);
		assertEquals(2, ports.size());
		assertEquals(odd, ports.get(0));
		assertEquals(oad, ports.get(1));

		ports = outPort.getPorts(cd2);
		assertEquals(2, ports.size());
		assertEquals(odd, ports.get(0));
		assertEquals(oad, ports.get(1));

		ports = outPort.getPorts(cd3);
		assertEquals(2, ports.size());
		assertEquals(oad, ports.get(0));
		assertEquals(odd, ports.get(1));
	}

	@Test
	public void testSimpleOutPort() {
		DoubleDataflow dd1 = new DoubleDataflow(), dd2 = new DoubleDataflow();
		AlphaDataflow ad1 = new AlphaDataflow(), ad2 = new AlphaDataflow();

		ComposedDataflow cd1 = new ComposedDataflow(), cd2 = new ComposedDataflow();
		cd1.addDataflow(dd1);
		cd1.addDataflow(ad1);
		cd2.addDataflow(ad2);
		cd2.addDataflow(dd2);

		OutPort odd = new OutPort("dd");
		NetFragment snf = new NetFragment("Test");
		DataflowOutPort outPort = snf.addOutPort(dd1, true, odd);

		assertEquals(1, outPort.getPorts().size());

		List<OutPort> ports = outPort.getPorts(dd2);
		assertEquals(1, ports.size());
		assertEquals(odd, ports.get(0));

		ports = outPort.getPorts(ad2);
		assertEquals(1, ports.size());
		assertEquals(null, ports.get(0));

		ports = outPort.getPorts(cd1);
		assertEquals(2, ports.size());
		assertEquals(odd, ports.get(0));
		assertEquals(null, ports.get(1));

		ports = outPort.getPorts(cd2);
		assertEquals(2, ports.size());
		assertEquals(null, ports.get(0));
		assertEquals(odd, ports.get(1));
	}

	@Test
	public void testNestedOutPort() {
		DoubleDataflow dd1 = new DoubleDataflow(), dd2 = new DoubleDataflow();
		AlphaDataflow ad1 = new AlphaDataflow(), ad2 = new AlphaDataflow();

		ComposedDataflow cd1 = new ComposedDataflow(), cd2 = new ComposedDataflow();
		cd1.addDataflow(dd1);
		cd1.addDataflow(cd2);
		cd2.addDataflow(ad1);

		ComposedDataflow cd3 = new ComposedDataflow();
		cd3.addDataflow(dd2);
		cd3.addDataflow(ad2);

		ComposedDataflow cd4 = new ComposedDataflow(), cd5 = new ComposedDataflow();
		cd5.addDataflow(dd2);
		cd5.addDataflow(ad2);
		cd4.addDataflow(cd5);
		cd4.addDataflow(ad2);
		cd4.addDataflow(cd5);
		cd4.addDataflow(new RelationDataflow(null, null));

		OutPort odd = new OutPort("dd"), oad = new OutPort("ad");
		NetFragment snf = new NetFragment("Test");
		DataflowOutPort outPort = snf.addOutPort(cd1, true, odd, oad);

		assertEquals(2, outPort.getPorts().size());

		List<OutPort> ports = outPort.getPorts(dd2);
		assertEquals(1, ports.size());
		assertEquals(odd, ports.get(0));

		ports = outPort.getPorts(ad2);
		assertEquals(1, ports.size());
		assertEquals(oad, ports.get(0));

		ports = outPort.getPorts(cd1);
		assertEquals(2, ports.size());
		assertEquals(odd, ports.get(0));
		assertEquals(oad, ports.get(1));

		ports = outPort.getPorts(cd3);
		assertEquals(2, ports.size());
		assertEquals(odd, ports.get(0));
		assertEquals(oad, ports.get(1));

		ports = outPort.getPorts(cd4);
		assertEquals(6, ports.size());
		assertEquals(odd, ports.get(0));
		assertEquals(oad, ports.get(1));
		assertEquals(oad, ports.get(2));
		assertEquals(odd, ports.get(3));
		assertEquals(oad, ports.get(4));
		assertEquals(null, ports.get(5));

	}

}
