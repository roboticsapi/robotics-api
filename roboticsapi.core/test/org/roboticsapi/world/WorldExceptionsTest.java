/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.RelationException;
import org.roboticsapi.world.TransformationException;
import org.roboticsapi.world.VelocityException;

// Tests for class *Exception of package world:

public class WorldExceptionsTest {
	// Tests for class RelationException:

	@Test
	public void testConstructorOfClassRelationExceptionWithMessageArgumentExpectingNotNull() {
		assertNotNull(new RelationException("TestRelationException"));
	}

	@Test
	public void testConstructorOfClassRelationExceptionWithMessageAndInnerExceptionArgumentExpectingNotNull() {
		assertNotNull(new RelationException("TestRelationException", new Exception("TestException")));
	}

	@Test
	public void testConstructorOfClassRelationExceptionWithInnerExceptionArgumentExpectingNotNull() {
		assertNotNull(new RelationException(new Exception("TestException")));
	}

	// Tests for class VelocityException:

	@Test
	public void testConstructorOfClassVelocityExceptionWithMessageArgumentExpectingNotNull() {
		assertNotNull(new VelocityException("TestVelocityException"));
	}

	@Test
	public void testConstructorOfClassVelocityExceptionWithMessageAndInnerExceptionArgumentExpectingNotNull() {
		assertNotNull(new VelocityException("TestVelocityException", new Exception("TestException")));
	}

	@Test
	public void testConstructorOfClassVelocityExceptionWithInnerExceptionArgumentExpectingNotNull() {
		assertNotNull(new VelocityException(new Exception("TestException")));
	}

	@Test
	public void testConstructorOfClassVelocityExceptionWithMessageArgumentAndFrameArgumentsExpectingNotNull() {
		Frame fromFrame = new Frame();
		Frame toFrame = new Frame();

		assertNotNull(new VelocityException("TestVelocityException", fromFrame, toFrame));
	}

	private final VelocityException testVelocityException = new VelocityException("TestVelocityException");

	@Test
	public void testMethodSetFromAndMethodGetFromOfClassVelocityExceptionExpectingTheSetTestFromFrame() {
		Frame fromFrame = new Frame();

		testVelocityException.setFrom(fromFrame);

		assertEquals(fromFrame, testVelocityException.getFrom());
	}

	@Test
	public void testMethodSetToAndMethodGetToOfClassVelocityExceptionExpectingTheSetTestToFrame() {
		Frame toFrame = new Frame();

		testVelocityException.setTo(toFrame);

		assertEquals(toFrame, testVelocityException.getTo());
	}

	// Tests for class TransformationException:

	@Test
	public void testConstructorOfClassTransformationExceptionWithMessageArgumentExpectingNotNull() {
		assertNotNull(new TransformationException("TestTransformationException"));
	}

	@Test
	public void testConstructorOfClassTransformationExceptionWithMessageAndInnerExceptionArgumentExpectingNotNull() {
		assertNotNull(new TransformationException("TestTransformationException", new Exception("TestException")));
	}

	@Test
	public void testConstructorOfClassTransformationExceptionWithInnerExceptionArgumentExpectingNotNull() {
		assertNotNull(new TransformationException(new Exception("TestException")));
	}

	@Test
	public void testConstructorOfClassTransformationExceptionWithMessageArgumentAndFrameArgumentsAndInnerExceptionArgumentExpectingNotNull() {
		Frame fromFrame = new Frame();
		Frame toFrame = new Frame();

		assertNotNull(new TransformationException("TestTransformationException", fromFrame, toFrame,
				new Exception("TestException")));
	}

	private final TransformationException testTransformationException = new TransformationException(
			"TestTransformationException");

	@Test
	public void testMethodSetFromAndMethodGetFromOfClassTransformationExceptionExpectingTheSetTestFromFrame() {
		Frame fromFrame = new Frame();

		testTransformationException.setFrom(fromFrame);

		assertEquals(fromFrame, testTransformationException.getFrom());
	}

	@Test
	public void testMethodSetToAndMethodGetToOfClassTransformationExceptionExpectingTheSetTestToFrame() {
		Frame toFrame = new Frame();

		testTransformationException.setTo(toFrame);

		assertEquals(toFrame, testTransformationException.getTo());
	}
}
