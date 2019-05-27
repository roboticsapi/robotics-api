/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.world.javarcc.primitives;

import org.roboticsapi.runtime.core.types.RPIdouble;
import org.roboticsapi.runtime.world.types.RPIFrame;
import org.roboticsapi.runtime.world.types.RPIRotation;
import org.roboticsapi.runtime.world.types.RPITwist;
import org.roboticsapi.runtime.world.types.RPIVector;
import org.roboticsapi.world.Rotation;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.Twist;
import org.roboticsapi.world.Vector;
import org.roboticsapi.world.mutable.MutableDouble;
import org.roboticsapi.world.mutable.MutableQuaternion;
import org.roboticsapi.world.mutable.MutableRotation;
import org.roboticsapi.world.mutable.MutableTransformation;
import org.roboticsapi.world.mutable.MutableTwist;
import org.roboticsapi.world.mutable.MutableVector;

public class RPICalc {

	public static MutableVector vectorCreate() {
		return new MutableVector();
	}

	public static MutableTwist twistCreate() {
		return new MutableTwist();
	}

	public static MutableRotation rotationCreate() {
		return new MutableRotation();
	}

	public static MutableQuaternion quaternionCreate() {
		return new MutableQuaternion();
	}

	public static MutableTransformation frameCreate() {
		return new MutableTransformation();
	}

	public static RPIRotation rpiRotationCreate() {
		return new RPIRotation();
	}

	public static RPIVector rpiVectorCreate() {
		return new RPIVector();
	}

	public static RPIFrame rpiFrameCreate() {
		return new RPIFrame();
	}

	public static RPITwist rpiTwistCreate() {
		return new RPITwist();
	}

	public static RPIdouble rpiDoubleCreate() {
		return new RPIdouble();
	}

	// conversion from/to RPITypes
	public static void rpiToTwist(RPITwist twist, MutableTwist ret) {
		RPIVector vel = twist.getVel();
		RPIVector rot = twist.getRot();
		ret.set(vel.getX().get(), vel.getY().get(), vel.getZ().get(), rot.getX().get(), rot.getY().get(),
				rot.getZ().get());
	}

	public static Twist rpiToTwist(RPITwist twist) {
		RPIVector vel = twist.getVel();
		RPIVector rot = twist.getRot();
		return new Twist(vel.getX().get(), vel.getY().get(), vel.getZ().get(), rot.getX().get(), rot.getY().get(),
				rot.getZ().get());
	}

	public static void twistToRpi(Twist t, RPITwist ret) {
		vectorToRpi(t.getTransVel(), ret.getVel());
		vectorToRpi(t.getRotVel(), ret.getRot());
	}

	public static void twistToRpi(MutableTwist t, RPITwist ret) {
		vectorToRpi(t.getTranslation(), ret.getVel());
		vectorToRpi(t.getRotation(), ret.getRot());
	}

	public static Transformation rpiToFrame(RPIFrame frame) {
		return new Transformation(rpiToRotation(frame.getRot()), rpiToVector(frame.getPos()));
	}

	public static void rpiToFrame(RPIFrame frame, MutableTransformation ret) {
		rpiToVector(frame.getPos(), ret.getTranslation());
		rpiToRotation(frame.getRot(), ret.getRotation());
	}

	public static void frameToRpi(MutableTransformation frame, RPIFrame ret) {
		vectorToRpi(frame.getTranslation(), ret.getPos());
		rotationToRpi(frame.getRotation(), ret.getRot());
	}

	public static void frameToRpi(Transformation frame, RPIFrame ret) {
		vectorToRpi(frame.getTranslation(), ret.getPos());
		rotationToRpi(frame.getRotation(), ret.getRot());
	}

	public static Rotation rpiToRotation(RPIRotation r) {
		return new Rotation(r.getA().get(), r.getB().get(), r.getC().get());
	}

	public static void rpiToRotation(RPIRotation r, MutableRotation ret) {
		ret.setEuler(r.getA().get(), r.getB().get(), r.getC().get());
	}

	public static void rotationToRpi(Rotation r, RPIRotation ret) {
		ret.getA().set(r.getA());
		ret.getB().set(r.getB());
		ret.getC().set(r.getC());
	}

	public static void rotationToRpi(MutableRotation r, RPIRotation ret) {
		ret.getA().set(r.getA());
		ret.getB().set(r.getB());
		ret.getC().set(r.getC());
	}

	public static Vector rpiToVector(RPIVector v) {
		return new Vector(v.getX().get(), v.getY().get(), v.getZ().get());
	}

	public static void rpiToVector(RPIVector v, MutableVector ret) {
		ret.set(v.getX().get(), v.getY().get(), v.getZ().get());
	}

	public static void vectorToRpi(Vector v, RPIVector ret) {
		ret.getX().set(v.getX());
		ret.getY().set(v.getY());
		ret.getZ().set(v.getZ());
	}

	public static void vectorToRpi(MutableVector v, RPIVector ret) {
		ret.getX().set(v.getX());
		ret.getY().set(v.getY());
		ret.getZ().set(v.getZ());
	}

	public static void doubleToRpi(double a, RPIdouble ret) {
		ret.set(a);
	}

	public static void doubleToRpi(MutableDouble d, RPIdouble ret) {
		ret.set(d.getValue());
	}

	public static double rpiToDouble(RPIdouble d) {
		return d.get();
	}

	public static void rpiToDouble(RPIdouble d, MutableDouble ret) {
		ret.setValue(d.get());
	}

}
