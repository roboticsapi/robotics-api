/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.roboticsapi.core.entity.ComposedEntity;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.PhysicalObject;
import org.roboticsapi.world.Relation;
import org.roboticsapi.world.World;

public class FrameUtils {

	public static List<Frame> getRootFrames(World world, List<Frame> frames) {
		List<Frame> result = new Vector<Frame>();

		Frame root = world.getOrigin();

		List<Frame> resultTrees = new ArrayList<Frame>();

		do {
			result.add(root);

			resultTrees.add(root);

			resultTrees.addAll(root.getConnectedFrames(false));
		} while ((root = selectRoot(getFramesOutside(frames, resultTrees))) != null);

		return result;
	}

	/**
	 * Gets all frames that are not part of the given list of frames.
	 *
	 * @param frames the frames that are 'inside'
	 * @return all frames not in the list
	 */
	private static List<Frame> getFramesOutside(List<Frame> allFrames, List<Frame> frames) {
		List<Frame> outsideFrames = new ArrayList<Frame>(allFrames);

		outsideFrames.removeAll(frames);

		return outsideFrames;
	}

	/**
	 * Choose a frame from a given list that is worth being the root of a subgraph
	 * in the given set of frames.
	 *
	 * @param frames the list of frames to select a root frame from
	 * @return the frame that is chosen to be root
	 */
	private static Frame selectRoot(List<Frame> frames) {
		if (frames.size() == 0) {
			return null;
		}

		Frame best = frames.get(0);

		if (frames.size() == 1) {
			return best;
		}

		// find base frames
		for (Frame f : frames) {
			ComposedEntity entity = f.getParent();

			if (entity instanceof PhysicalObject) {
				PhysicalObject physicalObject = (PhysicalObject) entity;

				if (physicalObject.getBase() == f) {
					return f;
				}
			}
			// if (f.getName().toLowerCase().contains("base"))
			// return f;
		}

		int i = 1;
		int bestIndex = 0;

		do {
			int currentIndex = 0;

			Frame frame = frames.get(i);
			for (Relation r : frame.getRelations()) {
				if (r.getFrom().equals(frame)) {
					currentIndex++;
				}
			}

			if (currentIndex > bestIndex) {
				best = frame;
				bestIndex = currentIndex;
			}

			++i;
		} while (i < frames.size());

		return best;
	}

}
