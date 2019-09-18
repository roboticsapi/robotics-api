/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2013-2019 ISSE, University of Augsburg 
 */

package org.roboticsapi.facet.runtime.rpi.matrix;

import org.roboticsapi.core.Command;
import org.roboticsapi.core.RealtimeValue;
import org.roboticsapi.core.RealtimeValueListener;
import org.roboticsapi.core.matrix.Matrix;
import org.roboticsapi.core.realtimevalue.realtimeboolean.RealtimeBoolean;
import org.roboticsapi.core.world.realtimevalue.realtimematrix.RealtimeMatrix;
import org.roboticsapi.facet.runtime.rpi.NetcommListener;
import org.roboticsapi.facet.runtime.rpi.NetcommValue;
import org.roboticsapi.facet.runtime.rpi.OutPort;
import org.roboticsapi.facet.runtime.rpi.Primitive;
import org.roboticsapi.facet.runtime.rpi.core.netcomm.ReadDoubleArrayFromNet;
import org.roboticsapi.facet.runtime.rpi.core.types.RPIdoubleArray;
import org.roboticsapi.facet.runtime.rpi.mapping.InterNetcommFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.MappingException;
import org.roboticsapi.facet.runtime.rpi.mapping.ObserverFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueConsumerFragment;
import org.roboticsapi.facet.runtime.rpi.mapping.RealtimeValueFragment;
import org.roboticsapi.facet.runtime.rpi.matrix.primitives.MatrixArrayGet;

public class RealtimeMatrixFragment extends RealtimeValueFragment<Matrix> {

	private RealtimeMatrix realtimeValue;

	public RealtimeMatrixFragment(RealtimeMatrix value, OutPort result, OutPort time, Primitive... children) {
		super(value, result, time, children);
		realtimeValue = value;
	}

	public RealtimeMatrixFragment(RealtimeMatrix value, OutPort result, Primitive... children) {
		super(value, result, children);
		realtimeValue = value;
	}

	public RealtimeMatrixFragment(RealtimeMatrix value) {
		super(value);
		realtimeValue = value;
	}

	private static int nr = 0;

	@Override
	public RealtimeValueConsumerFragment createObserverFragment(RealtimeBoolean condition,
			final RealtimeValueListener<Matrix> observer) throws MappingException {

		RealtimeValueConsumerFragment ret = new ObserverFragment();

		int rows = realtimeValue.getRowDimension();
		int cols = realtimeValue.getColumnDimension();
		int size = rows * cols;

		final ReadDoubleArrayFromNet netcomm = ret.add(new ReadDoubleArrayFromNet("m" + ++nr, size));
		final MatrixArrayGet convert = ret.add(new MatrixArrayGet(0, 0, cols, size));
		ret.connect(convert.getOutArray(), netcomm.getInValue());

		netcomm.getNetcomm().addNetcommListener(new NetcommListener() {
			@Override
			public void valueChanged(NetcommValue value) {
				RPIdoubleArray array = new RPIdoubleArray(value.getString());
				Double[] ret = new Double[realtimeValue.getSize()];
				for (int i = 0; i < ret.length; i++)
					ret[i] = array.get(i).get();
				Matrix mat = Matrix.createFromArray(rows, cols, ret);
				observer.onValueChanged(mat);
			}

			@Override
			public void updatePerformed() {
			}
		});

		// TODO: condition, requires MatrixSetNull primitive
		ret.addDependency(realtimeValue, "inMatrix", convert.getInMatrix());
		return ret;
	}

	@Override
	public InterNetcommFragment createInterNetcommFragment(RealtimeBoolean condition) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public RealtimeValue<Matrix> createInterNetcommValue(Command command, String key) {
		// TODO Auto-generated method stub
		return null;
	}

}
