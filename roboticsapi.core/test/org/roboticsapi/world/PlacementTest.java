/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Placement;
import org.roboticsapi.world.RelationException;
import org.roboticsapi.world.TaughtPlacement;
import org.roboticsapi.world.TeachingType;
import org.roboticsapi.world.Transformation;

//Tests for class Placement and class TaughtPlacement of package world:

public class PlacementTest {
	// Tests for class Placement:

	@Test
	public void testConstructorOfClassPlacementWithoutArgumentExpectingNotNull() {
		assertNotNull(new Placement());
	}

	private final double X = -0.4, Y = 5.3, Z = -3.2, A = 9.0, B = -6.7, C = 2.8;
	private final Placement testPlacement = new Placement(X, Y, Z, A, B, C);

	@Test
	public void testConstructorOfClassPlacementWithDoubleArgumentsForXYZABCExpectingNotNull() {
		assertNotNull(testPlacement);
	}

	@Test
	public void testGetXOfClassPlacementWithGivenTestPlacementExpectingInitX() {
		assertEquals(X, testPlacement.getX(), 0.001);
	}

	@Test
	public void testGetYOfClassPlacementWithGivenTestPlacementExpectingInitY() {
		assertEquals(Y, testPlacement.getY(), 0.001);
	}

	@Test
	public void testGetZOfClassPlacementWithGivenTestPlacementExpectingInitZ() {
		assertEquals(Z, testPlacement.getZ(), 0.001);
	}

	@Test
	public void testGetRelationSensorWithValidFrameFromAndFrameToAndTransformationExpectingRelationSensorNotNull()
			throws RelationException {
		Frame fromFrame = new Frame();
		Frame toFrame = new Frame();
		testPlacement.setFrom(fromFrame);
		testPlacement.setTo(toFrame);

		Transformation testTransformation = new Transformation();
		testPlacement.setTransformation(testTransformation);

		assertNotNull(testPlacement.getRelationSensor());
	}

	@Test
	public void testGetMeasuredRelationSensorWithValidFrameFromAndFrameToAndTransformationExpectingRelationSensorNotNull()
			throws RelationException {
		Frame fromFrame = new Frame();
		Frame toFrame = new Frame();
		testPlacement.setFrom(fromFrame);
		testPlacement.setTo(toFrame);

		Transformation testTransformation = new Transformation();
		testPlacement.setTransformation(testTransformation);

		assertNotNull(testPlacement.getMeasuredRelationSensor());
	}

	@Test
	public void testGetMeasuredTransformationExpectingNotNullDefaultTransformation() {
		Placement testPlacement = new Placement();

		Transformation testTransformation = testPlacement.getMeasuredTransformation();

		assertNotNull(testTransformation);

		assertTrue(testTransformation.isEqualTransformation(new Transformation()));
	}

	@Test
	public void testGetVelocitySensorWithoutSettingFromFrameAndToFrameExpectingNull() {
		Placement testPlacement = new Placement();

		assertNull(testPlacement.getVelocitySensor());
	}

	@Test
	public void testGetVelocitySensorAfterSettingFromFrameAndToFrameExpectingNotNull() {
		Placement testPlacement = new Placement();

		Frame fromFrm = new Frame();
		Frame toFrm = new Frame();

		testPlacement.setFrom(fromFrm);
		testPlacement.setTo(toFrm);

		assertNotNull(testPlacement.getVelocitySensor());
	}

	@Test
	public void testGetMeasuredVelocitySensorWithoutSettingFromFrameAndToFrameExpectingNull() {
		Placement testPlacement = new Placement();

		assertNull(testPlacement.getMeasuredVelocitySensor());
	}

	@Test
	public void testGetMeasuredVelocitySensorAfterSettingFromFrameAndToFrameExpectingNotNull() {
		Placement testPlacement = new Placement();

		Frame fromFrm = new Frame();
		Frame toFrm = new Frame();

		testPlacement.setFrom(fromFrm);
		testPlacement.setTo(toFrm);

		assertNotNull(testPlacement.getMeasuredVelocitySensor());
	}

	@Test
	public void testSetTransformationWithoutSettingFromFrameAndToFrameExpectingGetRelationSensorReturnsNull()
			throws RelationException {
		Placement testPlacement = new Placement();
		Transformation testTransformation = new Transformation();

		testPlacement.setTransformation(testTransformation);

		assertNull(testPlacement.getRelationSensor());
	}

	@Test
	public void testSetTransformationAfterSettingFromFrameAndToFrameExpectingGetRelationSensorReturnsNotNull()
			throws RelationException {
		Placement testPlacement = new Placement();
		Transformation testTransformation = new Transformation();

		Frame fromFrm = new Frame();
		Frame toFrm = new Frame();

		testPlacement.setFrom(fromFrm);
		testPlacement.setTo(toFrm);

		testPlacement.setTransformation(testTransformation);

		assertNotNull(testPlacement.getRelationSensor());
	}

	// Tests for class TaughtPlacement:

	@Test
	public void testConstructorWithTransformationAndTypeArgumentAndTestMethodGetTeachingTypeOfClassTaughtPlacementExpectingNotNullAndSetTeachedType() {
		Transformation testTransformation = new Transformation();
		TeachingType testTeachingType = new TeachingType() {
		};

		TaughtPlacement testTaughtPlacement = new TaughtPlacement(testTransformation, testTeachingType);

		assertNotNull(testTaughtPlacement);

		assertEquals(testTeachingType, testTaughtPlacement.getTeachingType());
	}
}
