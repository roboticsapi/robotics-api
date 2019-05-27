/* This Source Code Form is subject to the terms of the Mozilla Public
 * License, v. 2.0. If a copy of the MPL was not distributed with this
 * file, You can obtain one at https://mozilla.org/MPL/2.0/. 
 *
 * Copyright 2010-2017 ISSE, University of Augsburg 
 */

package org.roboticsapi.world.measurement;

import org.roboticsapi.configuration.ConfigurationProperty;
import org.roboticsapi.core.AbstractRoboticsObject;
import org.roboticsapi.core.PersistContext;
import org.roboticsapi.core.RoboticsRuntime;
import org.roboticsapi.core.exception.ConfigurationException;
import org.roboticsapi.core.exception.InitializationException;
import org.roboticsapi.core.exception.RoboticsException;
import org.roboticsapi.world.Connection;
import org.roboticsapi.world.Frame;
import org.roboticsapi.world.Transformation;
import org.roboticsapi.world.TransformationSensorConnection;
import org.roboticsapi.world.sensor.TransformationSensor;
import org.roboticsapi.world.util.FrameGraph;
import org.roboticsapi.world.util.FrameGraph.FrameGraphAdapter;

public class FrameMatchMeasurement extends AbstractRoboticsObject {

	private Frame from;
	private Frame to;
	private Frame measuredFrame;
	private Frame measurementFrame;
	private double smoothLength = 0.01;
	private double maximumAge = 10;
	private RoboticsRuntime runtime;

	private FrameGraph fromGraph, toGraph;

	private boolean fromToMeasured = false;
	private boolean fromToMeasurement = false;
	private boolean toToMeasured = false;
	private boolean toToMeasurement = false;

	private Connection connection = null;

	private final FrameGraphAdapter fromListener = new FrameGraphAdapter() {

		@Override
		public void onFrameAdded(Frame addedFrame) {
			if (addedFrame == measurementFrame) {
				fromToMeasurement = true;
			} else if (addedFrame == measuredFrame) {
				fromToMeasured = true;
			}
		}

		@Override
		public void onFrameRemoved(Frame removedFrame) {
			if (removedFrame == measurementFrame) {
				fromToMeasurement = false;
			} else if (removedFrame == measuredFrame) {
				fromToMeasured = false;
			}
		}
	};

	private final FrameGraphAdapter toListener = new FrameGraphAdapter() {
		@Override
		public void onFrameAdded(Frame addedFrame) {
			if (addedFrame == measurementFrame) {
				toToMeasurement = true;
			} else if (addedFrame == measuredFrame) {
				toToMeasured = true;
			}
			updateConnection();
		}

		@Override
		public void onFrameRemoved(Frame removedFrame) {
			if (removedFrame == measurementFrame) {
				toToMeasurement = false;
			} else if (removedFrame == measuredFrame) {
				toToMeasured = false;
			}
			updateConnection();
		}
	};
	private PersistContext<Transformation> persistContext;

	public RoboticsRuntime getRuntime() {
		return runtime;
	}

	public Frame getFrom() {
		return from;
	}

	protected void updateConnection() {
		try {
			if (toToMeasurement && !toToMeasured && !fromToMeasurement && fromToMeasured) {
				if (connection == null) {
					buildConnection(from, measuredFrame, measurementFrame, to);
				}
			} else if (!toToMeasurement && toToMeasured && fromToMeasurement && !fromToMeasured) {
				if (connection == null) {
					buildConnection(from, measurementFrame, measuredFrame, to);
				}
			} else if (connection != null && fromToMeasured && toToMeasured) {
				// we connected from and to... that's ok.

			} else if (connection != null && fromToMeasurement && toToMeasurement) {
				// we connected from and to... that's ok.

			} else if (connection != null) {
				if (persistContext != null) {
					persistContext.unpersist();
				}
				from.removeRelation(connection);
				persistContext = null;
				connection = null;
			}
		} catch (InitializationException ex) {
			ex.printStackTrace();
		} catch (RoboticsException e) {
			e.printStackTrace();
		}
	}

