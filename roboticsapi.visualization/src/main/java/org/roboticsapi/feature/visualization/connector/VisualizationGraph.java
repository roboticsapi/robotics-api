/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.feature.visualization.connector;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import org.roboticsapi.core.Property;
import org.roboticsapi.core.PropertyListener;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.RoboticsEntity;
import org.roboticsapi.core.RoboticsObject;
import org.roboticsapi.core.util.HashCodeUtil;
import org.roboticsapi.core.util.RAPILogger;
import org.roboticsapi.core.world.Frame;
import org.roboticsapi.core.world.FrameTopology;
import org.roboticsapi.core.world.GeometricRelation;
import org.roboticsapi.core.world.PhysicalObject;
import org.roboticsapi.core.world.Relation;
import org.roboticsapi.core.world.RelationListener;
import org.roboticsapi.core.world.Transformation;
import org.roboticsapi.core.world.realtimevalue.realtimetransformation.RealtimeTransformation;
import org.roboticsapi.extension.RoboticsObjectListener;
import org.roboticsapi.facet.visualization.property.Visualisation;
import org.roboticsapi.facet.visualization.property.VisualizationProperty;
import org.roboticsapi.feature.visualization.VisualizationClientScene;

public class VisualizationGraph implements RoboticsObjectListener {

	private VisualizationClientScene scene;
	boolean failed = false;
	private FrameTopology topology;
	private Frame origin;
	private Map<Frame, Integer> frameId = new HashMap<>();
	private Map<Relation, Runnable> relationListenerRemover = new HashMap<>();

	private PropertyListener propertyListener = new PropertyListener() {
		@Override
		public void onPropertyAdded(RoboticsEntity roboticsEntity, Property p) {
			if (p instanceof VisualizationProperty) {
				Visualisation model = ((VisualizationProperty) p).getModel();
				Transformation t = model.getTransformation();
				if (model.getMainFileName() != null && !model.getMainFileName().isEmpty()) {
					tryRun(() -> {
						String modelName = uploadModel(model);
						scene.addModel(roboticsEntity.toString(),
								frameId.get(((PhysicalObject) roboticsEntity).getBase()), modelName, t.getX(), t.getY(),
								t.getZ(), Math.toDegrees(t.getA()), Math.toDegrees(t.getB()), Math.toDegrees(t.getC()));
					});
				}
			}
		}

		private String uploadModel(Visualisation model) throws Exception {
			String modelName = model.getMainFileName();
			if (!scene.hasModel(modelName) && "COLLADA".equals(model.getType())) {
				try {
					InputStream modelStream = model.getMainFile().openStream();
					byte[] firstChunk = new byte[4096];
					modelStream.read(firstChunk);
					int hashCode = HashCodeUtil.hash(HashCodeUtil.SEED, firstChunk);
					modelName = modelName + "_" + Integer.toHexString(hashCode);
					if (!scene.hasModel(modelName)) {
						byte[] file = loadStreamToArray(model.getMainFile().openStream());
						Map<String, byte[]> auxFiles = null;
						if (model.getFiles() != null) {
							auxFiles = new HashMap<String, byte[]>();
							for (URL url : model.getFiles()) {
								String fn = url.getPath().substring(url.getPath().lastIndexOf("/") + 1);
								auxFiles.put(fn, loadStreamToArray(url.openStream()));
							}
						}
						RAPILogger.getLogger(this)
								.fine("Uploading model " + model.getMainFileName() + " to visualization");
						scene.uploadModel(modelName, file, auxFiles);
						RAPILogger.getLogger(this).fine("Model " + model.getMainFileName() + " completed");
					}
				} catch (IOException e) {
					RAPILogger.logException(this, e);
				}
			}
			return modelName;
		}

		private byte[] loadStreamToArray(InputStream in) throws IOException {
			ByteArrayOutputStream out = new ByteArrayOutputStream(4096);
			byte[] buf = new byte[4096];
			while (true) {
				int len = in.read(buf);
				if (len < 0) {
					break;
				}
				out.write(buf, 0, len);
			}
			in.close();
			return out.toByteArray();
		}
	};

	public interface FailingRunnable {
		void run() throws Exception;
	}

	private void tryRun(FailingRunnable r) {
		try {
			if (failed)
				return;
			r.run();
		} catch (Exception e) {
			failed = true;
		}
	}

	private RelationListener relationListener = new RelationListener() {

		@Override
		public void relationAdded(Relation addedRelation, Frame endpoint) {
			if (frameId.get(addedRelation.getFrom()) == null || frameId.get(addedRelation.getTo()) == null)
				return;
			tryRun(() -> {
				RealtimeTransformation transformation = topology.getRealtimeTransformation(addedRelation);
				Transformation currentTrans = transformation.getCurrentValue();
				if (currentTrans == null)
					return;
				scene.addRelation(frameId.get(addedRelation.getFrom()), frameId.get(addedRelation.getTo()),
						currentTrans.getX(), currentTrans.getY(), currentTrans.getZ(), currentTrans.getA(),
						currentTrans.getB(), currentTrans.getC());
				RealtimeValueListener<Transformation> listener = value -> tryRun(() -> scene.updateTransformation(
						frameId.get(addedRelation.getFrom()), frameId.get(addedRelation.getTo()), value.getX(),
						value.getY(), value.getZ(), value.getA(), value.getB(), value.getC()));
				transformation.addListener(listener);
				relationListenerRemover.put(addedRelation, () -> tryRun(() -> transformation.removeListener(listener)));
			});
		}

		@Override
		public void relationRemoved(Relation removedRelation, Frame endpoint) {
			if (frameId.get(removedRelation.getFrom()) == null || frameId.get(removedRelation.getTo()) == null)
				return;
			tryRun(() -> scene.removeRelation(frameId.get(removedRelation.getFrom()),
					frameId.get(removedRelation.getTo())));
			if (relationListenerRemover.containsKey(removedRelation))
				relationListenerRemover.remove(removedRelation).run();
		}
	};

	public VisualizationGraph(VisualizationClientScene scene, Frame origin, FrameTopology topology) {
		this.scene = scene;
		this.origin = origin;
		this.topology = topology.specialized(GeometricRelation.class);
		tryRun(() -> frameId.put(origin, scene.getRootFrame()));
	}

	@Override
	public void onAvailable(RoboticsObject object) {
		if (object == origin)
			return;
		if (object instanceof Frame) {
			tryRun(() -> frameId.put((Frame) object, scene.addFrame(object.getName())));
			topology.addRelationListener((Frame) object, relationListener);
		}
		if (object instanceof PhysicalObject) {
			PhysicalObject physicalObject = (PhysicalObject) object;
			for (Property p : physicalObject.getProperties())
				propertyListener.onPropertyAdded(physicalObject, p);
			physicalObject.addPropertyListener(propertyListener);
		}
	}

	@Override
	public void onUnavailable(RoboticsObject object) {
		if (object == origin)
			return;
		if (object instanceof Frame) {
			topology.removeRelationListener((Frame) object, relationListener);
			tryRun(() -> scene.removeFrame(frameId.remove(object)));
		} else if (object instanceof PhysicalObject) {
			((PhysicalObject) object).removePropertyListener(propertyListener);
		}
	}

	public void shutdown() {
		for (Frame f : frameId.keySet())
			tryRun(() -> scene.removeFrame(frameId.get(f)));
	}

}
