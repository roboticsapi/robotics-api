/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.javarcc;

public class JFragmentInPort<T> extends JInPort<T> {
	private JOutPort<T> innerPort = new JOutPort<T>() {
		@Override
		protected T get() {
			return getSource().get();
		}

		@Override
		protected int getWriteCycle() {
			if (getSource() == null)
				return -1;
			else
				return getSource().getWriteCycle();
		}
	};

	protected void setPrimitive(String name, JPrimitive primitive) {
		super.setPrimitive(name, primitive);
		innerPort.setPrimitive(name, primitive);
	};

	public JOutPort<T> getInnerPort() {
		return innerPort;
	}

	@Override
	public T get() {
		throw new IllegalArgumentException("FragmentInPorts cannot be read.");
	}
}