	private void buildConnection(Frame from, Frame f1, Frame f2, Frame to)
			throws RoboticsException, InitializationException {
		// calculate transformations of the circle from-f1-f2-to
		TransformationSensor ftm = from.getMeasuredRelationSensor(f1).getTransformationSensor();
		TransformationSensor mtt = f2.getMeasuredRelationSensor(to).getTransformationSensor();

		// take transformation of the measured frame at the time instant of the
		// measurement
		if (f2 == measurementFrame) {
			ftm = ftm.fromHistory(mtt.getSensorDataAgeSensor(), maximumAge);
		} else if (f1 == measurementFrame) {
			mtt = mtt.fromHistory(ftm.getSensorDataAgeSensor(), maximumAge);
		}

		// close the loop
		TransformationSensor trans = ftm.multiply(mtt);

		// smooth the result if requested
		if (smoothLength > 0) {
			trans = trans.slidingAverage(smoothLength);
		}

		// persist value (especially important when smoothing)
		persistContext = trans.persist(runtime);
		trans = (TransformationSensor) persistContext.getSensor();

		// build connection
		connection = new TransformationSensorConnection(trans, null);
		from.addRelation(connection, to);
	}

	public Frame getTo() {
		return to;
	}

	public Frame getMeasuredFrame() {
		return measuredFrame;
	}

	public Frame getMeasurementFrame() {
		return measurementFrame;
	}

	public double getSmoothLength() {
		return smoothLength;
	}

	public double getMaximumAge() {
		return maximumAge;
	}

	@ConfigurationProperty
	public void setRuntime(RoboticsRuntime runtime) {
		immutableWhenInitialized();
		this.runtime = runtime;
	}

	@ConfigurationProperty
	public void setMaximumAge(double maximumAge) {
		if (maximumAge <= 0) {
			throw new IllegalArgumentException("Argument maximumAge must be greater than 0.");
		}

		immutableWhenInitialized();
		this.maximumAge = maximumAge;
	}

	@ConfigurationProperty
	public void setFrom(Frame from) {
		immutableWhenInitialized();
		this.from = from;
	}

	@ConfigurationProperty
	public void setMeasuredFrame(Frame measuredFrame) {
		immutableWhenInitialized();
		this.measuredFrame = measuredFrame;
	}

	@ConfigurationProperty
	public void setMeasurementFrame(Frame measurementFrame) {
		immutableWhenInitialized();
		this.measurementFrame = measurementFrame;
	}

	@ConfigurationProperty
	public void setTo(Frame to) {
		immutableWhenInitialized();
		this.to = to;
	}

	@ConfigurationProperty
	public void setSmoothLength(double smoothLength) {
		if (smoothLength < 0) {
			throw new IllegalArgumentException("Argument smoothLength must not be negative.");
		}

		immutableWhenInitialized();
		this.smoothLength = smoothLength;
	}

	@Override
	protected void afterInitialization() throws RoboticsException {
		super.afterInitialization();

		fromGraph = new FrameGraph(from);
		toGraph = new FrameGraph(to);
		fromGraph.addFrameGraphListener(fromListener);
		toGraph.addFrameGraphListener(toListener);
	}

	@Override
	protected void beforeUninitialization() throws RoboticsException {
		super.beforeUninitialization();

		fromGraph.removeFrameGraphListener(fromListener, false);
		toGraph.removeFrameGraphListener(toListener, false);
		fromGraph = null;
		toGraph = null;

		if (connection != null) {
			connection.remove();
		}
		connection = null;
	}

	@Override
	protected void validateConfigurationProperties() throws ConfigurationException {
		super.validateConfigurationProperties();
		checkNotNullAndInitialized("from", from);
		checkNotNullAndInitialized("to", to);
		checkNotNullAndInitialized("measurementFrame", measurementFrame);
		checkNotNullAndInitialized("measuredFrame", measuredFrame);
		checkNotNullAndInitialized("runtime", runtime);
	}

}
