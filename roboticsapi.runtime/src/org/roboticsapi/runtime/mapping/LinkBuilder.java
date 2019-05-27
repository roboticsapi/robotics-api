/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.runtime.mapping;

import org.roboticsapi.runtime.mapping.net.DataflowType;

/**
 * Interface for a net fragment factory building converter nets between data
 * flow types
 */
public interface LinkBuilder {
	/**
	 * Builds a net fragment converting between data flow types
	 * 
	 * @param from source data type
	 * @param to   destination data type
	 * @return link builder result containing the net fragment to convert from
	 *         source to destination type
	 * @throws MappingException
	 */
	public LinkBuilderResult buildLink(DataflowType from, DataflowType to) throws MappingException;
}
