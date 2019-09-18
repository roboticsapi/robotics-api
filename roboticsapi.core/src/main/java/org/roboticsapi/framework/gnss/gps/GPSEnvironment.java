/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.framework.gnss.gps;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.realtimevalue.realtimedouble.RealtimeDouble;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.Point;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.TransformationException;
import org.roboticsapi.core.world.Vector;
import org.roboticsapi.core.world.World;
import org.roboticsapi.core.world.realtimevalue.RealtimePoint;
import org.roboticsapi.core.world.realtimevalue.realtimevector.RealtimeVector;

public class GPSEnvironment extends AbstractRoboticsObject {

	private final Dependency<Double> refLongitude, refLatitude, refHeight;
	private final Dependency<Frame> reference;

	public GPSEnvironment() {
		refLongitude = createDependency("refLongitude", 0d);
		refLatitude = createDependency("refLatitude", 0d);
		refHeight = createDependency("refHeight", 0d);
		reference = createDependency("reference", new Frame("GPS Reference"));
	}

	@ConfigurationProperty
	public void setRefLongitude(double longitude) {
		this.refLongitude.set(longitude);
	}

	@ConfigurationProperty
	public void setRefLongitudeDegrees(double longitude) {
		this.refLongitude.set(Math.toRadians(longitude));
	}

	public double getRefLongitude() {
		return refLongitude.get();
	}

	public double getRefLongitudeDegrees() {
		return Math.toDegrees(refLongitude.get());
	}

	@ConfigurationProperty
	public void setRefLatitude(double latitude) {
		this.refLatitude.set(latitude);
	}

	@ConfigurationProperty
	public void setRefLatitudeDegrees(double latitude) {
		this.refLatitude.set(Math.toRadians(latitude));
	}

	public double getRefLatitude() {
		return refLatitude.get();
	}

	public double getRefLatitudeDegrees() {
		return Math.toDegrees(refLatitude.get());
	}

	@ConfigurationProperty
	public void setRefHeight(double height) {
		this.refHeight.set(height);
	}

	public double getRefHeight() {
		return refHeight.get();
	}

	@ConfigurationProperty
	public void setReference(Frame reference) {
		this.reference.set(reference);
	}

	public Frame getReference() {
		return reference.get();
	}

	public Point createPoint(double lat, double lon, double height) {
		double r1 = WGS84Constants.getLatitutdeRadius(refLatitude.get());
		double r2 = WGS84Constants.getLongitudeRadius(refLatitude.get());
		return new Point(reference.get(), new Vector((lon - refLongitude.get()) * r2, (lat - refLatitude.get()) * r1,
				(height - this.refHeight.get())));
	}

	public Point createPointDegrees(double lat, double lon, double height) {
		return createPoint(Math.toRadians(lat), Math.toRadians(lon), height);
	}

	public RealtimePoint createPoint(RealtimeDouble lat, RealtimeDouble lon, RealtimeDouble height) {
		double r1 = WGS84Constants.getLatitutdeRadius(refLatitude.get());
		double r2 = WGS84Constants.getLongitudeRadius(refLatitude.get());
		return RealtimePoint.createFromVector(reference.get(),
				RealtimeVector.createFromXYZ(lon.minus(refLongitude.get()).multiply(r2),
						lat.minus(refLatitude.get()).multiply(r1), height.minus(this.refHeight.get())));
	}

	public RealtimePoint createPointDegrees(RealtimeDouble lat, RealtimeDouble lon, RealtimeDouble height) {
		return createPoint(lat.multiply(Math.PI / 180), lon.multiply(Math.PI / 180), height);
	}

	public double getLatitude(Point p) throws TransformationException {
		double r1 = WGS84Constants.getLatitutdeRadius(refLatitude.get());
		Transformation trans = reference.get().asPose().getCommandedTransformationTo(p.asPose());
		return refLatitude.get() + (trans.getTranslation().getY() / r1);
	}

	public double getLatitudeDegrees(Point p) throws TransformationException {
		return getLatitude(p) * 180 / Math.PI;
	}

	public double getLongitude(Point p) throws TransformationException {
		double r2 = WGS84Constants.getLongitudeRadius(refLatitude.get());
		Transformation trans = reference.get().asPose().getCommandedTransformationTo(p.asPose());
		return refLongitude.get() + (trans.getTranslation().getX() / r2);
	}

	public double getLongitudeDegrees(Point p) throws TransformationException {
		return getLongitude(p) * 180 / Math.PI;
	}

	public double getHeight(Point p) throws TransformationException {
		Transformation trans = reference.get().asPose().getCommandedTransformationTo(p.asPose());
		return refHeight.get() + trans.getTranslation().getZ();
	}

	@Deprecated
	public RealtimeDouble getLatitude(RealtimePoint p) throws TransformationException {
		double r1 = WGS84Constants.getLatitutdeRadius(refLatitude.get());
		return p.getVectorForRepresentation(reference.get(), reference.get().asOrientation(),
				World.getCommandedTopology()).getY().divide(r1).add(refLatitude.get());
	}

	@Deprecated
	public RealtimeDouble getLatitudeDegrees(RealtimePoint p) throws TransformationException {
		return getLatitude(p).multiply(180 / Math.PI);
	}

	@Deprecated
	public RealtimeDouble getLongitude(RealtimePoint p) throws TransformationException {
		double r2 = WGS84Constants.getLongitudeRadius(refLatitude.get());
		return p.getVectorForRepresentation(reference.get(), reference.get().asOrientation(),
				World.getCommandedTopology()).getX().divide(r2).add(refLongitude.get());
	}

	@Deprecated
	public RealtimeDouble getLongitudeDegrees(RealtimePoint p) throws TransformationException {
		return getLongitude(p).multiply(180 / Math.PI);
	}

	@Deprecated
	public RealtimeDouble getHeight(RealtimePoint p) throws TransformationException {
		return p.getVectorForRepresentation(reference.get(), reference.get().asOrientation(),
				World.getCommandedTopology()).getZ().add(refHeight.get());
	}

	public RealtimeDouble getLatitude(RealtimePoint p, FrameTopology topology) throws TransformationException {
		double r1 = WGS84Constants.getLatitutdeRadius(refLatitude.get());
		return p.getVectorForRepresentation(reference.get(), reference.get().asOrientation(), topology).getY()
				.divide(r1).add(refLatitude.get());
	}

	public RealtimeDouble getLatitudeDegrees(RealtimePoint p, FrameTopology topology) throws TransformationException {
		return getLatitude(p, topology).multiply(180 / Math.PI);
	}

	public RealtimeDouble getLongitude(RealtimePoint p, FrameTopology topology) throws TransformationException {
		double r2 = WGS84Constants.getLongitudeRadius(refLatitude.get());
		return p.getVectorForRepresentation(reference.get(), reference.get().asOrientation(), topology).getX()
				.divide(r2).add(refLongitude.get());
	}

	public RealtimeDouble getLongitudeDegrees(RealtimePoint p, FrameTopology topology) throws TransformationException {
		return getLongitude(p, topology).multiply(180 / Math.PI);
	}

	public RealtimeDouble getHeight(RealtimePoint p, FrameTopology topology) throws TransformationException {
		return p.getVectorForRepresentation(reference.get(), reference.get().asOrientation(), topology).getZ()
				.add(refHeight.get());
	}

}
