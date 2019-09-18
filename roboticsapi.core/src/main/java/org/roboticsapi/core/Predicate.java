/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

/**
 * 
 */
package org.roboticsapi.core;

/**
 * A generic predicate.
 * 
 */
public interface Predicate<T> {

	/**
	 * Decides whether the given object fulfills this predicate.
	 * 
	 * @param d The object to test for predicate fulfillment
	 * @return true if the object has this predicate, false otherwise
	 */
	boolean appliesTo(T d);

}
