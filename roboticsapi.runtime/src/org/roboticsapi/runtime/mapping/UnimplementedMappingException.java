/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping;

/**
 * An Exception stating that the given entity cannot be mapped yet.
 */
public class UnimplementedMappingException extends MappingException {
	/**
	 * serial version id
	 */
	private static final long serialVersionUID = 1L;

	/**
	 * ] Creates an unimplemented mapping exception
	 */
	public UnimplementedMappingException() {
		super("Mapping not yet implemented");
	}
}
