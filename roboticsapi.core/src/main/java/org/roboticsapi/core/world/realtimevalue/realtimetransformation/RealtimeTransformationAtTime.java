/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world.realtimevalue.realtimetransformation;

import java.util.Map;

import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;

public final class RealtimeTransformationAtTime extends RealtimeTransformation {

	@Override
	protected RealtimeTransformation performSubstitution(Map<RealtimeValue<?>, RealtimeValue<?>> substitutionMap) {
		return new RealtimeTransformationAtTime(transformation.substitute(substitutionMap),
				age.substitute(substitutionMap), maxAge);
	}

	private final RealtimeTransformation transformation;
	private final RealtimeDouble age;
	private final double maxAge;

	RealtimeTransformationAtTime(RealtimeTransformation transformation, RealtimeDouble age, double maxAge) {
		super(transformation, age);
		this.transformation = transformation;
		this.age = age;
		this.maxAge = maxAge;
	}

	public RealtimeTransformation getOther() {
		return transformation;
	}

	public RealtimeDouble getAge() {
		return age;
	}

	public double getMaxAge() {
		return maxAge;
	}

	@Override
	public boolean equals(Object obj) {
		return classEqual(obj) && transformation.equals(((RealtimeTransformationAtTime) obj).transformation)
				&& age.equals(((RealtimeTransformationAtTime) obj).age)
				&& maxAge == ((RealtimeTransformationAtTime) obj).maxAge;
	}

	@Override
	public int hashCode() {
		return classHash(transformation, age, maxAge);
	}

	@Override
	public boolean isAvailable() {
		return areAvailable(age, transformation);
	}

	@Override
	public String toString() {
		return "atTime(" + transformation + ", " + age + ")";
	}
}
