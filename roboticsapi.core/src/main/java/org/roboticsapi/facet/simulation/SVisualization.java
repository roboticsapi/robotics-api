/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.simulation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.Dependency;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.mutable.MutableTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.WritableRealtimeTransformation;
import org.roboticsapi.core.world.relation.MeasuredPosition;
import org.roboticsapi.core.world.relation.Placement;

public class SVisualization extends SEntity {
	private final Map<SEntity, WritableRealtimeTransformation> writableTransformations = new HashMap<SEntity, WritableRealtimeTransformation>();
	private final Map<SEntity, Transformation> transformations = new HashMap<SEntity, Transformation>();
	private final Map<SEntity, Frame> frames = new HashMap<SEntity, Frame>();
	private final Dependency<Frame> base;
	private final MutableTransformation relativePosition = new MutableTransformation();

	public SVisualization() {
		base = createDependency("base", new Frame("SWorld"));
	}

	@Override
	public MutableTransformation getRelativePosition() {
		return relativePosition;
	}

	public Frame getBaseFrame() {
		return base.get();
	}

	@ConfigurationProperty
	public void setBaseFrame(Frame base) {
		this.base.set(base);
	}

	@Override
	public double getSimulationHz() {
		return 30;
	}

	@Override
	public void simulateStep(Long time) {
		for (SEntity e : new ArrayList<SEntity>(getWorld().getEntities())) {
			if (!writableTransformations.containsKey(e)) {
				buildFrame(e);
			}
			MutableTransformation trans = e.getRelativePosition();
			Transformation t = transformations.get(e);
			if (t == null || t.getX() != trans.getTranslation().getX() || t.getY() != trans.getTranslation().getY()
					|| t.getZ() != trans.getTranslation().getZ() || t.getA() != trans.getRotation().getA()
					|| t.getB() != trans.getRotation().getB() || t.getC() != trans.getRotation().getC()) {
				t = new Transformation(trans.getTranslation().getX(), trans.getTranslation().getY(),
						trans.getTranslation().getZ(), trans.getRotation().getA(), trans.getRotation().getB(),
						trans.getRotation().getC());
				writableTransformations.get(e).setValue(t);
				transformations.put(e, t);
			}
		}
	}

	private void buildFrame(SEntity e) {
		Frame from = base.get();
		SEntity parent = e.getParent();
		if (parent != null) {
			if (!frames.containsKey(parent)) {
				buildFrame(parent);
			}
			if (frames.containsKey(parent)) {
				from = frames.get(parent);
			}
		}
		Frame to = new Frame(e.getName());
		WritableRealtimeTransformation trans = RealtimeTransformation.createWritable(Transformation.IDENTITY);
		new Placement(from, to).establish();
		new MeasuredPosition(from, to, trans, null).establish();
		frames.put(e, to);
		writableTransformations.put(e, trans);
	}
}
