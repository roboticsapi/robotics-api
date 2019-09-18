/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.core.world;

import java.util.Collection;

/**
 * Interface for a store managing persistent frames.
 */
public interface FrameStore {

	/**
	 * Checks if a Frame with the given name has been created before.
	 * 
	 * @param name the name to check
	 * @return true, if a Frame with the given name exists
	 */
	public abstract boolean hasFrame(String name);

	/**
	 * Gets the Frame with the given name or creates such a Frame, if it does not
	 * exist yet.
	 * 
	 * @param name the name
	 * @return the existing or created frame
	 */
	public abstract Frame getOrCreateFrame(String name);

	/**
	 * Gets the Frame with the specified name, if such exists.
	 * 
	 * @param name the name
	 * @return the frame, if such exists
	 * @throws FrameUnknownException if Frame with the specified name does not exist
	 */
	public abstract Frame getFrame(String name) throws FrameUnknownException;

	/**
	 * Gets all previously registered frames.
	 * 
	 * @return the previously registered frames
	 */
	public abstract Collection<Frame> getAllFrames();

	/**
	 * Creates a new Frame with the given name, if no such Frame exists.
	 * 
	 * @param name the name
	 * @return the newly created Frame, named as specified
	 * @throws FrameExistsException if Frame with given name already exists
	 */
	public abstract Frame createAndRegisterFrame(String name) throws FrameExistsException;

	/**
	 * Renames an existing frame.
	 * 
	 * @param frame   the frame to rename
	 * @param newName the new name
	 * @throws FrameException if renaming not possible
	 */
	public abstract void renameFrame(Frame frame, String newName) throws FrameException;

	/**
	 * Deletes the given Frame if possible.
	 * 
	 * @param frame the frame to deletes
	 */
	public abstract boolean delete(Frame frame);

	/**
	 * Deletes the Frame with the given name, if such exists.
	 * 
	 * @param name the name
	 */
	public abstract boolean delete(String name);

}